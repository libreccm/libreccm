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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * Used to annotate methods of an authoring step processing requests for a
 * specific sub path. The annoated MUST have the following signature:
 *
 * <pre>
 * actionMethod(String parameterPath, Map<String, String[]> parameters)
 * </pre>
 *
 * The {@link DocumentController} will put the rest of the path behind the path
 * fragments for the action in the {@code parameterPath} parameter. The
 * parameter {@code parameters} will contain the list of parameters for the
 * request as returned by {@link HttpServletRequest#getParameterMap() }.
 *
 * The return type of the annotated method MUST be {@code String} if the action
 * is showing a template ({@link #produces()} is set to
 * {@link MediaType#TEXT_HTML}), or any object type is {@link #produces() } is
 * set the {@link MediaType#APPLICATION_JSON}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MvcAuthoringAction {

    /**
     * The HTTP method used to invoke the action.
     *
     * @return The HTTP method used to invoke the action.
     */
    MvcAuthoringActionMethod method() default MvcAuthoringActionMethod.GET;

    /**
     * The path fragment for invoking the action. The value of this parameter is
     * added to the path of the authoring step. Any path fragments after the
     * value are provided to the method in the {@code parameterPath} parameter.
     *
     * @return The path fragment for invoking the action.
     */
    String path();

    /**
     * The type of data the the action produces. If the action shows a view the
     * value should be {@link MediaType#TEXT_HTML} (the default). If the action
     * produces JSON data the value MUST BE {@link MediaType#APPLICATION_JSON}.
     *
     * @return The type of data the the action produces.
     */
    String produces() default MediaType.TEXT_HTML;

}
