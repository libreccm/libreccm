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
package org.librecms.ui.contentsections.documents;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;
import org.librecms.assets.RelatedLink;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetFolderEntry;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.AttachmentListManager;
import org.librecms.contentsection.AttachmentListRepository;
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
import org.librecms.contentsection.ItemAttachment;
import org.librecms.contentsection.ItemAttachmentManager;
import org.librecms.ui.contentsections.AssetFolderRowModel;
import org.librecms.ui.contentsections.AssetFolderTree;
import org.librecms.ui.contentsections.AssetFolderTreeNode;
import org.librecms.ui.contentsections.AssetPermissionsModel;
import org.librecms.ui.contentsections.AssetPermissionsModelProvider;
import org.librecms.ui.contentsections.DocumentFolderRowModel;
import org.librecms.ui.contentsections.DocumentFolderTree;
import org.librecms.ui.contentsections.DocumentFolderTreeNode;
import org.librecms.ui.contentsections.DocumentPermissions;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import javax.inject.Named;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Authoring step for managing the {@link AttachmentList} and
 * {@link ItemAttachment}s assigned to a {@link ContentItem}.
 *
 * This class acts as controller for several views as well as named bean that
 * provides data for these views. Some of the views of the step use JavaScript
 * enhanced widgets. Therefore, some of the paths/endpoints provided by this
 * class return JSON data.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/")
@AuthoringStepPathFragment(RelatedInfoStep.PATH_FRAGMENT)
@Named("CmsRelatedInfoStep")
public class RelatedInfoStep implements MvcAuthoringStep {

    /**
     * The path fragment of the step.
     */
    static final String PATH_FRAGMENT = "relatedinfo";

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

    /**
     * {@link AssetManager} instance of managing {@link Asset}s.
     */
    @Inject
    private AssetManager assetManager;

    /**
     * Used to retrieve and save {@link Asset}s.
     */
    @Inject
    private AssetRepository assetRepo;

    /**
     * Provides access to the available asset types.
     */
    @Inject
    private AssetTypesManager assetTypesManager;

    /**
     * Model for the details view of an {@link AttachmentList}.
     */
    @Inject
    private AttachmentListDetailsModel listDetailsModel;

    /**
     * Manager for {@link AttachmentList}s.
     */
    @Inject
    private AttachmentListManager listManager;

    /**
     * Used to retrieve and save {@link AttachmentList}s.
     */
    @Inject
    private AttachmentListRepository listRepo;

    /**
     * The document folder tree of the current content section.
     */
    @Inject
    private DocumentFolderTree documentFolderTree;

    /**
     * Used to check permissions of the current content item.
     */
    @Inject
    private DocumentPermissions documentPermissions;

    /**
     * Used to retrieve the path of folders.
     */
    @Inject
    private FolderManager folderManager;

    /**
     * Used to retrieve folders.
     */
    @Inject
    private FolderRepository folderRepo;

    /**
     * Model for the details view of an internal {@link RelatedLink}.
     */
    @Inject
    private InternalLinkDetailsModel internalLinkDetailsModel;

    /**
     * Manages localization of {@link ContentItem}s.
     */
    @Inject
    private ContentItemL10NManager itemL10NManager;

    /**
     * Manages {@link ContentItem}s.
     */
    @Inject
    private ContentItemManager itemManager;

    /**
     * Used to retrieve the current content item.
     */
    @Inject
    private ContentItemRepository itemRepo;

    /**
     * Used to retrieve content types.
     */
    @Inject
    private ContentTypeRepository contentTypeRepo;

    /**
     * Used for globalization stuff.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to parse identifiers.
     */
    @Inject
    private IdentifierParser identifierParser;

    /**
     * Manages {@link ItemAttachment}.
     */
    @Inject
    private ItemAttachmentManager attachmentManager;

    /**
     * Used to provide data for the views without a named bean.
     */
    @Inject
    private Models models;

    /**
     * The current document/content item.
     */
    private ContentItem document;

    /**
     * The current content section.
     */
    private ContentSection section;

    @Override
    public Class<? extends ContentItem> supportedDocumentType() {
        return ContentItem.class;
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.relatedinfo.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.relatedinfo.description");
    }

    @Override
    public String getBundle() {
        return DefaultAuthoringStepConstants.BUNDLE;
    }

    @Override
    public ContentSection getContentSection() {
        return section;
    }

    @Override
    public void setContentSection(final ContentSection section) {
        this.section = section;
    }

    @Override
    public String getContentSectionLabel() {
        return section.getLabel();
    }

    @Override
    public String getContentSectionTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(section.getTitle());
    }

    @Override
    public ContentItem getContentItem() {
        return document;
    }

    @Override
    public void setContentItem(final ContentItem document) {
        this.document = document;
    }

    @Override
    public String getContentItemPath() {
        return itemManager.getItemPath(document);
    }

    @Override
    public String getContentItemTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(document.getTitle());
    }

    @Override
    public String showStep() {
        return "org/librecms/ui/documents/relatedinfo.xhtml";
    }

    /**
     * Gets the {@link AttachmentList}s of the current content item and converts
     * them to {@link AttachmentListDto}s to make data about the lists available
     * in the views.
     *
     * @return A list of the {@link AttachmentList} of the current content item.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<AttachmentListDto> getAttachmentLists() {
        return document
            .getAttachments()
            .stream()
            .filter(list -> !list.getName().startsWith("."))
            .map(this::buildAttachmentListDto)
            .collect(Collectors.toList());
    }

    /**
     * Gets the asset folder tree of the current content section as JSON data.
     *
     * @return The assets folder tree of the current content section as JSON
     *         data.
     */
    @GET
    @Path("/asset-folders")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<AssetFolderTreeNode> getAssetFolderTree() {
        return assetFolderTree.buildFolderTree(
            section, section.getRootAssetsFolder()
        );
    }

    /**
     * Gets the assets in the folder as JSON data.
     *
     * @param folderPath  The path of the folder.
     * @param firstResult The index of the firset result to show.
     * @param maxResults  The maximum number of results to show.
     * @param filterTerm  An optional filter term for filtering the assets in
     *                    the folder by their name.
     *
     * @return A list of the assets in the folder as JSON data.
     */
    @GET
    @Path("/asset-folders/{folderPath}/assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<AssetFolderRowModel> getAssetsInFolder(
        @PathParam("folderPath") final String folderPath,
        @QueryParam("firstResult") @DefaultValue("0") final int firstResult,
        @QueryParam("maxResults") @DefaultValue("20") final int maxResults,
        @QueryParam("filterTerm") @DefaultValue("") final String filterTerm
    ) {
        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootAssetsFolder();
        } else {
            final Optional<Folder> folderResult = folderRepo.findByPath(
                section, folderPath, FolderType.ASSETS_FOLDER
            );
            if (folderResult.isPresent()) {
                folder = folderResult.get();
            } else {
                return Collections.emptyList();
            }
        }
        return folderRepo
            .getAssetFolderEntries(
                folder, firstResult, maxResults, filterTerm
            )
            .stream()
            .map(entry -> buildAssetFolderRowModel(section, entry))
            .collect(Collectors.toList());
    }

    /**
     * Show all assets of a content section filtered by their name.
     *
     * @param firstResult The index of the first result to show.
     * @param maxResults  The maximum number of results to show.
     * @param searchTerm  An optional search term applied to the names of the
     *                    assets.
     *
     * @return A list of matching assets as JSON.
     */
    @GET
    @Path("/search-assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<AssetFolderRowModel> findAssets(
        @QueryParam("firstResult") @DefaultValue("0") final int firstResult,
        @QueryParam("maxResults") @DefaultValue("20") final int maxResults,
        @QueryParam("searchTerm") @DefaultValue("") final String searchTerm
    ) {
        return assetRepo.findByTitleAndContentSection(searchTerm, section)
            .stream()
            .map(asset -> buildAssetFolderRowModel(section, asset))
            .collect(Collectors.toList());

    }

    /**
     * Gets the document folder tree of the current content section as JSON
     * data.
     *
     * @return The document folder tree of the current content section as JSON
     *         data.
     */
    @GET
    @Path("/document-folders")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<DocumentFolderTreeNode> getDocumentFolderTree() {
        return documentFolderTree.buildFolderTree(
            section, section.getRootDocumentsFolder()
        );
    }

    /**
     * Gets the documents in the folder as JSON data.
     *
     * @param folderPath  The path of the folder.
     * @param firstResult The index of the firset result to show.
     * @param maxResults  The maximum number of results to show.
     * @param filterTerm  An optional filter term for filtering the documents in
     *                    the folder by their name.
     *
     * @return A list of the documents in the folder as JSON data.
     */
    @GET
    @Path("/document-folders/{folderPath}/documents")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<DocumentFolderRowModel> getDocumentsInFolder(
        @PathParam("folderPath") final String folderPath,
        @QueryParam("firstResult") @DefaultValue("0") final int firstResult,
        @QueryParam("maxResults") @DefaultValue("20") final int maxResults,
        @QueryParam("filterTerm") @DefaultValue("") final String filterTerm
    ) {
        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootDocumentsFolder();
        } else {
            final Optional<Folder> folderResult = folderRepo.findByPath(
                section, folderPath, FolderType.ASSETS_FOLDER
            );
            if (folderResult.isPresent()) {
                folder = folderResult.get();
            } else {
                return Collections.emptyList();
            }
        }

        return folderRepo
            .getDocumentFolderEntries(
                folder,
                firstResult,
                maxResults,
                filterTerm
            )
            .stream()
            .map(entry -> buildDocumentFolderRowModel(section, entry))
            .collect(Collectors.toList());
    }

    /**
     * Show all documents of a content section filtered by their name.
     *
     * @param firstResult The index of the first result to show.
     * @param maxResults  The maximum number of results to show.
     * @param searchTerm  An optional search term applied to the names of the
     *                    docuemnts.
     *
     * @return A list of matching documents/content items as JSON.
     */
    @GET
    @Path("/search-documents")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<DocumentFolderRowModel> findDocuments(
        @QueryParam("firstResult") @DefaultValue("0") final int firstResult,
        @QueryParam("maxResults") @DefaultValue("20") final int maxResults,
        @QueryParam("searchTerm") @DefaultValue("") final String searchTerm
    ) {
        return itemRepo.findByNameAndContentSection(searchTerm, section)
            .stream()
            .map(asset -> buildDocumentFolderRowModel(section, asset))
            .collect(Collectors.toList());
    }

    /**
     * Adds a new attachment list.
     *
     * @param name        The name of the list.
     * @param title       The title of the list for the language returned by {@link GlobalizationHelper#getNegotiatedLocale()
     *                    } .
     * @param description The description of the list of the default locale {@link GlobalizationHelper#getNegotiatedLocale().
     *
     * @return A redirect to the list of attachment lists.
     */
    @POST
    @Path("/attachmentlists/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addAttachmentList(
        @FormParam("listName") final String name,
        @FormParam("listTitle") final String title,
        @FormParam("listDescription") final String description
    ) {
        final AttachmentList list = listManager.createAttachmentList(
            document, name
        );
        list.getTitle().addValue(
            globalizationHelper.getNegotiatedLocale(), title
        );
        list.getDescription().addValue(
            globalizationHelper.getNegotiatedLocale(), description
        );
        listRepo.save(list);
        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s/attachmentslists/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT,
            list.getName()
        );
    }

    /**
     * Shows the details of an attachment list.
     *
     * @param listIdentifierParam The identifier of the list.
     *
     * @return The template for the details view.
     */
    @GET
    @Path("/attachmentlists/{attachmentListIdentifier}/@details")
    @Transactional(Transactional.TxType.REQUIRED)
    public String showAttachmentListDetails(
        @PathParam("attachmentListIdentifier") final String listIdentifierParam
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }

        listDetailsModel.setAttachmentList(listResult.get());

        return "org/librecms/ui/documents/relatedinfo-attachmentlist-details.xhtml";
    }

    /**
     * Updates an attachment list.
     *
     * @param listIdentifierParam The identifier of the list to update.
     * @param name                The new name of the list.
     *
     * @return A redirect to the list of attachment lists.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/@update")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateAttachmentList(
        @PathParam("attachmentListIdentifier") final String listIdentifierParam,
        @FormParam("listName") final String name
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }

        final AttachmentList list = listResult.get();
        list.setName(name);
        listRepo.save(list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s/attachmentslists/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT,
            list.getName()
        );
    }

    /**
     * Removes an attachment list and all item attachment of the list.
     *
     * @param listIdentifierParam The identifier of the list to remove.
     * @param confirm             The value of the confirm parameter. Must
     *                            contain {@code true} (as string not as
     *                            boolean), otherwise this method does nothing.
     *
     * @return A redirect to the list of attachment lists.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/@remove")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentList(
        @PathParam("attachmentListIdentifier") final String listIdentifierParam,
        @FormParam("confirm") final String confirm
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }

        if ("true".equalsIgnoreCase(confirm)) {
            listManager.removeAttachmentList(listResult.get());
        }

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Adds a localized title to an attachment list.
     *
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the new title value.
     * @param value               The value of the new title value.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addAttachmentListTitle(
        @PathParam("attachmentListIdentifier") final String listIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }

        final AttachmentList list = listResult.get();
        list.getTitle().addValue(new Locale(localeParam), value);
        listRepo.save(list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s/attachmentslists/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT,
            list.getName()
        );
    }

    /**
     * Updates a localized title value of an attachment list.
     *
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the title value to update.
     * @param value               The new title value.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateAttachmentListTitle(
        @PathParam("attachmentListIdentifier") final String listIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }

        final AttachmentList list = listResult.get();
        list.getTitle().addValue(new Locale(localeParam), value);
        listRepo.save(list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s/attachmentslists/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT,
            list.getName()
        );
    }

    /**
     * Removes a localized title value of an attachment list.
     *
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the title value to remove.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentListTitle(
        @PathParam("attachmentListIdentifier") final String listIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }

        final AttachmentList list = listResult.get();
        list.getTitle().removeValue(new Locale(localeParam));
        listRepo.save(list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s/attachmentslists/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT,
            list.getName()
        );
    }

    /**
     * Adds a localized description to an attachment list.
     *
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the new description value.
     * @param value               The value of the new description value.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/description/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addAttachmentListDescription(
        @PathParam("attachmentListIdentifier") final String listIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }

        final AttachmentList list = listResult.get();
        list.getDescription().addValue(new Locale(localeParam), value);
        listRepo.save(list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s/attachmentslists/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT,
            list.getName()
        );
    }

    /**
     * Updates a localized description value of an attachment list.
     *
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the description value to update.
     * @param value               The new description value.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/description/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateAttachmentListDescription(
        @PathParam("attachmentListIdentifier") final String listIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }

        final AttachmentList list = listResult.get();
        list.getDescription().addValue(new Locale(localeParam), value);
        listRepo.save(list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s/attachmentslists/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT,
            list.getName()
        );
    }

    /**
     * Removes a localized description value of an attachment list.
     *
     * @param listIdentifierParam The identifier of the list.
     * @param localeParam         The locale of the description value to remove.
     *
     * @return A redirect to the details view of the attachment list.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/description/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentListDescription(
        @PathParam("attachmentListIdentifier") final String listIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }

        final AttachmentList list = listResult.get();
        list.getDescription().removeValue(new Locale(localeParam));
        listRepo.save(list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s/attachmentslists/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT,
            list.getName()
        );
    }

    /**
     * Create new attachment.
     *
     * @param listIdentifierParam The identifier of the list to which the
     *                            attachment is added.
     * @param assetUuid           The asset to use for the attachment.
     *
     * @return A redirect to the list of attachment lists and attachments.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/attachments")
    @Transactional(Transactional.TxType.REQUIRED)
    public String createAttachment(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @FormParam("assetUuid") final String assetUuid
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<Asset> assetResult = assetRepo.findByUuid(assetUuid);
        if (!assetResult.isPresent()) {
            models.put("section", section.getLabel());
            models.put("assetUuid", assetUuid);
            return "org/librecms/ui/documents/asset-not-found.xhtml";
        }

        final Asset asset = assetResult.get();

        attachmentManager.attachAsset(asset, list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Shows the form for creating a new internal link.
     *
     * @param listIdentifierParam The identifier of the list to which the
     *                            attachment is added.
     *
     * @return The template for the form for creating a new internal link.
     */
    @GET
    @Path("/attachmentlists/{attachmentListIdentifier}/internal-links/@create")
    @Transactional(Transactional.TxType.REQUIRED)
    public String createInternalLink(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();
        models.put("attachmentList", list.getName());

        return "org/librecms/ui/documents/relatedinfo-create-internallink.xhtml";
    }

    /**
     * Create a new internal link.
     *
     * @param listIdentifierParam The identifier of the list to which the
     *                            attachment is added.
     * @param targetItemUuid      The UUID of the target item of the internal
     *                            link.
     * @param title               The title of the new internal link for the
     *                            language return by {@link GlobalizationHelper#getNegotiatedLocale()
     *                            }.
     *
     * @return A redirect to the list of attachment lists and attachments.
     */
    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/internal-links/@create")
    @Transactional(Transactional.TxType.REQUIRED)
    public String createInternalLink(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @FormParam("targetItemUuid") final String targetItemUuid,
        @FormParam("title") final String title
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<ContentItem> itemResult = itemRepo.findByUuid(
            targetItemUuid
        );
        if (!itemResult.isPresent()) {
            models.put("targetItemUuid", targetItemUuid);
            return "org/librecms/ui/documents/target-item-not-found.xhtml";
        }

        final RelatedLink relatedLink = new RelatedLink();
        relatedLink.getTitle().addValue(
            globalizationHelper.getNegotiatedLocale(), title
        );
        relatedLink.setTargetItem(document);

        attachmentManager.attachAsset(relatedLink, list);
        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Show the details of an internal link.
     *
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the link belongs.
     * @param internalLinkUuid    The UUID of the link.
     *
     * @return The template for the details view of the link, or the template
     *         for the link not found message if the link iwth the provided UUID
     *         is found in the provided attachment list.
     */
    @GET
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/internal-links/{interalLinkUuid}/@details")
    @Transactional(Transactional.TxType.REQUIRED)
    public String showInternalLinkDetails(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("internalLinkUuid") final String internalLinkUuid
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<RelatedLink> linkResult = list
            .getAttachments()
            .stream()
            .map(ItemAttachment::getAsset)
            .filter(asset -> asset instanceof RelatedLink)
            .map(asset -> (RelatedLink) asset)
            .filter(link -> link.getUuid().equals(internalLinkUuid))
            .findAny();

        if (!linkResult.isPresent()) {
            models.put("contentItem", itemManager.getItemPath(document));
            models.put("listIdentifier", listIdentifierParam);
            models.put("internalLinkUuid", internalLinkUuid);
            return "org/librecms/ui/documents/internal-link-asset-not-found.xhtml";
        }

        final RelatedLink link = linkResult.get();
        internalLinkDetailsModel.setListIdentifier(list.getName());
        internalLinkDetailsModel.setInternalLink(link);

        return "org/librecms/ui/documents/relatedinfo-internallink-details.xhtml";
    }

    /**
     * Updates the target of an internal link.
     *
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the link belongs.
     * @param internalLinkUuid    The UUID of the link.
     * @param targetItemUuid      The UUID of the new target item.
     *
     * @return A redirect to the details view of the link.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/internal-links/{interalLinkUuid}"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateInternalLinkTarget(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("internalLinkUuid") final String internalLinkUuid,
        @FormParam("targetItemUuid") final String targetItemUuid
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<ContentItem> itemResult = itemRepo.findByUuid(
            targetItemUuid
        );
        if (!itemResult.isPresent()) {
            models.put("targetItemUuid", targetItemUuid);
            return "org/librecms/ui/documents/target-item-not-found.xhtml";
        }

        final Optional<RelatedLink> linkResult = list
            .getAttachments()
            .stream()
            .map(ItemAttachment::getAsset)
            .filter(asset -> asset instanceof RelatedLink)
            .map(asset -> (RelatedLink) asset)
            .filter(link -> link.getUuid().equals(internalLinkUuid))
            .findAny();

        if (!linkResult.isPresent()) {
            models.put("contentItem", itemManager.getItemPath(document));
            models.put("listIdentifier", listIdentifierParam);
            models.put("internalLinkUuid", internalLinkUuid);
            return "org/librecms/ui/documents/internal-link-asset-not-found.xhtml";
        }

        final RelatedLink link = linkResult.get();
        link.setTargetItem(itemResult.get());
        assetRepo.save(link);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Add a localized title value to an internal link.
     *
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the link belongs.
     * @param internalLinkUuid    The UUID of the link.
     * @param localeParam         The locale of the new title value.
     * @param value               The localized value.
     *
     * @return A redirect to the details view of the link.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/internal-links/{interalLinkUuid}/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addInternalLinkTitle(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("internalLinkUuid") final String internalLinkUuid,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<RelatedLink> linkResult = list
            .getAttachments()
            .stream()
            .map(ItemAttachment::getAsset)
            .filter(asset -> asset instanceof RelatedLink)
            .map(asset -> (RelatedLink) asset)
            .filter(link -> link.getUuid().equals(internalLinkUuid))
            .findAny();

        if (!linkResult.isPresent()) {
            models.put("contentItem", itemManager.getItemPath(document));
            models.put("listIdentifierParam", listIdentifierParam);
            models.put("internalLinkUuid", internalLinkUuid);
            return "org/librecms/ui/documents/internal-link-asset-not-found.xhtml";
        }

        final RelatedLink link = linkResult.get();
        final Locale locale = new Locale(localeParam);
        link.getTitle().addValue(locale, value);
        assetRepo.save(link);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Updates a localized title value of an internal link.
     *
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the link belongs.
     * @param internalLinkUuid    The UUID of the link.
     * @param localeParam         The locale of the title value to update.
     * @param value               The localized value.
     *
     * @return A redirect to the details view of the link.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/internal-links/{interalLinkUuid}/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateInternalLinkTitle(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("internalLinkUuid") final String internalLinkUuid,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<RelatedLink> linkResult = list
            .getAttachments()
            .stream()
            .map(ItemAttachment::getAsset)
            .filter(asset -> asset instanceof RelatedLink)
            .map(asset -> (RelatedLink) asset)
            .filter(link -> link.getUuid().equals(internalLinkUuid))
            .findAny();

        if (!linkResult.isPresent()) {
            models.put("contentItem", itemManager.getItemPath(document));
            models.put("listIdentifierParam", listIdentifierParam);
            models.put("internalLinkUuid", internalLinkUuid);
            return "org/librecms/ui/documents/internal-link-asset-not-found.xhtml";
        }

        final RelatedLink link = linkResult.get();
        final Locale locale = new Locale(localeParam);
        link.getTitle().addValue(locale, value);
        assetRepo.save(link);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Removes a localized title value from an internal link.
     *
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the link belongs.
     * @param internalLinkUuid    The UUID of the link.
     * @param localeParam         The locale of the value to remove.
     *
     * @return A redirect to the details view of the link.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/internal-links/{interalLinkUuid}/title/@remove/{locale}"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeInternalLinkTitle(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("internalLinkUuid") final String internalLinkUuid,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<RelatedLink> linkResult = list
            .getAttachments()
            .stream()
            .map(ItemAttachment::getAsset)
            .filter(asset -> asset instanceof RelatedLink)
            .map(asset -> (RelatedLink) asset)
            .filter(link -> link.getUuid().equals(internalLinkUuid))
            .findAny();

        if (!linkResult.isPresent()) {
            models.put("contentItem", itemManager.getItemPath(document));
            models.put("listIdentifierParam", listIdentifierParam);
            models.put("internalLinkUuid", internalLinkUuid);
            return "org/librecms/ui/documents/internal-link-asset-not-found.xhtml";
        }

        final RelatedLink link = linkResult.get();
        final Locale locale = new Locale(localeParam);
        link.getTitle().removeValue(locale);
        assetRepo.save(link);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Removes an attachment from an {@link AttachmentList}. The {@link Asset}
     * of the attachment will not be deleted unless it is a related link.
     *
     * @param listIdentifierParam The identifier of the {@link AttachmentList}
     *                            to which the attachment belongs.
     * @param attachmentUuid      The UUID of the attachment to remove.
     * @param confirm             The value of the {@code confirm} parameter. If
     *                            the value anything other than the string
     *                            {@code true} the method does nothing.
     *
     * @return
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/attachments/{attachmentUuid}/@remove")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachment(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("attachmentUuid") final String attachmentUuid,
        @FormParam("confirm") final String confirm
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<ItemAttachment<?>> result = list
            .getAttachments()
            .stream()
            .filter(attachment -> attachment.getUuid().equals(attachmentUuid))
            .findFirst();

        if (result.isPresent() && "true".equalsIgnoreCase(confirm)) {
            final Asset asset = result.get().getAsset();
            attachmentManager.unattachAsset(asset, list);
            if (asset instanceof RelatedLink
                    && ((RelatedLink) asset).getTargetItem() != null) {
                assetRepo.delete(asset);
            }
        }

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Move an attachment list one position up.
     *
     * @param listIdentifierParam The identifer of the list to move.
     *
     * @return A redirect to list of attachment lists.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/@moveUp")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveListUp(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        listManager.moveUp(list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Move an attachment list one position down.
     *
     * @param listIdentifierParam The identifer of the list to move.
     *
     * @return A redirect to list of attachment lists.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/@moveDown")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveListDown(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        listManager.moveDown(list);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Move an attachment one position up.
     *
     * @param listIdentifierParam The identifer to which the attachment belongs.
     * @param attachmentUuid      The UUID of the attachment ot move.
     *
     * @return A redirect to list of attachment lists and attachments.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/attachments/{attachmentUuid}/@moveUp")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveAttachmentUp(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("attachmentUuid") final String attachmentUuid
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<ItemAttachment<?>> result = list
            .getAttachments()
            .stream()
            .filter(attachment -> attachment.getUuid().equals(attachmentUuid))
            .findFirst();

        if (result.isPresent()) {
            final ItemAttachment<?> attachment = result.get();
            final Asset asset = attachment.getAsset();
            attachmentManager.moveUp(asset, list);
        }

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Move an attachment one position down.
     *
     * @param listIdentifierParam The identifer to which the attachment belongs.
     * @param attachmentUuid      The UUID of the attachment ot move.
     *
     * @return A redirect to list of attachment lists and attachements.
     */
    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/attachments/{attachmentUuid}/@moveDown")
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveAttachmentDown(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
        @PathParam("attachmentUuid") final String attachmentUuid
    ) {
        final Optional<AttachmentList> listResult = findAttachmentList(
            listIdentifierParam
        );
        if (!listResult.isPresent()) {
            return showAttachmentListNotFound(listIdentifierParam);
        }
        final AttachmentList list = listResult.get();

        final Optional<ItemAttachment<?>> result = list
            .getAttachments()
            .stream()
            .filter(attachment -> attachment.getUuid().equals(attachmentUuid))
            .findFirst();

        if (result.isPresent()) {
            final ItemAttachment<?> attachment = result.get();
            final Asset asset = attachment.getAsset();
            attachmentManager.moveDown(asset, list);
        }

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * A helper function to find an attachment list.
     *
     * @param attachmentListIdentifier The idenfifier of the attachment list.
     *
     * @return An {@link Optional} with the attachment list or an empty optional
     *         if the current content item has no list with the provided
     *         identifier.
     */
    private Optional<AttachmentList> findAttachmentList(
        final String attachmentListIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            attachmentListIdentifier
        );
        final Optional<AttachmentList> listResult;
        switch (identifier.getType()) {
            case ID:
                listResult = listRepo
                    .findForItemAndId(
                        document, Long.parseLong(identifier.getIdentifier())
                    );
                break;
            case UUID:
                listResult = listRepo
                    .findForItemAndUuid(
                        document, identifier.getIdentifier()
                    );
                break;
            default:
                listResult = listRepo
                    .findForItemAndName(
                        document, identifier.getIdentifier()
                    );
                break;
        }

        return listResult;
    }

    /**
     * Show the "attachment list not found" error page.
     *
     * @param listIdentifier The identifier of the list that was not found.
     *
     * @return The template for the "attachment list not found" page.
     */
    private String showAttachmentListNotFound(final String listIdentifier) {
        models.put("contentItem", itemManager.getItemPath(document));
        models.put("listIdentifier", listIdentifier);
        return "org/librecms/ui/documents/attachmentlist-not-found.xhtml";
    }

    private AttachmentListDto buildAttachmentListDto(
        final AttachmentList attachmentList
    ) {
        final AttachmentListDto dto = new AttachmentListDto();
        dto.setAttachments(
            attachmentList
                .getAttachments()
                .stream()
                .map(this::buildItemAttachmentDto)
                .collect(Collectors.toList())
        );
        dto.setDescription(
            globalizationHelper
                .getValueFromLocalizedString(
                    attachmentList.getDescription()
                )
        );
        dto.setListId(attachmentList.getListId());
        dto.setName(attachmentList.getName());
        dto.setOrder(attachmentList.getOrder());
        dto.setTitle(
            globalizationHelper
                .getValueFromLocalizedString(
                    attachmentList.getTitle()
                )
        );
        dto.setUuid(attachmentList.getUuid());
        return dto;
    }

    /**
     * Helper function for building a {@link ItemAttachmentDto} for an
     * {@link ItemAttachment}.
     *
     * @param itemAttachment The {@link ItemAttachment} from which the
     *                       {@link ItemAttachmentDto} is build.
     *
     * @return The {@link ItemAttachmentDto}.
     */
    private ItemAttachmentDto buildItemAttachmentDto(
        final ItemAttachment<?> itemAttachment
    ) {
        final ItemAttachmentDto dto = new ItemAttachmentDto();
        final AssetTypeInfo assetTypeInfo = assetTypesManager
            .getAssetTypeInfo(itemAttachment.getAsset().getClass());
        dto.setAssetType(
            globalizationHelper
                .getLocalizedTextsUtil(assetTypeInfo.getLabelBundle())
                .getText(assetTypeInfo.getLabelKey())
        );
        dto.setAttachmentId(itemAttachment.getAttachmentId());
        dto.setInternalLink(
            itemAttachment.getAsset() instanceof RelatedLink
                && ((RelatedLink) itemAttachment.getAsset()).getTargetItem()
                       != null
        );
        dto.setSortKey(itemAttachment.getSortKey());
        dto.setTitle(
            globalizationHelper
                .getValueFromLocalizedString(
                    itemAttachment.getAsset().getTitle()
                )
        );
        dto.setUuid(itemAttachment.getUuid());
        return dto;
    }

    /**
     * Build the model for a row in the asset folder listing.
     *
     * @param section The content section.
     * @param entry   The {@link AssetFolderEntry} from which the model is
     *                build.
     *
     * @return The {@link AssetFolderRowModel} for the provided {@code entry}.
     */
    private AssetFolderRowModel buildAssetFolderRowModel(
        final ContentSection section, final AssetFolderEntry entry
    ) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(entry);

        final AssetFolderRowModel row = new AssetFolderRowModel();
        if (entry.isFolder()) {
            final Folder folder = folderRepo
                .findById(entry.getEntryId())
                .get();
            row.setDeletable(false);
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

    /**
     * Build the model for a row in the asset folder listing.
     *
     * @param section The content section.
     * @param asset   The {@link Asset} from which the model is build.
     *
     * @return The {@link AssetFolderRowModel} for the provided {@code asset}.
     */
    private AssetFolderRowModel buildAssetFolderRowModel(
        final ContentSection section, final Asset asset
    ) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(asset);

        final AssetFolderRowModel row = new AssetFolderRowModel();
        row.setDeletable(false);
        row.setFolder(false);
        row.setName(asset.getDisplayName());
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

        return row;
    }

    /**
     * Build the model for a row in the document folder listing.
     *
     * @param section The content section.
     * @param entry   The {@link DocumentFolderEntry} from which the model is
     *                build.
     *
     * @return The {@link DocumentFolderRowModel} for the provided
     *         {@code entry}.
     */
    private DocumentFolderRowModel buildDocumentFolderRowModel(
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
                documentPermissions.buildDocumentPermissionsModel(
                    contentItem
                )
            );
        }

        return row;
    }

    /**
     * Build the model for a row in the document folder listing.
     *
     * @param section     The content section.
     * @param contentItem The {@link Contentitem} from which the model is build.
     *
     * @return The {@link DocumentFolderRowModel} for the provided
     *         {@code contentItem}.
     */
    private DocumentFolderRowModel buildDocumentFolderRowModel(
        final ContentSection section, final ContentItem contentItem
    ) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(contentItem);

        final DocumentFolderRowModel row = new DocumentFolderRowModel();
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
        row.setName(contentItem.getDisplayName());
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
            documentPermissions.buildDocumentPermissionsModel(
                contentItem
            )
        );

        return row;
    }

}
