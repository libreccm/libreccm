/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contenttypes;

import org.libreccm.modules.CcmModule;
import org.librecms.contentsection.ContentItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentTypesManager {

    private static final String DEFAULT_DESCRIPTION_KEY = "description";
    private static final String DEFAULT_LABEL_KEY = "label";

    /**
     * A list of all content type currently available.
     */
    private List<ContentTypeInfo> availableContentTypes;

    /**
     * Initialises the class (is called by CDI). This method is called by the
     * CDI container after an instance of this class has been created by the CDI
     * container. This method fills the list {@link #availableContentTypes}.
     */
    @PostConstruct
    protected void initialize() {
        final ServiceLoader<CcmModule> modules = ServiceLoader.load(
            CcmModule.class);

        final SortedSet<Class<? extends ContentItem>> types = new TreeSet<>(
            (type1, type2) -> type1.getName().compareTo(type2.getName())
        );

        for (final CcmModule module : modules) {
            final ContentTypes annotation = module.getClass().getAnnotation(
                ContentTypes.class);

            if (annotation == null) {
                continue;
            }

            final List<Class<? extends ContentItem>> moduleTypes = Arrays
                .stream(annotation.value()).collect(Collectors.toList());

            types.addAll(moduleTypes);
        }

        availableContentTypes = types.stream()
            .filter(type -> type.getAnnotation(AuthoringKit.class) != null)
            .map(contentTypeClass -> createContentTypeInfo(contentTypeClass))
            .collect(Collectors.toList());
    }

    /**
     * Helper method for creating the info object for a content type.
     *
     * @param contentTypeClass The class which provides the implementation of
     *                         the content type.
     *
     * @return A {@link ContentTypeInfo} object describing the content type.
     */
    private ContentTypeInfo createContentTypeInfo(
        final Class<? extends ContentItem> contentTypeClass) {

        final ContentTypeInfo contentTypeInfo = new ContentTypeInfo();
        contentTypeInfo.setContentItemClass(contentTypeClass);

        final String defaultBundleName = String.join(
            "",
            contentTypeClass.getClass().getName(),
            "Bundle");
        final ContentTypeDescription typeDesc = contentTypeClass.getAnnotation(
            ContentTypeDescription.class);
        if (typeDesc == null) {
            contentTypeInfo.setLabelBundle(defaultBundleName);
            contentTypeInfo.setDescriptionBundle(defaultBundleName);
            contentTypeInfo.setLabelKey(DEFAULT_LABEL_KEY);
            contentTypeInfo.setDescriptionKey(DEFAULT_DESCRIPTION_KEY);
        } else {
            if (typeDesc.labelBundle().isEmpty()) {
                contentTypeInfo.setLabelBundle(defaultBundleName);
            } else {
                contentTypeInfo.setLabelBundle(typeDesc.labelBundle());
            }

            if (typeDesc.labelKey().isEmpty()) {
                contentTypeInfo.setLabelKey(DEFAULT_LABEL_KEY);
            } else {
                contentTypeInfo.setLabelKey(typeDesc.labelKey());
            }

            if (typeDesc.descriptionBundle().isEmpty()) {
                contentTypeInfo.setDescriptionBundle(defaultBundleName);
            } else {
                contentTypeInfo.setDescriptionBundle(typeDesc
                    .descriptionBundle());
            }

            if (typeDesc.descriptionKey().isEmpty()) {
                contentTypeInfo.setDescriptionKey(DEFAULT_DESCRIPTION_KEY);
            } else {
                contentTypeInfo.setDescriptionKey(typeDesc.descriptionKey());
            }
        }

        final AuthoringKit authoringKit = contentTypeClass.getAnnotation(
            AuthoringKit.class);
        final AuthoringKitInfo authoringKitInfo = new AuthoringKitInfo();
        authoringKitInfo.setCreateComponent(authoringKit.createComponent());

        final List<AuthoringStepInfo> steps = Arrays.stream(authoringKit
            .steps())
            .map(step -> createAuthoringStepInfo(contentTypeClass, step))
            .collect(Collectors.toList());
        authoringKitInfo.setAuthoringSteps(steps);
        steps.sort((step1, step2) -> Integer.compare(step1.getOrder(),
                                                     step2.getOrder()));

        return contentTypeInfo;
    }

    /**
     * Helper method for creating an info object about an authoring step.
     *
     * @param contentTypeClass The class which provides the implementation of
     *                         the content type.
     * @param authoringStep    The {@link AuthoringStep} annotation providing
     *                         the information about the authoring step.
     *
     * @return An {@link AuthoringStepInfo} object describing the authoring
     *         step.
     *
     */
    private AuthoringStepInfo createAuthoringStepInfo(
        final Class<? extends ContentItem> contentTypeClass,
        final AuthoringStep authoringStep) {

        final AuthoringStepInfo stepInfo = new AuthoringStepInfo();

        stepInfo.setComponent(authoringStep.component());
        stepInfo.setOrder(authoringStep.order());

        final String defaultBundleName = String.join(
            "",
            contentTypeClass.getClass().getName(),
            "Bundle");

        if (authoringStep.labelBundle().isEmpty()) {
            stepInfo.setLabelBundle(defaultBundleName);
        } else {
            stepInfo.setLabelBundle(authoringStep.labelBundle());
        }

        if (authoringStep.labelKey().isEmpty()) {
            stepInfo.setLabelKey(
                String.join(".",
                            authoringStep.component().getSimpleName(),
                            DEFAULT_LABEL_KEY));
        } else {
            stepInfo.setLabelKey(authoringStep.labelKey());
        }

        if (authoringStep.descriptionBundle().isEmpty()) {
            stepInfo.setDescriptionBundle(defaultBundleName);
        } else {
            stepInfo.setDescriptionBundle(authoringStep.descriptionBundle());
        }

        if (authoringStep.descriptionKey().isEmpty()) {
            stepInfo.setDescriptionKey(
                String.join(".",
                            authoringStep.component().getSimpleName(),
                            DEFAULT_DESCRIPTION_KEY));
        } else {
            stepInfo.setDescriptionKey(authoringStep.descriptionKey());
        }

        return stepInfo;
    }

    /**
     * Retrieves a list of all content types currently available on the system.
     * 
     * @return A list of all available content types.
     */
    public List<ContentTypeInfo> getAvailableContentTypes() {
        return Collections.unmodifiableList(availableContentTypes);
    }

}
