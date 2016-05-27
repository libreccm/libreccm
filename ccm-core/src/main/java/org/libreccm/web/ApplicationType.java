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
package org.libreccm.web;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationType {

    /**
     * The name of the application type.
     *
     * @return
     */
    String name();

    /**
     * (Optional) Fully qualified name of a resource bundle containing a
     * localised descKey of the application type. If not provided the
     * {@link ApplicationManager} will use the default value which is the name
     * of the descKey provided by {@link #name()} concatenated with
     * {@code Description}. For example for an application with the name
     * {@code org.example.ExampleApp} the default descKey bundle is
     * {@code org.example.ExampleAppDescription}.
     *
     * @return
     */
    String descBundle() default "";

    /**
     * The (optional) key for the description of the application in its resource
     * bundle. Defaults to {@code application_title}
     *
     * @return
     */
    String descKey() default "application_title";

    /**
     * The application type class. Default is {@link CcmApplication}. Most
     * application types will no need to extend these class and can leave the
     * default has it is.
     *
     * @return
     */
    Class<?> applicationClass() default CcmApplication.class;

    /**
     * Is the application type a singleton application?
     *
     * @return
     */
    boolean singleton() default false;

    /**
     * Path to the primary Servlet of the application type. If the servlet class
     * is provided and is annotated with the {@link WebServlet} annotation the
     * path can be determined from the annotation.
     *
     * @return
     */
    String servletPath() default "";

    /**
     * The primary servlet class of the application type.
     *
     * @return
     */
    Class<? extends HttpServlet> servlet() default HttpServlet.class;

    /**
     * The implementation of the {@link ApplicationCreator} interface for the
     * application type which is used to create the objects representing the
     * application instances.
     *
     * @return
     */
    Class<? extends ApplicationCreator> creator();
    
    //Class<? extends AbstractApplicationTypePane> appTypePane default com.arsdigita.ui.admin.applications.DefaultApplicationTypePane.class;
    
    

}
