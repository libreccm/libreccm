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
package com.arsdigita.ui.admin.usersgroupsroles.users;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

/**
 * Model builder for the {@link PrimaryEmailTableModel}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PrimaryEmailTableModelBuilder extends LockableImpl
    implements TableModelBuilder {

    private final ParameterSingleSelectionModel<String> selectedUserId;

    public PrimaryEmailTableModelBuilder(
        final ParameterSingleSelectionModel<String> selectedUserId) {
        this.selectedUserId = selectedUserId;
    }

    @Override
    public TableModel makeModel(final Table table, final PageState state) {
        final String userIdStr = selectedUserId.getSelectedKey(state);
        final User selectedUser;
        if (userIdStr == null || userIdStr.isEmpty()) {
            selectedUser = null;
        } else {
            final UserRepository userRepository = CdiUtil.createCdiUtil()
                .findBean(UserRepository.class);
            final long userId = Long.parseLong(userIdStr);
            selectedUser = userRepository.findById(userId);
        }
        
        return new PrimaryEmailTableModel(selectedUser);
    }

}
