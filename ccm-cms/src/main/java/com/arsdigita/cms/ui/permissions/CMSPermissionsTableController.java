/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.permissions;

import com.arsdigita.cms.CMS;

import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class CMSPermissionsTableController {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private PermissionManager permissionManager;

    @Inject
    private PermissionChecker permissionChecker;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<CMSPermissionsTableRow> buildDirectPermissionsRows(
        final CcmObject object, final String[] privileges) {

        final Optional<ContentSection> section = sectionRepo.findById(CMS
            .getContext().getContentSection().getObjectId());
        final List<Role> roles = section
            .orElseThrow(() -> new UnexpectedErrorException(String.format(
            "The content section %s from the CMS context was not found in"
                + "the database.",
            Objects.toString(CMS.getContext().getContentSection()))))
            .getRoles();

        return roles.stream()
            .map(role -> buildRow(role, object, privileges))
            .sorted((row1, row2) -> {
                return row1.getRoleName().compareTo(row2.getRoleName());
            })
            .collect(Collectors.toList());
    }

    private CMSPermissionsTableRow buildRow(final Role role,
                                            final CcmObject object,
                                            final String[] privileges) {

        final List<CMSPermissionsTableColumn> columns = Arrays
            .stream(privileges)
            .map(privilege -> buildColumn(role, object, privilege))
            .collect(Collectors.toList());

        final CMSPermissionsTableRow row = new CMSPermissionsTableRow();
        row.setObject(object);
        row.setRoleName(role.getName());
        row.setColumns(columns);

        return row;
    }

    private CMSPermissionsTableColumn buildColumn(final Role role,
                                                  final CcmObject object,
                                                  final String privilege) {
        final CMSPermissionsTableColumn column = new CMSPermissionsTableColumn();

        column.setPrivilege(privilege);
        column.setPermitted(permissionChecker.isPermitted(privilege,
                                                          object,
                                                          role));

        return column;

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void togglePermission(final CcmObject object,
                                 final String privilege,
                                 final Role role) {
        if (permissionChecker.isPermitted(privilege, object, role)) {
            permissionManager.revokePrivilege(privilege, role, object);
        } else {
            permissionManager.grantPrivilege(privilege, role, object);
        }
    }

}
