/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetFolderEntry;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;
import org.librecms.contentsection.privileges.AssetPrivileges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/assetfolders")
public class AssetFolderController {

    @Inject
    private AssetFolderModel assetFolderModel;

    @Inject
    private AssetFolderTree assetFolderTree;

    @Inject
    private AssetPermissionsModelProvider assetPermissions;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    @Inject
    private AssetManager assetManager;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private ContentSectionModel contentSectionModel;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private CurrentUserAssetPermissions currentUserPermissions;

    @Inject
    private FolderManager folderManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private GrantedAssetPrivileges grantedPrivileges;

    @Inject
    private Models models;

    @Inject
    private PermissionManager permissionManager;

    @Inject
    private RoleRepository roleRepo;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String list(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @QueryParam("filterTerm") @DefaultValue("") final String filterTerm,
        @QueryParam("firstResult") @DefaultValue("0") final int firstResult,
        @QueryParam("maxResults") @DefaultValue("20") final int maxResults
    ) {
        return list(
            sectionIdentifier, "", filterTerm, firstResult, maxResults
        );
    }

    @GET
    @Path("/{folderPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String list(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @QueryParam("filterTerm") @DefaultValue("") final String filterTerm,
        @QueryParam("firstResult") @DefaultValue("0") final int firstResult,
        @QueryParam("maxResults") @DefaultValue("20") final int maxResults
    ) {

        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        if (!assetPermissionsChecker.canEditAssets(section)) {
            sectionsUi.showAccessDenied("sectionIdentifier", sectionIdentifier);
        }

        contentSectionModel.setSection(section);

        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootAssetsFolder();
            assetFolderModel.setBreadcrumbs(Collections.emptyList());
        } else {
            final Optional<Folder> folderResult = folderRepo
                .findByPath(
                    section,
                    folderPath,
                    FolderType.ASSETS_FOLDER
                );
            if (folderResult.isPresent()) {
                folder = folderResult.get();
                assetFolderModel.setBreadcrumbs(buildBreadcrumbs(folderPath));
            } else {
                return showAssetFolderNotFound(section, folderPath);
            }
        }

        if (!assetPermissionsChecker.canEditAssets(folder)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "folderPath", folderPath
            );
        }

        final List<AssetFolderEntry> folderEntries = folderRepo
            .getAssetFolderEntries(folder, firstResult, maxResults, filterTerm);
        assetFolderModel.setCount(
            folderRepo.countAssetFolderEntries(folder, filterTerm)
        );
        assetFolderModel.setFirstResult(firstResult);
        assetFolderModel.setMaxResults(maxResults);

        contentSectionModel.setAssetFolders(
            assetFolderTree.buildFolderTree(section, folder)
        );

        assetFolderModel.setRows(
            folderEntries
                .stream()
                .map(entry -> buildRowModel(section, entry))
                .collect(Collectors.toList())
        );

        assetFolderModel.setPath(folderPath);
        assetFolderModel.setCanCreateSubFolders(
            assetPermissionsChecker.canCreateAssets(folder)
        );
        assetFolderModel.setCanCreateAssets(
            assetPermissionsChecker.canCreateAssets(folder)
        );
        assetFolderModel.setGrantedPermissions(
            grantedPrivileges.buildPermissionsMatrix(section, folder)
        );
        assetFolderModel.setPrivileges(
            permissionManager.listDefiniedPrivileges(AssetPrivileges.class)
        );
        assetFolderModel.setCurrentUserPermissions(
            currentUserPermissions.buildCurrentUserPermissions(folder)
        );

        return "org/librecms/ui/contentsection/assetfolder/assetfolder.xhtml";
    }

    @POST
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String newSubFolder(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @FormParam("folderName") final String folderName
    ) {
        return newSubFolder(
            sectionIdentifier, "", folderName
        );
    }

    @POST
    @Path("/{parentFolderPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String newSubFolder(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("parentFolderPath") final String parentFolderPath,
        @FormParam("folderName") final String folderName
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);

        final ContentSection section;
        if (sectionResult.isPresent()) {
            section = sectionResult.get();
        } else {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }

        if (!assetPermissionsChecker.canEditAssets(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier
            );
        }

        final Folder parentFolder;
        if (parentFolderPath.isEmpty()) {
            parentFolder = section.getRootAssetsFolder();
        } else {
            final Optional<Folder> parentFolderResult = folderRepo
                .findByPath(
                    section,
                    parentFolderPath,
                    FolderType.ASSETS_FOLDER
                );
            if (parentFolderResult.isPresent()) {
                parentFolder = parentFolderResult.get();
            } else {
                models.put("contentSection", section.getLabel());
                models.put("folderPath", parentFolderPath);
                return "org/librecms/ui/contentsection/assetfolder/assetfolder-not-found.xhtml";
            }
        }

        if (!assetPermissionsChecker.canEditAssets(parentFolder)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "folderPath", parentFolderPath
            );
        }

        folderManager.createFolder(folderName, parentFolder);

        return String.format(
            "redirect:/%s/assetfolders/%s",
            sectionIdentifier,
            parentFolderPath
        );
    }

    @POST
    @Path("/@permissions/{role}/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updatePermissions(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("role") final String roleParam,
        @FormParam("permissions") final List<String> permissions
    ) {
        return updatePermissions(
            sectionIdentifier, "", roleParam, permissions
        );
    }

    @POST
    @Path("/@permissions/{role}/{folderPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updatePermissions(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @PathParam("role") final String roleParam,
        @FormParam("permissions") final List<String> permissions
    ) {

        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        final ContentSection section;
        if (sectionResult.isPresent()) {
            section = sectionResult.get();
        } else {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        if (!assetPermissionsChecker.canEditAssets(section)) {
            models.put("sectionidentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootAssetsFolder();
            assetFolderModel.setBreadcrumbs(Collections.emptyList());
        } else {
            final Optional<Folder> folderResult = folderRepo
                .findByPath(
                    section,
                    folderPath,
                    FolderType.ASSETS_FOLDER
                );
            if (folderResult.isPresent()) {
                folder = folderResult.get();

                assetFolderModel.setBreadcrumbs(buildBreadcrumbs(folderPath));
            } else {
                models.put("contentSection", section.getLabel());
                models.put("folderPath", folderPath);
                return "org/librecms/ui/contentsection/assestfolder/assetfolder-not-found.xhtml";
            }
        }

        if (!assetPermissionsChecker.canEditAssets(folder)) {
            models.put("sectionidentifier", sectionIdentifier);
            models.put("folderPath", folderPath);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Optional<Role> roleResult = roleRepo.findByName(roleParam);
        if (!roleResult.isPresent()) {
            models.put("role", roleParam);
        }
        final Role role = roleResult.get();

        final List<String> privileges = permissionManager
            .listDefiniedPrivileges(AssetPrivileges.class);

        privileges
            .stream()
            .filter(privilege -> permissions.contains(privilege))
            .forEach(
                privilege -> permissionManager.grantPrivilege(
                    privilege, role, folder
                )
            );
        privileges
            .stream()
            .filter(privilege -> !permissions.contains(privilege))
            .forEach(
                privilege -> permissionManager.revokePrivilege(
                    privilege, role, folder
                )
            );

        return String.format(
            "redirect:/%s/assetfolders/%s",
            sectionIdentifier,
            folderPath
        );
    }

    @POST
    @Path("/@rename/{folderPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String renameFolder(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @FormParam("folderName") final String folderName
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        final ContentSection section;
        if (sectionResult.isPresent()) {
            section = sectionResult.get();
        } else {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        if (!assetPermissionsChecker.canEditAssets(section)) {
            models.put("sectionidentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Folder folder;
        final Optional<Folder> folderResult = folderRepo
            .findByPath(
                section,
                folderPath,
                FolderType.ASSETS_FOLDER
            );
        if (folderResult.isPresent()) {
            folder = folderResult.get();

            assetFolderModel.setBreadcrumbs(buildBreadcrumbs(folderPath));
        } else {
            models.put("contentSection", section.getLabel());
            models.put("folderPath", folderPath);
            return "org/librecms/ui/contentsection/assetfolder/assetfolder-not-found.xhtml";
        }

        if (!assetPermissionsChecker.canEditAssets(folder)) {
            models.put("sectionidentifier", sectionIdentifier);
            models.put("folderPath", folderPath);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        folder.setName(folderName);
        folderRepo.save(folder);

        final String[] folderPathTokens = folderPath.split("/");
        final String returnFolderPath = String.join(
            "/",
            Arrays.copyOf(folderPathTokens, folderPathTokens.length - 1)
        );

        return String.format(
            "redirect:/%s/assetfolders/%s",
            sectionIdentifier,
            returnFolderPath
        );
    }

    private String showAssetFolderNotFound(
        final ContentSection section, final String folderPath
    ) {
        models.put("contentSection", section.getLabel());
        models.put("folderPath", folderPath);
        return "org/librecms/ui/contentsection/assetfolder/asssetfolder-not-found.xhtml";
    }

    private List<FolderBreadcrumbsModel> buildBreadcrumbs(
        final String folderPath
    ) {
        final List<FolderBreadcrumbsModel> breadcrumbs
            = new ArrayList<>();
        final List<String> tokens = Arrays
            .stream(folderPath.split("/"))
            .filter(token -> !token.isEmpty())
            .collect(Collectors.toList());
        for (final String token : tokens) {
            final String path = breadcrumbs
                .stream()
                .map(FolderBreadcrumbsModel::getPathToken)
                .collect(Collectors.joining("/"));
            final FolderBreadcrumbsModel breadcrumb
                = new FolderBreadcrumbsModel();
            breadcrumb.setPath(path);
            breadcrumb.setPathToken(token);
            breadcrumbs.add(breadcrumb);
        }
        breadcrumbs
            .get(breadcrumbs.size() - 1)
            .setCurrentFolder(true);
        return breadcrumbs;
    }

    private AssetFolderRowModel buildRowModel(
        final ContentSection section, final AssetFolderEntry entry
    ) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(entry);

        final AssetFolderRowModel row = new AssetFolderRowModel();
        if (entry.isFolder()) {
            final Folder folder = folderRepo
                .findById(entry.getEntryId())
                .get();
            row.setDeletable(
                folderManager.folderIsDeletable(folder)
                    == FolderManager.FolderIsDeletable.YES
            );
            row.setFolder(true);
            row.setFolderPath(
                folderManager
                    .getFolderPath(folder)
                    .substring(
                        folderManager
                            .getFolderPath(section.getRootAssetsFolder())
                            .length()
                    )
            );
            row.setName(entry.getDisplayName());
            row.setTitle(
                globalizationHelper.getValueFromLocalizedString(
                    folder.getTitle()
                )
            );
            row.setType(
                globalizationHelper.getLocalizedTextsUtil(
                    "org.librecms.CmsAdminMessages"
                ).getText("contentsection.assetfolder.types.folder")
            );
            row.setPermissions(
                assetPermissions.buildAssetPermissionsModel(folder)
            );
        } else {
            final Asset asset = assetRepo
                .findById(entry.getEntryId())
                .get();
            row.setDeletable(!assetManager.isAssetInUse(asset));
            row.setFolder(false);
            row.setName(entry.getDisplayName());
            row.setNoneCmsObject(false);
            row.setTitle(
                globalizationHelper.getValueFromLocalizedString(
                    asset.getTitle()
                )
            );
            row.setType(asset.getClass().getName());
            row.setPermissions(
                assetPermissions.buildAssetPermissionsModel(asset)
            );
        }

        return row;
    }

}
