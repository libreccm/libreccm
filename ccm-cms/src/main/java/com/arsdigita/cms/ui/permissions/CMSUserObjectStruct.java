/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.permissions;

import com.arsdigita.bebop.PageState;
import com.arsdigita.ui.CcmObjectSelectionModel;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import java.util.Optional;

/**
 * This class is mainly instantiated from a PageState It is very context
 * specific for permissions. It tries to read the object_id and load the
 * corresponding ACSObject, as well as the party_id and the corresponding
 * entity.
 *
 * @author Stefan Deusch (sdeusch@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CMSUserObjectStruct {

    private final Role role;
    private final CcmObject object;

    CMSUserObjectStruct(final PageState state,
                        final CcmObjectSelectionModel selectionModel) {
        this(getRole(state), getObject(state, selectionModel));
    }

    CMSUserObjectStruct(final Long partyId,
                        final Long objectId) {
        this(loadRole(partyId), loadObject(objectId));
    }

    CMSUserObjectStruct(final Role role, final CcmObject object) {
        this.role = role;
        this.object = object;
    }

    Role getRole() {
        return role;
    }

    CcmObject getObject() {
        return object;
    }

    // Utility factory methods
    static CcmObject loadObject(final Long objectId) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CcmObjectRepository objectRepo = cdiUtil.findBean(
            CcmObjectRepository.class);

        final Optional<CcmObject> ccmObject = objectRepo.findById(objectId);
        if (!ccmObject.isPresent()) {
            throw new UncheckedWrapperException(String.format(
                "Failed to find object with ID %d.", objectId));
        }

        return ccmObject.get();
    }

    // use in package
    static Role loadRole(final Long roleId) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final RoleRepository roleRepo = cdiUtil
            .findBean(RoleRepository.class);

        final Optional<Role> role = roleRepo.findById(roleId);

        if (!role.isPresent()) {
            throw new UncheckedWrapperException(String.format(
                "Failed to find party with ID %d.", roleId));
        }

        return role.get();
    }

    public static Role getRole(final PageState state) {
//        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final Shiro shiro = cdiUtil.findBean(Shiro.class);
//
//        return shiro.getUser();
        return null;
    }

    public static CcmObject getObject(
        final PageState state, final CcmObjectSelectionModel selectionModel) {
        return selectionModel.getSelectedObject(state);
    }

}
