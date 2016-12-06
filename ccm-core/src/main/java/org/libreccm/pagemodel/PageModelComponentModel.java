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
package org.libreccm.pagemodel;

import com.arsdigita.bebop.Form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ResourceBundle;

/**
 * Used in the description of {@link CcmModule} to specify which
 * {@link ComponentModel}s are provided by an module.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageModelComponentModel {

    /**
     * Fully qualified name of a resource bundle providing the title and
     * description of the {@link ComponentModel}.
     *
     * @return The fully qualified name of the {@link ResourceBundle} which
     * provides the title and description of the {@link ComponentModel}.
     */
    String descBundle() default "";

    /**
     * Key for the title of the {@link ComponentModel} in the
     * {@link ResourceBundle} specified by {@link #descBundle()}.
     *
     * @return The key for the title of the {@link ComponentModel}.
     */
    String titleKey() default "component_model_title";

    /**
     * Key for the description of the {@link ComponentModel} in the
     * {@link ResourceBundle} specified by {@link #descBundle()}.
     *
     * @return The key for the description of the {@link ComponentModel}.
     */
    String descKey() default "component_model_desc";

    /**
     * The class which provides the {@link ComponentModel}.
     *
     * @return The class which provides the {@link ComponentModel}.
     */
    Class<? extends ComponentModel> modelClass();

    /**
     * A (Bebop) form for editing the properties of an instance of the
     * {@link ComponentModel}.
     *
     * @return A (Bebop) form for editing the {@code ComponentModel}.
     */
    Class<? extends Form> editor();

}
