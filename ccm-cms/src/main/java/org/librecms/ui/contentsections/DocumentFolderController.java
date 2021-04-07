/*
 * Copyright (C) 2021 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.librecms.ui.contentsections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;
import org.librecms.contentsection.DocumentFolderEntry;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.contentsection.privileges.TypePrivileges;
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.ContentTypesManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

/**
 * Controller for managing doucment folders.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/documentfolders")
public class DocumentFolderController {

    private static final Logger LOGGER = LogManager.getLogger(
        DocumentFolderController.class
    );

    /**
     * Used for actions on {@link ContentItem}s in the folder.
     */
    @Inject
    private ContentItemManager itemManager;

    /**
     * Used to retrieve {@link ContentItem}s in the folder.
     */
    @Inject
    private ContentItemRepository itemRepo;

    /**
     * Used for localization actions for the items in the folder.
     */
    @Inject
    private ContentItemL10NManager itemL10NManager;

    /**
     * Model for the current {@link ContentSection}.
     */
    @Inject
    private ContentSectionModel contentSectionModel;

    /**
     * Provides common functions for all controllers working with
     * {@link ContentSection}s.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * Provides functions for working with content types.
     *
     */
    @Inject
    private ContentTypesManager typesManager;

    /**
     * Used to retrieve {@link ContentType}s.
     */
    @Inject
    private ContentTypeRepository contentTypeRepo;

    /**
     * Model for the current document folder.
     */
    @Inject
    private DocumentFolderModel documentFolderModel;

    /**
     * Used for actions on folders.
     */
    @Inject
    private FolderManager folderManager;

    /**
     * Used to retrieve and save folders.
     */
    @Inject
    private FolderRepository folderRepo;

    /**
     * Used for globalization actions.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to provide data for the views without a named bean.
     */
    @Inject
    private Models models;

    // Used to check permissions
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Used to update the permissions of the folder.
     */
    @Inject
    private PermissionManager permissionManager;

    /**
     * Used to retrieve roles.
     */
    @Inject
    private RoleRepository roleRepo;

    /**
     * Used to check permissions on {@link ContentItem}s.
     */
    @Inject
    private DocumentPermissions documentPermissions;

    /**
     * Model for the document folder tree.
     */
    @Inject
    private DocumentFolderTree documentFolderTree;

    /**
     * Privileges granted to the current for the items in the folder.
     */
    @Inject
    private GrantedItemPrivileges grantedPrivileges;

    /**
     * Privileges granted to the current for the items in the folder.
     */
    @Inject
    private CurrentUserDocumentPermissions currentUserPermissions;

    /**
     * Permission checker for {@link ContentItem}s.
     */
    @Inject
    private ItemPermissionChecker itemPermissionChecker;

    /**
     * List the content items and subfolders of the root folder of a content
     * section.
     *
     * @param sectionIdentifier The identifier of the content section.
     * @param filterTerm        An optional filter term for filtering the items
     *                          and subfolders.
     * @param firstResult       The index of the first result to show.
     * @param maxResults        The maximum number of results to show.
     *
     * @return The template for showing the content of a document folder.
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
     * List the content items and subfolders of a folder of a content section.
     *
     * @param sectionIdentifier The identifier of the content section.
     * @param folderPath        Path of the folder.
     * @param filterTerm        An optional filter term for filtering the items
     *                          and subfolders.
     * @param firstResult       The index of the first result to show.
     * @param maxResults        The maximum number of results to show.
     *
     * @return The template for showing the content of a document folder.
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
        final long start = System.currentTimeMillis();
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        LOGGER.info("Retrieved content section in {} ms",
                    System.currentTimeMillis() - start
        );

        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }

        final ContentSection section = sectionResult.get();
        if (!itemPermissionChecker.canEditItems(section)) {
            return sectionsUi.showAccessDenied(
                "sectionidentifier", sectionIdentifier
            );
        }

        contentSectionModel.setSection(section);

        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootDocumentsFolder();
            documentFolderModel.setBreadcrumbs(Collections.emptyList());
        } else {
            final Optional<Folder> folderResult = folderRepo
                .findByPath(
                    section,
                    folderPath,
                    FolderType.DOCUMENTS_FOLDER
                );
            if (folderResult.isPresent()) {
                folder = folderResult.get();

                documentFolderModel.setBreadcrumbs(buildBreadcrumbs(folderPath));
            } else {
                return showDocumentFolderNotFound(section, folderPath);
            }
        }

        if (!itemPermissionChecker.canEditItems(folder)) {
            return sectionsUi.showAccessDenied(
                "sectionidentifier", sectionIdentifier,
                "folderPath", folderPath
            );
        }

        final List<DocumentFolderEntry> folderEntries = folderRepo
            .getDocumentFolderEntries(
                folder,
                firstResult,
                maxResults,
                filterTerm
            );
        documentFolderModel.setCount(
            folderRepo.countDocumentFolderEntries(folder, filterTerm)
        );
        documentFolderModel.setFirstResult(firstResult);
        documentFolderModel.setMaxResults(maxResults);

        contentSectionModel.setDocumentFolders(
            documentFolderTree.buildFolderTree(section, folder)
        );

        contentSectionModel.setAvailableDocumentTypes(
            section
                .getContentTypes()
                .stream()
                .filter(
                    type -> permissionChecker.isPermitted(
                        TypePrivileges.USE_TYPE, type)
                )
                .map(typesManager::getContentTypeInfo)
                .sorted(
                    (typeInfo1, typeInfo2) -> globalizationHelper
                        .getLocalizedTextsUtil(
                            typeInfo1.getLabelBundle()).getText(
                        typeInfo1.getLabelKey()).compareTo(globalizationHelper
                        .getLocalizedTextsUtil(typeInfo2.getLabelBundle())
                        .getText(typeInfo2.getLabelKey()))
                )
                .collect(
                    Collectors.toMap(
                        typeInfo -> typeInfo.getContentItemClass().getName(),
                        typeInfo -> globalizationHelper
                            .getLocalizedTextsUtil(
                                typeInfo.getLabelBundle()).getText(
                            typeInfo.getLabelKey()
                        ),
                        (key1, key2) -> key1,
                        () -> new LinkedHashMap<>()
                    )
                )
        );

        documentFolderModel.setRows(
            folderEntries
                .stream()
                .map(entry -> buildRowModel(section, entry))
                .collect(Collectors.toList())
        );

        documentFolderModel.setPath(folderPath);
        documentFolderModel.setCanCreateSubFolders(
            itemPermissionChecker.canCreateNewItems(folder)
        );
        documentFolderModel.setCanCreateItems(
            itemPermissionChecker.canCreateNewItems(folder)
        );
        documentFolderModel.setCanAdminister(
            itemPermissionChecker.canAdministerItems(folder)
        );
        documentFolderModel.setGrantedPermissions(
            grantedPrivileges.buildPermissionsMatrix(section, folder)
        );
        documentFolderModel.setPrivileges(
            permissionManager.listDefiniedPrivileges(ItemPrivileges.class)
        );
        documentFolderModel.setCurrentUserPermissions(
            currentUserPermissions.buildCurrentUserPermissions(folder)
        );

        return "org/librecms/ui/contentsection/documentfolder/documentfolder.xhtml";
    }

    /**
     * Only for testing, will be removed.
     *
     * @param sectionIdentifier
     *
     * @return
     *
     * @deprecated
     */
    @GET
    @Path("/create-testdata")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Deprecated
    public String createTestData(
        @PathParam("sectionIdentifier") final String sectionIdentifier
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);

        if (sectionResult.isPresent()) {
            final ContentSection section = sectionResult.get();
            if (itemPermissionChecker.canEditItems(section)) {
                if (section.getRootDocumentsFolder().getObjects().isEmpty()) {
                    folderManager.createFolder(
                        "folder-1", section.getRootDocumentsFolder()
                    );
                    final Folder folder2 = folderManager.createFolder(
                        "folder-2", section.getRootDocumentsFolder()
                    );
                    folderManager.createFolder(
                        "folder-3", section.getRootDocumentsFolder()
                    );

                    final Article article = itemManager.createContentItem(
                        "test-article",
                        section,
                        section.getRootDocumentsFolder(),
                        Article.class,
                        Locale.ENGLISH
                    );
                    article.getTitle().addValue(Locale.ENGLISH, "Article 1");
                    article.getTitle().addValue(Locale.GERMAN, "Artikel 1");
                    itemRepo.save(article);

                    final Folder folder2a = folderManager.createFolder(
                        "folder-2a", folder2
                    );

                    final Article article2 = itemManager.createContentItem(
                        "test-article-in-folder-2",
                        section,
                        folder2,
                        Article.class,
                        Locale.ENGLISH
                    );
                    article2.getTitle().addValue(
                        Locale.ENGLISH, "Article in Folder 2"
                    );
                    article2.getTitle().addValue(
                        Locale.GERMAN, "Artikel in Ordner 2"
                    );

                    models.put(
                        "testdataMessage", "Test data created successfully."
                    );
                    return "org/librecms/ui/contentsection/documentfolder/testdata.xhtml";
                } else {
                    models.put(
                        "testdataMessage", "Test data was already created..."
                    );
                    return "org/librecms/ui/contentsection/documentfolder/testdata.xhtml";
                }
            } else {
                return sectionsUi.showAccessDenied(
                    "sectionidentifier", sectionIdentifier
                );
            }
        } else {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
    }

    /**
     * Creates a new subfolder in the root folder of a content section.
     *
     * @param sectionIdentifier The identifier of the content section.
     * @param folderName        The name of the new folder.
     *
     * @return A redirect to the listing of the root folder.
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
     * Create a new subfolder in a folder.
     *
     * @param sectionIdentifier The identifier of the content section.
     * @param parentFolderPath  The path of the parent folder.
     * @param folderName        The name of the new folder.
     *
     * @return A redirect to the listing of the parent folder.
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
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }

        final ContentSection section = sectionResult.get();
        if (!itemPermissionChecker.canEditItems(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier
            );
        }

        final Folder parentFolder;
        if (parentFolderPath.isEmpty()) {
            parentFolder = section.getRootDocumentsFolder();
        } else {
            final Optional<Folder> parentFolderResult = folderRepo
                .findByPath(
                    section,
                    parentFolderPath,
                    FolderType.DOCUMENTS_FOLDER
                );
            if (parentFolderResult.isPresent()) {
                parentFolder = parentFolderResult.get();
            } else {
                return showDocumentFolderNotFound(section, folderName);
            }
        }

        if (!itemPermissionChecker.canCreateNewItems(parentFolder)) {
            return sectionsUi.showAccessDenied(
                "sectionidentifier", sectionIdentifier,
                "folderPath", parentFolderPath
            );
        }

        folderManager.createFolder(folderName, parentFolder);

        return String.format(
            "redirect:/%s/documentfolders/%s",
            sectionIdentifier,
            parentFolderPath
        );
    }

    /**
     * Updates the permissions of the root folder of a content section.
     *
     * @param sectionIdentifier The identifier of the content section.
     * @param roleParam         The identifier of the role for which the
     *                          permissions are updated.
     * @param permissions       The updated permissions.
     *
     * @return A redirect to the listing of the folder.
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
     * Updates the permissions of afolder of a content section.
     *
     * @param sectionIdentifier The identifier of the content section.
     * @param folderPath        The path of the folder.
     * @param roleParam         The identifier of the role for which the
     *                          permissions are updated.
     * @param permissions       The updated permissions.
     *
     * @return A redirect to the listing of the folder.
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
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }

        final ContentSection section = sectionResult.get();
        if (!itemPermissionChecker.canEditItems(section)) {
            sectionsUi.showAccessDenied("sectionidentifier", sectionIdentifier);
        }

        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootDocumentsFolder();
            documentFolderModel.setBreadcrumbs(Collections.emptyList());
        } else {
            final Optional<Folder> folderResult = folderRepo
                .findByPath(
                    section,
                    folderPath,
                    FolderType.DOCUMENTS_FOLDER
                );
            if (folderResult.isPresent()) {
                folder = folderResult.get();

                documentFolderModel.setBreadcrumbs(buildBreadcrumbs(folderPath));
            } else {
                return showDocumentFolderNotFound(section, folderPath);
            }
        }

        if (!itemPermissionChecker.canAdministerItems(folder)) {
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
            .listDefiniedPrivileges(ItemPrivileges.class);

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
            "redirect:/%s/documentfolders/%s",
            sectionIdentifier,
            folderPath
        );
    }

    /**
     * Renames a folder.
     *
     * @param sectionIdentifier The identifier of the content section
     * @param folderPath        The path of the folder.
     * @param folderName        The new name of the folder.
     *
     * @return A redirect to the folder.
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
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }

        final ContentSection section = sectionResult.get();
        if (!itemPermissionChecker.canEditItems(section)) {
            return sectionsUi.showAccessDenied(
                "sectionidentifier", sectionIdentifier
            );
        }

        final Folder folder;
        final Optional<Folder> folderResult = folderRepo
            .findByPath(
                section,
                folderPath,
                FolderType.DOCUMENTS_FOLDER
            );
        if (folderResult.isPresent()) {
            folder = folderResult.get();

            documentFolderModel.setBreadcrumbs(buildBreadcrumbs(folderPath));
        } else {
            return showDocumentFolderNotFound(section, folderPath);
        }

        if (!itemPermissionChecker.canEditItems(folder)) {
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
            "redirect:/%s/documentfolders/%s",
            sectionIdentifier,
            returnFolderPath
        );
    }

    /**
     * A helper method for building the breadcrumb trail of a folder.
     *
     * @param folderPath The path of the folder.
     *
     * @return The breadcrumb trail for the folder.
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
     * Helper method for building a {@link DocumentFolderRowModel} for an entry
     * in the document folder.
     *
     * @param section The content section of the folder.
     * @param entry   The entry from which the row is created.
     *
     * @return A {@link DocumentFolderRowModel} for the provided {@code entry}.
     */
    private DocumentFolderRowModel buildRowModel(
        final ContentSection section, final DocumentFolderEntry entry
    ) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(entry);

        final DocumentFolderRowModel row = new DocumentFolderRowModel();
        if (entry.isFolder()) {
            final Folder folder = folderRepo
                .findById(entry.getEntryId())
                .get();
            row.setCreated("");
            row.setDeletable(
                folderManager
                    .folderIsDeletable(folder)
                    == FolderManager.FolderIsDeletable.YES
            );
            row.setFolder(true);
            row.setFolderPath(
                folderManager
                    .getFolderPath(folder)
                    .substring(
                        folderManager
                            .getFolderPath(section.getRootDocumentsFolder())
                            .length()
                    )
            );
            row.setLanguages(Collections.emptySortedSet());
            row.setLastEditPublished(false);
            row.setLastEdited("");
            row.setName(entry.getDisplayName());
            row.setTitle(
                globalizationHelper.getValueFromLocalizedString(
                    folder.getTitle()
                )
            );
            row.setType(
                globalizationHelper.getLocalizedTextsUtil(
                    "org.librecms.CmsAdminMessages"
                ).getText("contentsection.documentfolder.types.folder")
            );
            row.setPermissions(
                documentPermissions.buildDocumentPermissionsModel(folder)
            );
        } else {
            final ContentItem contentItem = itemRepo
                .findById(entry.getEntryId())
                .get();
            row.setCreated(
                DateTimeFormatter.ISO_DATE.format(
                    LocalDate.ofInstant(
                        contentItem.getCreationDate().toInstant(),
                        ZoneId.systemDefault()
                    )
                )
            );
            row.setDeletable(!itemManager.isLive(contentItem));
            row.setFolder(false);
            row.setFolderPath(itemManager.getItemPath(contentItem));
            row.setLanguages(
                new TreeSet<>(
                    itemL10NManager
                        .availableLanguages(contentItem)
                        .stream()
                        .map(Locale::toString)
                        .collect(Collectors.toSet())
                )
            );
            if (itemManager.isLive(contentItem)) {
                final LocalDate draftLastModified = LocalDate.ofInstant(
                    contentItem.getLastModified().toInstant(),
                    ZoneId.systemDefault()
                );
                final LocalDate liveLastModified = LocalDate.ofInstant(
                    itemManager
                        .getLiveVersion(contentItem, contentItem.getClass())
                        .map(ContentItem::getLastModified)
                        .map(Date::toInstant)
                        .get(),
                    ZoneId.systemDefault()
                );
                row.setLastEditPublished(
                    liveLastModified.isBefore(draftLastModified)
                );
            } else {
                row.setLastEditPublished(false);
            }

            row.setLastEdited(
                DateTimeFormatter.ISO_DATE.format(
                    LocalDate.ofInstant(
                        contentItem.getLastModified().toInstant(),
                        ZoneId.systemDefault()
                    )
                )
            );
            row.setName(entry.getDisplayName());
            row.setNoneCmsObject(false);
            row.setTitle(
                globalizationHelper.getValueFromLocalizedString(
                    contentItem.getTitle()
                )
            );
            row.setType(
                contentTypeRepo
                    .findByContentSectionAndClass(
                        section, contentItem.getClass()
                    )
                    .map(ContentType::getLabel)
                    .map(
                        label -> globalizationHelper
                            .getValueFromLocalizedString(
                                label
                            )
                    ).orElse("?")
            );
            row.setPermissions(
                documentPermissions.buildDocumentPermissionsModel(contentItem)
            );
        }

        return row;
    }

    /**
     * Helper method for showing the "document folder not found" page if there
     * is not folder for the provided path.
     *
     * @param section    The content section.
     * @param folderPath The folder path.
     *
     * @return The template of the "document folder not found" page.
     */
    private String showDocumentFolderNotFound(
        final ContentSection section, final String folderPath
    ) {
        models.put("contentSection", section.getLabel());
        models.put("folderPath", folderPath);
        return "org/librecms/ui/contentsection/documentfolder/documentfolder-not-found.xhtml";
    }

    private DocumentTypeInfoModel buildContentTypeInfo(
        final ContentType contentType
    ) {
        final DocumentTypeInfoModel model = new DocumentTypeInfoModel();
        model.setContentItemClass(contentType.getContentItemClass());
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                contentType.getDescription()
            )
        );
        model.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                contentType.getLabel()
            )
        );

        return model;
    }

}
