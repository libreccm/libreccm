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
package com.arsdigita.web;

import com.arsdigita.runtime.CCMResourceManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Listener used to initialise several parameters for legacy CCM classes. This
 * class is not identical with the class with same name in old versions. It has
 * been modified so that only those things are done which necessary to get the
 * legacy classes, for instance the classes derived from
 * {@link com.arsdigita.runtime.AbstractConfig} working. Also it is not longer
 * necessary to include an entry in the {@code web.xml} for this class because
 * this can be done using {@link @WebListener} annotation in the Servlet API
 * 3.0.
 *
 * The following is the documentation from the original class which is provided
 * here for reference. The information is outdated!
 *
 * Web application lifecycle listener, used to perform central initialisation
 * tasks at CCM startup in a Servlet container / web application server,
 * expecially setting the runtime context (file locations) and (in the future)
 * the database connection.
 *
 * @author pboy
 * @author Jens Pelzetter <a href="mailto:jens.pelzetter@googlemail.com">Jens
 * Pelzetter</a>
 */
@WebListener
public class CCMApplicationContextListener implements ServletContextListener {

    public static final Logger LOGGER = LogManager.getLogger(
        CCMApplicationContextListener.class);

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        final ServletContext context = event.getServletContext();
        
        final String appBase = context.getRealPath("/");
        
        LOGGER.info(String.format("Setting base directory to %s", appBase));
        CCMResourceManager.setBaseDirectory(appBase);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        //Nothing yet
    }

}
