/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
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
 * Controller for managing the asset folders of a content section.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/assetfolders")
public class AssetFolderController {

    /**
     * The {@link AssetFolderModel} stores information about the current asset
     * folder for the view.
     */
    @Inject
    private AssetFolderModel assetFolderModel;

    /**
     * The {@link AssetFolderTree} stores information about the current asset
     * folder for the view.
     */
    @Inject
    private AssetFolderTree assetFolderTree;

    /**
     * Used to build the {@link AssetPermissionsModel}.
     */
    @Inject
    private AssetPermissionsModelProvider assetPermissions;

    /**
     * A special permissions checker for {@link Asset}s.
     */
    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    /**
     * {@link AssetManager} for performing operations on {@link Asset}s.
     */
    @Inject
    private AssetManager assetManager;

    /**
     * {@link AssetRepository} for storing and retrieving {@link Asset}s.
     */
    @Inject
    private AssetRepository assetRepo;

    /**
     * Stores information about the currnet {@link ContentSection} for the view.
     */
    @Inject
    private ContentSectionModel contentSectionModel;

    /**
     * Provides several services.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * Provides information about the permissions of a user for {@link Asset}s.
     */
    @Inject
    private CurrentUserAssetPermissions currentUserPermissions;

    /**
     * Performs operations on folders.
     */
    @Inject
    private FolderManager folderManager;

    /**
     * Stores and retrieves {@link Folder}s.
     */
    @Inject
    private FolderRepository folderRepo;

    /**
     * A helper for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Helper for getting the privileges for the current user for an
     * {@link Asset}.
     */
    @Inject
    private GrantedAssetPrivileges grantedPrivileges;

    /**
     * MVC {@link Models} instance. Used to store data for the view which is not
     * provided by a model class.
     */
    @Inject
    private Models models;

    /**
     * Performs operations on permissions.
     */
    @Inject
    private PermissionManager permissionManager;

    /**
     * Used to retrieve roles.
     */
    @Inject
    private RoleRepository roleRepo;

    /**
     * Lists the assets and subfolders in the
     * {@link ContentSection#rootAssetsFolder}.
     *
     * @param sectionIdentifier The identifier for the content section.
     * @param filterTerm        An optional filter term.
     * @param firstResult       The index of the first result to show.
     * @param maxResults        The maximum number of results to show.
     *
     * @return The template to use for generating the view.
     */
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

    /**
     * Lists the assets and subfolders in an assets folder.
     *
     * @param sectionIdentifier The identifier for the content section.
     * @param folderPath        The path of the folder.
     * @param filterTerm        An optional filter term.
     * @param firstResult       The index of the first result to show.
     * @param maxResults        The maximum number of results to show.
     *
     * @return The template to use for generating the view.
     */
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

    /**
     * Creates a new subfolder in the {@link ContentSection#rootAssetsFolder}.
     *
     * @param sectionIdentifier The identifier for the content section.
     * @param folderName        The name of the new folder.
     *
     * @return A redirect for showing the to the listing of the root folder.
     */
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

    /**
     * Creates a new subfolder in the {@link ContentSection#rootAssetsFolder}.
     *
     * @param sectionIdentifier The identifier for the content section.
     * @param parentFolderPath  Path of the parent folder of the new folder.
     * @param folderName        The name of the new folder.
     *
     * @return A redirect for showing the to the listing of the parent folder.
     */
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
                return showAssetFolderNotFound(section, folderName);
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

    /**
     * Update the permissions of a role for the
     * {@link ContentSection#rootAssetsFolder}.
     *
     * @param sectionIdentifier The identifier for the content section.
     * @param roleParam         The name of the role.
     * @param permissions       The new permissions of the role for the root
     *                          assets folder.
     *
     * @return A redirect to the listing of the root assets folder.
     */
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

    /**
     * Update the permissions of a role for a folder.
     *
     * @param sectionIdentifier The identifier for the content section.
     * @param folderPath        The path of the folder.
     * @param roleParam         The name of the role.
     * @param permissions       The new permissions of the role for the assets
     *                          folder.
     *
     * @return A redirect to the listing of the assets folder.
     */
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
            return sectionsUi.showAccessDenied(
                "sectionidentifier", sectionIdentifier
            );
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
                return showAssetFolderNotFound(section, folderPath);
            }
        }

        if (!assetPermissionsChecker.canEditAssets(folder)) {
            return sectionsUi.showAccessDenied(
                "sectionidentifier", sectionIdentifier,
                "folderPath", folderPath
            );
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

    /**
     * Renames a folder.
     *
     * @param sectionIdentifier The identifier for the content section.
     * @param folderPath        The path of the folder.
     * @param folderName        The new name of the folder.
     *
     * @return A redirect to the listing of the assets folder.
     */
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
            return sectionsUi.showAccessDenied(
                "sectionidentifier", sectionIdentifier
            );
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
            return showAssetFolderNotFound(section, folderPath);
        }

        if (!assetPermissionsChecker.canEditAssets(folder)) {
            return sectionsUi.showAccessDenied(
                "sectionidentifier", sectionIdentifier,
                "folderPath", folderPath
            );
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

    /**
     * Helper method for showing a error message if there is not folder for the
     * provided path.
     *
     * @param section    The current {@link ContentSection}.
     * @param folderPath The requested folder path.
     *
     * @return The error message view.
     */
    private String showAssetFolderNotFound(
        final ContentSection section, final String folderPath
    ) {
        models.put("contentSection", section.getLabel());
        models.put("folderPath", folderPath);
        return "org/librecms/ui/contentsection/assetfolder/asssetfolder-not-found.xhtml";
    }

    /**
     * Helper methods for building the breadcrumbs.
     *
     * @param folderPath The path of the current folder.
     *
     * @return The {@link FolderBreadcrumbsModel} for the path of the folder.
     */
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

    /**
     * Helper method for building the {@link AssetFolderRowModel} for a
     * subfolder.
     *
     * @param section The current content section.
     * @param entry   The entry to process.
     *
     * @return A {@link AssetFolderRowModel} for hte provided entry.
     */
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
