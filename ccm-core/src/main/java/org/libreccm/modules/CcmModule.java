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

import javax.persistence.EntityManager;

/**
 * This interface is implemented by all CCM module classes. It defines several
 * methods which an be used by a module the execute code when the module is
 * installed, initialised, shutdown or uninstalled. If a module does not need
 * one of the methods defined by this interface the implementation can simply be
 * left empty. The link {@link ModuleManager} provides each method with an
 * event object which provides access to the {@link EntityManager}.
 *
 * A module may also needs some metadata which is provided by the {@link Module}
 * annotation.
 *
 * When installing a module please refer also to the
 * <a href="../../../../../../module-system.html">modules pages</a> in the CCM 
 * documentation.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface CcmModule {

    /**
     * An implementation of this method is called after the module is installed.
     *
     * @param event
     */
    void install(InstallEvent event);

    void init(InitEvent event);

    void shutdown(ShutdownEvent event);

    void uninstall(UnInstallEvent event);
}
