/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.db;

import org.libreccm.security.RequiresPrivilege;
import org.libreccm.theming.ThemingPrivileges;
import org.libreccm.theming.manifest.ThemeManifest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Provides methods for managing themes stored in the database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ThemeManager {

    @Inject
    private ThemeRepository themeRepository;

    /**
     * Creates a new theme, including the root directory and a theme manifest
     * file.
     *
     * @see ThemeManifest
     *
     * @param name The name of the theme.
     *
     * @return The new theme.
     */
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public Theme createTheme(final String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Deletes a theme. The theme must be published.
     *
     * @param theme The theme to delete.
     */
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public void deleteTheme(final Theme theme) {
        throw new UnsupportedOperationException();
    }

    /**
     * Publishes a theme. This method will delete the current live version of
     * the theme (if there is a live version) and create a new live version by
     * copying all files from the draft version.
     *
     * @param theme The theme to publish. If the live version of a theme is
     *              passed here the method will lookup the draft version first.
     *
     */
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public void publishTheme(final Theme theme) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unpublishes a theme by deleting the live version of the theme.
     *
     * @param theme The theme to unpublish.
     */
    @RequiresPrivilege(ThemingPrivileges.ADMINISTER_THEMES)
    public void unpublishTheme(final Theme theme) {
        throw new UnsupportedOperationException();
    }

}
