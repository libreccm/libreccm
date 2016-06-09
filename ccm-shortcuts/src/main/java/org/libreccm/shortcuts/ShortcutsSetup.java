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
package org.libreccm.shortcuts;

import org.libreccm.modules.InstallEvent;
import org.libreccm.web.AbstractCcmApplicationSetup;
import org.libreccm.web.CcmApplication;

import java.util.UUID;
import org.libreccm.security.ShortcutsRolesSetup;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ShortcutsSetup extends AbstractCcmApplicationSetup {

    public ShortcutsSetup(final InstallEvent event) {
        super(event);
    }

    @Override
    public void setup() {
        final CcmApplication shortcuts = new CcmApplication();
        shortcuts.setUuid(UUID.randomUUID().toString());
        shortcuts.setApplicationType(ShortcutsConstants.SHORTCUTS_APP_TYPE);
        shortcuts.setPrimaryUrl(ShortcutsConstants.SHORTCUTS_PRIMARY_URL);
        getEntityManager().persist(shortcuts);

        final ShortcutsRolesSetup rolesSetup = new ShortcutsRolesSetup(
                getEntityManager());
        rolesSetup.setupShortcutsRoles();

//        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final RoleRepository roleRepository = cdiUtil.findBean(
//            RoleRepository.class);
//        roleRepository.save(shortcutsManager);
//        final PermissionManager permissionManager = cdiUtil.findBean(
//            PermissionManager.class);
//        permissionManager.grantPrivilege("manage_shortcuts", shortcutsManager);
    }

}
