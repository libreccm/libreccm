/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ResourceBundle;

/**
 * Marks a class as configuration class which is managed by the
 * {@link ConfigurationManager}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    /**
     * The name of the configuration. If left blank the simple name of the class
     * is used.
     *
     * @return Name of the configuration.
     */
    //String name() default "";
    /**
     * Points to the {@link ResourceBundle} containing the descriptions of the
     * configuration and all entries of the configuration.
     *
     * @return Fully qualified name of the {@link ResourceBundle}.
     */
    String descBundle() default "";

    /**
     * Key of the title of the description in the resource bundle provided by
     * {@link #descBundle()}.
     * 
     * @return Key of the title
     */
    String titleKey() default "";

    /**
     * Key of the description of the configuration in the resource bundle
     * provided by {@link #descBundle()}.
     *
     * @return Key of the description.
     */
    String descKey() default "";

}
