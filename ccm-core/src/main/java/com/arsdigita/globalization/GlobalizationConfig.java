/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.globalization;

import java.util.Objects;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration(
    descBundle = "com.arsdigita.globalization.GlobalizationConfigDescription",
    descKey = "globalization.config.description")
public class GlobalizationConfig {

    @Setting(descKey = "globalization.config.default_charset")
    private String defaultCharset = "UTF-8";

    public static GlobalizationConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(GlobalizationConfig.class);
    }

    public String getDefaultCharset() {
        return defaultCharset;
    }

    public void setDefaultCharset(final String defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(defaultCharset);
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
        if (!(obj instanceof GlobalizationConfig)) {
            return false;
        }
        final GlobalizationConfig other = (GlobalizationConfig) obj;
        return Objects.equals(defaultCharset, other.defaultCharset);
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "defaultCharset = \"%s\""
                                 + " }",
                             super.toString(),
                             defaultCharset);
    }

}
