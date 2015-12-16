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
package org.libreccm.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.web.CcmApplication;

import java.util.Objects;

/**
 * Base class for application instance specific configurations.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(
        ApplicationConfiguration.class);

    /**
     * The primary URL identifying the application instance for which this
     * configuration stores settings.
     */
    @Setting
    private String applicationInstance;

    /**
     * The fully qualified name of the application class.
     */
    @Setting
    private String applicationClass;

    public String getApplicationInstance() {
        return applicationInstance;
    }

    public void setApplicationInstance(final String applicationInstance) {
        this.applicationInstance = applicationInstance;
    }

    public Class<CcmApplication> getApplicationClass() {
        try {
            @SuppressWarnings("unchecked")
            final Class<CcmApplication> clazz = (Class<CcmApplication>) Class
                .forName(applicationClass);
            return clazz;
        } catch (ClassNotFoundException ex) {
            LOGGER.warn(String.format(
                "Class '%s' for ApplicationConfiguration was not found.",
                applicationClass),
                        ex);
            return null;
        }
    }

    public void setApplicationClass(final Class<CcmApplication> clazz) {
        applicationClass = clazz.getName();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(applicationInstance);
        hash = 79 * hash + Objects.hashCode(applicationClass);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ApplicationConfiguration)) {
            return false;
        }

        final ApplicationConfiguration other = (ApplicationConfiguration) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(applicationInstance, other.getApplicationInstance())) {
            return false;
        }
        if (Objects.equals(applicationClass, other.getApplicationClass())) {
        } else {
            return false;
        }
        return true;
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ApplicationConfiguration;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "applicationInstance = \"%s\", "
                                 + "applicationClass = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             applicationInstance,
                             applicationClass,
                             data);
    }

}
