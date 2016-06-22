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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class ShortcutsConstants {

    /**
     * Name of the shortcuts application type
     */
    public static final String SHORTCUTS_APP_TYPE
                                   = "org.libreccm.shortcuts.Shortcuts";

    public static final String SHORTCUTS_BUNDLE
                               = "org.libreccm.shortcuts.ShortcutsResources";

    /**
     * Primary URL of the singleton Shortcuts application instance.
     */
    public static final String SHORTCUTS_PRIMARY_URL = "/shortcuts/";

    public static final String SHORTSCUT_MANAGE_PRIVILEGE = "manage_shortcuts";

    private ShortcutsConstants() {
        //Nothing
    }

}
