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

import org.libreccm.theming.manifest.ThemeManifest;

import java.util.Objects;

/**
 * Informations about a theme.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ThemeInfo {

    /**
     * The manifest of the theme.
     */
    private ThemeManifest manifest;

    /**
     * The version of the theme.
     */
    private ThemeVersion version;

    /**
     * The {@link ThemeProvider} implementation which is responsible for the
     * theme.
     */
    private Class<? extends ThemeProvider> provider;

    public ThemeManifest getManifest() {
        return manifest;
    }

    public void setManifest(final ThemeManifest manifest) {
        this.manifest = manifest;
    }

    /**
     * Convenient getter for name of theme.
     *
     * @return {@link #manifest#getName()}
     */
    public String getName() {
        return manifest.getName();
    }

    public ThemeVersion getVersion() {
        return version;
    }

    public void setVersion(final ThemeVersion version) {
        this.version = version;
    }

    /**
     * Convenient getter for type of theme.
     *
     * @return {@link #manifest#getType()}
     */
    public String getType() {
        return manifest.getType();
    }

    public Class<? extends ThemeProvider> getProvider() {
        return provider;
    }

    public void setProvider(final Class<? extends ThemeProvider> provider) {
        this.provider = provider;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(manifest);
        hash = 73 * hash + Objects.hashCode(version);
        if (provider != null) {
            hash = 73 * hash + Objects.hashCode(provider.getName());
        }
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
        if (!(obj instanceof ThemeInfo)) {
            return false;
        }
        final ThemeInfo other = (ThemeInfo) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(manifest, other.getManifest())) {
            return false;
        }
        if (provider != null && other.getProvider() == null) {
            return false;
        } else if (provider == null && other.getProvider() != null) {
            return false;
        } else {
            if (!Objects.equals(provider.getName(),
                                other.getProvider().getName())) {
                return false;
            }
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

        final String providerClassName;
        if (provider == null) {
            providerClassName = "";
        } else {
            providerClassName = provider.getName();
        }

        return String.format("%s{ "
                                 + "mainfest = %s, "
                                 + "version = %s, "
                                 + "provider = %s, "
                                 + "%s }",
                             super.toString(),
                             Objects.toString(manifest),
                             Objects.toString(version),
                             providerClassName,
                             data);
    }

}
