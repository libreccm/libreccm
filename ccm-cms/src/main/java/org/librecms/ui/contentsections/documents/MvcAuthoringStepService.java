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
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.ui.contentsections.ContentSectionsUi;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class MvcAuthoringStepService {

    @Inject
    private DocumentUi documentUi;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private ContentSection section;

    private ContentItem document;

    private String documentPath;

    public ContentSection getContentSection() {
        return section;
    }

    public ContentItem getDocument() {
        return document;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    /**
     * Gets the label for an authoring step.
     *
     * @param step The authoring step class.
     *
     * @return The label for the authoring step. If the provided class is not
     *         annotated with {@link MvcAuthoringStep} the string {@code ???} is
     *         returned.
     */
    public String getLabel(final Class<?> step) {
        return Optional
            .ofNullable(step.getAnnotation(MvcAuthoringStep.class))
            .map(
                annotation -> globalizationHelper.getLocalizedTextsUtil(
                    annotation.bundle()
                ).getText(annotation.labelKey())
            )
            .orElse("???");
    }

    /**
     * Gets the description for an authoring step.
     *
     * @param step The authoring step class.
     *
     * @return The label for the authoring step. If the provided class is not
     *         annotated with {@link MvcAuthoringStep} an empty stringis
     *         returned.
     */
    public String getDescription(final Class<?> step) {
        return Optional
            .ofNullable(step.getAnnotation(MvcAuthoringStep.class))
            .map(
                annotation -> globalizationHelper.getLocalizedTextsUtil(
                    annotation.bundle()
                ).getText(annotation.descriptionKey())
            )
            .orElse("");
    }

    /**
     * Sets the properties {@link #section}, {@link #document} and
     * {@link #documentPath} to content section and the document/content item
     * identified by the provided parameters.
     *
     * @param sectionIdentifier The identifier of the content section.
     * @param documentPath      The identifier of the document/content item.
     *
     * @throws ContentSectionNotFoundException If there is no content section
     *                                         identified by
     *                                         {@code sectionIdentifier}.
     * @throws DocumentNotFoundException       If there is not document/content
     *                                         item with the path
     *                                         {@code documentPath} in the
     *                                         content section.
     */
    public void setSectionAndDocument(
        final String sectionIdentifier, final String documentPath
    ) throws ContentSectionNotFoundException, DocumentNotFoundException {
        section = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new ContentSectionNotFoundException(
                    sectionsUi.showContentSectionNotFound(sectionIdentifier),
                    String.format(
                        "ContentSection %s not found.",
                        sectionIdentifier)
                )
            );

        document = itemRepo
            .findByPath(section, documentPath)
            .orElseThrow(
                () -> new DocumentNotFoundException(
                    documentUi.showDocumentNotFound(
                        section, documentPath),
                    String.format(
                        "Not document for path %s in section %s.",
                        documentPath,
                        section.getLabel()
                    )
                )
            );

        this.documentPath = itemManager.getItemPath(document);
    }

    /**
     * Builds the redirect path of the authoring step provided by the class
     * {@code step}. This path is most often used to implement the redirect
     * after post pattern.
     *
     * @param step The authoring step class.
     *
     * @return The redirect path. If the the provided class is not annotated
     *         with {@link Path} an empty string is returned.
     */
    public String buildRedirectPathForStep(final Class<?> step) {
        Objects.requireNonNull(step);
        return Optional
            .ofNullable(step.getAnnotation(Path.class))
            .map(Path::value)
            .map(
                path -> path
                    .replace(
                        String.format(
                            "{%s}",
                            MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM
                        ),
                        section.getLabel()
                    )
                    .replace(
                        String.format("{%s}",
                            MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME
                        ),
                        documentPath
                    )
            )
            .map(path -> String.format("redirect:%s", path))
            .orElse("");
    }

    /**
     * Builds the redirect path of the authoring step provided by the class
     * {@code step}.This path is most often used to implement the redirect after
     * post pattern.
     *
     * @param step    The authoring step class.
     * @param subPath additional path fragment(s) that are appended to the path
     *                of the authoring step.
     *
     * @return The redirect path. If the the provided class is not annotated
     *         with {@link Path} an empty string is returned.
     */
    public String buildRedirectPathForStep(
        final Class<?> step, final String subPath
    ) {
        Objects.requireNonNull(step);
        Objects.requireNonNull(subPath);
        final String subPathNormalized;
        if (subPath.startsWith("/")) {
            subPathNormalized = subPath.substring(1);
        } else {
            subPathNormalized = subPath;
        }

        return Optional
            .ofNullable(step.getAnnotation(Path.class))
            .map(Path::value)
            .map(
                path -> path
                    .replace(
                        String.format(
                            "{%s}",
                            MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM
                        ),
                        section.getLabel()
                    )
                    .replace(
                        String.format("{%s}",
                            MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME
                        ),
                        documentPath
                    )
            )
            .map(
                path -> String.format(
                    "redirect:%s/%s", path, subPathNormalized)
            ).orElse("");
    }

}
