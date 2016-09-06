/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.role;

import com.arsdigita.bebop.list.ListModel;
import org.libreccm.security.Role;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a {@link ListModel} implementation for Collections of Roles.
 * This class is usable like an iterator, with an exception.
 * The {@link #next()} method only moves the iterator forward. To get elements you need to first
 * use {@link #next()} and afterwards {@link #getRole()}, {@link #getElement()} or {@link #getKey()}.
 *
 * Also remember that the iterator does not move unless {@link #next()} is called.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
class RoleListModel implements ListModel {

    private final Collection<Role> m_roles;

    private Iterator<Role> iterator;

    private Role currentRole;

    public RoleListModel(final Collection<Role> roles) {
        m_roles = roles;
        iterator = roles.iterator();
    }

    public final boolean next() {
        currentRole = iterator.next();
        return currentRole != null;
    }

    public final Object getElement() {
        return currentRole.getName();
    }

    public final String getKey() {
        return Long.toString(currentRole.getRoleId());
    }

    public final boolean isEmpty() {
        return m_roles.isEmpty();
    }

    public final Role getRole() {
        return currentRole;
    }

    public final void reset() {
        iterator = m_roles.iterator();
    }
}
