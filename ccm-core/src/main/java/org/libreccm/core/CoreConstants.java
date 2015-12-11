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
package org.libreccm.core;

/**
 * Some constants for the Core package
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class CoreConstants {

    /**
     * XML namespace used for XML created by and from classes in the ccm-core
     * module.
     */
    public static final String CORE_XML_NS = "http://core.libreccm.org";

    /**
     * Name of the database schema (namespace) for the tables storing the
     * entities from the ccm-core module.
     */
    public static final String DB_SCHEMA = "CCM_CORE";

    /**
     * String used as display name for the virtual <i>Access denied</i> objects
     * in the security API.
     */
    public static final String ACCESS_DENIED = "Access denied";

    private CoreConstants() {
        //Nothing
    }

}
