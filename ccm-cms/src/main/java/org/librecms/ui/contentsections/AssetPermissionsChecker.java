/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.AssetPrivileges;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetPermissionsChecker {

    @Inject
    private PermissionChecker permissionChecker;

    public boolean canCreateAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.CREATE_NEW, asset
        );
    }

    public boolean canCreateAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.CREATE_NEW, section.getRootAssetsFolder()
        );
    }

    public boolean canCreateAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.CREATE_NEW, folder
        );
    }

    public boolean canDeleteAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.DELETE, asset
        );
    }

    public boolean canDeleteAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.DELETE, section.getRootAssetsFolder()
        );
    }

    public boolean canDeleteAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.DELETE, folder
        );
    }

    public boolean canUseAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.USE, asset
        );
    }

    public boolean canUseAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.USE, section.getRootAssetsFolder()
        );
    }

    public boolean canUseAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.USE, folder
        );
    }

    public boolean canEditAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.EDIT, asset
        );
    }

    public boolean canEditAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.EDIT, section.getRootAssetsFolder()
        );
    }

    public boolean canEditAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.EDIT, folder
        );
    }

    public boolean canViewAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.VIEW, asset
        );
    }

    public boolean canViewAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.VIEW, section.getRootAssetsFolder()
        );
    }

    public boolean canViewAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.VIEW, folder
        );
    }

}
