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

import com.arsdigita.bebop.table.TableModel;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.security.PermissionManager;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.Iterator;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CMSPermissionsTableModel implements TableModel {

    private final Iterator<CMSPermissionsTableRow> iterator;
    private CMSPermissionsTableRow currentRow;

    public CMSPermissionsTableModel(final CcmObject object) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CMSPermissionsTableController controller = cdiUtil.findBean(
            CMSPermissionsTableController.class);

        iterator = controller.buildDirectPermissionsRows(object).iterator();
    }

    @Override
    public int getColumnCount() {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionManager permissionManager = cdiUtil.findBean(
            PermissionManager.class);

        return permissionManager.listDefiniedPrivileges(ItemPrivileges.class)
            .size() + 2;
    }

    @Override
    public boolean nextRow() {
        if (iterator.hasNext()) {
            currentRow = iterator.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getElementAt(final int columnIndex) {
        if (columnIndex == 0) {
            return currentRow.getRoleName();
        } else if (columnIndex == getColumnCount() - 1) {
            return "Remove all";
        } else {
            return currentRow.getColumns().get(columnIndex - 1);
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {

        if (columnIndex == 0) {
            return String.format("%s-%s-role",
                                 currentRow.getObject().getUuid(),
                                 currentRow.getRoleName());
        } else if (columnIndex >= currentRow.getColumns().size() - 1) {
            return String.format("%s-%s-remove-all",
                                 currentRow.getObject().getUuid(),
                                 currentRow.getRoleName());
        } else {
            return String.format(
                "%s-%s-%s",
                currentRow.getObject().getUuid(),
                currentRow.getRoleName(),
                currentRow.getColumns().get(columnIndex - 1).getPrivilege()
            );
        }
    }

}
