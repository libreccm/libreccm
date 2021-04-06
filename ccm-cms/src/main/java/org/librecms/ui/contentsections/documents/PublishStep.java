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

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.lifecycle.Lifecycle;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;
import org.librecms.ui.contentsections.ItemPermissionChecker;

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
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Authoring step (part of the default steps) for publishing a
 * {@link ContentItem}. This class acts as controller for the view(s) of the
 * publish step as well as named bean providing some data for these views.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
@AuthoringStepPathFragment(PublishStep.PATH_FRAGMENT)
@Named("CmsPublishStep")
public class PublishStep implements MvcAuthoringStep {

    /**
     * The path fragment of the publish step.
     */
    static final String PATH_FRAGMENT = "publish";

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private DefaultStepsMessageBundle defaultStepsMessageBundle;

    @Inject
    private DocumentUi documentUi;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ItemPermissionChecker itemPermissionChecker;

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
        if (itemPermissionChecker.canPublishItems(document)) {
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
        } else {
            return documentUi.showAccessDenied(
                section,
                document,
                defaultStepsMessageBundle.getMessage(
                    "access_to_authoringstep_denied", new String[]{getLabel()}
                )
            );
        }
    }

    /**
     * Is the current document live?
     *
     * @return
     */
    public boolean isLive() {
        return itemManager.isLive(document);
    }

    /**
     * Get the label of the lifecycle assigned to the current content item. The
     * value is determined from the label of the definition of the lifecycle
     * using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     *
     * @return The label of the lifecycle assigned to the current content item,
     *         or an empty string if no lifecycle is assigned to the item.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public String getAssignedLifecycleLabel() {
        return Optional
            .ofNullable(document.getLifecycle())
            .map(Lifecycle::getDefinition)
            .map(LifecycleDefinition::getLabel)
            .map(globalizationHelper::getValueFromLocalizedString)
            .orElse("");

    }

    /**
     * Get the description of the lifecycle assigned to the current content
     * item. The value is determined from the description of the definition of
     * the lifecycle using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     *
     * @return The description of the lifecycle assigned to the current content
     *         item, or an empty string if no lifecycle is assigned to the item.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public String getAssignedLifecycleDecription() {
        return Optional
            .ofNullable(document.getLifecycle())
            .map(Lifecycle::getDefinition)
            .map(LifecycleDefinition::getDescription)
            .map(globalizationHelper::getValueFromLocalizedString)
            .orElse("");
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

        if (!itemPermissionChecker.canPublishItems(document)) {
            return documentUi.showAccessDenied(
                section,
                document,
                "item.publish"
            );
        }

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

    /**
     * Unpublishes the current content item.
     *
     * @return A redirect to the publish step.
     */
    @POST
    @Path("/@unpublish")
    @Transactional(Transactional.TxType.REQUIRED)
    public String unpublish() {
          if (!itemPermissionChecker.canPublishItems(document)) {
            return documentUi.showAccessDenied(
                section,
                document,
                "item.unpublish"
            );
        }
        
        itemManager.unpublish(document);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

}
