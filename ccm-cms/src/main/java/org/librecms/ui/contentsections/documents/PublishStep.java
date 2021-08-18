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

import org.libreccm.core.UnexpectedErrorException;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.lifecycle.Lifecycle;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;
import org.librecms.ui.contentsections.ItemPermissionChecker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Authoring step (part of the default steps) for publishing a
 * {@link ContentItem}. This class acts as controller for the view(s) of the
 * publish step as well as named bean providing some data for these views.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "publish")
@Controller
@MvcAuthoringStepDef(
    bundle = DefaultAuthoringStepConstants.BUNDLE,
    descriptionKey = "authoringsteps.publish.description",
    labelKey = "authoringsteps.publish.label",
    supportedDocumentType = ContentItem.class
)
public class PublishStep extends AbstractMvcAuthoringStep {

    private static final String TEMPLATE
        = "org/librecms/ui/contentsection/documents/publish.xhtml";

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

    @Inject
    private PublishStepModel publishStepModel;

    @Override
    public Class<PublishStep> getStepClass() {
        return PublishStep.class;
    }

    @Override
    protected void init() throws ContentSectionNotFoundException,
                                 DocumentNotFoundException {
        super.init();

        publishStepModel.setAvailableLifecycles(
            getContentSection()
                .getLifecycleDefinitions()
                .stream()
                .map(this::buildLifecycleListEntry)
                .collect(Collectors.toList())
        );

        final ContentItem document = getDocument();

        if (itemManager.isLive(document)) {
            final ContentItem live = itemManager
                .getLiveVersion(document, document.getClass())
                .orElseThrow(
                    () -> new UnexpectedErrorException(
                        String.format(
                            "ContentItem %s is reported as live by "
                                + "ContentItemManager#isLive"
                                + "but has no live version.",
                            document.getUuid()
                        )
                    )
                );
            publishStepModel.setAssignedLifecycleLabel(
                Optional
                    .ofNullable(live.getLifecycle())
                    .map(Lifecycle::getDefinition)
                    .map(LifecycleDefinition::getLabel)
                    .map(globalizationHelper::getValueFromLocalizedString)
                    .orElse("")
            );

            publishStepModel.setAssignedLifecycleDescription(
                Optional
                    .ofNullable(live.getLifecycle())
                    .map(Lifecycle::getDefinition)
                    .map(LifecycleDefinition::getDescription)
                    .map(globalizationHelper::getValueFromLocalizedString)
                    .orElse("")
            );
        } else {
            publishStepModel.setAssignedLifecycleDescription("");
            publishStepModel.setAssignedLifecycleLabel("");
        }

        publishStepModel.setLive(itemManager.isLive(getDocument()));
    }

    @GET
    @Path("/")
    @Transactional(Transactional.TxType.REQUIRED)
    public String showStep(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final ContentItem document = getDocument();
        if (itemPermissionChecker.canPublishItems(getDocument())) {
            final String lifecycleDefUuid;
            if (itemManager.isLive(document)) {
                lifecycleDefUuid = itemManager
                    .getLiveVersion(document, document.getClass())
                    .orElseThrow(
                        () -> new UnexpectedErrorException(
                            String.format(
                                "ContentItem %s is reported as live by "
                                    + "ContentItemManager#isLive"
                                    + "but has no live version.",
                                document.getUuid()
                            )
                        )
                    )
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
            return TEMPLATE;
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                defaultStepsMessageBundle.getMessage(
                    "access_to_authoringstep_denied",
                    new String[]{getLabel()}
                )
            );
        }
    }

    /**
     * Publishes the current content item.If the item is already live, the
     * {@code selectedLifecycleDefUuid} is ignored.The apply a new lifecycle the
     * document the unpublished first.
     *
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param selectedLifecycleDefUuid The ID of the lifecycle definition from
     *                                 which the lifecycle for the item is
     *                                 created.
     * @param startDateParam
     * @param startTimeParam
     * @param endDateParam
     * @param endTimeParam
     *
     * @return A redirect the the publish step.
     */
    @POST
    @Path("/")
    @Transactional(Transactional.TxType.REQUIRED)
    public String publish(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @FormParam("selectedLifecycleDefUuid")
        final String selectedLifecycleDefUuid,
        @FormParam("startDate") @DefaultValue("")
        final String startDateParam,
        @FormParam("startTime") @DefaultValue("")
        final String startTimeParam,
        @FormParam("endDate") @DefaultValue("")
        final String endDateParam,
        @FormParam("endTime") @DefaultValue("")
        final String endTimeParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final ContentItem document = getDocument();

        if (selectedLifecycleDefUuid.isEmpty()) {
            models.put("missingLifecycleDefinitionUuid", true);
            models.put("startDate", startDateParam);
            models.put("startTime", startTimeParam);
            models.put("endDate", endDateParam);
            models.put("endTime", endTimeParam);

            return TEMPLATE;
        }
        if (startDateParam.isEmpty() || startTimeParam.isEmpty()) {
            models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
            models.put("missingStartDateTime", true);
            models.put("startDate", startDateParam);
            models.put("startTime", startTimeParam);
            models.put("endDate", endDateParam);
            models.put("endTime", endTimeParam);

            return TEMPLATE;
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
            models.put("startDate", startDateParam);
            models.put("startTime", startTimeParam);
            models.put("endDate", endDateParam);
            models.put("endTime", endTimeParam);

            return TEMPLATE;
        }

        final LocalTime localStartTime;
        try {
            localStartTime = LocalTime.parse(startTimeParam, isoTimeFormatter);
        } catch (DateTimeParseException ex) {
            models.put("invalidStartTime", true);
            models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
            models.put("startDate", startDateParam);
            models.put("startTime", startTimeParam);
            models.put("endDate", endDateParam);
            models.put("endTime", endTimeParam);

            return TEMPLATE;
        }

        final LocalDateTime startLocalDateTime = LocalDateTime.of(
            localStartDate, localStartTime
        );

        final Date startDateTime = Date.from(
            startLocalDateTime.atZone(ZoneId.systemDefault())
                .toInstant()
        );

        final Date endDateTime;
        if (endDateParam == null || endDateParam.isBlank()) {
            endDateTime = null;
        } else {
            final LocalDate localEndDate;
            try {
                localEndDate = LocalDate.parse(endDateParam, isoDateFormatter);
            } catch (DateTimeParseException ex) {
                models.put("invalidEndDate", true);
                models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
                models.put("startDate", startDateParam);
                models.put("startTime", startTimeParam);
                models.put("endDate", endDateParam);
                models.put("endTime", endTimeParam);

                return TEMPLATE;
            }

            final LocalTime localEndTime;
            try {
                localEndTime = LocalTime.parse(endTimeParam, isoTimeFormatter);
            } catch (DateTimeParseException ex) {
                models.put("invalidEndTime", true);
                models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
                models.put("startDate", startDateParam);
                models.put("startTime", startTimeParam);
                models.put("endDate", endDateParam);
                models.put("endTime", endTimeParam);

                return TEMPLATE;
            }

            final LocalDateTime endLocalDateTime = LocalDateTime.of(
                localEndDate, localEndTime
            );
            endDateTime = Date.from(
                endLocalDateTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            );
        }

        if (!itemPermissionChecker.canPublishItems(document)) {
            return documentUi.showAccessDenied(
                getContentSection(),
                document,
                "item.publish"
            );
        }

        if (selectedLifecycleDefUuid.isEmpty()) {
            if (itemManager.isLive(document)) {
                final LifecycleDefinition definition;
                definition = itemManager
                    .getLiveVersion(document, document.getClass())
                    .orElseThrow(
                        () -> new UnexpectedErrorException(
                            String.format(
                                "ContentItem %s is reported as live by "
                                    + "ContentItemManager#isLive"
                                    + "but has no live version.",
                                document.getUuid()
                            )
                        )
                    )
                    .getLifecycle()
                    .getDefinition();
                itemManager.publish(
                    document, definition, startDateTime, endDateTime
                );
            } else {
                itemManager
                    .publish(document, startDateTime, endDateTime);
            }
        } else {
            final Optional<LifecycleDefinition> definitionResult
                = lifecycleDefRepo.findByUuid(selectedLifecycleDefUuid);
            if (!definitionResult.isPresent()) {
                models.put(
                    "contentSection",
                    getContentSection().getLabel()
                );
                models.put("lifecycleDefinitionUuid", selectedLifecycleDefUuid);
                return "org/librecms/ui/documents/lifecycle-definition-not-found.xhtml";
            }
            final LifecycleDefinition definition = definitionResult.get();
            itemManager.publish(
                document, definition, startDateTime, endDateTime
            );
        }

        return buildRedirectPathForStep();
    }

    @POST
    @Path("/republish")
    @Transactional(Transactional.TxType.REQUIRED)
    public String republish() {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final ContentItem document = getDocument();
        if (!itemManager.isLive(document)) {
            models.put("republishNoneLive", true);
            models.put("document", itemManager.getItemPath(document));
            return TEMPLATE;
        }

        final ContentItem live = itemManager
            .getLiveVersion(document, document.getClass())
            .orElseThrow(
                () -> new UnexpectedErrorException(
                    String.format(
                        "ContentItem %s is reported as live by "
                            + "ContentItemManager#isLive"
                            + "but has no live version.",
                        document.getUuid()
                    )
                )
            );

        final Lifecycle lifecycle = live.getLifecycle();
        final LifecycleDefinition definition = lifecycle.getDefinition();
        final Date startDateTime = lifecycle.getStartDateTime();
        final Date endDateTime = lifecycle.getEndDateTime();

        itemManager.publish(
            document, definition, startDateTime, endDateTime
        );

        return buildRedirectPathForStep();
    }

    /**
     * Unpublishes the current content item.
     *
     * @return A redirect to the publish step.
     */
    @POST
    @Path("/unpublish")
    @Transactional(Transactional.TxType.REQUIRED)
    public String unpublish() {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final ContentItem document = getDocument();
        if (!itemPermissionChecker.canPublishItems(document)) {
            return documentUi.showAccessDenied(
                getContentSection(),
                document,
                "item.unpublish"
            );
        }

        itemManager.unpublish(document);

        return buildRedirectPathForStep();
    }

    private LifecycleListEntry buildLifecycleListEntry(
        final LifecycleDefinition definition
    ) {
        final LifecycleListEntry entry = new LifecycleListEntry();
        entry.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                definition.getDescription()
            )
        );
        entry.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                definition.getLabel()
            )
        );
        entry.setUuid(definition.getUuid());

        return entry;
    }

}
