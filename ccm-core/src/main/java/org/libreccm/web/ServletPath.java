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

import javax.servlet.annotation.WebServlet;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides the path name of the location of the applications servlet/JSP.
 *
 * Replaces the old <code>getServletPath</code> method of the application class.
 * Applications which have their own Servlet should be annotated with this
 * annotation. The name provided here must be mapped to the Servlet by the
 * {@link WebServlet} annotation or by the <code>web.xml</code>.
 *
 * NOTE: According to Servlet API the path always starts with a leading '/' and
 * includes either the servlet name or a path to the servlet, but does not
 * include any extra path information or a query string. Returns an empty string
 * ("") is the servlet used was matched using the "/*" pattern.
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ServletPath {

    String value() default "/templates/servlet/legacy-adapter";
    
}
