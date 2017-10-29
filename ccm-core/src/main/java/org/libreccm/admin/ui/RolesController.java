/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui;

import org.libreccm.security.PartyRepository;
import org.libreccm.security.RoleRepository;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class RolesController implements Serializable {

    private static final long serialVersionUID = 429870757932223171L;
    
    @Inject
    private RolePartiesController partiesController;
    
    @Inject
    private PartyRepository partyRepository;
    
    @Inject
    private PartySelectorDataProvider partySelectorDataProvider;
    
    @Inject
    private RolePartiesDataProvider rolePartiesDataProvider;
    
    @Inject
    private RoleRepository roleRepository;
    
    protected RolePartiesController getPartiesController() {
        return partiesController;
    }
    
    protected PartyRepository getPartyRepository() {
        return partyRepository;
    }
    
    protected PartySelectorDataProvider getPartySelectorDataProvider() {
        return partySelectorDataProvider;
    }
    
    protected RoleRepository getRoleRepository() {
        return roleRepository;
    }
    
    protected RolePartiesDataProvider getRolePartiesDataProvider() {
        return rolePartiesDataProvider;
    }
}
