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

import java.util.Collections;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Describes a configuration. Useful for generating user interfaces.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class ConfigurationInfo {

    private final static Logger LOGGER = LogManager.getLogger(
        ConfigurationInfo.class);

    /**
     * The fully qualified name of the configuration.
     */
    private String name;

    /**
     * The resource bundle containing the description of the configuration and
     * its settings.
     */
    private String descBundle;

    /**
     * Key for the localised title of the configuration class in the resource
     * bundle.
     */
    private String titleKey;

    /**
     * The key for the description of the configuration in the resource bundle.
     */
    private String descKey;

    /**
     * A navigable map containing a {@link SettingInfo} object for each setting
     * of the configuration.
     */
    private NavigableMap<String, SettingInfo> settings;

    public ConfigurationInfo() {
        this.settings = new TreeMap<>();
    }

    public String getName() {
        return name;
    }

    void setName(final String name) {
        this.name = name;
    }

    public String getDescBundle() {
        return descBundle;
    }

    void setDescBundle(final String descBundle) {
        this.descBundle = descBundle;
    }

    public ResourceBundle getDescriptionBundle(final Locale locale) {
        try {
            return ResourceBundle.getBundle(descBundle);
        } catch (MissingResourceException ex) {
            LOGGER.warn(
                "Failed to find ResourceBundle for base name '{}' and "
                    + "locale '{}'.",
                descBundle, locale);
            return null;
        }
    }

    public String getTitleKey() {
        return titleKey;
    }

    void setTitleKey(final String titleKey) {
        this.titleKey = titleKey;
    }

    public String getDescKey() {
        return descKey;
    }

    void setDescKey(final String descKey) {
        this.descKey = descKey;
    }

    public String getTitle(final Locale locale) {
        final ResourceBundle bundle = getDescriptionBundle(locale);

        if (bundle == null) {
            return name;
        } else {
            try {
                return bundle.getString(titleKey);
            } catch (MissingResourceException ex) {
                LOGGER.warn("Can't find resource for bundle '{}', "
                                + "key '{}' and locale '{}'.",
                            descBundle,
                            titleKey,
                            locale);
                return name;
            }
        }
    }

    public String getDescription(final Locale locale) {
        final ResourceBundle bundle = getDescriptionBundle(locale);

        if (bundle == null) {
            return "";
        } else {
            try {
                return bundle.getString(descKey);
            } catch (MissingResourceException ex) {
                LOGGER.warn("Can't find resource for bundle '{}', "
                                + "key '{}' and locale '{}'.",
                            descBundle,
                            descKey,
                            locale);
                return "";
            }
        }
    }

    public NavigableMap<String, SettingInfo> getSettings() {
        return Collections.unmodifiableNavigableMap(settings);
    }

    void setSettings(final NavigableMap<String, SettingInfo> settings) {
        this.settings = settings;
    }

    void addSetting(final SettingInfo info) {
        settings.put(info.getName(), info);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(name);
        hash = 59 * hash + Objects.hashCode(descBundle);
        hash = 59 * hash + Objects.hashCode(titleKey);
        hash = 59 * hash + Objects.hashCode(descKey);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ConfigurationInfo)) {
            return false;
        }
        final ConfigurationInfo other = (ConfigurationInfo) obj;
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(descBundle, other.getDescBundle())) {
            return false;
        }

        if (!Objects.equals(titleKey, other.getTitleKey())) {
            return false;
        }

        return Objects.equals(descKey, other.getDescKey());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "name = \"%s\", "
                                 + "descBundle = \"%s\", "
                                 + "titleKey = \"%s\", "
                                 + "descKey = \"%s\""
                                 + " }",
                             super.toString(),
                             name,
                             descBundle,
                             titleKey,
                             descKey);
    }

}
