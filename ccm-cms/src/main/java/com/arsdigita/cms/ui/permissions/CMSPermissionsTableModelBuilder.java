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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.util.LockableImpl;

import org.libreccm.core.CcmObject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CMSPermissionsTableModelBuilder extends LockableImpl
    implements TableModelBuilder {

    private final CMSPermissionsPane parent;
    
    public CMSPermissionsTableModelBuilder(final CMSPermissionsPane parent) {
        this.parent = parent;
    }
    
    @Override
    public TableModel makeModel(final Table table, final PageState state) {
        final CcmObject object = parent.getObject(state);
        return new CMSPermissionsTableModel(object, parent.getPrivileges());
    }

}
