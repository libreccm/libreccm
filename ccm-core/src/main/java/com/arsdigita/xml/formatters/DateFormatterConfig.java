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
package com.arsdigita.xml.formatters;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class DateFormatterConfig {

    @Setting
    private String locale = null;

    public static DateFormatterConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(DateFormatterConfig.class);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.locale);
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
        if (!(obj instanceof DateFormatterConfig)) {
            return false;
        }
        final DateFormatterConfig other = (DateFormatterConfig) obj;
        return Objects.equals(this.locale, other.getLocale());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "locale = %s"
                                 + " }",
                             super.toString(),
                             locale);
    }

}
