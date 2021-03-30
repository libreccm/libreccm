/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.ItemPrivileges;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class DocumentPermissions {

    @Inject
    private PermissionChecker permissionChecker;

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
