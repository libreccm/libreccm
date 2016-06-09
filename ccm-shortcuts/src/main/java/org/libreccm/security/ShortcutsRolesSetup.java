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
package org.libreccm.security;

import javax.persistence.EntityManager;
import org.libreccm.shortcuts.ShortcutsConstants;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ShortcutsRolesSetup {

    private final EntityManager entityManager;

    public ShortcutsRolesSetup(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setupShortcutsRoles() {
        final Role shortcutsManager = new Role();
        shortcutsManager.setName("shortcuts-manager");
        entityManager.persist(shortcutsManager);

        final Permission permission = new Permission();
        permission.setGrantee(shortcutsManager);
        permission.setGrantedPrivilege(
                ShortcutsConstants.SHORTSCUT_MANAGE_PRIVILEGE);
        permission.setObject(null);
        
        entityManager.persist(permission);
    }

}
