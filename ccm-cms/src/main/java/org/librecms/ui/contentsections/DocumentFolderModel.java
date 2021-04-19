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
 * Model for displaying a document folder.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("DocumentFolderModel")
public class DocumentFolderModel {

    /**
     * Count of items (content items and subfolders) in the folder.
     */
    private long count;

    /**
     * The index of the first item in the folder shown.
     */
    private int firstResult;

    /**
     * The maximum number of items shown.
     */
    private int maxResults;

    /**
     * The rows for the table showing the contents of the folder.
     */
    private List<DocumentFolderRowModel> rows;

    /**
     * The breadcrumb trail of the folder.
     */
    private List<FolderBreadcrumbsModel> breadcrumbs;

    /**
     * The path of the folder.
     */
    private String path;

    /**
     * Can the current user create subfolders in the folder.
     */
    private boolean canCreateSubFolders;

    /**
     * Can the current user create items in the folder.
     */
    private boolean canCreateItems;

    /**
     * Can the current user administer items in the folder.
     */
    private boolean canAdminister;

    /**
     * Permissions of the current user for the folder.
     */
    private List<GrantedPrivilegeModel> currentUserPermissions;

    /**
     * Privileges granted to the roles of the content section for the folder.
     */
    private List<PrivilegesGrantedToRoleModel> grantedPermissions;

    /**
     * A list of the privileges that can be granted for a document folder.
     */
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

    protected void setFirstResult(final int firstResult) {
        this.firstResult = firstResult;
    }

    public int getMaxResults() {
        return maxResults;
    }

    protected void setMaxResults(final int maxResults) {
        this.maxResults = maxResults;
    }

    public long getNumberOfPages() {
        return (long) Math.ceil((double) count / maxResults);
    }

    public long getCurrentPage() {
        return (long) Math.ceil((double) firstResult / maxResults) + 1;
    }

    public List<DocumentFolderRowModel> getRows() {
        return Collections.unmodifiableList(rows);
    }

    protected void setRows(final List<DocumentFolderRowModel> rows) {
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

    public String getPathWithTrailingSlash() {
        if (path.isEmpty()) {
            return "";
        } else {
            return String.format("%s/", path);
        }
    }

    protected void setPath(final String path) {
        this.path = path;
    }

    public boolean isCanCreateSubFolders() {
        return canCreateSubFolders;
    }

    protected void setCanCreateSubFolders(final boolean canCreateSubFolders) {
        this.canCreateSubFolders = canCreateSubFolders;
    }

    public boolean isCanCreateItems() {
        return canCreateItems;
    }

    protected void setCanCreateItems(final boolean canCreateItems) {
        this.canCreateItems = canCreateItems;
    }

    public boolean isCanAdminister() {
        return canAdminister;
    }

    public void setCanAdminister(boolean canAdminister) {
        this.canAdminister = canAdminister;
    }

    public List<PrivilegesGrantedToRoleModel> getGrantedPermissions() {
        return Collections.unmodifiableList(grantedPermissions);
    }

    public void setGrantedPermissions(
        final List<PrivilegesGrantedToRoleModel> grantedPermissions
    ) {
        this.grantedPermissions = grantedPermissions;
    }

    public List<String> getPrivileges() {
        return Collections.unmodifiableList(privileges);
    }

    public void setPrivileges(final List<String> privileges) {
        this.privileges = privileges;
    }

    public List<GrantedPrivilegeModel> getCurrentUserPermissions() {
        return Collections.unmodifiableList(currentUserPermissions);
    }

    public void setCurrentUserPermissions(
        final List<GrantedPrivilegeModel> currentUserPermissions
    ) {
        this.currentUserPermissions = new ArrayList<>(currentUserPermissions);
    }

}
