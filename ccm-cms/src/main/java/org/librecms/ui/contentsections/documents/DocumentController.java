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
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.lifecycle.Lifecycle;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.Phase;
import org.librecms.ui.contentsections.ContentSectionModel;
import org.librecms.ui.contentsections.ContentSectionsUi;
import org.librecms.ui.contentsections.DocumentFolderController;
import org.librecms.ui.contentsections.ItemPermissionChecker;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Controller for the UI for managing documents ({@link ContentItem}s.)
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{sectionIdentifier}/documents")
@Controller
public class DocumentController {

    @Inject
    private AuthoringStepsValidator stepsValidator;

    /**
     * Item manager instance for performing operations on {@link ContentItem}s.
     */
    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentSectionModel sectionModel;

    /**
     * {@link ContentSectionsUi} instance providing for helper functions for
     * dealing with {@link ContentSection}s.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * {@link DocumentUi} instance providing some common functions for managing
     * documents.
     */
    @Inject
    private DocumentUi documentUi;

    /**
     * {@link FolderRepository} instance for retrieving folders.
     */
    @Inject
    private FolderRepository folderRepo;

    /**
     * {@link ContentItemRepository} instance for retrieving content items.
     */
    @Inject
    private ContentItemRepository itemRepo;

    /**
     * All available {@link MvcAuthoringStepDef}s.
     */
    @Inject
    @Any
    private Instance<MvcAuthoringStepDef> authoringSteps;

    /**
     * All available {@link MvcDocumentCreateStep}s.
     */
    @Inject
    @Any
    private Instance<MvcDocumentCreateStep<?>> createSteps;

    /**
     * Messages for default steps
     */
    @Inject
    private DefaultStepsMessageBundle defaultStepsMessageBundle;

    /**
     * {@link GlobalizationHelper} for working with localized texts etc.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ItemPermissionChecker itemPermissionChecker;

    /**
     * Used to make avaiable in the views without a named bean.
     */
    @Inject
    private Models models;

    /**
     * Used to check permissions on content items.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Model for the {@link PublishStep}.
     */
    @Inject
    private PublishStepModel publishStepModel;

    /**
     * Named beans providing access to the properties of the selected document
     * (content item} from the view.
     */
    @Inject
    private SelectedDocumentModel selectedDocumentModel;

//    @GET
//    @Path("/@pathtest")
//    public String pathTest() {
//
//        models.put("folderPath", "--root folder--");
//
//        return "org/librecms/ui/contentsection/documents/pathTest.xhtml";
//    }
//
//    @GET
//    @Path("/{folderPath:(.*)?}/@pathtest")
//    public String pathTest(@PathParam("folderPath") final String folderPath) {
//        if (folderPath == null || folderPath.isEmpty()) {
//            models.put("folderPath", "--root folder--");
//        } else {
//            models
//                .put("folderPath", String.format("folderPath: %s", folderPath));
//        }
//        return "org/librecms/ui/contentsection/documents/pathTest.xhtml";
//    }
    /**
     * Redirect requests to the root path of this controller to the
     * {@link DocumentFolderController}. The root path of this controller has no
     * function. We assume that somebody who access the root folders wants to
     * browse all documents in the content section. Therefore we redirect these
     * requests to the {@link DocumentFolderController}.
     *
     * @param sectionIdentifier The identififer of the current content section.
     *
     * @return A redirect to the {@link DocumentFolderController}.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String redirectToDocumentFolders(
        @PathParam("sectionIdentifier") final String sectionIdentifier
    ) {
        return String.format(
            "redirect:/%s/documentfolders/",
            sectionIdentifier
        );
    }

    /**
     * Delegates requests for the path {@code @create} to the create step
     * (subresource) of the document type. The new document will be created in
     * the root folder of the current content section.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentType      The type of the document to create.
     *
     * @return The template of the create step.
     */
    @GET
    @Path("/@create/{documentType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCreateStep(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentType") final String documentType
    ) {
        return showCreateStep(sectionIdentifier, "", documentType);
    }

    @POST
    @Path("/@create")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCreateStepPost(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @FormParam("documentType") final String documentType
    ) {
        return String.format(
            "redirect:/%s/documents/@create/%s",
            sectionIdentifier,
            documentType
        );
    }

    /**
     * Shows the create step of the document type.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param folderPath        Path of the folder in which the new document is
     *                          created.
     * @param documentType      The type of the document to create.
     *
     * @return The create step template.
     */
    @GET
    @Path("/{folderPath:(.+)?}/@create/{documentType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("unchecked")
    public String showCreateStep(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @FormParam("documentType") final String documentType
    ) {
        final CreateStepResult result = findCreateStep(
            sectionIdentifier,
            folderPath,
            documentType
        );

        if (result.isCreateStepAvailable()) {
            return result.getCreateStep().showCreateStep();
        } else {
            return result.getErrorTemplate();
        }
    }

    @POST
    @Path("/{folderPath:(.+)?}/@create/{documentType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("unchecked")
    public String showCreateStepPost(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @FormParam("documentType") final String documentType
    ) {
        return String.format(
            "redirect:/%s/documents/%s/@create/%s",
            sectionIdentifier,
            folderPath,
            documentType
        );
    }

    @POST
    @Path("/@create/{documentType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String createDocument(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentType") final String documentType,
        @Context final HttpServletRequest request
    ) {
        return createDocument(
            sectionIdentifier,
            "",
            documentType,
            request
        );
    }

    @POST
    @Path("/{folderPath:(.+)?}/@create/{documentType}")
    @AuthorizationRequired
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings({"unchecked", "unchecked"})
    public String createDocument(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @PathParam("documentType") final String documentType,
        @Context final HttpServletRequest request
    ) {
        final CreateStepResult result = findCreateStep(
            sectionIdentifier,
            folderPath,
            documentType
        );

        if (result.isCreateStepAvailable()) {
            return result.getCreateStep().createItem(request.getParameterMap());
        } else {
            return result.getErrorTemplate();
        }
    }

//    /**
//     * Redirects to the first authoring step for the document identified by the
//     * provided path.
//     *
//     * @param sectionIdentifier The identifier of the current content section.
//     * @param documentPath      The path of the document.
//     *
//     * @return A redirect to the first authoring step of the document, or the
//     *         {@link DocumentNotFound} pseudo authoring step.
//     */
//    @GET
//    @Path("/{documentPath:(.+)?}")
//    @AuthorizationRequired
//    @Transactional(Transactional.TxType.REQUIRED)
//    public String showEditDocument(
//        @PathParam("sectionIdentifier") final String sectionIdentifier,
//        @PathParam("documentPath") final String documentPath
//    ) {
//        final Optional<ContentSection> sectionResult = sectionsUi
//            .findContentSection(sectionIdentifier);
//        if (!sectionResult.isPresent()) {
//            sectionsUi.showContentSectionNotFound(sectionIdentifier);
//        }
//        final ContentSection section = sectionResult.get();
//
//        final Optional<ContentItem> itemResult = itemRepo
//            .findByPath(section, documentPath);
//        if (!itemResult.isPresent()) {
//            models.put("section", section.getLabel());
//            models.put("documentPath", documentPath);
//            documentUi.showDocumentNotFound(section, documentPath);
//        }
//        final ContentItem item = itemResult.get();
//        if (!itemPermissionChecker.canEditItem(item)) {
//            return documentUi.showAccessDenied(
//                section,
//                item,
//                defaultStepsMessageBundle.getMessage("edit_denied")
//            );
//        }
//
//        return String.format(
//            "redirect:/%s/documents/%s/@authoringsteps/%s",
//            sectionIdentifier,
//            documentPath,
//            findFirstAuthoringStep(item)
//        );
//    }
//
//    /**
//     * Redirect requests for an authoring step to the subresource of the
//     * authoring step.
//     *
//     * @param sectionIdentifier       The identifier of the current content
//     *                                section.
//     * @param documentPath            The path of the document to edit.
//     * @param authoringStepIdentifier The identifier/path fragment of the
//     *                                authoring step.
//     * @param request
//     *
//     * @return The authoring step subresource.
//     */
//    @GET
//    @Path("/{documentPath:(.+)?}/@authoringsteps/{authoringStep}")
//    @AuthorizationRequired
//    @Transactional(Transactional.TxType.REQUIRED)
//    public String showEditDocument(
//        @PathParam("sectionIdentifier") final String sectionIdentifier,
//        @PathParam("documentPath") final String documentPath,
//        @PathParam("authoringStep") final String authoringStepIdentifier,
//        @Context final HttpServletRequest request
//    ) {
//        final Optional<ContentSection> sectionResult = sectionsUi
//            .findContentSection(sectionIdentifier);
//        if (!sectionResult.isPresent()) {
//            models.put("sectionIdentifier", sectionIdentifier);
//            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
//        }
//        final ContentSection section = sectionResult.get();
//
//        final Optional<ContentItem> itemResult = itemRepo
//            .findByPath(section, documentPath);
//        if (!itemResult.isPresent()) {
//            models.put("section", section.getLabel());
//            models.put("documentPath", documentPath);
//            return documentUi.showDocumentNotFound(section, documentPath);
//        }
//        final ContentItem item = itemResult.get();
//        if (!itemPermissionChecker.canEditItem(item)) {
//            models.put("section", section.getLabel());
//            models.put("documentPath", itemManager.getItemFolder(item));
//            models.put(
//                "step", defaultStepsMessageBundle.getMessage("edit_step")
//            );
//            return documentUi.showAccessDenied(
//                section, documentPath, documentPath
//            );
//        }
//
//        final Instance<MvcAuthoringStep> instance = authoringSteps
//            .select(
//                new AuthoringStepPathFragmentLiteral(
//                    authoringStepIdentifier
//                )
//            );
//        if (instance.isUnsatisfied() || instance.isAmbiguous()) {
//            models.put("section", section.getLabel());
//            models.put("documentPath", documentPath);
//            models.put("authoringStep", authoringStepIdentifier);
//            return showAuthoringStepNotAvailable(authoringStepIdentifier);
//        }
//        final MvcAuthoringStepDef authoringStep = instance.get();
//
//        if (!authoringStep.supportedDocumentType().isAssignableFrom(item
//            .getClass())) {
//            models.put("section", section.getLabel());
//            models.put("documentPath", documentPath);
//            models.put("documentType", item.getClass().getName());
//            models.put("authoringStep", authoringStepIdentifier);
//            return showUnsupportedDocumentType(
//                authoringStepIdentifier,
//                item.getClass().getName()
//            );
//        }
//
//        models.put("authoringStep", authoringStepIdentifier);
//
//        selectedDocumentModel.setContentItem(item);
//
//        authoringStep.setContentSection(section);
//        authoringStep.setContentItem(item);
//
//        return authoringStep.showStep();
//    }
    @GET
    @Path("/{documentPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDocument(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.EDIT, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        return String.format("redirect:%s", findFirstAuthoringStep(item));
    }

    /**
     * Show the document history page.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentPath      The path of the document.
     *
     * @return The template for the document history page.
     */
    @POST
    @Path("/{documentPath:(.+)?}/@history")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showHistory(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.EDIT, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }
        selectedDocumentModel.setContentItem(item);

        if (!itemPermissionChecker.canEditItem(item)) {
            models.put("section", section.getLabel());
            models.put("documentPath", itemManager.getItemFolder(item));
            models.put(
                "step", defaultStepsMessageBundle.getMessage("edit_step")
            );
            return documentUi.showAccessDenied(
                section,
                documentPath,
                defaultStepsMessageBundle.getMessage("history")
            );
        }

        models.put(
            "revisions",
            itemRepo.retrieveRevisions(item, item.getObjectId())
        );

        return "org/librecms/ui/contentsection/documents/history.xhtml";
    }

    /**
     * Shows the publish step for the current document.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentPath      The path of the document to publish.
     *
     * @return The template for the publish step.
     */
    @GET
    @Path("/{documentPath:(.+)?}/@publish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showPublishStep(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        publishStepModel.setAvailableLifecycles(
            section
                .getLifecycleDefinitions()
                .stream()
                .map(this::buildLifecycleListEntry)
                .collect(Collectors.toList())
        );
        publishStepModel.setPhases(
            Optional
                .ofNullable(item.getLifecycle())
                .map(Lifecycle::getPhases)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::buildPhaseListEntry)
                .collect(Collectors.toList())
        );

        models.put("authoringStep", "publish");

        return "org/librecms/ui/contentsection/documents/publish.xhtml";
    }

    /**
     * Published a document.
     *
     * @param sectionIdentifier     The identifier of the current content
     *                              section.
     * @param documentPath          The path of the document to publish.
     * @param selectedLifecycleUuid The UUID of the lifecycle selected for the
     *                              document.
     *
     * @return A redirect to the publish step (redirect after POST pattern).
     */
    @POST
    @Path("/{documentPath:(.+)?}/@publish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String publish(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @FormParam("selectedLifecycleUuid") @DefaultValue("")
        final String selectedLifecycleUuid
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        if (selectedLifecycleUuid.isEmpty()) {
            itemManager.publish(item);
        } else {
            final Optional<LifecycleDefinition> result = section
                .getLifecycleDefinitions()
                .stream()
                .filter(
                    definition -> definition.getUuid().equals(
                        selectedLifecycleUuid
                    )
                ).findAny();
            if (!result.isPresent()) {
                models.put("section", section.getLabel());
                models.put("lifecycleDefUuid", selectedLifecycleUuid);
                return "org/librecms/ui/contentsection/documents/lifecycle-def-not-found.xhtml";
            }
        }

        return String.format(
            "redirect:/%s/documents/%s/@publish",
            sectionIdentifier,
            documentPath
        );
    }

    /**
     * Republishes a document.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentPath      The path of the document to republish.
     *
     * @return A redirect to the publish step (redirect after POST pattern).
     */
    @POST
    @Path("/{documentPath:(.+)?}/@republish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String republish(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        itemManager.publish(item);

        return String.format(
            "redirect:/%s/documents/%s/@publish",
            sectionIdentifier,
            documentPath
        );
    }

    /**
     * Unpublishes a document.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param documentPath      The path of the document to unpublish.
     *
     * @return A redirect to the publish step (redirect after POST pattern).
     */
    @POST
    @Path("/{documentPath:(.+)?}/@unpublish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String unpublish(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            return documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        if (!permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier,
                "documentPath", documentPath
            );
        }

        itemManager.unpublish(item);

        return String.format(
            "redirect:/%s/documents/%s/@publish",
            sectionIdentifier,
            documentPath
        );
    }

    /**
     * Helper method for reading the authoring steps for the current content
     * item.
     *
     * @param item The content item.
     *
     * @return A list of authoring steps for the provided item.
     */
    private List<Class<?>> readAuthoringSteps(
        final ContentItem item
    ) {
        final MvcAuthoringKit authoringKit = item
            .getClass()
            .getAnnotation(MvcAuthoringKit.class);

        return Arrays
            .stream(authoringKit.authoringSteps())
            .filter(stepsValidator::validateAuthoringStep)
            .filter(stepClass -> stepsValidator.supportsItem(stepClass, item))
            .collect(Collectors.toList());
    }

    /**
     * Helper method for finding the path fragment for the first authoring step
     * for a content item.
     *
     * @param item The content item.
     *
     * @return The path of the first authoring step of the item.
     *
     */
    private String findFirstAuthoringStep(final ContentItem item) {
        final List<Class<?>> steps = readAuthoringSteps(item);

        final Class<?> firstStep = steps.get(0);
        final Path pathAnnotation = firstStep.getAnnotation(Path.class);
        return pathAnnotation
            .value()
            .replace(
                String.format(
                    "{%s}",
                    MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM
                ),
                item.getContentType().getContentSection().getLabel()
            )
            .replace(
                String.format("/{%s}",
                    MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM
                ),
                itemManager.getItemPath(item)
            );
    }

    /**
     * Helper method for building an entry in the list of lifecycles for the
     * view.
     *
     * @param definition The lifecycle definition from which the entry is
     *                   created.
     *
     * @return A {@link LifecycleListEntry} for the provided
     *         {@link LifecycleDefinition}.
     */
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

    /**
     * Builds a {@link PhaseListEntry} for displaying the phases of a
     * {@link Lifecycle}.
     *
     * @param phase The phase from which the entry is created.
     *
     * @return A {@link PhaseListEntry} for the provided {@link Phase}.
     */
    private PhaseListEntry buildPhaseListEntry(final Phase phase) {
        final DateTimeFormatter dateTimeFormatter
            = DateTimeFormatter.ISO_DATE_TIME
                .withZone(ZoneId.systemDefault());
        final PhaseListEntry entry = new PhaseListEntry();
        entry.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                phase.getDefinition().getDescription()
            )
        );
        entry.setEndDateTime(
            dateTimeFormatter.format(phase.getEndDateTime().toInstant())
        );
        entry.setFinished(phase.isFinished());
        entry.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                phase.getDefinition().getLabel()
            )
        );
        entry.setPhaseId(phase.getPhaseId());
        entry.setStartDateTime(
            dateTimeFormatter.format(phase.getStartDateTime().toInstant())
        );
        entry.setStarted(phase.isStarted());
        return entry;
    }

    /**
     * Helper method for showing the "document folder not found" page if there
     * is not folder for the provided path.
     *
     * @param section    The content section.
     * @param folderPath The folder path.
     *
     * @return The template of the "document folder not found" page.
     */
    private String showDocumentFolderNotFound(
        final ContentSection section, final String folderPath
    ) {
        models.put("contentSection", section.getLabel());
        models.put("folderPath", folderPath);

        return "org/librecms/ui/contentsection/documentfolder/documentfolder-not-found.xhtml";
    }

    /**
     * Helper method for showing the "documenttype not available" page if the
     * requested document type is not available for the current content section.
     *
     * @param section    The content section.
     * @param folderPath The folder path.
     *
     * @return The template of the "document folder not found" page.
     */
    private String showDocumentTypeNotFound(
        final ContentSection section, final String documentType
    ) {
        models.put("section", section.getLabel());
        models.put("documentType", documentType);
        return "org/librecms/ui/contentsection/documents/document-type-not-available.xhtml";
    }

    private String showCreateStepNotAvailable(
        final ContentSection section,
        final String folderPath,
        final String documentType
    ) {
        models.put("section", section.getLabel());
        models.put("folderPath", folderPath);
        models.put("documentType", documentType);

        return "org/librecms/ui/contentsection/documents/create-step-not-available.xhtml";
    }

    private CreateStepResult findCreateStep(
        final String sectionIdentifier,
        final String folderPath,
        final String documentType
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return new CreateStepResult(
                sectionsUi.showContentSectionNotFound(sectionIdentifier)
            );
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootDocumentsFolder();
        } else {
            final Optional<Folder> folderResult = folderRepo
                .findByPath(
                    section, folderPath, FolderType.DOCUMENTS_FOLDER
                );
            if (!folderResult.isPresent()) {
                return new CreateStepResult(
                    showDocumentFolderNotFound(section, folderPath)
                );
            }
            folder = folderResult.get();
        }

        if (!itemPermissionChecker.canCreateNewItems(folder)) {
            return new CreateStepResult(
                sectionsUi.showAccessDenied(
                    "sectionidentifier", sectionIdentifier,
                    "folderPath", folderPath,
                    "step", defaultStepsMessageBundle.getMessage("create_step")
                )
            );
        }

        final Class<?> clazz;
        try {
            clazz = Class.forName(documentType);
        } catch (ClassNotFoundException ex) {
            return new CreateStepResult(
                showDocumentTypeNotFound(section, documentType)
            );
        }
        @SuppressWarnings("unchecked")
        final Class<? extends ContentItem> documentClass
            = (Class<? extends ContentItem>) clazz;

        final boolean hasRequestedType = section
            .getContentTypes()
            .stream()
            .anyMatch(
                type -> type.getContentItemClass().equals(documentType)
            );
        if (!hasRequestedType) {
            return new CreateStepResult(
                showDocumentTypeNotFound(section, documentType)
            );
        }

        final Optional<MvcAuthoringKit> authoringKitResult = Optional
            .ofNullable(
                documentClass.getDeclaredAnnotation(MvcAuthoringKit.class)
            );
        if (!authoringKitResult.isPresent()) {
            return new CreateStepResult(
                showCreateStepNotAvailable(section, folderPath, documentType)
            );
        }
        final MvcAuthoringKit authoringKit = authoringKitResult.get();
        final Class<? extends MvcDocumentCreateStep<?>> createStepClass
            = authoringKit.createStep();

//        final Instance<MvcDocumentCreateStep<?>> instance = createSteps
//            .select(new CreateDocumentOfTypeLiteral(documentClass));
        final Instance<? extends MvcDocumentCreateStep<?>> instance
            = createSteps.select(createStepClass);
        if (instance.isUnsatisfied() || instance.isAmbiguous()) {
            return new CreateStepResult(
                showCreateStepNotAvailable(section, folderPath, documentType)
            );
        }
        final MvcDocumentCreateStep<? extends ContentItem> createStep = instance
            .get();

        createStep.setContentSection(section);
        createStep.setAvailableWorkflows(
            section
                .getWorkflowTemplates()
                .stream()
                .collect(
                    Collectors.toMap(
                        workflow -> workflow.getUuid(),
                        workflow -> globalizationHelper
                            .getValueFromLocalizedString(
                                workflow.getName()
                            ),
                        (value1, value2) -> value1,
                        () -> new LinkedHashMap<String, String>()
                    )
                )
        );
        createStep.setFolder(folder);

        return new CreateStepResult(createStep);
    }

    private class CreateStepResult {

        private final MvcDocumentCreateStep<? extends ContentItem> createStep;

        private final boolean createStepAvailable;

        private final String errorTemplate;

        public CreateStepResult(
            final MvcDocumentCreateStep<? extends ContentItem> createStep
        ) {
            this.createStep = createStep;
            createStepAvailable = true;
            errorTemplate = null;
        }

        public CreateStepResult(final String errorTemplate) {
            this.createStep = null;
            createStepAvailable = false;
            this.errorTemplate = errorTemplate;
        }

        public MvcDocumentCreateStep<? extends ContentItem> getCreateStep() {
            return createStep;
        }

        public boolean isCreateStepAvailable() {
            return createStepAvailable;
        }

        public String getErrorTemplate() {
            return errorTemplate;
        }

    }

}
