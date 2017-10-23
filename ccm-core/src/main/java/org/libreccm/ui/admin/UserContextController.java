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
package org.libreccm.ui.admin;

import org.apache.shiro.subject.Subject;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Optional;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Named
@RequestScoped
public class UserContextController implements Serializable {

    private static final long serialVersionUID = 2046117182808198398L;

    @Inject
    private Shiro shiro;

    @Inject
    private transient Subject subject;

    public boolean isLoggedIn() {
        return subject.isAuthenticated();
    }

    public String getCurrentUserName() {
        final Optional<User> user = shiro.getUser();
        
        if (user.isPresent()) {
            return String.format("%s %s",
                                 user.get().getGivenName(),
                                 user.get().getFamilyName());
            
        } else {
            return "";
        }
    }

    public void changePassword() {

    }

    public void logout() {
        subject.logout();
    }

}
