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

import com.arsdigita.ui.admin.applications.AdminApplicationCreator;
import com.arsdigita.ui.admin.AdminServlet;
import com.arsdigita.ui.admin.AdminUiConstants;
import com.arsdigita.ui.admin.applications.AdminApplicationSetup;
import com.arsdigita.ui.login.LoginApplicationCreator;
import com.arsdigita.ui.login.LoginServlet;
import com.arsdigita.ui.login.LoginApplicationSetup;
import com.arsdigita.ui.login.LoginConstants;

import org.libreccm.modules.CcmModule;
import org.libreccm.modules.InitEvent;
import org.libreccm.modules.InstallEvent;
import org.libreccm.modules.Module;
import org.libreccm.modules.ShutdownEvent;
import org.libreccm.modules.UnInstallEvent;
import org.libreccm.security.SystemUsersSetup;

import org.libreccm.web.ApplicationType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Module(applicationTypes = {
    @ApplicationType(name = LoginConstants.LOGIN_APP_TYPE,
                     descBundle = "com.arsdigita.ui.login.LoginResources",
                     singleton = true,
                     creator = LoginApplicationCreator.class,
                     servlet = LoginServlet.class),
    @ApplicationType(name = AdminUiConstants.ADMIN_APP_TYPE,
                     descBundle = "com.arsdigita.ui.admin.AdminResources",
                     singleton = true,
                     creator = AdminApplicationCreator.class,
                     servlet = AdminServlet.class)},
        configurations = {
            com.arsdigita.bebop.BebopConfig.class,
            com.arsdigita.dispatcher.DispatcherConfig.class,
            com.arsdigita.globalization.GlobalizationConfig.class,
            com.arsdigita.kernel.KernelConfig.class,
            com.arsdigita.kernel.security.SecurityConfig.class,
            com.arsdigita.mail.MailConfig.class,
            com.arsdigita.notification.NotificationConfig.class,
            com.arsdigita.templating.TemplatingConfig.class,
            com.arsdigita.ui.UIConfig.class,
            com.arsdigita.web.WebConfig.class,
            com.arsdigita.workflow.simple.WorkflowConfig.class,
            com.arsdigita.xml.XmlConfig.class,
            com.arsdigita.xml.formatters.DateFormatterConfig.class,
            org.libreccm.configuration.ExampleConfiguration.class,
            org.libreccm.security.EmailTemplates.class,
            org.libreccm.security.OneTimeAuthConfig.class,
        })
public class CcmCore implements CcmModule {

    @Override
    public void install(final InstallEvent event) {
        final SystemUsersSetup systemUsersSetup = new SystemUsersSetup(
            event);
        systemUsersSetup.setupSystemUsers();

        final AdminApplicationSetup adminSetup
                                    = new AdminApplicationSetup(event);
        adminSetup.setup();

        final LoginApplicationSetup loginSetup
                                    = new LoginApplicationSetup(event);
        loginSetup.setup();
        
        // Load category domains from bundle/classpath
        // File format: JAXB (but Jackson for reading the XML)
    }

    @Override
    public void init(final InitEvent event) {
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
