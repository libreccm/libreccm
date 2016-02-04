/*
 * Copyright (c) 2014 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Provides the system name of the CCM Spin off (eg aplaws or ScientificCMS) and
 * the version number. It's primary use is to provide the theme engine with that
 * information for display. The data displayed is stored in the
 * /WEB-INF/systeminformation.properties, which is usually provided by the
 * bundle. The ccm-sci-bundle for example provides this file, which can be found
 * in {@code ccm-sci-bundle/web/WEB-INF} directory. At the moment it is
 * necessary to update this (these) file(s) manually.
 *
 * A {@code systeminformations.properties} should contain at least these three
 * properties:
 * <dl>
 * <dt>version</dt>
 * <dd>The version of the specific CCM distribution.</dd>
 * <dt>appname</dt>
 * <dd>The name of the CCM distribution, for example
 * <strong>ScientificCMS</strong>
 * <dt>apphomepage</dt>
 * <dd>
 * The URL of the website of the CCM distribution, for example
 * {@code http://www.scientificcms.org}
 * </dd>
 * </dl>
 *
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SystemInformation {

    /**
     * Map containing all informations provided by the
     * {@code systeminformation.properties} file.
     */
    private final Map<String, String> sysInfo = new HashMap<String, String>();
    /**
     * The one and only instance of this class
     */
    private final static SystemInformation INSTANCE = new SystemInformation();

    /**
     * The constructor takes care of loading the data from the properties file
     * and placing them into {@code HashMap}.
     */
    public SystemInformation() {

        final Properties properties = new Properties();
        try {
            final InputStream stream = getClass().getResourceAsStream(
                    "WEB-INF/systeminformation.properties");
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

        for (String key : properties.stringPropertyNames()) {
            sysInfo.put(key, properties.getProperty(key));
        }

    }

    /**
     * @return The instance of this class.
     */
    public static SystemInformation getInstance() {
        return INSTANCE;
    }

    /*
     * Get system informations by key.
     *
     * @param key Key for the map
     *
     * @return value for key
     *
     * @throws IllegalArgumentException if key is null or empty
     */
    final public String get(final String key) throws IllegalArgumentException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException(
                    "Parameter key must not be null or empty.");
        }
        return sysInfo.get(key);
    }

    /**
     * Get iterator of this map.
     *
     * @return iterator of map
     */
    final public Iterator<Map.Entry<String, String>> iterator() {
        return sysInfo.entrySet().iterator();
    }

    /**
     *
     * @return
     */
    final public boolean isEmpty() {
        return sysInfo.isEmpty();

    }
}
