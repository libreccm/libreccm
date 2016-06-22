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
package org.libreccm.shortcuts;

import org.libreccm.core.CcmCore;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.InitEvent;
import org.libreccm.modules.InstallEvent;
import org.libreccm.modules.Module;
import org.libreccm.modules.RequiredModule;
import org.libreccm.modules.ShutdownEvent;
import org.libreccm.modules.UnInstallEvent;
import org.libreccm.shortcuts.ui.ShortcutsSettingsPane;
import org.libreccm.web.ApplicationType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Module(
        requiredModules = {
            @RequiredModule(module = CcmCore.class)
        },
        applicationTypes = {
            @ApplicationType(name = ShortcutsConstants.SHORTCUTS_APP_TYPE,
                             descBundle = ShortcutsConstants.SHORTCUTS_BUNDLE,
                             singleton = true,
                             settingsPane = ShortcutsSettingsPane.class,
                             creator = ShortcutsApplicationCreator.class)})
public class Shortcuts implements CcmModule {

    @Override
    public void install(final InstallEvent event) {
        final ShortcutsSetup setup = new ShortcutsSetup(event);
        setup.setup();
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
