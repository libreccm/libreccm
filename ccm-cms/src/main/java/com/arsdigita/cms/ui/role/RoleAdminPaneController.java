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
package com.arsdigita.cms.ui.role;

import org.libreccm.security.Party;
import org.libreccm.security.Permission;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.ArrayList;
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
public class RoleAdminPaneController {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private RoleRepository roleRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Role> findRolesForContentSection(final ContentSection section) {
        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with id %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getRoles());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String generateGrantedPermissionsString(final Role role) {
        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No role with ID %d in the database. Where did that Id come from?",
            role.getRoleId())));

        return theRole.getPermissions().stream()
            .map(Permission::getGrantedPrivilege)
            .collect(Collectors.joining(", "));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Party> createRoleMemberList(final Role role) {
        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No role with ID %d in the database. Where did that Id come from?",
            role.getRoleId())));

        return theRole.getMemberships()
            .stream()
            .map(membership -> membership.getMember())
            .sorted((member1, member2) -> {
                return member1.getName().compareTo(member2.getName());
            })
            .collect(Collectors.toList());
    }

}
