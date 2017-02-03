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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.security.Permission;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMembership;
import org.libreccm.security.RoleRepository;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.Set;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Named
@ViewScoped
public class RolesController implements Serializable {

    private static final long serialVersionUID = 9092665507680111584L;
    private static final Logger LOGGER = LogManager.getLogger(
        RolesController.class);

    @Inject
    private RoleRepository roleRepo;

    private final LazyDataModel<Role> tableModel;

    private Role selectedRole;
    private String selectedRoleName;

    public RolesController() {
        LOGGER.debug("Intializing RolesController and creating table model...");
        tableModel = new RolesTableModel();
    }

    public LazyDataModel<Role> getTableModel() {
        LOGGER.debug("getTableModel invoked...");
        return tableModel;
    }

    public List<Role> getRoles() {
        LOGGER.debug("getRoles invoked...");
        return roleRepo.findAll();
    }

    public Role getSelectedRole() {
        LOGGER.debug("getSelectedRole invoked...");
        return selectedRole;
    }

    public void setSelectedRole(final Role selectedRole) {
        LOGGER.debug("Setting selected role to \"{}\"...", selectedRole);
        this.selectedRole = selectedRole;
        selectedRoleName = selectedRole.getName();
    }

    public String getSelectedRoleName() {
        return selectedRoleName;
    }

    public void setSelectedRoleName(final String name) {
        selectedRoleName = name;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Set<RoleMembership> getSelectedRoleMemberships() {
        final Role role = roleRepo.findById(selectedRole.getRoleId()).get();
        return role.getMemberships();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Permission> getSelectedRolePermissions() {
        final Role role = roleRepo.findById(selectedRole.getRoleId(),
                                            Role.ENTITY_GRPAH_WITH_PERMISSIONS)
            .get();
        return role.getPermissions();
    }

    public void renameSelectedRole() {
        selectedRole.setName(selectedRoleName);
        roleRepo.save(selectedRole);
    }

    public void renameSelectedRoleCancel() {
        selectedRoleName = selectedRole.getName();
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
