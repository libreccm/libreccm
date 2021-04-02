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

import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.AssetPrivileges;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * A permissions checker for assets. Checks the permissions of the current user
 * for an {@link Asset}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetPermissionsChecker {

    /**
     * The {@link PermissionChecker} instance to use.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Checks for a permission granting the {@link AssetPrivileges#CREATE_NEW}
     * for the provided {@link Asset}.
     *
     * @param asset The asset to use.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#CREATE_NEW} privilege for the provided
     *         {@link Asset} to the current user, {@code false} otherwise.
     */
    public boolean canCreateAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.CREATE_NEW, asset
        );
    }

    /**
     * Checks for a permission granting the {@link AssetPrivileges#CREATE_NEW}
     * for the provided {@link ContentSection}.
     *
     * @param section The content section.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#CREATE_NEW} privilege for the provided
     *         {@link ContentSection} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canCreateAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.CREATE_NEW, section.getRootAssetsFolder()
        );
    }

     /**
     * Checks for a permission granting the {@link AssetPrivileges#CREATE_NEW}
     * for the provided {@link Folder}.
     *
     * @param folder The folder.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#CREATE_NEW} privilege for the provided
     *         {@link Folder} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canCreateAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.CREATE_NEW, folder
        );
    }

      /**
     * Checks for a permission granting the {@link AssetPrivileges#DELETE}
     * for the provided {@link Asset}.
     *
     * @param asset The asset to use.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#DELETE} privilege for the provided
     *         {@link Asset} to the current user, {@code false} otherwise.
     */
    public boolean canDeleteAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.DELETE, asset
        );
    }

    /**
     * Checks for a permission granting the {@link AssetPrivileges#DELETE}
     * for the provided {@link ContentSection}.
     *
     * @param section The content section.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#DELETE} privilege for the provided
     *         {@link ContentSection} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canDeleteAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.DELETE, section.getRootAssetsFolder()
        );
    }

     /**
     * Checks for a permission granting the {@link AssetPrivileges#DELETE}
     * for the provided {@link Folder}.
     *
     * @param folder The folder.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#DELETE} privilege for the provided
     *         {@link Folder} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canDeleteAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.DELETE, folder
        );
    }

      /**
     * Checks for a permission granting the {@link AssetPrivileges#USE}
     * for the provided {@link Asset}.
     *
     * @param asset The asset to use.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#USE} privilege for the provided
     *         {@link Asset} to the current user, {@code false} otherwise.
     */
    public boolean canUseAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.USE, asset
        );
    }

    /**
     * Checks for a permission granting the {@link AssetPrivileges#USE}
     * for the provided {@link ContentSection}.
     *
     * @param section The content section.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#USE} privilege for the provided
     *         {@link ContentSection} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canUseAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.USE, section.getRootAssetsFolder()
        );
    }

     /**
     * Checks for a permission granting the {@link AssetPrivileges#USE}
     * for the provided {@link Folder}.
     *
     * @param folder The folder.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#USE} privilege for the provided
     *         {@link Folder} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canUseAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.USE, folder
        );
    }

      /**
     * Checks for a permission granting the {@link AssetPrivileges#EDIT}
     * for the provided {@link Asset}.
     *
     * @param asset The asset to use.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#EDIT} privilege for the provided
     *         {@link Asset} to the current user, {@code false} otherwise.
     */
    public boolean canEditAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.EDIT, asset
        );
    }

    /**
     * Checks for a permission granting the {@link AssetPrivileges#EDIT}
     * for the provided {@link ContentSection}.
     *
     * @param section The content section.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#EDIT} privilege for the provided
     *         {@link ContentSection} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canEditAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.EDIT, section.getRootAssetsFolder()
        );
    }

     /**
     * Checks for a permission granting the {@link AssetPrivileges#EDIT}
     * for the provided {@link Folder}.
     *
     * @param folder The folder.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#EDIT} privilege for the provided
     *         {@link Folder} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canEditAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.EDIT, folder
        );
    }

      /**
     * Checks for a permission granting the {@link AssetPrivileges#VIEW}
     * for the provided {@link Asset}.
     *
     * @param asset The asset to use.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#VIEW} privilege for the provided
     *         {@link Asset} to the current user, {@code false} otherwise.
     */
    public boolean canViewAssets(final Asset asset) {
        return permissionChecker.isPermitted(
            AssetPrivileges.VIEW, asset
        );
    }

    /**
     * Checks for a permission granting the {@link AssetPrivileges#VIEW}
     * for the provided {@link ContentSection}.
     *
     * @param section The content section.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#VIEW} privilege for the provided
     *         {@link ContentSection} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canViewAssets(final ContentSection section) {
        return permissionChecker.isPermitted(
            AssetPrivileges.VIEW, section.getRootAssetsFolder()
        );
    }

     /**
     * Checks for a permission granting the {@link AssetPrivileges#VIEW}
     * for the provided {@link Folder}.
     *
     * @param folder The folder.
     *
     * @return {@code true} if there is permission granting the
     *         {@link AssetPrivileges#VIEW} privilege for the provided
     *         {@link Folder} to the current user, {@code false}
     *         otherwise.
     */
    public boolean canViewAssets(final Folder folder) {
        return permissionChecker.isPermitted(
            AssetPrivileges.VIEW, folder
        );
    }

}
