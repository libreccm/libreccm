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
package org.libreccm.theming;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ThemeInfo {

    private String name;

    private ThemeVersion version;

    private String type;

    private Class<ThemeProvider> provider;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ThemeVersion getVersion() {
        return version;
    }

    public void setVersion(final ThemeVersion version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Class<ThemeProvider> getProvider() {
        return provider;
    }

    public void setProvider(final Class<ThemeProvider> provider) {
        this.provider = provider;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(name);
        hash = 73 * hash + Objects.hashCode(version);
        hash = 73 * hash + Objects.hashCode(type);
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
        if (!(obj instanceof ThemeInfo)) {
            return false;
        }
        final ThemeInfo other = (ThemeInfo) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(type, other.getType())) {
            return false;
        }
        return version == other.getVersion();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ThemeInfo;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "name = \"%s\", "
                                 + "version = %s, "
                                 + "type = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             name,
                             Objects.toString(version),
                             type,
                             data);
    }

}
