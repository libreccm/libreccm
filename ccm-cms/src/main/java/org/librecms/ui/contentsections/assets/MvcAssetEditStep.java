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

import org.librecms.contentsection.Asset;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Metadata of an edit step for assets.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MvcAssetEditStep {

    /**
     * The name of the resource bundle providing the localized values for
     * {@link #labelKey} and {@link descriptionKey}.
     *
     * @return The resource bundle providing the localized labelKey and
     *         descriptionKey.
     */
    String bundle();

    /**
     * The key for the localized description of the step.
     *
     * @return The key for the localized description of the step.
     */
    String descriptionKey();

    /**
     * The key for the localized label of the authoring step..
     *
     * @return The key for the localized label of the authoring step...
     */
    String labelKey();

    /**
     * Edit steps only support a specific type, and all subtypes.
     *
     * @return The asset type supported by the edit step.
     */

    Class<? extends Asset> supportedAssetType();

}
