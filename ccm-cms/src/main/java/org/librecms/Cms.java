/**
 * Add your license here, for example LGPL
 */
package org.librecms;

import org.libreccm.modules.CcmModule;
import org.libreccm.modules.InitEvent;
import org.libreccm.modules.InstallEvent;
import org.libreccm.modules.Module;
import org.libreccm.modules.RequiredModule;
import org.libreccm.modules.ShutdownEvent;
import org.libreccm.modules.UnInstallEvent;

@Module(packageName = "org.libreccm.cms",
        requiredModules = {
            @RequiredModule(module = org.libreccm.core.CcmCore.class)
        }
)
public class Cms implements CcmModule {

    @Override
    public void install(final InstallEvent event) {
        //ToDo Create initial data for the module if neccessary
        
        // Create initial content section
        // If a list of sections is provided in the integration properties
        // use that list, otherwise create a content section named 'info'.
        
        // Map given domains to content section(s)
        
        // Also create the following roles for each content section and assign
        // the permissions
        // 
        // * Alert Recipient
        // * Author         : Categorize Items, Create New Items, Edit Items, View Published Items, Preview Items
        // * Editor         : Categorize Items, Create New Items, Edit Items, Approve Items, Delete Items, View Published Items, Preview Items
        // * Manager        : Administer Roles, Administer Workflow, Administer Lifecycles, Administer Categories, Administer Content Types, Categorize Items, Create New Items, Edit Items, Approve Items, Publish Items, Delete Items, View Published Items, Preview Items
        // * Publisher      : Categorize Items, Create New Items, Edit Items, Approve Items, Publish Items, Delete Items, View Published Items, Preview Items
        // * [Thrusted User]: Categorize Items, Create New Items, Edit Items, Apply Alternate Workflows, Approve Items, Publish Items, Delete Items, View Published Items, Preview Items
        // * Content Reader: View Published Items
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
