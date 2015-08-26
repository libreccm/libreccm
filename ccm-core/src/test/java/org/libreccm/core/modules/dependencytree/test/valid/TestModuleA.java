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
package org.libreccm.core.modules.dependencytree.test.valid;

import org.libreccm.core.modules.CcmModule;
import org.libreccm.core.modules.InitEvent;
import org.libreccm.core.modules.InstallEvent;
import org.libreccm.core.modules.Module;
import org.libreccm.core.modules.RequiredModule;
import org.libreccm.core.modules.ShutdownEvent;
import org.libreccm.core.modules.UnInstallEvent;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Module(requiredModules = {
    @RequiredModule(module = TestModuleRoot.class, minVersion = "1.0.0")})
public class TestModuleA implements CcmModule {

    @Override
    public void install(final InstallEvent event) {
        //Nothing
    }

    @Override
    public void init(final InitEvent event) {
        //Nothing
    }

    @Override
    public void shutdown(final ShutdownEvent event) {
        //Nothing
    }

    @Override
    public void uninstall(final UnInstallEvent event) {
        //Nothing
    }

}
