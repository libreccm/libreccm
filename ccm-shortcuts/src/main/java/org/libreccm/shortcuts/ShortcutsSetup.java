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

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.modules.InstallEvent;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.web.AbstractCcmApplicationSetup;
import org.libreccm.web.CcmApplication;

import java.util.UUID;

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

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final RoleRepository roleRepository = cdiUtil.findBean(
            RoleRepository.class);
        final Role shortcutsManager = new Role();
        shortcutsManager.setName("shortcuts-manager");
        roleRepository.save(shortcutsManager);

        final PermissionManager permissionManager = cdiUtil.findBean(
            PermissionManager.class);
        permissionManager.grantPrivilege("manage_shortcuts", shortcutsManager);
    }

}
