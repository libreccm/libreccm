/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ItemPermissionChecker {

    @Inject
    private PermissionChecker permissionChecker;

    public boolean canAdministerItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.ADMINISTER, item
        );
    }

    public boolean canAdministerItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.ADMINISTER, section.getRootDocumentsFolder()
        );
    }

    public boolean canAdministerItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.ADMINISTER, folder
        );
    }

    public boolean canApplyAlternateWorkflowItems(
        final ContentSection section
    ) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPLY_ALTERNATE_WORKFLOW,
            section.getRootDocumentsFolder()
        );
    }

    public boolean canApplyAlternateWorkflowItems(
        final ContentItem item
    ) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, item
        );
    }

    public boolean canApplyAlternateWorkflowItems(
        final Folder folder
    ) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, folder
        );
    }

    public boolean canApproveItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPROVE, item
        );
    }

    public boolean canApproveItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPROVE, section.getRootDocumentsFolder()
        );
    }

    public boolean canApproveItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.APPROVE, folder
        );
    }

    public boolean canCategorizeItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CATEGORIZE, item
        );
    }

    public boolean canCategorizeItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CATEGORIZE, section.getRootDocumentsFolder()
        );
    }

    public boolean canCategorizeItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CATEGORIZE, folder
        );
    }

    public boolean canCreateNewItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CREATE_NEW, item
        );
    }

    public boolean canCreateNewItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CREATE_NEW, section.getRootDocumentsFolder()
        );
    }

    public boolean canCreateNewItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.CREATE_NEW, folder
        );
    }

    public boolean canDeleteItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.DELETE, item
        );
    }

    public boolean canDeleteItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.DELETE, section.getRootDocumentsFolder()
        );
    }

    public boolean canDeleteItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.DELETE, folder
        );
    }

    public boolean canEditItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.EDIT, item
        );
    }

    public boolean canEditItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.EDIT, section.getRootDocumentsFolder()
        );
    }

    public boolean canEditItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.EDIT, folder
        );
    }

    public boolean canPreviewItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PREVIEW, item
        );
    }

    public boolean canPreviewItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PREVIEW, section.getRootDocumentsFolder()
        );
    }

    public boolean canPreviewItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PREVIEW, folder
        );
    }

    public boolean canPublishItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PUBLISH, item
        );
    }

    public boolean canPublishItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PUBLISH, section.getRootDocumentsFolder()
        );
    }

    public boolean canPublishItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.PUBLISH, folder
        );
    }

    public boolean canViewPublishedItems(final ContentItem item) {
        return permissionChecker.isPermitted(
            ItemPrivileges.VIEW_PUBLISHED, item
        );
    }

    public boolean canViewPublishedItems(final ContentSection section) {
        return permissionChecker.isPermitted(
            ItemPrivileges.VIEW_PUBLISHED, section.getRootDocumentsFolder()
        );
    }

    public boolean canViewPublishedItems(final Folder folder) {
        return permissionChecker.isPermitted(
            ItemPrivileges.VIEW_PUBLISHED, folder
        );
    }

}
