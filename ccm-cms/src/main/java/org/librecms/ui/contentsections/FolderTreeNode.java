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
 * @param <T>
 * @param <P>
 */
public class FolderTreeNode<T extends FolderTreeNode, P extends PermissionsModel> {

    private long folderId;

    private String uuid;

    private String name;

    private String path;

    private List<T> subFolders;

    private boolean open;

    private boolean selected;

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
