/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;
import org.librecms.ui.contentsections.ContentSectionsUi;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{sectionIdentifier}/documents")
@Controller
public class DocumentController {

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private Instance<MvcAuthoringStep> authoringSteps;

    @Inject
    private Instance<MvcDocumentCreateStep<?>> createSteps;

    @Inject
    private Models models;

    @GET
    @Path("/")
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
    @Transactional(Transactional.TxType.REQUIRED)
    public MvcDocumentCreateStep<? extends ContentItem> createDocument(
        @PathParam("sectionIdentifider") final String sectionIdentifier,
        @PathParam("documentType") final String documentType
    ) {
        return createDocument(sectionIdentifier, "", documentType);
    }

    @Path("/{folderPath:(.+)?}/@create/{documentType}")
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

    @Path("/{documentPath:(.+)?}/@authoringsteps/{authoringStep}")
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

        if (!authoringStep.supportedDocumenType().isAssignableFrom(item
            .getClass())) {
            models.put("section", section.getLabel());
            models.put("documentPath", documentPath);
            models.put("documentType", item.getClass().getName());
            models.put("authoringStep", authoringStepIdentifier);
            return new UnsupportedDocumentType();
        }

        authoringStep.setContentSection(section);
        authoringStep.setContentItem(item);

        return authoringStep;
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
