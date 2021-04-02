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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model for the current asset folder. Provides data about the folder for the 
 * view template.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("AssetFolderModel")
public class AssetFolderModel {

    /**
     * How many objects are in the folder (subfolders and assets)?
     */
    private long count;

    /**
     * Index of the first result shown.
     */
    private int firstResult;

    /**
     * The maximum number of results on a page.
     */
    private int maxResults;

    /**
     * The objects (subfolders and assets) in the folder.
     */
    private List<AssetFolderRowModel> rows;

    /**
     * The breadcrumbs of the folder path.
     */
    private List<FolderBreadcrumbsModel> breadcrumbs;

    /**
     * The path of the folder.
     */
    private String path;

    /**
     * Can the current user create sub folders in this folder?
     */
    private boolean canCreateSubFolders;

    /**
     * Can the current folder create assets in this folder?
     */
    private boolean canCreateAssets;

    /**
     * The permissions granted to the current user.
     */
    private List<GrantedPrivilegeModel> currentUserPermissions;

    /**
     * The privileges granted to different roles for the current folder.
     */
    private List<PrivilegesGrantedToRoleModel> grantedPermissions;

    private List<String> privileges;

    public long getCount() {
        return count;
    }

    public void setCount(final long count) {
        this.count = count;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(final int firstResult) {
        this.firstResult = firstResult;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(final int maxResults) {
        this.maxResults = maxResults;
    }

    public long getNumberOfPages() {
        return (long) Math.ceil((double) count / maxResults);
    }

    public long getCurrentPage() {
        return (long) Math.ceil((double) firstResult / maxResults) + 1;
    }

    public List<AssetFolderRowModel> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public void setRows(final List<AssetFolderRowModel> rows) {
        this.rows = new ArrayList<>(rows);
    }

    public List<FolderBreadcrumbsModel> getBreadcrumbs() {
        return Collections.unmodifiableList(breadcrumbs);
    }

    public void setBreadcrumbs(
        final List<FolderBreadcrumbsModel> breadcrumbs
    ) {
        this.breadcrumbs = new ArrayList<>(breadcrumbs);
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public boolean isCanCreateSubFolders() {
        return canCreateSubFolders;
    }

    public void setCanCreateSubFolders(final boolean canCreateSubFolders) {
        this.canCreateSubFolders = canCreateSubFolders;
    }

    public boolean isCanCreateAssets() {
        return canCreateAssets;
    }

    public void setCanCreateAssets(final boolean canCreateAssets) {
        this.canCreateAssets = canCreateAssets;
    }

    public List<GrantedPrivilegeModel> getCurrentUserPermissions() {
        return Collections.unmodifiableList(currentUserPermissions);
    }

    public void setCurrentUserPermissions(
        final List<GrantedPrivilegeModel> currentUserPermissions
    ) {
        this.currentUserPermissions = new ArrayList<>(currentUserPermissions);
    }

    public List<PrivilegesGrantedToRoleModel> getGrantedPermissions() {
        return Collections.unmodifiableList(grantedPermissions);
    }

    public void setGrantedPermissions(
        final List<PrivilegesGrantedToRoleModel> grantedPermissions
    ) {
        this.grantedPermissions = new ArrayList<>(grantedPermissions);
    }

    public List<String> getPrivileges() {
        return Collections.unmodifiableList(privileges);
    }

    public void setPrivileges(final List<String> privileges) {
        this.privileges = new ArrayList<>(privileges);
    }

}
