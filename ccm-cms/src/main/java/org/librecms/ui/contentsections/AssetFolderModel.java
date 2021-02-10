/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("AssetFolderModel")
public class AssetFolderModel {

    private long count;

    private int firstResult;

    private int maxResults;

    private List<AssetFolderRowModel> rows;

    private List<FolderBreadcrumbsModel> breadcrumbs;

    private String path;

    private boolean canCreateSubFolders;

    private boolean canCreateAssets;

    private List<GrantedPrivilegeModel> currentUserPermissions;

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
