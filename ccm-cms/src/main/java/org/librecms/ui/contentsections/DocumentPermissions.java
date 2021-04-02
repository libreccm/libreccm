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
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.ItemPrivileges;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * Used to determine the priviliges granted on a document/{@link ContentItem}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class DocumentPermissions {

    /**
     * The {@link PermissionChecker} instance used for checking permissions.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Builds a {@link DocumentPermissionsModel} for the current user and the
     * provided {@code folder}.
     *
     * @param folder The folder.
     *
     * @return A {@link DocumentPermissionsModel} for the folder and items in
     *         the folder.
     */
    public DocumentPermissionsModel buildDocumentPermissionsModel(
        final Folder folder
    ) {
        final DocumentPermissionsModel model = new DocumentPermissionsModel();
        model.setGrantedAdminister(
            permissionChecker.isPermitted(
                ItemPrivileges.ADMINISTER, folder
            )
        );
        model.setGrantedApplyAlternateWorkflow(
            permissionChecker.isPermitted(
                ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, folder
            )
        );
        model.setGrantedApprove(
            permissionChecker.isPermitted(
                ItemPrivileges.APPROVE, folder
            )
        );
        model.setGrantedCategorize(
            permissionChecker.isPermitted(
                ItemPrivileges.CATEGORIZE, folder
            )
        );
        model.setGrantedCreateNew(
            permissionChecker.isPermitted(
                ItemPrivileges.CREATE_NEW, folder
            )
        );
        model.setGrantedDelete(
            permissionChecker.isPermitted(
                ItemPrivileges.DELETE, folder
            )
        );
        model.setGrantedEdit(
            permissionChecker.isPermitted(
                ItemPrivileges.EDIT, folder
            )
        );
        model.setGrantedPreview(
            permissionChecker.isPermitted(
                ItemPrivileges.PREVIEW, folder
            )
        );
        model.setGrantedPublish(
            permissionChecker.isPermitted(
                ItemPrivileges.PUBLISH, folder
            )
        );
        model.setGrantedViewPublished(
            permissionChecker.isPermitted(
                ItemPrivileges.VIEW_PUBLISHED, folder
            )
        );
        return model;
    }

    /**
     * Builds a {@link DocumentPermissionsModel} for the current user and
     * specific content item.
     *
     *
     * @param item The {@link ContentItem}.
     *
     * @return A {@link DocumentPermissionsModel} for the item.
     */
    public DocumentPermissionsModel buildDocumentPermissionsModel(
        final ContentItem item
    ) {
        final DocumentPermissionsModel model = new DocumentPermissionsModel();
        model.setGrantedAdminister(
            permissionChecker.isPermitted(
                ItemPrivileges.ADMINISTER, item
            )
        );
        model.setGrantedApplyAlternateWorkflow(
            permissionChecker.isPermitted(
                ItemPrivileges.APPLY_ALTERNATE_WORKFLOW, item
            )
        );
        model.setGrantedApprove(
            permissionChecker.isPermitted(
                ItemPrivileges.APPROVE, item
            )
        );
        model.setGrantedCategorize(
            permissionChecker.isPermitted(
                ItemPrivileges.CATEGORIZE, item
            )
        );
        model.setGrantedCreateNew(
            permissionChecker.isPermitted(
                ItemPrivileges.CREATE_NEW, item
            )
        );
        model.setGrantedDelete(
            permissionChecker.isPermitted(
                ItemPrivileges.DELETE, item
            )
        );
        model.setGrantedEdit(
            permissionChecker.isPermitted(
                ItemPrivileges.EDIT, item
            )
        );
        model.setGrantedPreview(
            permissionChecker.isPermitted(
                ItemPrivileges.PREVIEW, item
            )
        );
        model.setGrantedPublish(
            permissionChecker.isPermitted(
                ItemPrivileges.PUBLISH, item
            )
        );
        model.setGrantedViewPublished(
            permissionChecker.isPermitted(
                ItemPrivileges.VIEW_PUBLISHED, item
            )
        );
        return model;
    }

}
