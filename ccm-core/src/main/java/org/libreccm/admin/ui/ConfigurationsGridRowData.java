/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui;

import org.libreccm.configuration.ConfigurationInfo;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ConfigurationsGridRowData {

    private Class<?> configurationClass;
    private ConfigurationInfo configurationInfo;
    private String name;
    private String title;
    private String description;

    public ConfigurationsGridRowData() {
    }

    public Class<?> getConfigurationClass() {
        return configurationClass;
    }

    public void setConfigurationClass(
        Class<?> configurationClass) {
        this.configurationClass = configurationClass;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ConfigurationInfo getConfigurationInfo() {
        return configurationInfo;
    }

    public void setConfigurationInfo(final ConfigurationInfo configurationInfo) {
        this.configurationInfo = configurationInfo;
    }

    
    
}
