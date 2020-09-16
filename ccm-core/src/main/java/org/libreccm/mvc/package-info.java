/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
/**
 * The classes in this package integrate LibreCCM with Jakarta EE MVC and its
 * reference implementation Eclipse Krazo.
 *
 * And the the moment the ViewEngines for Facelets and Freemarker are supported.
 * The integration allows it to load templates for these ViewEngines either from
 * the default locations or from a theme. If the path of template starts with
 * {@code @themes/} or {@code /@themes/} the integration will delegate loading of
 * the template to {@link Themes} of the theme. The path must follow the following
 * pattern: 
 * 
 * <pre>
 *     @themes/$themeName/$version/$pathToTemplate
 * </pre>
 * 
 * Where {@code $themeName} is the name of the theme, {@code $version} is the
 * version of the theme (either {@code live} or {@code draft} and 
 * {@code $pathToFile} is the path the template.
 * 
 * If the path does not start with {@code @themes/} or {@code /@themes/} the
 * template will be loaded from the default location(s) used by Jakarta EE
 * MVC/Eclipse Krazo.
 *
 * @see https://www.mvc-spec.org/
 */
package org.libreccm.mvc;
