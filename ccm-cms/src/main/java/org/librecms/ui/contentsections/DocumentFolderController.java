/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.Permission;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;
import org.librecms.contentsection.DocumentFolderEntry;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.contenttypes.Article;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
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

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemL10NManager itemL10NManager;

    @Inject
    private ContentSectionModel contentSectionModel;

    @Inject
    private ContentTypeRepository contentTypeRepo;

    @Inject
    private DocumentFolderModel documentFolderModel;

    @Inject
    private FolderManager folderManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private PermissionManager permissionManager;

    @Inject
    private RoleRepository roleRepo;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listItems(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @QueryParam("filterTerm") @DefaultValue("") final String filterTerm,
        @QueryParam("firstResult") @DefaultValue("0") final int firstResult,
        @QueryParam("maxResults") @DefaultValue("20") final int maxResults
    ) {
        return listItems(
            sectionIdentifier, "", filterTerm, firstResult, maxResults
        );
    }

    @GET
    @Path("/{folderPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listItems(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @QueryParam("filterTerm") @DefaultValue("") final String filterTerm,
        @QueryParam("firstResult") @DefaultValue("0") final int firstResult,
        @QueryParam("maxResults") @DefaultValue("20") final int maxResults
    ) {
        final long start = System.currentTimeMillis();
        final Optional<ContentSection> sectionResult = retrieveContentSection(
            sectionIdentifier
        );
        LOGGER.info("Retrieved content section in {} ms",
                    System.currentTimeMillis() - start
        );

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }

        final ContentSection section = sectionResult.get();
        if (!permissionChecker.isPermitted(
            ItemPrivileges.EDIT, section.getRootDocumentsFolder()
        )) {
            models.put("sectionidentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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
                models.put("contentSection", section.getLabel());
                models.put("folderPath", folderPath);
                return "org/librecms/ui/contentsection/documentfolder/documentfolder-not-found.xhtml";
            }
        }

        if (!permissionChecker.isPermitted(ItemPrivileges.EDIT, folder)) {
            models.put("sectionidentifier", sectionIdentifier);
            models.put("folderPath", folderPath);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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

        final List<FolderTreeNode> folderTree = buildFolderTree(
            section, folder
        );
        contentSectionModel.setFolders(folderTree);

        documentFolderModel.setRows(
            folderEntries
                .stream()
                .map(entry -> buildRowModel(section, entry))
                .collect(Collectors.toList())
        );

        documentFolderModel.setPath(folderPath);
        documentFolderModel.setCanCreateSubFolders(
            permissionChecker.isPermitted(
                ItemPrivileges.CREATE_NEW, folder
            )
        );
        documentFolderModel.setCanCreateItems(
            permissionChecker.isPermitted(
                ItemPrivileges.CREATE_NEW, folder
            )
        );
        documentFolderModel.setCanAdminister(
            permissionChecker.isPermitted(
                ItemPrivileges.ADMINISTER, folder
            )
        );
        documentFolderModel.setGrantedPermissions(
            buildPermissionsMatrix(section, folder)
        );
        documentFolderModel.setPrivileges(
            permissionManager.listDefiniedPrivileges(ItemPrivileges.class)
        );
        documentFolderModel.setCurrentUserPermissions(
            buildCurrentUserPermissions(folder)
        );

        return "org/librecms/ui/contentsection/documentfolder/documentfolder.xhtml";
    }

    @GET
    @Path("/create-testdata")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String createTestData(
        @PathParam("sectionIdentifier") final String sectionIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            sectionIdentifier
        );
        final Optional<ContentSection> sectionResult;
        switch (identifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(identifier
                    .getIdentifier());
                break;
            default:
                sectionResult = sectionRepo.findByLabel(identifier
                    .getIdentifier());
                break;
        }

        if (sectionResult.isPresent()) {
            final ContentSection section = sectionResult.get();

            if (permissionChecker.isPermitted(
                ItemPrivileges.EDIT, section.getRootDocumentsFolder()
            )) {
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
                models.put("sectionidentifier", sectionIdentifier);
                return "org/librecms/ui/contentsection/access-denied.xhtml";
            }
        } else {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
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
        final Optional<ContentSection> sectionResult = retrieveContentSection(
            sectionIdentifier
        );

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }

        final ContentSection section = sectionResult.get();
        if (!permissionChecker.isPermitted(
            ItemPrivileges.EDIT, section.getRootDocumentsFolder()
        )) {
            models.put("sectionidentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        final Folder parentFolder;
        if (parentFolderPath.isEmpty()) {
            parentFolder = section.getRootDocumentsFolder();
        } else {
            final Optional<Folder> parentFolderResult = folderRepo
                .findByPath(section, parentFolderPath,
                            FolderType.DOCUMENTS_FOLDER);
            if (parentFolderResult.isPresent()) {
                parentFolder = parentFolderResult.get();
            } else {
                models.put("contentSection", section.getLabel());
                models.put("folderPath", parentFolderPath);
                return "org/librecms/ui/contentsection/documentfolder/documentfolder-not-found.xhtml";
            }
        }

        if (!permissionChecker.isPermitted(
            ItemPrivileges.CREATE_NEW, parentFolder
        )) {
            models.put("sectionidentifier", sectionIdentifier);
            models.put("folderPath", parentFolderPath);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
        }

        folderManager.createFolder(folderName, parentFolder);

        return String.format(
            "redirect:/%s/documentfolders/%s",
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
        final Optional<ContentSection> sectionResult = retrieveContentSection(
            sectionIdentifier
        );
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }

        final ContentSection section = sectionResult.get();
        if (!permissionChecker.isPermitted(
            ItemPrivileges.EDIT, section.getRootDocumentsFolder()
        )) {
            models.put("sectionidentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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
                models.put("contentSection", section.getLabel());
                models.put("folderPath", folderPath);
                return "org/librecms/ui/contentsection/documentfolder/documentfolder-not-found.xhtml";
            }
        }

        if (!permissionChecker.isPermitted(ItemPrivileges.ADMINISTER, folder)) {
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

    @POST
    @Path("/@rename/{folderPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String renameFolder(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @FormParam("folderName") final String folderName
    ) {
        final Optional<ContentSection> sectionResult = retrieveContentSection(
            sectionIdentifier
        );
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }

        final ContentSection section = sectionResult.get();
        if (!permissionChecker.isPermitted(
            ItemPrivileges.EDIT, section.getRootDocumentsFolder()
        )) {
            models.put("sectionidentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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
            models.put("contentSection", section.getLabel());
            models.put("folderPath", folderPath);
            return "org/librecms/ui/contentsection/documentfolder/documentfolder-not-found.xhtml";
        }

        if (!permissionChecker.isPermitted(ItemPrivileges.EDIT, folder)) {
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
            "redirect:/%s/documentfolders/%s",
            sectionIdentifier,
            returnFolderPath
        );
    }

    private Optional<ContentSection> retrieveContentSection(
        final String sectionIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            sectionIdentifier
        );

        final Optional<ContentSection> sectionResult;
        switch (identifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(identifier
                    .getIdentifier());
                break;
            default:
                sectionResult = sectionRepo.findByLabel(identifier
                    .getIdentifier());
                break;
        }
        return sectionResult;
    }

    private List<FolderTreeNode> buildFolderTree(
        final ContentSection section, final Folder currentFolder
    ) {
        final Folder root = section.getRootDocumentsFolder();
        final String currentFolderPath = folderManager
            .getFolderPath(currentFolder)
            .substring(
                folderManager
                    .getFolderPath(section.getRootDocumentsFolder())
                    .length() - 1
            );

        return root
            .getSubFolders()
            .stream()
            .sorted(
                (folder1, folder2)
                -> folder1.getName().compareTo(folder2.getName())
            )
            .map(folder -> buildFolderTreeNode(section, currentFolderPath,
                                               folder))
            .collect(Collectors.toList());
    }

    private FolderTreeNode buildFolderTreeNode(
        final ContentSection section,
        final String currentFolderPath,
        final Folder folder
    ) {
        final String folderPath = folderManager
            .getFolderPath(folder)
            .substring(
                folderManager
                    .getFolderPath(section.getRootDocumentsFolder())
                    .length() - 1
            );

        final FolderTreeNode node = new FolderTreeNode();
        node.setFolderId(folder.getObjectId());
        node.setUuid(folder.getUuid());
        node.setName(folder.getName());
        node.setPath(folderPath);
        node.setOpen(currentFolderPath.startsWith(folderPath));
        node.setSelected(currentFolderPath.equals(folderPath));
        node.setPermissions(buildItemPermissionsModel(folder));
        node.setSubFolders(
            folder
                .getSubFolders()
                .stream()
                .sorted(
                    (folder1, folder2)
                    -> folder1.getName().compareTo(folder2.getName())
                )
                .map(
                    subFolder -> buildFolderTreeNode(
                        section, currentFolderPath, subFolder
                    )
                )
                .collect(Collectors.toList())
        );

        return node;
    }

    private List<DocumentFolderBreadcrumbModel> buildBreadcrumbs(
        final String folderPath
    ) {
        final List<DocumentFolderBreadcrumbModel> breadcrumbs
            = new ArrayList<>();
        final List<String> tokens = Arrays
            .stream(folderPath.split("/"))
            .filter(token -> !token.isEmpty())
            .collect(Collectors.toList());
        for (final String token : tokens) {
            final String path = breadcrumbs
                .stream()
                .map(DocumentFolderBreadcrumbModel::getPathToken)
                .collect(Collectors.joining("/"));
            final DocumentFolderBreadcrumbModel breadcrumb
                = new DocumentFolderBreadcrumbModel();
            breadcrumb.setPath(path);
            breadcrumb.setPathToken(token);
            breadcrumbs.add(breadcrumb);
        }
        breadcrumbs
            .get(breadcrumbs.size() - 1)
            .setCurrentFolder(true);
        return breadcrumbs;
    }

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
            row.setPermissions(buildItemPermissionsModel(folder));
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
            row.setPermissions(buildItemPermissionsModel(contentItem));
        }

        return row;
    }

    private ItemPermissionsModel buildItemPermissionsModel(
        final Folder folder
    ) {
        final ItemPermissionsModel model = new ItemPermissionsModel();
        model.setGrantedAdminister(
            permissionChecker.isPermitted(
                ItemPrivileges.ADMINISTER, folder
            )
        );
        model.setGrantedApplyAlternateWorkflow(
            permissionChecker.isPermitted(
                ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, folder
            )
        );
        model.setGrantedApprove(
            permissionChecker.isPermitted(
                ItemPrivileges.APPROVE, folder
            )
        );
        model.setGrantedCategorize(
            permissionChecker.isPermitted(
                ItemPrivileges.CATEGORIZE, folder
            )
        );
        model.setGrantedCreateNew(
            permissionChecker.isPermitted(
                ItemPrivileges.CREATE_NEW, folder
            )
        );
        model.setGrantedDelete(
            permissionChecker.isPermitted(
                ItemPrivileges.DELETE, folder
            )
        );
        model.setGrantedEdit(
            permissionChecker.isPermitted(
                ItemPrivileges.EDIT, folder
            )
        );
        model.setGrantedPreview(
            permissionChecker.isPermitted(
                ItemPrivileges.PREVIEW, folder
            )
        );
        model.setGrantedPublish(
            permissionChecker.isPermitted(
                ItemPrivileges.PUBLISH, folder
            )
        );
        model.setGrantedViewPublished(
            permissionChecker.isPermitted(
                ItemPrivileges.VIEW_PUBLISHED, folder
            )
        );
        return model;
    }

    private ItemPermissionsModel buildItemPermissionsModel(
        final ContentItem item
    ) {
        final ItemPermissionsModel model = new ItemPermissionsModel();
        model.setGrantedAdminister(
            permissionChecker.isPermitted(
                ItemPrivileges.ADMINISTER, item
            )
        );
        model.setGrantedApplyAlternateWorkflow(
            permissionChecker.isPermitted(
                ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, item
            )
        );
        model.setGrantedApprove(
            permissionChecker.isPermitted(
                ItemPrivileges.APPROVE, item
            )
        );
        model.setGrantedCategorize(
            permissionChecker.isPermitted(
                ItemPrivileges.CATEGORIZE, item
            )
        );
        model.setGrantedCreateNew(
            permissionChecker.isPermitted(
                ItemPrivileges.CREATE_NEW, item
            )
        );
        model.setGrantedDelete(
            permissionChecker.isPermitted(
                ItemPrivileges.DELETE, item
            )
        );
        model.setGrantedEdit(
            permissionChecker.isPermitted(
                ItemPrivileges.EDIT, item
            )
        );
        model.setGrantedPreview(
            permissionChecker.isPermitted(
                ItemPrivileges.PREVIEW, item
            )
        );
        model.setGrantedPublish(
            permissionChecker.isPermitted(
                ItemPrivileges.PUBLISH, item
            )
        );
        model.setGrantedViewPublished(
            permissionChecker.isPermitted(
                ItemPrivileges.VIEW_PUBLISHED, item
            )
        );
        return model;
    }

    private List<PrivilegesGrantedToRoleModel> buildPermissionsMatrix(
        final ContentSection section, final Folder folder
    ) {
        return section
            .getRoles()
            .stream()
            .map(role -> buildPrivilegesGrantedToRoleModel(role, folder))
            .collect(Collectors.toList());
    }

    private PrivilegesGrantedToRoleModel buildPrivilegesGrantedToRoleModel(
        final Role role, final Folder folder
    ) {
        final List<GrantedPrivilegeModel> grantedPrivilges = permissionManager
            .listDefiniedPrivileges(ItemPrivileges.class)
            .stream()
            .map(
                privilege -> buildGrantedPrivilegeModel(
                    role,
                    folder,
                    privilege,
                    permissionManager.findPermissionsForRoleAndObject(
                        role, folder
                    )
                )
            )
            .collect(Collectors.toList());

        final PrivilegesGrantedToRoleModel model
            = new PrivilegesGrantedToRoleModel();
        model.setGrantedPrivileges(grantedPrivilges);
        model.setGrantee(role.getName());

        return model;
    }

    private GrantedPrivilegeModel buildGrantedPrivilegeModel(
        final Role role,
        final Folder folder,
        final String privilege,
        final List<Permission> permissions
    ) {
        final GrantedPrivilegeModel model = new GrantedPrivilegeModel();
        model.setGranted(permissionChecker.isPermitted(privilege, folder, role));
        model.setInherited(
            model.isGranted()
                && permissions
                .stream()
                .anyMatch(
                    permission
                    -> permission.getGrantee().equals(role)
                           && permission.getGrantedPrivilege().equals(privilege)
                           && permission.getObject().equals(folder)
                           && permission.getInheritedFrom() != null
                )
        );
        model.setPrivilege(privilege);

        return model;
    }

    private List<GrantedPrivilegeModel> buildCurrentUserPermissions(
        final Folder folder
    ) {
        return permissionManager
            .listDefiniedPrivileges(ItemPrivileges.class)
            .stream()
            .map(privilege -> buildCurrentUserPermission(folder, privilege))
            .collect(Collectors.toList());
    }

    private GrantedPrivilegeModel buildCurrentUserPermission(
        final Folder folder, final String privilege
    ) {
        final GrantedPrivilegeModel model = new GrantedPrivilegeModel();
        model.setPrivilege(privilege);
        model.setGranted(permissionChecker.isPermitted(privilege, folder));
        return model;
    }

}
