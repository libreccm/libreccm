/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.webdav;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.webdav.xml.WebDavContextResolver;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.xml.bind.JAXBException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationPath("/DAV/themes")
public class WebDAV extends Application {

    private static final Logger LOGGER = LogManager.getLogger(WebDAV.class);
    
    @Override
    public Set<Class<?>> getClasses() {

        final Set<Class<?>> classes = new HashSet<>();
        classes.add(ThemeFiles.class);

        return classes;
    }

    @Override
    public Set<Object> getSingletons() {

        LOGGER.warn("Adding singletons...");
        
        final HashSet<Object> singletons = new HashSet<>();
        try {
            singletons.add(new WebDavContextResolver());
        } catch (JAXBException ex) {
            throw new UnexpectedErrorException(ex);
        }

        LOGGER.warn("Added singletons");
        return singletons;
    }

}
