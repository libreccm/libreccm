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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * An implementation of the {@link ServletContextListener} interface for 
 * initialising the modules.
 * 
 * The real work is done by the {@link ModuleManager}. This class only delegates
 * to the {@code ModuleManager}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebListener
public class CcmModuleContextListener implements ServletContextListener {

    private static final Logger LOGGER = LogManager.getLogger(
            CcmModuleContextListener.class);

    @Inject
    private ModuleManager moduleManager;

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        LOGGER.info("ServletContext initalised. Initalising modules...");
        moduleManager.initModules();
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        LOGGER.info("ServletContext destroyed. Shutting modules down...");
        moduleManager.shutdownModules();
    }

}
