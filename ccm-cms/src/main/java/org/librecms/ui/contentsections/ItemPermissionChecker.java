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
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.ItemPrivileges;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Used to check permissions on {@link ContentItem}s for the current user.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ItemPermissionChecker {

    /**
     * The {@link PermissionChecker} instance used to perform the permission
     * checks.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Can the current user administer the provided content item?
     *
     * @param item The content item.
     *
     * @return {@code true} if the current user can administer the item,
     *         {@code false} otherwise.
     */
    public boolean canAdministerItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.ADMINISTER, item
        );
    }

    /**
     * Can the current user administer the content items in the provided content
     * section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can administer items of the
     *         content section, {@code false} otherwise.
     */
    public boolean canAdministerItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.ADMINISTER, section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user administer the content items in the provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can administer items of the
     *         folder, {@code false} otherwise.
     */
    public boolean canAdministerItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.ADMINISTER, folder
        );
    }

    /**
     * Can the current user apply alternate workflows to the content items in
     * the provided content section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can apply alternate workflows to
     *         the items of the content section, {@code false} otherwise.
     */
    public boolean canApplyAlternateWorkflowItems(
        final ContentSection section
    ) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPLY_ALTERNATE_WORKFLOW,
            section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user apply alternate workflows to the provided content
     * item?
     *
     * @param item The content item.
     *
     * @return {@code true} if the current user can apply alternate workflows to
     *         the item, {@code false} otherwise.
     */
    public boolean canApplyAlternateWorkflowItems(
        final ContentItem item
    ) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, item
        );
    }

    /**
     * Can the current user apply alternate workflow to the content items in the
     * provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can apply alternate workflows to
     *         the items of the folder, {@code false} otherwise.
     */
    public boolean canApplyAlternateWorkflowItems(
        final Folder folder
    ) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, folder
        );
    }

    /**
     * Can the current user approve the provided content item?
     *
     * @param item The content item.
     *
     * @return {@code true} if the current user can approve the item,
     *         {@code false} otherwise.
     */
    public boolean canApproveItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPROVE, item
        );
    }

    /**
     * Can the current user approve the content items in the provided content
     * section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can approve items of the content
     *         section, {@code false} otherwise.
     */
    public boolean canApproveItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPROVE, section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user approve the content items in the provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can approve items of the folder,
     *         {@code false} otherwise.
     */
    public boolean canApproveItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPROVE, folder
        );
    }

    /**
     * Can the current user categorize the provided content item?
     *
     * @param item The content item.
     *
     * @return {@code true} if the current user can categorize the item,
     *         {@code false} otherwise.
     */
    public boolean canCategorizeItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CATEGORIZE, item
        );
    }

    /**
     * Can the current user categorize the content items in the provided content
     * section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can categorize items of the
     *         content section, {@code false} otherwise.
     */
    public boolean canCategorizeItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CATEGORIZE, section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user categorize the content items in the provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can categorize items of the
     *         folder, {@code false} otherwise.
     */
    public boolean canCategorizeItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CATEGORIZE, folder
        );
    }

    /**
     * Can the current user create new content items in the provided content
     * section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can create new items in the
     *         content section, {@code false} otherwise.
     */
    public boolean canCreateNewItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CREATE_NEW, section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user create new content items in the provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can create new items in the
     *         folder, {@code false} otherwise.
     */
    public boolean canCreateNewItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CREATE_NEW, folder
        );
    }

    /**
     * Can the current user delete the provided content item?
     *
     * @param item The content item.
     *
     * @return {@code true} if the current user can delete the item,
     *         {@code false} otherwise.
     */
    public boolean canDeleteItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.DELETE, item
        );
    }

    /**
     * Can the current user delete content items in the provided content
     * section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can delete items in the content
     *         section, {@code false} otherwise.
     */
    public boolean canDeleteItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.DELETE, section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user delete the content items in the provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can delete items in the folder,
     *         {@code false} otherwise.
     */
    public boolean canDeleteItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.DELETE, folder
        );
    }

    /**
     * Can the current user edit the provided content item?
     *
     * @param item The content item.
     *
     * @return {@code true} if the current user can edit the item, {@code false}
     *         otherwise.
     */
    public boolean canEditItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.EDIT, item
        );
    }

    /**
     * Can the current user edit the content items in the provided content
     * section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can edit items of the content
     *         section, {@code false} otherwise.
     */
    public boolean canEditItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.EDIT, section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user edit the content items in the provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can administer edit items in the
     *         folder, {@code false} otherwise.
     */
    public boolean canEditItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.EDIT, folder
        );
    }

    /**
     * Can the current user preview the provided content item?
     *
     * @param item The content item.
     *
     * @return {@code true} if the current user can preview the item,
     *         {@code false} otherwise.
     */
    public boolean canPreviewItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PREVIEW, item
        );
    }

    /**
     * Can the current user preview the content items in the provided content
     * section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can preview items of the content
     *         section, {@code false} otherwise.
     */
    public boolean canPreviewItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PREVIEW, section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user preview the content items in the provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can preview items in the folder,
     *         {@code false} otherwise.
     */
    public boolean canPreviewItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PREVIEW, folder
        );
    }

    /**
     * Can the current user publish the provided content item?
     *
     * @param item The content item.
     *
     * @return {@code true} if the current user can publish the item,
     *         {@code false} otherwise.
     */
    public boolean canPublishItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PUBLISH, item
        );
    }

    /**
     * Can the current user publish the content items in the provided content
     * section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can publish items of the content
     *         section, {@code false} otherwise.
     */
    public boolean canPublishItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PUBLISH, section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user publish the content items in the provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can publish items in the folder,
     *         {@code false} otherwise.
     */
    public boolean canPublishItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PUBLISH, folder
        );
    }

    /**
     * Can the current user view the published version the provided content
     * item?
     *
     * @param item The content item.
     *
     * @return {@code true} if the current user can view the published of the
     *         item, {@code false} otherwise.
     */
    public boolean canViewPublishedItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.VIEW_PUBLISHED, item
        );
    }

    /**
     * Can the current user view publish content items of the provided content
     * section?
     *
     * @param section The content section
     *
     * @return {@code true} if the current user can view publish items of the
     *         content section, {@code false} otherwise.
     */
    public boolean canViewPublishedItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.VIEW_PUBLISHED, section.getRootDocumentsFolder()
        );
    }

    /**
     * Can the current user view the published version of content items in the
     * provided folder?
     *
     * @param folder The folder.
     *
     * @return {@code true} if the current user can view the published version
     *         of items in the folder, {@code false} otherwise.
     */
    public boolean canViewPublishedItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.VIEW_PUBLISHED, folder
        );
    }

}
