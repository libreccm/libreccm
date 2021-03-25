/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.AssetTypeInfo;
import org.librecms.assets.AssetTypesManager;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.AttachmentListL10NManager;
import org.librecms.contentsection.AttachmentListManager;
import org.librecms.contentsection.AttachmentListRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.contentsection.ItemAttachmentManager;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mvc.Controller;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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
    private AssetTypesManager assetTypesManager;

    @Inject
    private AttachmentListManager listManager;

    @Inject
    private AttachmentListL10NManager listL10NManager;

    @Inject
    private AttachmentListRepository listRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ItemAttachmentManager attachmentManager;

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
        return "org/librecms/ui/contenttypes/relatedinfo.xhtml";
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

    // ToDo Remove Attachment List
    // Add attachment (attach asset)
    // Add internal link
    // Remove attachment
    // ToDo Move attachment list to first position
    // ToDo Move attachment list up
    // ToDo Move attachment list down
    // ToDo Move item attachment to first position
    // ToDo Move item attachment up
    // ToDo Move item attachment down
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
