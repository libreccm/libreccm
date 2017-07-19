/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.ui.authoring;

import com.arsdigita.bebop.Component;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.contentsection.ContentSectionConfig;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Provides easy access to information about the default authoring step which
 * are available for every content type.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemAuthoringStepManager {

    @Inject
    private ConfigurationManager confManager;

    private List<ContentItemAuthoringStepInfo> stepInfos;

    @PostConstruct
    protected void initialize() {

        final ContentSectionConfig config = confManager
            .findConfiguration(ContentSectionConfig.class);
        final List<String> classNames = config.getDefaultAuthoringSteps();

        stepInfos = classNames
            .stream()
            .map(className -> createStepInfo(className))
            .collect(Collectors.toList());
    }

    public List<ContentItemAuthoringStepInfo> getContentItemAuthoringStepInfos() {

        return Collections.unmodifiableList(stepInfos);
    }

    @SuppressWarnings("unchecked")
    private ContentItemAuthoringStepInfo createStepInfo(final String className) {

        Objects.requireNonNull(className);

        if (className.isEmpty()) {
            throw new IllegalArgumentException("The name of the authoring step "
                                                   + "class can't be empty.");
        }

        final Class<? extends Component> clazz;
        try {
            clazz = (Class<? extends Component>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(String
                .format("No class for class name \"%s\" available.",
                        className),
                                               ex);
        }
    
        return createStepInfo(clazz);
    }

    private ContentItemAuthoringStepInfo createStepInfo(
        final Class<? extends Component> clazz) {
    
        final ContentItemAuthoringStepInfo stepInfo
                                               = new ContentItemAuthoringStepInfo();

        final ContentItemAuthoringStep step = clazz
            .getAnnotation(ContentItemAuthoringStep.class);

        final String defaultBundleName = String
            .join("", clazz.getName(), "Bundle");
        final String defaultLabelKey = String.join(".",
                                                   clazz.getSimpleName(),
                                                   "label");
        final String defaultDescKey = String.join(".",
                                                  clazz.getSimpleName(),
                                                  "description");

        if (step == null) {
            stepInfo.setLabelBundle(defaultBundleName);
            stepInfo.setDescriptionBundle(defaultBundleName);
            stepInfo.setLabelKey(defaultLabelKey);
            stepInfo.setDescriptionKey(defaultDescKey);
        } else {
            if (step.labelBundle() == null || step.labelBundle().isEmpty()) {
                stepInfo.setLabelBundle(defaultBundleName);
            } else {
                stepInfo.setLabelBundle(step.labelBundle());
            }
            if (step.labelKey() == null || step.labelKey().isEmpty()) {
                stepInfo.setLabelKey(defaultLabelKey);
            } else {
                stepInfo.setLabelKey(step.labelKey());
            }
            if (step.descriptionBundle() == null
                    || step.descriptionBundle().isEmpty()) {
                stepInfo.setDescriptionBundle(defaultBundleName);
            } else {
                stepInfo.setDescriptionBundle(step.descriptionBundle());
            }
            if (step.descriptionKey() == null
                    || step.descriptionKey().isEmpty()) {
                stepInfo.setDescriptionKey(defaultDescKey);
            }
        }

        stepInfo.setStep(clazz);

        return stepInfo;
    }

}
