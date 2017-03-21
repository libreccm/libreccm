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
package com.arsdigita.cms.ui.type;

import org.librecms.contentsection.privileges.TypePrivileges;

import com.arsdigita.bebop.table.RowData;

import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class TypePermissionsTableController {

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private PermissionManager permissionManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentTypeRepository typeRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<RowData<Long>> retrieveTypePermissions(
        final ContentType type, final ContentSection section) {

        //Ensure that we use a sane object for the type
        return retrieveTypePermissions(type.getObjectId(), section);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<RowData<Long>> retrieveTypePermissions(
        final long typeId, final ContentSection section) {

        final ContentType type = typeRepo.findById(typeId).get();
        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that Id come from?",
            section.getObjectId())));

        final List<Role> roles = contentSection.getRoles();

        return roles.stream()
            .map(role -> retrievePermissionsForRole(type, role))
            .collect(Collectors.toList());
    }

    private RowData<Long> retrievePermissionsForRole(final ContentType type,
                                                     final Role role) {
        final RowData<Long> rowData = new RowData<>(2);
        rowData.setRowKey(role.getRoleId());
        rowData.setColData(0, role.getName());

        if (permissionChecker.isPermitted(TypePrivileges.USE_TYPE, type)) {
            rowData.setColData(1, "cms.ui.type.permissions.can_use.yes");
        } else {
            rowData.setColData(1, "cms.ui.type.permissions.can_use.no");
        }

        return rowData;
    }

    public void toggleTypeUsePermission(final ContentType type,
                                        final Role role) {
        if (permissionChecker.isPermitted(TypePrivileges.USE_TYPE, type, role)) {
            permissionManager.revokePrivilege(TypePrivileges.USE_TYPE,
                                              role,
                                              type);
        } else {
            permissionManager.grantPrivilege(TypePrivileges.USE_TYPE,
                                             role,
                                             type);
        }
    }

}
