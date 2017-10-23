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
/**
 * <p>
 * The {@code pagemodel} packages provides an abstraction layer between the data
 * model of page and its generating components. This layer replaces the JSP
 * templates which were used in previous versions of CCM for this purpose.
 * </p>
 * <p>
 * The Page Model system allows it to specify which components are used on a
 * page and therefore which information is displayed on a page. It is intended
 * to be used for public pages (like the item page of a content item category
 * page in ccm-cms module. The Page Model system uses data containers which are
 * read by a renderer class.
 * </p>
 * <p>
 * The central interface is the {@link org.libreccm.pagemodel.PageRenderer}
 * interface. An implementation of this interface will take a
 * {@link org.libreccm.pagemodel.PageModel} and process it and create a page
 * from it using the view technology supported by the implementation.
 * {@code PageRenderer}s are CDI beans. Implementations can be retrieved using
 * the
 * {@link org.libreccm.pagemodel.PageRendererManager#findPageRenderer(String, Class)}
 * method.
 * </p>
 */
package org.libreccm.pagemodel;
