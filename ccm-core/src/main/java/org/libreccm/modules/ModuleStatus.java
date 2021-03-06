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

/**
 * Enumeration describing the status of a module.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public enum ModuleStatus {

    /**
     * Marks a module as new. This state should be set by the DB migration
     */
    NEW,
    /**
     * Marks a module as installed and ready to use.
     */
    INSTALLED,
    /**
     * Marks a module be scheduled for uninstall. When the application is
     * shutdown the module is removed from the database. Before starting the
     * applications again the module classes (JAR) should be removed. Otherwise
     * the module will be reinstalled.
     */
    UNINSTALL

}
