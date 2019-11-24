/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.profilesite;

import org.libreccm.modules.CcmModule;
import org.libreccm.modules.InitEvent;
import org.libreccm.modules.InstallEvent;
import org.libreccm.modules.RequiredModule;
import org.libreccm.modules.ShutdownEvent;
import org.libreccm.modules.UnInstallEvent;
import org.librecms.contenttypes.ContentTypes;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.libreccm.modules.Module(
    requiredModules = {
        @RequiredModule(module = org.librecms.Cms.class)
    }
)
@ContentTypes({
    ProfileSiteItem.class
})
public class ProfileSite implements CcmModule {

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
