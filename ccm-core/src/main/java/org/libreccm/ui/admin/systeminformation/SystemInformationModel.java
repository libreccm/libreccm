/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.systeminformation;

import com.arsdigita.util.UncheckedWrapperException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model providing the date for the system information page.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SystemInformationModel")
public class SystemInformationModel {

    /**
     * Get some data about this LibreCCM installation, eg. version, application
     * name, and homepage.
     *
     * @return The information about this CCM installation.
     */
    public Map<String, String> getCcmSystemInformation() {
        final Properties properties = new Properties();
        try {
            final InputStream stream = getClass().getResourceAsStream(
                "systeminformation.properties");
            if (stream == null) {
                properties.put("version", "");
                properties.put("appname", "LibreCCM");
                properties.put("apphomepage", "http://www.libreccm.org");
            } else {
                properties.load(stream);
            }
//            properties.load(getClass().getResourceAsStream(
//                    "WEB-INF/systeminformation.properties"));
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        final Map<String, String> sysInfo = new HashMap<>();

        for (String key : properties.stringPropertyNames()) {
            sysInfo.put(key, properties.getProperty(key));
        }

        return sysInfo;
    }

    /**
     * Get the Java System Properties from the runtime environment.
     * 
     * @return The Java System Properties of the runtime environment.
     */
    public Map<String, String> getJavaSystemProperties() {
        final Properties systemProperties = System.getProperties();
        final Map<String, String> result = new HashMap<>();
        for (final Object key : systemProperties.keySet()) {
            result.put(
                (String) key, systemProperties.getProperty((String) key)
            );
        }
        return result;
    }

}
