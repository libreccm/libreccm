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
import org.librecms.contentsection.AttachmentListL10NManager;
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/")
@AuthoringStepPathFragment(RelatedInfoStep.PATH_FRAGMENT)
@Named("CmsRelatedInfoStep")
public class RelatedInfoStep implements MvcAuthoringStep {

    static final String PATH_FRAGMENT = "relatedinfo";

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
    private AssetTypesManager assetTypesManager;

    @Inject
    private AttachmentListDetailsModel listDetailsModel;

    @Inject
    private AttachmentListManager listManager;

    @Inject
    private AttachmentListL10NManager listL10NManager;

    @Inject
    private AttachmentListRepository listRepo;

    @Inject
    private DocumentFolderTree documentFolderTree;

    @Inject
    private DocumentPermissions documentPermissions;

    @Inject
    private FolderManager folderManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private InternalLinkDetailsModel internalLinkDetailsModel;

    @Inject
    private ContentItemL10NManager itemL10NManager;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentTypeRepository contentTypeRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private ItemAttachmentManager attachmentManager;

    @Inject
    private Models models;

    private ContentItem document;

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

    @Transactional(Transactional.TxType.REQUIRED)
    public List<AttachmentListDto> getAttachmentLists() {
        return document
            .getAttachments()
            .stream()
            .filter(list -> !list.getName().startsWith("."))
            .map(this::buildAttachmentListDto)
            .collect(Collectors.toList());
    }

    @GET
    @Path("/asset-folders")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<AssetFolderTreeNode> getAssetFolderTree() {
        return assetFolderTree.buildFolderTree(
            section, section.getRootAssetsFolder()
        );
    }

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

    @GET
    @Path("/document-folders")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<DocumentFolderTreeNode> getDocumentFolderTree() {
        return documentFolderTree.buildFolderTree(
            section, section.getRootDocumentsFolder()
        );
    }

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

    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentListTitle(
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

    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/description/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentListDescription(
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
