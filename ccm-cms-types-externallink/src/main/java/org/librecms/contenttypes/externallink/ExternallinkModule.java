/**
 * Add your license here, for example LGPL
 */
package org.librecms.contenttypes.externallink;

import org.libreccm.modules.CcmModule;
import org.libreccm.modules.InitEvent;
import org.libreccm.modules.InstallEvent;
import org.libreccm.modules.Module;
import org.libreccm.modules.RequiredModule;
import org.libreccm.modules.ShutdownEvent;
import org.libreccm.modules.UnInstallEvent;

@Module(packageName = "org.librecms.contenttypes.externallink",
        requiredModules = {
            @RequiredModule(module = org.libreccm.core.CcmCore.class)})
public class ExternallinkModule implements CcmModule {

    @Override
    public void install(final InstallEvent event) {
        //ToDo Create initial data for the module if neccessary
    }

    @Override
    public void init(final InitEvent event) {
        //ToDo Add initialisation logic necessary for your module
    }

    @Override
    public void shutdown(final ShutdownEvent event) {
        //ToDo Add shutdown logic if necessary
    }

    @Override
    public void uninstall(final UnInstallEvent event) {
        //ToDo Remove module data
    }

}
