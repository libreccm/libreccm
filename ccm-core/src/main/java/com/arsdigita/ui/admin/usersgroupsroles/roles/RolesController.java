package com.arsdigita.ui.admin.usersgroupsroles.roles;

import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleMembership;
import org.libreccm.security.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class RolesController {
    
    @Inject
    private PartyRepository partyRepo;
    
    @Inject
    private RoleRepository roleRepo;
    
    @Inject
    private RoleManager roleManager;
    
    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Party> getMembersOfRole(final Role role) {

        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Role with ID %d in the database.",
                    role.getRoleId())));

        return theRole
            .getMemberships()
            .stream()
            .map(RoleMembership::getMember)
            .sorted((role1, role2) -> role1.getName().compareTo(role2.getName()))
            .collect(Collectors.toList());
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    protected List<String> getNamesOfMembersOfRole(final Role role) {
        
        final Role theRole = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Role with ID %d in the database.",
                    role.getRoleId())));

        return theRole
            .getMemberships()
            .stream()
            .map(RoleMembership::getMember)
            .map(Party::getName)
            .sorted((name1, name2) -> name1.compareTo(name2))
            .collect(Collectors.toList());
        
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    protected void assignRoleToParty(final Role role, final Party party) {
        
        final Party assignee = partyRepo
        .findById(party.getPartyId())
        .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Party with ID %d in the database.",
                    party.getPartyId())));
        
        final Role assignTo = roleRepo
            .findById(role.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Role with ID %d in the database.",
                    role.getRoleId())));
        
        roleManager.assignRoleToParty(assignTo, assignee);
    }
    
}
 