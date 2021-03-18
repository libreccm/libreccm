/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.Phase;
import org.librecms.ui.contentsections.ContentSectionsUi;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{sectionIdentifier}/documents")
@Controller
@Named("CmsDocumentController")
public class DocumentController {

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private DocumentUi documentUi;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private Instance<MvcAuthoringStep> authoringSteps;

    @Inject
    private Instance<MvcDocumentCreateStep<?>> createSteps;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private PublishStepModel publishStepModel;

    @Inject
    private SelectedDocumentModel selectedDocumentModel;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String redirectToDocumentFolders(
        @PathParam("sectionIdentifider") final String sectionIdentifier
    ) {
        return String.format(
            "redirect:/%s/documentfolders/",
            sectionIdentifier
        );
    }

    @Path("/@create/{documentType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public MvcDocumentCreateStep<? extends ContentItem> createDocument(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentType") final String documentType
    ) {
        return createDocument(sectionIdentifier, "", documentType);
    }

    @Path("/{folderPath:(.+)?}/@create/{documentType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("unchecked")
    public MvcDocumentCreateStep<? extends ContentItem> createDocument(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @PathParam("documentType") final String documentType
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return new ContentSectionNotFound();
        }
        final ContentSection section = sectionResult.get();

        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootDocumentsFolder();
        } else {
            final Optional<Folder> folderResult = folderRepo
                .findByPath(
                    section, folderPath, FolderType.DOCUMENTS_FOLDER
                );
            if (!folderResult.isPresent()) {
                models.put("section", section.getLabel());
                models.put("folderPath", folderPath);
                return new FolderNotFound();
            }
            folder = folderResult.get();
        }

        final Class<? extends ContentItem> documentClass;
        try {
            documentClass = (Class<? extends ContentItem>) Class.forName(
                documentType
            );
        } catch (ClassNotFoundException ex) {
            models.put("documentType", documentType);
            return new DocumentTypeClassNotFound();
        }

        final boolean hasRequestedType = section
            .getContentTypes()
            .stream()
            .anyMatch(
                type -> type.getContentItemClass().equals(documentType)
            );
        if (!hasRequestedType) {
            models.put("documentType", documentType);
            models.put("section", section.getLabel());
            return new ContentTypeNotAvailable();
        }

        final Instance<MvcDocumentCreateStep<?>> instance = createSteps
            .select(new CreateDocumentOfTypeLiteral(documentClass));
        if (instance.isUnsatisfied() || instance.isAmbiguous()) {
            models.put("section", section.getLabel());
            models.put("folderPath", folderPath);
            models.put("documentType", documentType);
            return new CreateStepNotAvailable();
        }
        final MvcDocumentCreateStep<? extends ContentItem> createStep = instance
            .get();

        createStep.setContentSection(section);
        createStep.setFolder(folder);

        return createStep;
    }

    @Path("/{documentPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDocument(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            models.put("section", section.getLabel());
            models.put("documentPath", documentPath);
            documentUi.showDocumentNotFound(section, documentPath);
        }
        final ContentItem item = itemResult.get();
        return String.format(
            "redirect:/%s/documents/%s/@authoringsteps/%s",
            sectionIdentifier,
            documentPath,
            findPathFragmentForFirstStep(item)
        );
    }

    @Path("/{documentPath:(.+)?}/@authoringsteps/{authoringStep}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public MvcAuthoringStep editDocument(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentPath") final String documentPath,
        @PathParam("authoringStep") final String authoringStepIdentifier
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return new ContentSectionNotFound();
        }
        final ContentSection section = sectionResult.get();

        final Optional<ContentItem> itemResult = itemRepo
            .findByPath(section, documentPath);
        if (!itemResult.isPresent()) {
            models.put("section", section.getLabel());
            models.put("documentPath", documentPath);
            return new DocumentNotFound();
        }
        final ContentItem item = itemResult.get();

        final Instance<MvcAuthoringStep> instance = authoringSteps
            .select(
                new AuthoringStepPathFragmentLiteral(
                    authoringStepIdentifier
                )
            );
        if (instance.isUnsatisfied() || instance.isAmbiguous()) {
            models.put("section", section.getLabel());
            models.put("documentPath", documentPath);
            models.put("authoringStep", authoringStepIdentifier);
            return new AuthoringStepNotAvailable();
        }
        final MvcAuthoringStep authoringStep = instance.get();

        if (!authoringStep.supportedDocumentType().isAssignableFrom(item
            .getClass())) {
            models.put("section", section.getLabel());
            models.put("documentPath", documentPath);
            models.put("documentType", item.getClass().getName());
            models.put("authoringStep", authoringStepIdentifier);
            return new UnsupportedDocumentType();
        }

        selectedDocumentModel.setContentItem(item);

        authoringStep.setContentSection(section);
        authoringStep.setContentItem(item);

        return authoringStep;
    }

    @POST
    @Path("/{documentPath:(.+)?}/@history")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showHistory(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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

        models.put(
            "revisions",
            itemRepo.retrieveRevisions(item, item.getObjectId())
        );

        return "org/librecms/ui/contentsection/documents/history.xhtml";
    }

    @GET
    @Path("/{documentPath:(.+)?}/@publish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showPublishStep(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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
        if (item.getLifecycle() != null) {
            publishStepModel.setPhases(
                item
                    .getLifecycle()
                    .getPhases()
                    .stream()
                    .map(this::buildPhaseListEntry)
                    .collect(Collectors.toList())
            );
        }

        return "org/librecms/ui/contentsection/documents/publish.xhtml";
    }

    @POST
    @Path("/{documentPath:(.+)?}/@publish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String publish(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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

    @POST
    @Path("/{documentPath:(.+)?}/@republish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String republish(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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

    @POST
    @Path("/{documentPath:(.+)?}/@publish")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String unpublish(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
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

    private List<MvcAuthoringStep> readAuthoringSteps(
        final ContentItem item
    ) {
        final MvcAuthoringKit authoringKit = item
            .getClass()
            .getAnnotation(MvcAuthoringKit.class);

        final Class<? extends MvcAuthoringStep>[] stepClasses = authoringKit
            .authoringSteps();

        return Arrays
            .stream(stepClasses)
            .map(authoringSteps::select)
            .filter(instance -> instance.isResolvable())
            .map(Instance::get)
            .collect(Collectors.toList());
    }

    private String findPathFragmentForFirstStep(final ContentItem item) {
        final List<MvcAuthoringStep> steps = readAuthoringSteps(item);
        
        final MvcAuthoringStep firstStep = steps.get(0);
        final AuthoringStepPathFragment pathFragment = firstStep
            .getClass()
            .getAnnotation(AuthoringStepPathFragment.class);
        return pathFragment.value();
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

    private static class CreateDocumentOfTypeLiteral
        extends AnnotationLiteral<CreatesDocumentOfType>
        implements CreatesDocumentOfType {

        private static final long serialVersionUID = 1L;

        private final Class<? extends ContentItem> value;

        public CreateDocumentOfTypeLiteral(
            final Class<? extends ContentItem> value
        ) {
            this.value = value;
        }

        @Override
        public Class<? extends ContentItem> value() {
            return value;
        }

    }

    private static class AuthoringStepPathFragmentLiteral
        extends AnnotationLiteral<AuthoringStepPathFragment>
        implements AuthoringStepPathFragment {

        private static final long serialVersionUID = 1L;

        private final String value;

        public AuthoringStepPathFragmentLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

    }

}
