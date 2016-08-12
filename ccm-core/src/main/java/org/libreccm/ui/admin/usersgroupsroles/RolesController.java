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
package org.libreccm.ui.admin.usersgroupsroles;

import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named
public class RolesController {

    @Inject
    private RoleRepository roleRepo;

    private final LazyDataModel<Role> tableModel;

    private Role selectedRole;
    
    public RolesController() {
        tableModel = new RolesTableModel();
    }

    public LazyDataModel<Role> getTableModel() {
        return tableModel;
    }

    public List<Role> getRoles() {
        return roleRepo.findAll();
    }
    
    public Role getSelectedRole() {
        return selectedRole;
    }
    
    public void setSelectedRole(final Role selectedRole) {
        this.selectedRole = selectedRole;
    }
    
    

    private class RolesTableModel extends LazyDataModel<Role> {

        private static final long serialVersionUID = 8878060757439667086L;

        @Override
        public List<Role> load(final int first,
                               final int pageSize,
                               final String sortField,
                               final SortOrder sortOrder,
                               final Map<String, Object> filters) {
            final List<Role> roles;
            if (filters.containsKey("name")) {
                final String name = (String) filters.get("name");
                roles = roleRepo.searchByName(name, pageSize, first);
                setRowCount((int) roleRepo.searchByNameCount(name));
            } else {
                roles = roleRepo.findAllOrderedByRole(pageSize, first);
                setRowCount((int) roleRepo.count());
            }

            return roles;
        }

    }

}
