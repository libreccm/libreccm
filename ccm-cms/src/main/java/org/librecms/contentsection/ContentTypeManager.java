/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

import org.librecms.contentsection.privileges.TypePrivileges;

import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Role;
import org.libreccm.workflow.WorkflowTemplate;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.lifecycle.LifecycleDefinition;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Methods for managing the default lifecycle and workflow of a content type.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentTypeManager {

    @Inject
    private ContentTypeRepository typeRepo;

    @Inject
    private PermissionManager permissionManager;

    /**
     * Converts the class name of a content type to {@link Class} object.
     *
     * @param className The class name of the content type.
     *
     * @return The class for the content type.
     *
     * @throws IllegalArgumentException If the provided class is not a sub class
     *                                  of {@link ContentItem}.
     */
    @SuppressWarnings("unchecked")
    public Class<? extends ContentItem> classNameToClass(final String className) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(String.format(
                "No class with the name \"%s\" exists.", className),
                                               ex);
        }

        if (ContentItem.class.isAssignableFrom(clazz)) {
            return (Class<? extends ContentItem>) clazz;
        } else {
            throw new IllegalArgumentException(String.format(
                "Class \"%s\" is not a content type.", className));
        }
    }

    /**
     * Sets the default lifecycle to use for new items of a content type.
     *
     * @param type
     * @param definition The {@link LifecycleDefinition} for the lifecycle to
     *                   use for new items of the provided {@code type}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void setDefaultLifecycle(
        @RequiresPrivilege(AdminPrivileges.ADMINISTER_CONTENT_TYPES)
        final ContentType type,
        final LifecycleDefinition definition) {

        type.setDefaultLifecycle(definition);

        typeRepo.save(type);
    }

    /**
     * Sets the default workflow to use for new items of a content type.
     *
     * @param type
     * @param template The {@link WorkflowTemplate} for the workflow to use for
     *                 new items of the provided {@code type}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void setDefaultWorkflow(
        @RequiresPrivilege(AdminPrivileges.ADMINISTER_CONTENT_TYPES)
        final ContentType type,
        final WorkflowTemplate template) {

        type.setDefaultWorkflow(template);

        typeRepo.save(type);
    }

    /**
     * Creates a permission granting the {@link TypePrivileges#USE_TYPE}
     * privilege to a role.
     *
     * @param type The type on which the privilege is granted.
     * @param role The role to which the privilege is granted.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void grantUsageOfType(
        @RequiresPrivilege(AdminPrivileges.ADMINISTER_CONTENT_TYPES)
        final ContentType type,
        final Role role) {

        permissionManager.grantPrivilege(TypePrivileges.USE_TYPE, role, type);
    }

    /**
     * Denies usages of the provided content type by revoking any existing
     * permissions granting the {@link TypePrivileges#USE_TYPE} privilege to the
     * provided role.
     *
     * @param type The type for which the permission is revoked.
     * @param role The role from which the permission is removed.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void denyUsageOnType(
        @RequiresPrivilege(AdminPrivileges.ADMINISTER_CONTENT_TYPES)
        final ContentType type,
        final Role role) {

        permissionManager.revokePrivilege(TypePrivileges.USE_TYPE, role, type);
    }

}
