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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.librecms.contentsection.ContentItem;

import javax.enterprise.context.Dependent;
import javax.mvc.Controller;
import javax.ws.rs.Path;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class AuthoringStepsValidator {
    
    private static final Logger LOGGER = LogManager.getLogger(
        AuthoringStepsValidator.class
    );
    
    public boolean validateAuthoringStep(final Class<?> stepClass) {
        if (stepClass.getAnnotation(Controller.class) == null) {
            LOGGER.warn(
                "Class {} is part of a set of authoringsteps, but is not"
                    + " annotated with {}. The class will be ignored.",
                stepClass.getName(),
                Controller.class.getName());
            return false;
        }
        
        final Path pathAnnotation = stepClass.getAnnotation(Path.class);
        if (pathAnnotation == null) {
            LOGGER.warn(
                "Class {} is part of a set of authoring steps, but is not "
                    + "annotated with {}. the class will be ignored.",
                stepClass.getName(),
                Path.class.getName()
            );
            return false;
        }

        final String path = pathAnnotation.value();
        if (path == null
                || !path.startsWith(MvcAuthoringSteps.PATH_PREFIX)) {
            LOGGER.warn(
                "Class {} is part of a set of authoring steps, but the value"
                    + "of the {} annotation of the class does not start "
                    + "with {}. The class will be ignored.",
                stepClass.getName(),
                Path.class.getName(),
                MvcAuthoringSteps.PATH_PREFIX
            );
        }
        
        if (stepClass.getAnnotation(MvcAuthoringStepDef.class) == null) {
            LOGGER.warn(
                "Class {} is part of a set of authoring steps, but is not "
                    + "annotated with {}. The class will be ignored.",
                stepClass.getName(),
                MvcAuthoringStepDef.class
            );
        }

        return true;
    }
    
    public boolean supportsItem(
        final Class<?> stepClass, final ContentItem item
    ) {
        final MvcAuthoringStepDef stepAnnotation = stepClass.getAnnotation(MvcAuthoringStepDef.class
        );

        if (stepAnnotation == null) {
            return false;
        }

        return item.getClass().isAssignableFrom(
            stepAnnotation.supportedDocumentType()
        );
    }
}
