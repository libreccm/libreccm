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

import java.util.Collections;
import java.util.List;

/**
 * Node of a folder tree.
 *
 * This class is not idented for direct. In most cases the subclasses
 * {@link AssetFolderTreeNode} or {@link DocumentFolderTreeNode} should be used.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The subtype of folder tree node.
 * @param <P> The type of the permissions model to use.
 */
public class FolderTreeNode<T extends FolderTreeNode<T, P>, P extends PermissionsModel> {

    /**
     * The ID of the folder.
     */
    private long folderId;

    /**
     * The UUID of the folder.
     */
    private String uuid;

    /**
     * The name of the folder.
     */
    private String name;

    /**
     * The path of the folder.
     */
    private String path;

    /**
     * The subfolders of the folder.
     */
    private List<T> subFolders;

    /**
     * Should the folder open (sub folder are visible) in the view.
     */
    private boolean open;

    /**
     * Is the folder the currently selected folder?
     */
    private boolean selected;

    /**
     * Permissions of the current user for the folder.
     */
    private P permissions;

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(final long folderId) {
        this.folderId = folderId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public List<T> getSubFolders() {
        return Collections.unmodifiableList(subFolders);
    }

    public void setSubFolders(final List<T> subFolders) {
        this.subFolders = subFolders;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(final boolean open) {
        this.open = open;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public P getPermissions() {
        return permissions;
    }

    public void setPermissions(final P permissions) {
        this.permissions = permissions;
    }

}
