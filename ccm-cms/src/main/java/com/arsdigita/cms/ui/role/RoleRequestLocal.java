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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;

import org.libreccm.security.Role;

import java.util.Optional;

/**
 * See {@link RequestLocal} for more information.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
abstract class RoleRequestLocal extends RequestLocal {

    final Role getRole(final PageState state) {
        @SuppressWarnings("unchecked")
        final Optional<Role> role = (Optional<Role>) get(state);
        
        return role.get();
    }

}
