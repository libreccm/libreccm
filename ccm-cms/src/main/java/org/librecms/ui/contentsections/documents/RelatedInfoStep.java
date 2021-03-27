/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;
import org.librecms.assets.RelatedLink;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.AttachmentListL10NManager;
import org.librecms.contentsection.AttachmentListManager;
import org.librecms.contentsection.AttachmentListRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.contentsection.ItemAttachmentManager;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
    private AssetRepository assetRepo;

    @Inject
    private AssetTypesManager assetTypesManager;

    @Inject
    private AttachmentListManager listManager;

    @Inject
    private AttachmentListL10NManager listL10NManager;

    @Inject
    private AttachmentListRepository listRepo;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

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

    @Transactional
    public List<AttachmentListDto> getAttachmentLists() {
        return document
            .getAttachments()
            .stream()
            .filter(list -> !list.getName().startsWith("."))
            .map(this::buildAttachmentListDto)
            .collect(Collectors.toList());
    }

    @POST
    @Path("/attachmentlists/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addAttachmentList(
        @FormParam("listName") final String name,
        @FormParam("listTitle") final String title
    ) {
        final AttachmentList list = listManager.createAttachmentList(
            document, name
        );
        list.getTitle().addValue(
            globalizationHelper.getNegotiatedLocale(), title
        );
        listRepo.save(list);
        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/@remove")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeAttachmentList(
        @PathParam("attachmentListIdentifier")
        final String listIdentifierParam,
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

    @POST
    @Path("/attachmentlists/{attachmentListIdentifier}/internal-links")
    @Transactional(Transactional.TxType.REQUIRED)
    public String createInteralLink(
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

    @POST
    @Path(
        "/attachmentlists/{attachmentListIdentifier}/internal-links/{interalLinkUuid}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateInteralLinkTarget(
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
        link.setTargetItem(document);
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
    public String addInteralLinkTitle(
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
    public String updateInteralLinkTitle(
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
        "/attachmentlists/{attachmentListIdentifier}/internal-links/{interalLinkUuid}/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeInteralLinkTitle(
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

}
