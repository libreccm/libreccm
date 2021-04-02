/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

import org.libreccm.l10n.GlobalizationHelper;

/**
 * A object in an assets folder, either a subfolder or an asset.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetFolderRowModel {

    /**
     * Is the object deletable?
     */
    private boolean deletable;

    /**
     * Is the object a folder?
     */
    private boolean folder;

    /**
     * If the object is a folder: The path of the folder, otherwise
     * {@code null}.
     */
    private String folderPath;

    /**
     * The name of the object.
     */
    private String name;

    /**
     * The object is a not a CMS object, but some other object put into the
     * category backing the folder.
     */
    private boolean noneCmsObject;

    /**
     * The localized title of the folder. If available this title is provided in
     * the negotiated language (see
     * {@link GlobalizationHelper#getNegotiatedLocale()}. If this is not
     * possible, default value for the default language is used.s
     */
    private String title;

    /**
     * The type of the object.
     */
    private String type;

    /**
     * The permissions granted to the current user for the object.
     */
    private AssetPermissionsModel permissions;

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(final boolean folder) {
        this.folder = folder;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(final String folderPath) {
        this.folderPath = folderPath;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isNoneCmsObject() {
        return noneCmsObject;
    }

    public void setNoneCmsObject(final boolean noneCmsObject) {
        this.noneCmsObject = noneCmsObject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public AssetPermissionsModel getPermissions() {
        return permissions;
    }

    public void setPermissions(final AssetPermissionsModel permissions) {
        this.permissions = permissions;
    }

}
