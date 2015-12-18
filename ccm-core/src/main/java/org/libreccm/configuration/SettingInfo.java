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

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Describes a setting in a configuration class. This class is not designed for
 * extension.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class SettingInfo {

    /**
     * The fully qualified name of the setting.
     */
    private String name;
    /**
     * The type of the setting.
     */
    private String valueType;
    /**
     * The default value of the setting.
     */
    private String defaultValue;
    /**
     * The configuration class to which the setting belongs.
     */
    private String confClass;

    /**
     * ResourceBundle with the description of the setting.
     */
    private String descBundle;

    /**
     * Key of the description of the setting.
     */
    private String descKey;

    public String getName() {
        return name;
    }

    void setName(final String name) {
        this.name = name;
    }

    public String getValueType() {
        return valueType;
    }

    void setValueType(final String valueType) {
        this.valueType = valueType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getConfClass() {
        return confClass;
    }

    void setConfClass(final String confClass) {
        this.confClass = confClass;
    }

    public String getDescBundle() {
        return descBundle;
    }

    void setDescBundle(final String descBundle) {
        this.descBundle = descBundle;
    }

    public ResourceBundle getDescriptionBundle(final Locale locale) {
        return ResourceBundle.getBundle(descBundle, locale);
    }

    public String getDescKey() {
        return descKey;
    }

    void setDescKey(final String descKey) {
        this.descKey = descKey;
    }

    public String getDescription(final Locale locale) {
        return getDescriptionBundle(locale).getString(descKey);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(name);
        hash = 79 * hash + Objects.hashCode(valueType);
        hash = 79 * hash + Objects.hashCode(defaultValue);
        hash = 79 * hash + Objects.hashCode(confClass);
        hash = 79 * hash + Objects.hashCode(descBundle);
        hash = 79 * hash + Objects.hashCode(descKey);
        return hash;
    }

    @Override
    @SuppressWarnings("PMD.NPathComplexity")
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SettingInfo)) {
            return false;
        }
        final SettingInfo other = (SettingInfo) obj;
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(valueType, other.getValueType())) {
            return false;
        }
        if (!Objects.equals(defaultValue, other.getDefaultValue())) {
            return false;
        }
        if (!Objects.equals(confClass, other.getConfClass())) {
            return false;
        }

        if (!Objects.equals(descBundle, other.getDescBundle())) {
            return false;
        }

        return Objects.equals(descKey, other.getDescKey());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "name = \"%s\", "
                                 + "valueType = \"%s\", "
                                 + "defaultValue = \"%s\", "
                                 + "configurationClass = \"%s\", "
                                 + "descBundle = \"%s\","
                                 + "descKey = \"%s\""
                                 + " }",
                             super.toString(),
                             name,
                             valueType,
                             defaultValue,
                             confClass,
                             descBundle,
                             descKey);
    }

}
