/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FolderTreeNode {

    private long folderId;

    private String uuid;

    private String name;

    private String path;

    private List<FolderTreeNode> subFolders;

    private boolean open;

    private boolean selected;

    private ItemPermissionsModel permissions;

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

    public List<FolderTreeNode> getSubFolders() {
        return Collections.unmodifiableList(subFolders);
    }

    public void setSubFolders(final List<FolderTreeNode> subFolders) {
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

    public ItemPermissionsModel getPermissions() {
        return permissions;
    }

    public void setPermissions(
        final ItemPermissionsModel permissions
    ) {
        this.permissions = permissions;
    }

}
