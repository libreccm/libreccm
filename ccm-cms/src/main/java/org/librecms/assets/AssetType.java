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
package org.librecms.assets;

import com.arsdigita.cms.ui.assets.AssetForm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to describe an asset type. It provides the class name of edit/create
 * form for the asset. Can also be used to customise the keys and bundles used
 * to retrieve the label and the description of an asset type.
 *
 * The only required parameter is {@link #assetForm()} which provides the form
 * for editing and creating assets of the annotated type. The other parameters
 * can be left empty. If left empty a internal default value will be used.
 *
 * This annotation should only be used on subclasses of
 * {@link org.librecms.contentsection.Asset}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AssetType {

    /**
     * The form for editing and creating an asset of the annotated class. This
     * parameter is required.
     *
     * @return The form for editing and creating assets of the annotated sub
     *         class {@link org.librecms.contentsection.Asset}.
     */
    Class<? extends AssetForm> assetForm();

    /**
     * The key for the localised label of the asset type. If not set the default
     * value {@code label} is used.
     *
     * @return The key for the localised label of the asset type.
     */
    String labelKey() default "";

    /**
     * The bundle which provides the label of the asset type. If not set the
     * default value is used. Default is the fully qualified class name of the
     * annotated class with suffix {@code Bundle}. For example the default
     * bundle for the asset type {@link org.librecms.assets.Image} is
     * {@code org.librecms.assets.ImageBundle}.
     *
     * @return The fully qualified name of the bundle providing the label for
     *         the asset type.
     */
    String labelBundle() default "";

    /**
     * The key for the description of the asset type in the description bundle.
     * Default value is {@code descripion}.
     *
     * @return The key for the description of the asset type.
     */
    String descriptionKey() default "";

    /**
     * The bundle which provides the description of the asset type. If not set
     * the default value is used. Default is the fully qualified class name of
     * the annotated class with the suffix {@code Bundle}. For example the
     * default bundle for the asset type {@link org.librecms.assets.Image} is
     * {@code org.librecms.assets.ImageBundle}.
     *
     * @return The fully qualified name of the bundle providing the description
     * of the asset type.
     */
    String descriptionBundle() default "";

}
