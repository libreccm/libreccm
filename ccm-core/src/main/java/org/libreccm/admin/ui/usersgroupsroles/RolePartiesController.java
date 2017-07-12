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
package org.libreccm.admin.ui.usersgroupsroles;

import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;

import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class RolePartiesController {

    @Inject
    private PartyRepository partyRepo;

    @Inject
    private RoleRepository roleRepo;

    @Inject
    private RoleManager roleManager;

    @Transactional(Transactional.TxType.REQUIRED)
    protected void assignPartiesToRole(final Set<Party> parties,
                                       final Role role) {

        parties.forEach(party -> assignPartyToRole(party, role));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void assignPartyToRole(final Party party, final Role role) {

        final Party theParty = partyRepo
            .findById(party.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Party with ID %d in the database.",
                    party.getPartyId())));

        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Role with ID %d in the database.",
                    role.getRoleId())));
        
        roleManager.assignRoleToParty(theRole, theParty);
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    protected void removePartyFromRole(final Party party, final Role role) {
        
        final Party theParty = partyRepo
            .findById(party.getPartyId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Party with ID %d in the database.",
                    party.getPartyId())));

        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Role with ID %d in the database.",
                    role.getRoleId())));
        
        roleManager.removeRoleFromParty(theRole, theParty);
    }

}
