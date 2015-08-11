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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface Module {

    /**
     * Called by the {@link ModuleManager} after the database tables for the
     * module have been created. Use this method to create initial or example
     * data.
     */
    void prepare();

    /**
     * Called by the {@link ModuleManager} when a module is removed from the
     * installation. If necessary clean up the data of the module in the
     * implementation of this method.
     */
    void uninstall();

    /**
     * Called each time the CCM application is started. Use an implementation of
     * this method for creating static instances or for integrity checking.
     */
    void init();

    /**
     * Called each time the CCM application stops.
     */
    void shutdown();

}
