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
package org.libreccm.modules;

import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.libreccm.web.ApplicationType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Annotation for describing some meta data of a module.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    /**
     * Name of the module, overriding the name provided by the pom.xml and the
     * module info file of the module.
     *
     * @return The name of the module, overriding the value provided by the
     *         module-info file.
     */
    String name() default "";

    /**
     * Package name of resources of the package like DB migrations, overriding
     * default value constructed from the group id and the artifact id of the
     * module.
     *
     * @return The package name for resources of the module.
     */
    String packageName() default "";

    /**
     * The version of module, overriding the value provided by the module info
     * file of the module.
     *
     * @return The version of the module.
     */
    String version() default "";

    /**
     * Modules required by the annotated module.
     *
     * @return An array of the dependencies of the module.
     */
    RequiredModule[] requiredModules() default {};

    /**
     * ApplicationType types provided by the annotated module.
     *
     * @return An array containing the type descriptions for all application
     *         types provided by the annotated module.
     */
    ApplicationType[] applicationTypes() default {};

    /**
     * Configuration classes provided by the module.
     *
     * @return An array containing all configuration classes provided by the
     *         module.
     *
     * @see Configuration
     * @see ConfigurationManager
     */
    Class<?>[] configurations() default {};

    /**
     * Components for use in {@link PageModel}s provided by the annotated
     * module.
     *
     * @return An array containing all {@link ComponentModel}s provided by the
     *         annotated module.
     */
    PageModelComponentModel[] pageModelComponentModels() default {};

}
