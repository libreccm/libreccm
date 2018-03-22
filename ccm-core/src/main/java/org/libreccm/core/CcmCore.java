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

import com.arsdigita.pagemodel.layout.ui.FlexLayoutComponentForm;
import com.arsdigita.ui.admin.AdminServlet;
import com.arsdigita.ui.admin.AdminUiConstants;
import com.arsdigita.ui.admin.applications.AdminApplicationCreator;
import com.arsdigita.ui.admin.applications.AdminApplicationSetup;
import com.arsdigita.ui.login.LoginApplicationCreator;
import com.arsdigita.ui.login.LoginApplicationSetup;
import com.arsdigita.ui.login.LoginConstants;
import com.arsdigita.ui.login.LoginServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.admin.ui.AdminJsfApplicationCreator;
import org.libreccm.admin.ui.AdminJsfApplicationSetup;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.InitEvent;
import org.libreccm.modules.InstallEvent;
import org.libreccm.modules.Module;
import org.libreccm.modules.ShutdownEvent;
import org.libreccm.modules.UnInstallEvent;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.libreccm.pagemodel.layout.FlexLayout;

import org.libreccm.security.SystemUsersSetup;
import org.libreccm.web.ApplicationType;

import javax.persistence.EntityManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Module(applicationTypes = {
    @ApplicationType(name = LoginConstants.LOGIN_APP_TYPE,
                     descBundle = "com.arsdigita.ui.login.LoginResources",
                     singleton = true,
                     creator = LoginApplicationCreator.class,
                     servlet = LoginServlet.class)
    ,
    @ApplicationType(name = AdminUiConstants.ADMIN_APP_TYPE,
                     descBundle = "com.arsdigita.ui.admin.AdminResources",
                     singleton = true,
                     creator = AdminApplicationCreator.class,
                     servlet = AdminServlet.class)
    ,
    @ApplicationType(name = "org.libreccm.ui.admin.AdminFaces",
                     descBundle = "com.arsdigita.ui.admin.AdminResources",
                     singleton = true,
                     creator = AdminJsfApplicationCreator.class,
                     servletPath = "/admin-jsf/admin.xhtml")},
        pageModelComponentModels = {
            @PageModelComponentModel(
                modelClass = FlexLayout.class,
                editor = FlexLayoutComponentForm.class,
                descBundle = ADMIN_BUNDLE,
                titleKey = "ui.pagemodel.components.flexlayout.title",
                descKey = "ui.pagemodel.components.flexlayout.desc"
            )
        },
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
            org.libreccm.files.CcmFilesConfiguration.class,
            org.libreccm.search.SearchConfig.class,
            org.libreccm.security.EmailTemplates.class,
            org.libreccm.security.OneTimeAuthConfig.class,})
public class CcmCore implements CcmModule {

    private static final Logger LOGGER = LogManager.getLogger(CcmCore.class);

    @Override
    public void install(final InstallEvent event) {
        LOGGER.info("Setting up system users...");
        final SystemUsersSetup systemUsersSetup = new SystemUsersSetup(
            event);
        systemUsersSetup.setupSystemUsers();

        LOGGER.info("Setting up admin application (/ccm/admin/)...");
        final AdminApplicationSetup adminSetup
                                        = new AdminApplicationSetup(event);
        adminSetup.setup();

        LOGGER.info("Setting up admin-jsf application (/ccm/admin-jsf/)...");
        final AdminJsfApplicationSetup adminJsfSetup
                                           = new AdminJsfApplicationSetup(event);
        adminJsfSetup.setup();

        LOGGER.info("Setting up login application...");
        final LoginApplicationSetup loginSetup
                                        = new LoginApplicationSetup(event);
        loginSetup.setup();

        LOGGER.info("Importing category domains from bundle (if any)...");
        final Properties integrationProps = new Properties();
        try (final InputStream inputStream = getClass().getResourceAsStream(
            CoreConstants.INTEGRATION_PROPS)) {
            if (inputStream == null) {
                LOGGER.warn("Integration properties file was not found.");
            } else {
                integrationProps.load(inputStream);
            }
        } catch (IOException ex) {
            LOGGER.warn("Failed to read integration properties. "
                            + "Using empty proeprties.");
        }

        if (integrationProps.containsKey("bundle.domains")) {
            importDomains(integrationProps.getProperty("bundle.domains"),
                          event.getEntityManager());
        }

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

    private void importDomains(final String domainFiles,
                               final EntityManager entityManager) {
        final String[] tokens = domainFiles.split(",");

        for (final String token : tokens) {
            importDomain(token, entityManager);
        }
    }

    private void importDomain(final String domainFile,
                              final EntityManager entityManager) {
        // ToDo Will be implemented when general importer is ready
//        LOGGER.info("Importing category domain from {}...", domainFile);
//        try (final InputStream inputStream = getClass().getResourceAsStream(
//                domainFile)) {
//            if (inputStream == null) {
//                LOGGER.warn("Category domain file {} was not found. Ignoring.",
//                            domainFile);
//            } else {
//                final Domain domain = JAXB.unmarshal(inputStream, Domain.class);
//                entityManager.persist(domain);
//            }
//        } catch (IOException ex) {
//            LOGGER.warn("Failed to load category domain file {}. "
//                                + "Domain will not be imported.",
//                        domainFile);
//        }
    }

}
