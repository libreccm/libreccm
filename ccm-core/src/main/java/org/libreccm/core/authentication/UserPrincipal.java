/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.core.authentication;

import java.security.Principal;
import java.util.Objects;
import org.libreccm.core.User;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class UserPrincipal implements Principal {

    private final User user;

    public UserPrincipal(final User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return user.getScreenName();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * Objects.hash(user);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserPrincipal other = (UserPrincipal) obj;
        return Objects.equals(user, other.getUser());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "user = %s"
                                 + " }",
                             super.toString(),
                             Objects.toString(user));
    }

}
