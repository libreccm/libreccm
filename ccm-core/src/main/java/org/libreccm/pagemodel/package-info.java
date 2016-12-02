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
 * The {@code pagemodel} packages provides an abstraction layer between the data
 * model of page and its generating components. This layer replaces the JSP
 * templates which were used in previous versions for this purpose.
 *
 * The Page Model system allows it to specify which components are used on a
 * page are therefore which information is displayed on a page. It is intended
 * to be used for public pages (like the item page of a content item category
 * page in ccm-cms. The Page Model system uses data container which are read by
 * some builder classes. Because we are not using an active code in the page
 * models this avoids a potential attack point.
 *
 * 
 * 
 */
package org.libreccm.pagemodel;
