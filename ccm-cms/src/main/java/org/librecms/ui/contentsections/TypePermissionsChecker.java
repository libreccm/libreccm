/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.privileges.TypePrivileges;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class TypePermissionsChecker {

    @Inject
    private PermissionChecker permissionChecker;

    public boolean canUseType(final ContentType type) {
        return permissionChecker.isPermitted(
            TypePrivileges.USE_TYPE, type
        );
    }

}
