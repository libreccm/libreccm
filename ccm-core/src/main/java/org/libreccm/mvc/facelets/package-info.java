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
 * Integration of the LibreCCM theme system with the Facelet ViewEngine of
 * Eclipse Krazo. The integration allows it to load Facelets either from
 * a theme using a {@link Themes} or from the default locations. To enable the
 * integration the following snippet has to be added to the
 * {@code faces-config.xml}:
 * <pre>
 *     &lt;application&gt;
 *         &tl;resource-handler&gt;org.libreccm.ui.CcmFaceletsResourceHandler&lt;/resource-handler&gt;
 *     &lt;/application&gt;
 * </pre>
 *
 */
package org.libreccm.mvc.facelets;
