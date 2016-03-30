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

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ChallengeManager {

    public String createEmailVerification(final User user) {
        throw new UnsupportedOperationException();
    }
    
    public void sendEmailVerification(final User user) {
        throw new UnsupportedOperationException();
    }

    public void finishEmailVerification(final User user,
                                        final String submittedToken) {
        throw new UnsupportedOperationException();
    }

    public String createEmailActivation(final User user) {
        throw new UnsupportedOperationException();
    }
    
    public void sendUserActivation(final User user) {
        throw new UnsupportedOperationException();
    }

    public void finishUserActivation(final User user,
                                     final String submittedToken) {
        throw new UnsupportedOperationException();
    }
    
    public void sendPasswordRecover(final User user) {
        throw new UnsupportedOperationException();
    }
    
    public void sendPasswordRevover(final User user) {
        throw new UnsupportedOperationException();
    }
    
    public void finishPasswordRecover(final User user, 
                                      final String submittedToken,
                                      final String newPassword) {
        throw new UnsupportedOperationException();
    }

}
