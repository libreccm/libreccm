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

import org.libreccm.configuration.ConfigurationManager;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class OneTimeAuthManager {
    
    @Inject
    private EntityManager entityManager;
    
    @Inject
    private ConfigurationManager configurationManager;
    
    public OneTimeAuthToken createForUser(final User user) {
        throw new UnsupportedOperationException();
    }
    
    public OneTimeAuthToken retrieveForUser(final User user) {
        throw new UnsupportedOperationException();
    }
    
    public boolean validTokenExistsForUser(final User user) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isValid(final OneTimeAuthToken token) {
        throw new UnsupportedOperationException();
    }
    
    public void invalidate(final OneTimeAuthToken token) {
        throw new UnsupportedOperationException();
    }
    
    
}
