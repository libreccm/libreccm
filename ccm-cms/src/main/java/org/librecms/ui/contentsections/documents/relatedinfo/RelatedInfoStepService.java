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
package org.librecms.ui.contentsections.documents.relatedinfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.AttachmentListRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentTypeRepository;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.contentsection.ItemAttachmentManager;
import org.librecms.ui.contentsections.AssetFolderTree;
import org.librecms.ui.contentsections.AssetPermissionsModel;
import org.librecms.ui.contentsections.AssetPermissionsModelProvider;
import org.librecms.ui.contentsections.ContentSectionsUi;
import org.librecms.ui.contentsections.DocumentFolderTree;
import org.librecms.ui.contentsections.DocumentPermissions;
import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "relatedinfo-service")
public class RelatedInfoStepService {

    private static final Logger LOGGER = LogManager.getLogger(
        RelatedInfoStepService.class
    );

    /**
     * The asset folder tree of the current content section.
     */
    @Inject
    private AssetFolderTree assetFolderTree;

    /**
     * Used to build the {@link AssetPermissionsModel}.
     */
    @Inject
    private AssetPermissionsModelProvider assetPermissions;

    @Inject
    private AssetManager assetManager;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AttachmentListRepository attachmentListRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemL10NManager itemL10NManager;

    @Inject
    private ContentTypeRepository contentTypeRepo;

    @Inject
    private DocumentFolderTree documentFolderTree;

    /**
     * Used to check permissions of the current content item.
     */
    @Inject
    private DocumentPermissions documentPermissions;

    @Inject
    private ItemAttachmentManager attachmentManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private FolderManager folderManager;

    /**
     * Used to retrieve folders.
     */
    @Inject
    private FolderRepository folderRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private ContentSectionsUi sectionsUi;

    @POST
    @Path("/save-order")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public Response saveOrder(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        final RelatedInfoStepAttachmentOrder order
    ) {
        // ToDo
        LOGGER.info("order = {}", order);

        final ContentSection contentSection = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new NotFoundException(
                    String.format(
                        "No content identifed by %s found.",
                        sectionIdentifier
                    )
                )
            );

        final ContentItem document = itemRepo
            .findByPath(contentSection, documentPath)
            .orElseThrow(
                () -> new NotFoundException(
                    String.format(
                        "No document for path %s in section %s.",
                        documentPath,
                        contentSection.getLabel()
                    )
                )
            );

        final List<AttachmentList> attachmentLists = document.getAttachments();
        final List<String> attachmentListsOrder = order
            .getAttachmentListsOrder();

        if (attachmentListsOrder.size() != attachmentLists.size()) {
            throw new BadRequestException(
                String.format(
                    "Size of lists of attachment lists does not match list of "
                        + "attachment order list. attachmentLists.size = %d, "
                        + "attachmentListsOrder.size = %d.",
                    attachmentLists.size(),
                    attachmentListsOrder.size()
                )
            );
        }

        for (int i = 0; i < attachmentListsOrder.size(); i++) {
            final String listUuid = attachmentListsOrder.get(i);
            final AttachmentList attachmentList = attachmentLists
                .stream()
                .filter(list -> listUuid.equals(list.getUuid()))
                .findAny()
                .orElseThrow(
                    () -> new BadRequestException(
                        String.format(
                            "attachmentListsOrder has an entry for attachment "
                                + "list %s, but there no attachment list with "
                                + "that UUID.",
                            listUuid
                        )
                    )
                );
            
                attachmentList.setListOrder(i);
                attachmentListRepo.save(attachmentList);
        }

        for (final Map.Entry<String, List<String>> attachmentsOrder : order
            .getAttachmentsOrder().entrySet()) {
            final AttachmentList attachmentList = document
                .getAttachments()
                .stream()
                .filter(list -> attachmentsOrder.getKey().equals(list.getUuid()))
                .findAny()
                .orElseThrow(
                    () -> new BadRequestException(
                        String.format(
                            "attachmentsOrder contains an entry for "
                                + "attachment list %s, but there no attachment "
                                + "list with that UUID.",
                            attachmentsOrder.getKey()
                        )
                    )
                );
            
            final List<ItemAttachment<?>> attachments = attachmentList.getAttachments();
            if (attachments.size() != attachmentsOrder.getValue().size()) {
                throw new BadRequestException(
                    String.format(
                        "Size of attachmentsOrder list does not match the size"
                        + "of the attachments list. "
                        + "attachmentsOrder.size = %d, "
                        + "attachmentsList.size = %d",
                        attachmentsOrder.getValue().size(),
                        attachments.size()
                    )
                );
            }
            
            for(int i = 0; i < attachmentsOrder.getValue().size(); i++) {
                final String attachmentUuid = attachmentsOrder.getValue().get(i);
                final ItemAttachment<?> attachment = attachments
                    .stream()
                    .filter(current -> attachmentUuid.equals(current.getUuid()))
                    .findAny()
                    .orElseThrow(
                        () -> new BadRequestException(
                            String.format(
                                "attachmentOrder order for attachment list %s "
                                    + "has an entry for attachment %s but "
                                    + "there is attachment with that UUID in "
                                    + "the list.",
                                attachmentList.getUuid(),
                                attachmentUuid
                            )
                        )
                    );
                attachment.setSortKey(i);
                attachmentManager.save(attachment);
            }
        }

        return Response.ok().build();

//        
//        //final Map<String, Integer> attachmentListIndexes = 
    }

//    /**
//     * Gets the asset folder tree of the current content section as JSON data.
//     *
//     * @param sectionIdentifier
//     * @param documentPath
//     *
//     * @return The assets folder tree of the current content section as JSON
//     *         data.
//     */
//    @GET
//    @Path("/asset-folders")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Transactional(Transactional.TxType.REQUIRED)
//    public List<AssetFolderTreeNode> getAssetFolderTree(
//        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
//        final String sectionIdentifier,
//        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
//        final String documentPath
//    ) {
//        final ContentSection section = retrieveContentSection(
//            sectionIdentifier
//        );
//
//        if (permissionChecker.isPermitted(
//            ItemPrivileges.EDIT, retrieveDocument(section, documentPath)
//        )) {
//            return assetFolderTree.buildFolderTree(
//                section, section.getRootAssetsFolder()
//            );
//        } else {
//            throw new ForbiddenException();
//        }
//    }
//
//    /**
//     * Gets the assets in the folder as JSON data.
//     *
//     * @param folderPath        The path of the folder.
//     * @param firstResult       The index of the firset result to show.
//     * @param maxResults        The maximum number of results to show.
//     * @param filterTerm        An optional filter term for filtering the assets
//     *                          in the folder by their name.
//     * @param documentPath
//     * @param sectionIdentifier
//     *
//     * @return A list of the assets in the folder as JSON data.
//     */
//    @GET
//    @Path("/asset-folders/{folderPath}/assets")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Transactional(Transactional.TxType.REQUIRED)
//    public List<AssetFolderRowModel> getAssetsInFolder(
//        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
//        final String sectionIdentifier,
//        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
//        final String documentPath,
//        @PathParam("folderPath")
//        final String folderPath,
//        @QueryParam("firstResult")
//        @DefaultValue("0")
//        final int firstResult,
//        @QueryParam("maxResults")
//        @DefaultValue("20")
//        final int maxResults,
//        @QueryParam("filterTerm")
//        @DefaultValue("")
//        final String filterTerm
//    ) {
//        final ContentSection section = retrieveContentSection(sectionIdentifier);
//
//        if (permissionChecker.isPermitted(
//            ItemPrivileges.EDIT, retrieveDocument(section, documentPath)
//        )) {
//            final Folder folder;
//            if (folderPath.isEmpty()) {
//                folder = section.getRootAssetsFolder();
//            } else {
//                final Optional<Folder> folderResult = folderRepo.findByPath(
//                    section, folderPath, FolderType.ASSETS_FOLDER
//                );
//                if (folderResult.isPresent()) {
//                    folder = folderResult.get();
//                } else {
//                    return Collections.emptyList();
//                }
//            }
//            return folderRepo
//                .getAssetFolderEntries(
//                    folder, firstResult, maxResults, filterTerm
//                )
//                .stream()
//                .map(entry -> buildAssetFolderRowModel(section, entry))
//                .collect(Collectors.toList());
//        } else {
//            throw new ForbiddenException();
//        }
//    }
//
//    /**
//     * Show all assets of a content section filtered by their name.
//     *
//     * @param sectionIdentifier
//     * @param documentPath
//     * @param firstResult       The index of the first result to show.
//     * @param maxResults        The maximum number of results to show.
//     * @param searchTerm        An optional search term applied to the names of
//     *                          the assets.
//     *
//     * @return A list of matching assets as JSON.
//     */
//    @GET
//    @Path("/search-assets")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Transactional(Transactional.TxType.REQUIRED)
//    public List<AssetFolderRowModel> findAssets(
//        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
//        final String sectionIdentifier,
//        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
//        final String documentPath,
//        @QueryParam("firstResult")
//        @DefaultValue("0")
//        final int firstResult,
//        @QueryParam("maxResults")
//        @DefaultValue("20")
//        final int maxResults,
//        @QueryParam("searchTerm")
//        @DefaultValue("")
//        final String searchTerm
//    ) {
//        final ContentSection section = retrieveContentSection(sectionIdentifier);
//
//        if (permissionChecker.isPermitted(
//            ItemPrivileges.EDIT, retrieveDocument(section, documentPath)
//        )) {
//            return assetRepo.findByTitleAndContentSection(searchTerm, section)
//                .stream()
//                .map(asset -> buildAssetFolderRowModel(section, asset))
//                .collect(Collectors.toList());
//        } else {
//            throw new ForbiddenException();
//        }
//    }
//
//    private ContentSection retrieveContentSection(
//        final String sectionIdentifier
//    ) {
//        return sectionsUi.findContentSection(
//            sectionIdentifier
//        ).orElseThrow(
//            () -> new NotFoundException(
//                String.format(
//                    "No content section identified by %s available.",
//                    sectionIdentifier
//                )
//            )
//        );
//    }
//
//    /**
//     * Gets the document folder tree of the current content section as JSON
//     * data.
//     *
//     * @param sectionIdentifier
//     * @param documentPath
//     *
//     * @return The document folder tree of the current content section as JSON
//     *         data.
//     */
//    @GET
//    @Path("/document-folders")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Transactional(Transactional.TxType.REQUIRED)
//    public List<DocumentFolderTreeNode> getDocumentFolderTree(
//        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
//        final String sectionIdentifier,
//        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
//        final String documentPath
//    ) {
//        final ContentSection section = retrieveContentSection(sectionIdentifier);
//        final ContentItem document = retrieveDocument(section, documentPath);
//
//        if (permissionChecker.isPermitted(
//            ItemPrivileges.EDIT, document
//        )) {
//            return documentFolderTree.buildFolderTree(
//                section, section.getRootDocumentsFolder()
//            );
//        } else {
//            throw new ForbiddenException();
//        }
//    }
//
//    /**
//     * Gets the documents in the folder as JSON data.
//     *
//     * @param sectionIdentifier
//     * @param documentPath
//     * @param folderPath        The path of the folder.
//     * @param firstResult       The index of the firset result to show.
//     * @param maxResults        The maximum number of results to show.
//     * @param filterTerm        An optional filter term for filtering the
//     *                          documents in the folder by their name.
//     *
//     * @return A list of the documents in the folder as JSON data.
//     */
//    @GET
//    @Path("/document-folders/{folderPath}/documents")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Transactional(Transactional.TxType.REQUIRED)
//    public List<DocumentFolderRowModel> getDocumentsInFolder(
//        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
//        final String sectionIdentifier,
//        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
//        final String documentPath,
//        @PathParam("folderPath")
//        final String folderPath,
//        @QueryParam("firstResult")
//        @DefaultValue("0")
//        final int firstResult,
//        @QueryParam("maxResults")
//        @DefaultValue("20")
//        final int maxResults,
//        @QueryParam("filterTerm")
//        @DefaultValue("")
//        final String filterTerm
//    ) {
//        final ContentSection section = retrieveContentSection(sectionIdentifier);
//        final ContentItem document = retrieveDocument(section, documentPath);
//
//        if (permissionChecker.isPermitted(
//            ItemPrivileges.EDIT, document
//        )) {
//            final Folder folder;
//            if (folderPath.isEmpty()) {
//                folder = section.getRootDocumentsFolder();
//            } else {
//                final Optional<Folder> folderResult = folderRepo.findByPath(
//                    section, folderPath, FolderType.ASSETS_FOLDER
//                );
//                if (folderResult.isPresent()) {
//                    folder = folderResult.get();
//                } else {
//                    return Collections.emptyList();
//                }
//            }
//
//            return folderRepo
//                .getDocumentFolderEntries(
//                    folder,
//                    firstResult,
//                    maxResults,
//                    filterTerm
//                )
//                .stream()
//                .map(entry -> buildDocumentFolderRowModel(section, entry))
//                .collect(Collectors.toList());
//        } else {
//            throw new ForbiddenException();
//        }
//    }
//
//    /**
//     * Show all documents of a content section filtered by their name.
//     *
//     * @param sectionIdentifier
//     * @param documentPath
//     * @param firstResult       The index of the first result to show.
//     * @param maxResults        The maximum number of results to show.
//     * @param searchTerm        An optional search term applied to the names of
//     *                          the docuemnts.
//     *
//     * @return A list of matching documents/content items as JSON.
//     */
//    @GET
//    @Path("/search-documents")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Transactional(Transactional.TxType.REQUIRED)
//    public List<DocumentFolderRowModel> findDocuments(
//        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
//        final String sectionIdentifier,
//        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
//        final String documentPath,
//        @QueryParam("firstResult")
//        @DefaultValue("0")
//        final int firstResult,
//        @QueryParam("maxResults")
//        @DefaultValue("20")
//        final int maxResults,
//        @QueryParam("searchTerm")
//        @DefaultValue("")
//        final String searchTerm
//    ) {
//        final ContentSection section = retrieveContentSection(sectionIdentifier);
//        final ContentItem document = retrieveDocument(section, documentPath);
//
//        if (permissionChecker.isPermitted(
//            ItemPrivileges.EDIT, document
//        )) {
//            return itemRepo.findByNameAndContentSection(searchTerm, section)
//                .stream()
//                .map(asset -> buildDocumentFolderRowModel(section, asset))
//                .collect(Collectors.toList());
//        } else {
//            throw new ForbiddenException();
//        }
//    }
//
//    private ContentItem retrieveDocument(
//        final ContentSection section, final String documentPath
//    ) {
//        return itemRepo
//            .findByPath(section, documentPath)
//            .orElseThrow(
//                () -> new NotFoundException(
//                    String.format(
//                        "No document with path %s found in content section %s.",
//                        documentPath,
//                        section.getLabel()
//                    )
//                )
//            );
//    }
//
//    /**
//     * Build the model for a row in the asset folder listing.
//     *
//     * @param section The content section.
//     * @param entry   The {@link AssetFolderEntry} from which the model is
//     *                build.
//     *
//     * @return The {@link AssetFolderRowModel} for the provided {@code entry}.
//     */
//    private AssetFolderRowModel buildAssetFolderRowModel(
//        final ContentSection section, final AssetFolderEntry entry
//    ) {
//        Objects.requireNonNull(section);
//        Objects.requireNonNull(entry);
//
//        final AssetFolderRowModel row = new AssetFolderRowModel();
//        if (entry.isFolder()) {
//            final Folder folder = folderRepo
//                .findById(entry.getEntryId())
//                .get();
//            row.setDeletable(false);
//            row.setFolder(true);
//            row.setFolderPath(
//                folderManager
//                    .getFolderPath(folder)
//                    .substring(
//                        folderManager
//                            .getFolderPath(section.getRootAssetsFolder())
//                            .length()
//                    )
//            );
//            row.setName(entry.getDisplayName());
//            row.setTitle(
//                globalizationHelper.getValueFromLocalizedString(
//                    folder.getTitle()
//                )
//            );
//            row.setType(
//                globalizationHelper.getLocalizedTextsUtil(
//                    "org.librecms.CmsAdminMessages"
//                ).getText("contentsection.assetfolder.types.folder")
//            );
//            row.setPermissions(
//                assetPermissions.buildAssetPermissionsModel(folder)
//            );
//        } else {
//            final Asset asset = assetRepo
//                .findById(entry.getEntryId())
//                .get();
//            row.setDeletable(!assetManager.isAssetInUse(asset));
//            row.setFolder(false);
//            row.setName(entry.getDisplayName());
//            row.setNoneCmsObject(false);
//            row.setTitle(
//                globalizationHelper.getValueFromLocalizedString(
//                    asset.getTitle()
//                )
//            );
//            row.setType(asset.getClass().getName());
//            row.setPermissions(
//                assetPermissions.buildAssetPermissionsModel(asset)
//            );
//        }
//
//        return row;
//    }
//
//    /**
//     * Build the model for a row in the asset folder listing.
//     *
//     * @param section The content section.
//     * @param asset   The {@link Asset} from which the model is build.
//     *
//     * @return The {@link AssetFolderRowModel} for the provided {@code asset}.
//     */
//    private AssetFolderRowModel buildAssetFolderRowModel(
//        final ContentSection section, final Asset asset
//    ) {
//        Objects.requireNonNull(section);
//        Objects.requireNonNull(asset);
//
//        final AssetFolderRowModel row = new AssetFolderRowModel();
//        row.setDeletable(false);
//        row.setFolder(false);
//        row.setName(asset.getDisplayName());
//        row.setNoneCmsObject(false);
//        row.setTitle(
//            globalizationHelper.getValueFromLocalizedString(
//                asset.getTitle()
//            )
//        );
//        row.setType(asset.getClass().getName());
//        row.setPermissions(
//            assetPermissions.buildAssetPermissionsModel(asset)
//        );
//
//        return row;
//    }
//
//    /**
//     * Build the model for a row in the document folder listing.
//     *
//     * @param section The content section.
//     * @param entry   The {@link DocumentFolderEntry} from which the model is
//     *                build.
//     *
//     * @return The {@link DocumentFolderRowModel} for the provided
//     *         {@code entry}.
//     */
//    private DocumentFolderRowModel buildDocumentFolderRowModel(
//        final ContentSection section, final DocumentFolderEntry entry
//    ) {
//        Objects.requireNonNull(section);
//        Objects.requireNonNull(entry);
//
//        final DocumentFolderRowModel row = new DocumentFolderRowModel();
//        if (entry.isFolder()) {
//            final Folder folder = folderRepo
//                .findById(entry.getEntryId())
//                .get();
//            row.setCreated("");
//            row.setDeletable(
//                folderManager
//                    .folderIsDeletable(folder)
//                    == FolderManager.FolderIsDeletable.YES
//            );
//            row.setFolder(true);
//            row.setFolderPath(
//                folderManager
//                    .getFolderPath(folder)
//                    .substring(
//                        folderManager
//                            .getFolderPath(section.getRootDocumentsFolder())
//                            .length()
//                    )
//            );
//            row.setLanguages(Collections.emptySortedSet());
//            row.setLastEditPublished(false);
//            row.setLastEdited("");
//            row.setName(entry.getDisplayName());
//            row.setTitle(
//                globalizationHelper.getValueFromLocalizedString(
//                    folder.getTitle()
//                )
//            );
//            row.setType(
//                globalizationHelper.getLocalizedTextsUtil(
//                    "org.librecms.CmsAdminMessages"
//                ).getText("contentsection.documentfolder.types.folder")
//            );
//            row.setPermissions(
//                documentPermissions.buildDocumentPermissionsModel(folder)
//            );
//        } else {
//            final ContentItem contentItem = itemRepo
//                .findById(entry.getEntryId())
//                .get();
//            row.setCreated(
//                DateTimeFormatter.ISO_DATE.format(
//                    LocalDate.ofInstant(
//                        contentItem.getCreationDate().toInstant(),
//                        ZoneId.systemDefault()
//                    )
//                )
//            );
//            row.setDeletable(!itemManager.isLive(contentItem));
//            row.setFolder(false);
//            row.setFolderPath(itemManager.getItemPath(contentItem));
//            row.setLanguages(
//                new TreeSet<>(
//                    itemL10NManager
//                        .availableLanguages(contentItem)
//                        .stream()
//                        .map(Locale::toString)
//                        .collect(Collectors.toSet())
//                )
//            );
//            if (itemManager.isLive(contentItem)) {
//                final LocalDate draftLastModified = LocalDate.ofInstant(
//                    contentItem.getLastModified().toInstant(),
//                    ZoneId.systemDefault()
//                );
//                final LocalDate liveLastModified = LocalDate.ofInstant(
//                    itemManager
//                        .getLiveVersion(contentItem, contentItem.getClass())
//                        .map(ContentItem::getLastModified)
//                        .map(Date::toInstant)
//                        .get(),
//                    ZoneId.systemDefault()
//                );
//                row.setLastEditPublished(
//                    liveLastModified.isBefore(draftLastModified)
//                );
//            } else {
//                row.setLastEditPublished(false);
//            }
//
//            row.setLastEdited(
//                DateTimeFormatter.ISO_DATE.format(
//                    LocalDate.ofInstant(
//                        contentItem.getLastModified().toInstant(),
//                        ZoneId.systemDefault()
//                    )
//                )
//            );
//            row.setName(entry.getDisplayName());
//            row.setNoneCmsObject(false);
//            row.setTitle(
//                globalizationHelper.getValueFromLocalizedString(
//                    contentItem.getTitle()
//                )
//            );
//            row.setType(
//                contentTypeRepo
//                    .findByContentSectionAndClass(
//                        section, contentItem.getClass()
//                    )
//                    .map(ContentType::getLabel)
//                    .map(
//                        label -> globalizationHelper
//                            .getValueFromLocalizedString(
//                                label
//                            )
//                    ).orElse("?")
//            );
//            row.setPermissions(
//                documentPermissions.buildDocumentPermissionsModel(
//                    contentItem
//                )
//            );
//        }
//
//        return row;
//    }
//
//    /**
//     * Build the model for a row in the document folder listing.
//     *
//     * @param section     The content section.
//     * @param contentItem The {@link Contentitem} from which the model is build.
//     *
//     * @return The {@link DocumentFolderRowModel} for the provided
//     *         {@code contentItem}.
//     */
//    private DocumentFolderRowModel buildDocumentFolderRowModel(
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
//        row.setFolder(false);
//        row.setFolderPath(itemManager.getItemPath(contentItem));
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
//                .findByContentSectionAndClass(
//                    section, contentItem.getClass()
//                )
//                .map(ContentType::getLabel)
//                .map(
//                    label -> globalizationHelper
//                        .getValueFromLocalizedString(
//                            label
//                        )
//                ).orElse("?")
//        );
//        row.setPermissions(
//            documentPermissions.buildDocumentPermissionsModel(
//                contentItem
//            )
//        );
//
//        return row;
//    }
}
