/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeManager;
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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}")
public class ContentSectionController {

    private static final Logger LOGGER = LogManager.getLogger(
        ContentSectionController.class
    );

    @Inject
    private CmsAdminMessages cmsAdminMessages;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemL10NManager itemL10NManager;

    @Inject
    private ContentSectionModel contentSectionModel;

    @Inject
    private ContentTypeManager contentTypeManager;

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

    @GET
    @Path("/document-folders{folderPath:(/.+)?}")
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
        LOGGER.info("Retrieved content section in {} ms", System
                    .currentTimeMillis() - start);

        if (sectionResult.isPresent()) {
            final ContentSection section = sectionResult.get();

            final long permissionCheckStart = System.currentTimeMillis();
            if (permissionChecker.isPermitted(
                ItemPrivileges.EDIT, section.getRootDocumentsFolder()
            )) {
                contentSectionModel.setSection(section);
                LOGGER.info(
                    "Checked in permisisons in {} ms.",
                    System.currentTimeMillis() - permissionCheckStart
                );

                final Folder folder;
                if (folderPath.isBlank()) {
                    folder = section.getRootDocumentsFolder();
                    documentFolderModel.setBreadcrumbs(Collections.emptyList());
                } else {
                    final Optional<Folder> folderResult = folderRepo
                        .findByPath(section,
                                    folderPath,
                                    FolderType.DOCUMENTS_FOLDER
                        );
                    if (folderResult.isPresent()) {
                        folder = folderResult.get();
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
                        documentFolderModel.setBreadcrumbs(breadcrumbs);
                    } else {
                        models.put("contentSection", section.getLabel());
                        models.put("folderPath", folderPath);
                        return "org/librecms/ui/content-section/document-folder-not-found.xhtml";
                    }
                }

                final long objectsStart = System.currentTimeMillis();
                final List<DocumentFolderEntry> folderEntries = folderRepo
                    .getDocumentFolderEntries(
                        folder,
                        firstResult,
                        maxResults,
                        filterTerm
                    );
                LOGGER.info(
                    "Retrieved objects in {} ms",
                    System.currentTimeMillis() - objectsStart
                );
                documentFolderModel.setCount(
                    folderRepo.countDocumentFolderEntries(folder, filterTerm)
                );
                documentFolderModel.setFirstResult(firstResult);
                documentFolderModel.setMaxResults(maxResults);
                LOGGER.info(
                    "Retrieved and counted objects in {} ms",
                    System.currentTimeMillis() - objectsStart
                );

                final List<FolderTreeNode> folderTree = buildFolderTree(
                    section, folder
                );
                contentSectionModel.setFolders(folderTree);

                final long rowsStart = System.currentTimeMillis();
                documentFolderModel.setRows(
                    folderEntries
                        .stream()
                        .map(entry -> buildRowModel(section, entry))
                        .collect(Collectors.toList())
                );
                LOGGER.info(
                    "Build rows in {} ms.",
                    System.currentTimeMillis() - rowsStart
                );

                return "org/librecms/ui/content-section/document-folder.xhtml";
            } else {
                models.put("sectionidentifier", sectionIdentifier);
                return "org/librecms/ui/content-section/access-denied.xhtml";
            }
        } else {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/content-section/contentsection-not-found.xhtml";
        }

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
                    return "org/librecms/ui/content-section/testdata.xhtml";
                } else {
                    models.put(
                        "testdataMessage", "Test data was already created..."
                    );
                    return "org/librecms/ui/content-section/testdata.xhtml";
                }
            } else {
                models.put("sectionidentifier", sectionIdentifier);
                return "org/librecms/ui/content-section/access-denied.xhtml";
            }
        } else {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/content-section/contentsection-not-found.xhtml";
        }
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
                    .findByContentSectionAndClass(section, contentItem
                                                  .getClass())
                    .map(ContentType::getLabel)
                    .map(
                        label -> globalizationHelper
                            .getValueFromLocalizedString(
                                label
                            )
                    ).orElse("?")
            );

        }

        return row;
    }

//    private DocumentFolderRowModel buildRowModel(
//        final ContentSection section, final CcmObject object
//    ) {
//        Objects.requireNonNull(section);
//        Objects.requireNonNull(object);
//        if (object instanceof ContentItem) {
//            return buildRowModel(section, (ContentItem) object);
//        } else if (object instanceof Folder) {
//            return buildRowModel(section, (Folder) object);
//        } else {
//            final DocumentFolderRowModel row = new DocumentFolderRowModel();
//
//            row.setCreated("");
//            row.setDeletable(false);
//            row.setIsFolder(false);
//            row.setLanguages(Collections.emptySortedSet());
//            row.setLastEditPublished(false);
//            row.setLastEdited("");
//            row.setName(object.getDisplayName());
//            row.setTitle("");
//            row.setType(object.getClass().getSimpleName());
//
//            return row;
//        }
//    }
//
//    private DocumentFolderRowModel buildRowModel(
//        final ContentSection section, final ContentItem contentItem
//    ) {
//        Objects.requireNonNull(section);
//        Objects.requireNonNull(contentItem);
//
//        final DocumentFolderRowModel row = new DocumentFolderRowModel();
//        row.setCreated(
//            DateTimeFormatter.ISO_DATE.format(
//                LocalDate.ofInstant(
//                    contentItem.getCreationDate().toInstant(),
//                    ZoneId.systemDefault()
//                )
//            )
//        );
//        row.setDeletable(!itemManager.isLive(contentItem));
//        row.setIsFolder(false);
//        row.setLanguages(
//            new TreeSet<>(
//                itemL10NManager
//                    .availableLanguages(contentItem)
//                    .stream()
//                    .map(Locale::toString)
//                    .collect(Collectors.toSet())
//            )
//        );
//        if (itemManager.isLive(contentItem)) {
//            final LocalDate draftLastModified = LocalDate.ofInstant(
//                contentItem.getLastModified().toInstant(),
//                ZoneId.systemDefault()
//            );
//            final LocalDate liveLastModified = LocalDate.ofInstant(
//                itemManager
//                    .getLiveVersion(contentItem, contentItem.getClass())
//                    .map(ContentItem::getLastModified)
//                    .map(Date::toInstant)
//                    .get(),
//                ZoneId.systemDefault()
//            );
//            row.setLastEditPublished(
//                liveLastModified.isBefore(draftLastModified)
//            );
//
//        } else {
//            row.setLastEditPublished(false);
//        }
//
//        row.setLastEdited(
//            DateTimeFormatter.ISO_DATE.format(
//                LocalDate.ofInstant(
//                    contentItem.getLastModified().toInstant(),
//                    ZoneId.systemDefault()
//                )
//            )
//        );
//        row.setName(contentItem.getDisplayName());
//        row.setNoneCmsObject(false);
//        row.setTitle(
//            globalizationHelper.getValueFromLocalizedString(
//                contentItem.getTitle()
//            )
//        );
//        row.setType(
//            contentTypeRepo
//                .findByContentSectionAndClass(section, contentItem.getClass())
//                .map(ContentType::getLabel)
//                .map(
//                    label -> globalizationHelper.getValueFromLocalizedString(
//                        label
//                    )
//                ).orElse("?")
//        );
//
//        return row;
//    }
//
//    private DocumentFolderRowModel buildRowModel(
//        final ContentSection section, final Folder folder
//    ) {
//        Objects.requireNonNull(section);
//        Objects.requireNonNull(folder);
//
//        final DocumentFolderRowModel row = new DocumentFolderRowModel();
//        row.setCreated("");
//        row.setDeletable(
//            folderManager.folderIsDeletable(folder)
//                == FolderManager.FolderIsDeletable.YES
//        );
//        row.setIsFolder(true);
//        row.setLanguages(Collections.emptySortedSet());
//        row.setLastEditPublished(false);
//        row.setLastEdited("");
//        row.setName(folder.getDisplayName());
//        row.setNoneCmsObject(false);
//        row.setTitle(
//            globalizationHelper.getValueFromLocalizedString(folder.getTitle())
//        );
//        row.setType(
//            globalizationHelper.getLocalizedTextsUtil(
//                "org.librecms.CmsAdminMessages"
//            ).getText("contentsection.documentfolder.types.folder")
//        );
//
//        return row;
//    }
}
