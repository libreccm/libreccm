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
package org.librecms.ui.contentsections.assets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.librecms.contentsection.Asset;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.mvc.Controller;
import javax.ws.rs.Path;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class AssetEditStepsValidator {

    private static final Logger LOGGER = LogManager.getLogger(
        AssetEditStepsValidator.class
    );

    public boolean validateEditStep(final Class<?> stepClass) {
        if (stepClass.getAnnotation(Controller.class) == null) {
            LOGGER.warn(
                "Class {} is part of a set of asset edit steps, but is not"
                    + " annotated with {}. The class will be ignored.",
                stepClass.getName(),
                Controller.class.getName()
            );
            return false;
        }

        final Path pathAnnotation = stepClass.getAnnotation(Path.class);
        if (pathAnnotation == null) {
            LOGGER.warn(
                "Class {} is part of a set of asset edit steps, but is not "
                    + "annotated with {}. the class will be ignored.",
                stepClass.getName(),
                Path.class.getName()
            );
            return false;
        }

        final String path = pathAnnotation.value();
        if (path == null
                || !path.startsWith(MvcAssetEditSteps.PATH_PREFIX)) {
            LOGGER.warn(
                "Class {} is part of a set of asset edit steps, but the value"
                    + "of the {} annotation of the class does not start "
                    + "with {}. The class will be ignored.",
                stepClass.getName(),
                Path.class.getName(),
                MvcAssetEditSteps.PATH_PREFIX
            );
            return false;
        }

        if (stepClass.getAnnotation(MvcAssetEditStep.class) == null) {
            LOGGER.warn(
                "Class {} is part of a set of asset edit steps, but is not "
                    + "annotated with {}. The class will be ignored.",
                stepClass.getName(),
                MvcAssetEditStep.class
            );
        }

        return true;
    }

    public boolean supportsAsset(final Class<?> stepClass, final Asset asset) {
        return Optional
            .ofNullable(stepClass.getAnnotation(MvcAssetEditStep.class))
            .map(
                stepAnnotation -> asset.getClass().isAssignableFrom(
                    stepAnnotation.supportedAssetType()
                )
            )
            .orElse(false);

//        final MvcAssetEditStep stepAnnotation = stepClass.getAnnotation(
//            MvcAssetEditStep.class
//        );
//        
//        if (stepAnnotation == null) {
//            return false;
//        } else {
//            return asset.getClass().isAssignableFrom(
//                stepAnnotation.supportedAssetType());
//        }
    }

}
