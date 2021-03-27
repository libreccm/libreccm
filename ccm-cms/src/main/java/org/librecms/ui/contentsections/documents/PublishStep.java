/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mvc.Controller;
import javax.mvc.Models;
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
@AuthoringStepPathFragment(PublishStep.PATH_FRAGMENT)
@Named("CmsPublishStep")
public class PublishStep implements MvcAuthoringStep {

    static final String PATH_FRAGMENT = "publish";

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private LifecycleDefinitionRepository lifecycleDefRepo;

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
            .getText("authoringsteps.publish.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.publish.description");
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
        final String lifecycleDefUuid;
        if (itemManager.isLive(document)) {
            lifecycleDefUuid = document
                .getLifecycle()
                .getDefinition()
                .getUuid();
        } else {
            lifecycleDefUuid = document
                .getContentType()
                .getDefaultLifecycle()
                .getUuid();
        }
        models.put("lifecycleDefinitionUuid", lifecycleDefUuid);
        return "org/librecms/ui/documents/publish.xhtml";
    }

    public boolean isLive() {
        return itemManager.isLive(document);
    }

    public String getAssignedLifecycleLabel() {
        return globalizationHelper.getValueFromLocalizedString(
            document.getLifecycle().getDefinition().getLabel()
        );
    }

    public String getAssignedLifecycleDecription() {
        return globalizationHelper.getValueFromLocalizedString(
            document.getLifecycle().getDefinition().getDescription()
        );
    }

    /**
     * Publishes the current content item.If the item is already live, the
     * {@code selectedLifecycleDefUuid} is ignored.The apply a new lifecycle the
     * document the unpublished first.
     *
     * @param selectedLifecycleDefUuid The ID of the lifecycle definition from
     *                                 which the lifecycle for the item is
     *                                 created.
     * @param startDateParam
     * @param startTimeParam
     * @param endDateParam
     * @param endTimeParam
     *
     *
     * @return A redirect the the publish step.
     */
    @POST
    @Path("/@publish")
    @Transactional(Transactional.TxType.REQUIRED)
    public String publish(
        @FormParam("selectedLifecycleDefUuid")
        final String selectedLifecycleDefUuid,
        @FormParam("startDate") final String startDateParam,
        @FormParam("startTime") final String startTimeParam,
        @FormParam("endDate") final String endDateParam,
        @FormParam("endTime") final String endTimeParam
    ) {
        if (selectedLifecycleDefUuid == null) {
            models.put("missingLifecycleDefinitionUuid", true);
            models.put("startDateTime", startDateParam);
            models.put("startDateTime", startTimeParam);
            models.put("endDateTime", endDateParam);
            models.put("endDateTime", endTimeParam);
            return "org/librecms/ui/documenttypes/publish.xhtml";
        }
        if (startDateParam == null
                || startDateParam.isEmpty()
                || startTimeParam == null
                || startTimeParam.isEmpty()) {
            models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
            models.put("missingStartDateTime", true);
            models.put("startDateTime", startDateParam);
            models.put("startDateTime", startTimeParam);
            models.put("endDateTime", endDateParam);
            models.put("endDateTime", endTimeParam);
            return "org/librecms/ui/documents/publish.xhtml";
        }

        final DateTimeFormatter isoDateFormatter = DateTimeFormatter.ISO_DATE
            .withZone(ZoneId.systemDefault());
        final DateTimeFormatter isoTimeFormatter = DateTimeFormatter.ISO_TIME
            .withZone(ZoneId.systemDefault());

        final LocalDate localStartDate;
        try {
            localStartDate = LocalDate.parse(startDateParam, isoDateFormatter);
        } catch (DateTimeParseException ex) {
            models.put("invalidStartDate", true);
            models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
            models.put("startDateTime", startDateParam);
            models.put("startDateTime", startTimeParam);
            models.put("endDateTime", endDateParam);
            models.put("endDateTime", endTimeParam);
            return "org/librecms/ui/documents/publish.xhtml";
        }

        final LocalTime localStartTime;
        try {
            localStartTime = LocalTime.parse(startTimeParam, isoTimeFormatter);
        } catch (DateTimeParseException ex) {
            models.put("invalidStartTime", true);
            models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
            models.put("startDateTime", startDateParam);
            models.put("startDateTime", startTimeParam);
            models.put("endDateTime", endDateParam);
            models.put("endDateTime", endTimeParam);
            return "org/librecms/ui/documents/publish.xhtml";
        }

        final LocalDateTime startLocalDateTime = LocalDateTime.of(
            localStartDate, localStartTime
        );
        final Date startDateTime = Date.from(
            startLocalDateTime.toInstant(
                ZoneOffset.from(startLocalDateTime)
            )
        );

        final LocalDate localEndDate;
        try {
            localEndDate = LocalDate.parse(endDateParam, isoDateFormatter);
        } catch (DateTimeParseException ex) {
            models.put("invalidEndDate", true);
            models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
            models.put("startDateTime", startDateParam);
            models.put("startDateTime", startTimeParam);
            models.put("endDateTime", endDateParam);
            models.put("endDateTime", endTimeParam);
            return "org/librecms/ui/documents/publish.xhtml";
        }

        final LocalTime localEndTime;
        try {
            localEndTime = LocalTime.parse(endTimeParam, isoTimeFormatter);
        } catch (DateTimeParseException ex) {
            models.put("invalidEndTime", true);
            models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
            models.put("startDateTime", startDateParam);
            models.put("startDateTime", startTimeParam);
            models.put("endDateTime", endDateParam);
            models.put("endDateTime", endTimeParam);
            return "org/librecms/ui/documents/publish.xhtml";
        }

        final LocalDateTime endLocalDateTime = LocalDateTime.of(
            localEndDate, localEndTime
        );
        final Date endDateTime = Date.from(
            endLocalDateTime.toInstant(
                ZoneOffset.from(endLocalDateTime)
            )
        );

        if (selectedLifecycleDefUuid.isEmpty()) {
            if (itemManager.isLive(document)) {
                final LifecycleDefinition definition;
                definition = document.getLifecycle().getDefinition();
                itemManager.publish(
                    document, definition, startDateTime, endDateTime
                );
            } else {
                itemManager.publish(document, startDateTime, endDateTime);
            }
        } else {
            final Optional<LifecycleDefinition> definitionResult
                = lifecycleDefRepo.findByUuid(selectedLifecycleDefUuid);
            if (!definitionResult.isPresent()) {
                models.put("contentSection", section.getLabel());
                models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
                return "org/librecms/ui/documents/lifecycle-definition-not-found.xhtml";
            }
            final LifecycleDefinition definition = definitionResult.get();
            itemManager.publish(
                document, definition, startDateTime, endDateTime
            );
        }

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    @POST
    @Path("/@unpublish")
    @Transactional(Transactional.TxType.REQUIRED)
    public String unpublish() {
        itemManager.unpublish(document);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

}
