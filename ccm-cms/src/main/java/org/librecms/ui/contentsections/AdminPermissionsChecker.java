/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * A helper for checking if the current user has a permission granting certain
 * privileges.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AdminPermissionsChecker {

    /**
     * The {@link PermissionChecker} instance to use.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_CATEGORIES} privilege
     * has been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the {@link AdminPrivileges#ADMINISTER_CATEGORIES}
     *         privilege has been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerCategories(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CATEGORIES, section
        );
    }

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_CONTENT_TYPES}
     * privilege has been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the
     *         {@link AdminPrivileges#ADMINISTER_CONTENT_TYPES} privilege has
     *         been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerContentTypes(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section
        );
    }

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_LIFECYLES} privilege
     * has been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the {@link AdminPrivileges#ADMINISTER_LIFECYLES}
     *         privilege has been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerLifecycles(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_LIFECYLES, section
        );
    }

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_ROLES} privilege has
     * been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the {@link AdminPrivileges#ADMINISTER_ROLES}
     *         privilege has been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerRoles(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_ROLES, section
        );
    }

    /**
     * Checks of the the {@link AdminPrivileges#ADMINISTER_WORKFLOWS} privilege
     * has been granted to the current user.
     *
     * @param section The {@link ContentSection} one which the privilege might
     *                has been granted.
     *
     * @return {@code true} if the {@link AdminPrivileges#ADMINISTER_WORKFLOWS}
     *         privilege has been granted to the current user for the provided
     *         {@link ContentSection}, otherwise {@code false}.
     */
    public boolean canAdministerWorkflows(final ContentSection section) {
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_WORKFLOWS, section
        );
    }

}
