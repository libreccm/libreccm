/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
 * This packages provides several classes for managing sites or virtual hosts
 * in CCM. In some way this replaces the old Subsite module which was available
 * for previous versions. On the other hand there are several differences.
 *
 * The most important difference is that the classes in this package do not hook
 * into the request/response cycle
 * like the old Subsite module. Instead a subclass of {@link CcmApplication}
 * class ({@link SiteAwareApplication}) is provided. The application itself is
 * responsible for interpreting the site specific parts.
 */
package org.libreccm.sites;
