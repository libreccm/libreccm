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
package org.libreccm.shortcuts;

/**
 * Constants for the {@code Shortcuts} module.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class ShortcutsConstants {

    /**
     * Name of the shortcuts application type
     */
    public static final String SHORTCUTS_APP_TYPE
                                   = "org.libreccm.shortcuts.Shortcuts";

    /**
     * Resource bundle which provides the localised messages and labels for
     * the UIs of the {@code Shortcuts} module.
     */
    public static final String SHORTCUTS_BUNDLE
                               = "org.libreccm.shortcuts.ShortcutsResources";

    /**
     * Primary URL of the singleton Shortcuts application instance.
     */
    public static final String SHORTCUTS_PRIMARY_URL = "/shortcuts/";

    /**
     * The privilege which must be granted to a user if the user wants to
     * manage (create, edit, delete) shortcuts.
     */
    public static final String SHORTSCUT_MANAGE_PRIVILEGE = "manage_shortcuts";

    /**
     * Private constructor to forbid creation of instances of this class.
     */
    private ShortcutsConstants() {
        //Nothing
    }

}
