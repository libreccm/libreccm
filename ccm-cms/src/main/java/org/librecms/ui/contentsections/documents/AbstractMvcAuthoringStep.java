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

import org.librecms.ui.contentsections.ContentSectionNotFoundException;
import org.hibernate.LazyInitializationException;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.ui.contentsections.ContentSectionModel;
import org.librecms.ui.contentsections.ContentSectionsUi;
import org.librecms.ui.contentsections.ItemPermissionChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractMvcAuthoringStep implements MvcAuthoringStep {

    @Inject
    private DocumentUi documentUi;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ItemPermissionChecker itemPermissionChecker;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentSectionModel sectionModel;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private HttpServletRequest request;

    @Inject
    private Models models;

    @Inject
    private SelectedDocumentModel documentModel;

    @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
    private String sectionIdentifier;

    @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
    private String documentPathParam;

    private ContentSection contentSection;

    private ContentItem document;

    private String documentPath;

    private String stepPath;

    /**
     * Inits the step. This method MUST be called by all resource methods (all
     * methods annotated with {@link Path} in an authoring step. This is
     * neccessary to keep all JPA operations inside a transaction to avoid
     * {@link LazyInitializationException}s.
     *
     *
     * @throws ContentSectionNotFoundException
     * @throws DocumentNotFoundException
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected void init()
        throws ContentSectionNotFoundException, DocumentNotFoundException {
        contentSection = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new ContentSectionNotFoundException(
                    sectionsUi.showContentSectionNotFound(sectionIdentifier),
                    String.format(
                        "ContentSection %s not found.",
                        sectionIdentifier
                    )
                )
            );
        sectionModel.setSection(contentSection);

        document = itemRepo
            .findByPath(contentSection, documentPathParam)
            .orElseThrow(
                () -> new DocumentNotFoundException(
                    documentUi.showDocumentNotFound(
                        contentSection, documentPathParam
                    ),
                    String.format(
                        "No document for path %s in section %s.",
                        documentPathParam,
                        contentSection.getLabel()
                    )
                )
            );
        documentModel.setContentItem(document);

        documentPath = itemManager.getItemPath(document);
        final Map<String, String> values = new HashMap<>();
        values.put(
            MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM,
            contentSection.getLabel()
        );
        values.put(
            MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME,
            documentPath
        );

        stepPath = Optional
            .ofNullable(getStepClass().getAnnotation(Path.class))
            .map(Path::value)
            .map(
                path -> UriBuilder
                    .fromPath(path)
                    .buildFromMap(values)
                    .toString()
            )
            .orElse("");

        models.put("activeDocumentTab", "editTab");
        models.put("stepPath", stepPath);
    }

    @Override
    public ContentSection getContentSection() {
        return Optional
            .ofNullable(contentSection)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Authoring Step %s was not initalized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAuthoringStep.class.getName()
                    )
                )
            );
    }

    @Override
    public ContentItem getDocument() {
        return Optional
            .ofNullable(document)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Authoring Step %s was not initalized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAuthoringStep.class.getName()
                    )
                )
            );
    }

    @Override
    public String getDocumentPath() {
        return Optional
            .ofNullable(documentPath)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Authoring Step %s was not initalized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAuthoringStep.class.getName()
                    )
                )
            );
    }

    @Override
    public boolean getCanEdit() {
        if (!itemPermissionChecker.canEditItem(document)) {
            return false;
        }

        if (documentModel.getCurrentTask() == null) {
            return false;
        }

        return documentModel.getCurrentTask().isAssignedToCurrentUser()
                   || itemPermissionChecker.canAdministerItems(document);
    }

    @Override
    public String getLabel() {
        return Optional
            .ofNullable(
                getStepClass().getAnnotation(MvcAuthoringStepDef.class)
            )
            .map(
                annotation -> globalizationHelper.getLocalizedTextsUtil(
                    annotation.bundle()
                ).getText(annotation.labelKey())
            )
            .orElse("???");
    }

    @Override
    public String getDescription() {
        return Optional
            .ofNullable(
                getStepClass().getAnnotation(MvcAuthoringStepDef.class)
            )
            .map(
                annotation -> globalizationHelper.getLocalizedTextsUtil(
                    annotation.bundle()
                ).getText(annotation.descriptionKey())
            )
            .orElse("");
    }

    @Override
    public void updateDocumentPath() {
        documentPath = itemManager.getItemPath(document).substring(1); // Without leading slash
    }

    @Override
    public String getStepPath() {
        return stepPath;
    }

    @Override
    public String buildRedirectPathForStep() {
        final ContentSection section = Optional
            .ofNullable(contentSection)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Authoring Step %s was not initalized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAuthoringStep.class.getName()
                    )
                )
            );
        final String docPath = Optional
            .ofNullable(documentPath)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Authoring Step %s was not initalized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAuthoringStep.class.getName()
                    )
                )
            );

        final Map<String, String> values = new HashMap<>();
        values.put(
            MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM,
            section.getLabel()
        );
        values.put(
            MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME,
            docPath
        );

        return Optional
            .ofNullable(getStepClass().getAnnotation(Path.class))
            .map(Path::value)
            .map(
                path -> UriBuilder
                    .fromPath(path)
                    .buildFromMap(values)
                    .toString()
            )
            .map(path -> String.format("redirect:%s", path))
            .orElse("");
    }

    @Override
    public String buildRedirectPathForStep(final String subPath) {
        final ContentSection section = Optional
            .ofNullable(contentSection)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Authoring Step %s was not initalized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAuthoringStep.class.getName()
                    )
                )
            );
        final String docPath = Optional
            .ofNullable(documentPath)
            .map(path -> path.substring(1))
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Authoring Step %s was not initalized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAuthoringStep.class.getName()
                    )
                )
            );

        final Map<String, String> values = new HashMap<>();
        values.put(
            MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM,
            section.getLabel()
        );
        values.put(
            MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME,
            docPath
        );

        return Optional
            .ofNullable(getStepClass().getAnnotation(Path.class))
            .map(Path::value)
            .map(
                path -> UriBuilder
                    .fromPath(path)
                    .path(subPath)
                    .buildFromMap(values)
                    .toString()
            )
            .map(path -> String.format("redirect:%s", path))
            .orElse("");
    }

}
