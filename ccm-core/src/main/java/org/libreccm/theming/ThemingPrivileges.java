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
package org.libreccm.theming;

/**
 * Constants for the privileges which allow actions on themes.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class ThemingPrivileges {

    /**
     * Allows a user to create, delete and publish themes.
     */
    public static final String ADMINISTER_THEMES = "administer_themes";
    
    /**
     * Allows a user to edit the files of a theme.
     */
    public static final String EDIT_THEME = "edit_theme";
    
    /**
     * Allows a user to view draft themes.
     */
    public static final String PREVIEW_THEME = "preview_theme";
}
