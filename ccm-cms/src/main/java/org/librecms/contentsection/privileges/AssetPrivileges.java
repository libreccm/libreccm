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
package org.librecms.contentsection.privileges;

/**
 * Constants for privileges allowing actions on the assets of a content section.
 * All privileges defined in this class can either be assigned for the complete 
 * {@link ContentSection} or for a specific assets {@link Folder}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class AssetPrivileges {
    
    /**
     * Allows the creation of new shared {@link Asset}s.
     */
    public static final String CREATE_NEW = "create_new_assets";
    /**
     * Allows the removal of unused shared {@link Asset}s.
     */
    public static final String DELETE = "delete_assets";
    /**
     * Allows the usage of assets (associating them with a content item).
     */
    public static final String USE = "use_asset";
    /**
     * Allows editing of existing assets.
     */
    public static final String EDIT = "edit_asset";
    /**
     * Allows the user to view assets.
     */
    public static final String VIEW = "view_asset";
    
    private AssetPrivileges() {
        //Nothing
    }
    
}
