/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.usersgroupsroles;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.User;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;
import org.libreccm.ui.admin.AdminMessages;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.mvc.MvcContext;
import javax.mvc.binding.BindingResult;
import javax.mvc.binding.MvcBinding;
import javax.transaction.Transactional;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Controller managing the user post requests from the user edit form.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@Path("/users-groups-roles/users/")
@RequestScoped
public class UserFormController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private BindingResult bindingResult;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;
    
    @Inject
    private MvcContext mvc;

    @Inject
    private UserManager userManager;

    @Inject
    private UserRepository userRepository;

    @MvcBinding
    @FormParam("userName")
    @NotBlank
    private String userName;

    @FormParam("givenName")
    private String givenName;

    @FormParam("familyName")
    private String familyName;

    @MvcBinding
    @FormParam("primaryEmailAddress")
    @NotBlank
    @Email
    private String primaryEmailAddress;

    @FormParam("primaryEmailAddressBouncing")
    private boolean primaryEmailAddressBouncing;

    @FormParam("primaryEmailAddressVerified")
    private boolean primaryEmailAddressVerified;

    @FormParam("banned")
    private boolean banned;

    @FormParam("passwordResetRequired")
    private boolean passwordResetRequired;

    @FormParam("password")
    private String password;

    @FormParam("passwordConfirmation")
    private String passwordConfirmation;

    @POST
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createUser() {
        if (bindingResult.isFailed()) {
            models.put("errors", bindingResult.getAllMessages());
            return "org/libreccm/ui/admin/users-groups-roles/user-form.xhtml";
        }

        if (password == null || password.isEmpty()) {
            models.put("errors", Arrays.asList(
                       adminMessages.get(
                           "usersgroupsroles.users.new.errors.password.empty")
                   ));
            return "org/libreccm/ui/admin/users-groups-roles/user-form.xhtml";
        }

        if (!Objects.equals(password, passwordConfirmation)) {
            models.put("errors", Arrays.asList(
                       adminMessages.get(
                           "usersgroupsroles.users.new.errors.password.no_match")
                   ));
            return "org/libreccm/ui/admin/users-groups-roles/user-form.xhtml";
        }

        userManager.createUser(
            givenName, familyName, userName, primaryEmailAddress, password
        );

        return "redirect:users-groups-roles/users";
    }

    @POST
    @Path("{userIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateUser(
        @PathParam("userIdentifier") final String userIdentifierParam
    ) {
        if (bindingResult.isFailed()) {
            models.put("errors", bindingResult.getAllMessages());
            return "org/libreccm/ui/admin/users-groups-roles/user-form.xhtml";
        }

        final Identifier identifier = identifierParser.parseIdentifier(
            userIdentifierParam
        );
        final Optional<User> result;
        switch (identifier.getType()) {
            case ID:
                result = userRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                result = userRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = userRepository.findByName(identifier.getIdentifier());
                break;
        }

        if (result.isPresent()) {
            final User user = result.get();
            user.setUuid(userName);
            user.setGivenName(givenName);
            user.setFamilyName(familyName);
            user.getPrimaryEmailAddress().setAddress(primaryEmailAddress);
            user
                .getPrimaryEmailAddress()
                .setBouncing(primaryEmailAddressBouncing);
            user
                .getPrimaryEmailAddress()
                .setBouncing(primaryEmailAddressVerified);
            user.setBanned(banned);
            user.setPasswordResetRequired(passwordResetRequired);

            userRepository.save(user);
            return "redirect:users-groups-roles/users";
        } else {
            models.put("errors", Arrays.asList(
                       adminMessages.getMessage(
                           "usersgroupsroles.users.not_found.message",
                           Arrays.asList(userIdentifierParam)
                       )
                   ));
            return "org/libreccm/ui/admin/users-groups-roles/user-form.xhtml";
        }
    }
    
    @POST
    @Path("{userIdentifier}/groups")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateGroupMemberships(
        @PathParam("userIdentifier") final String userIdentifierParam,
        @FormParam("userGroups") final String[] userGroups
    ) {
        // ToDo
        return String.format(
            "redirect:%s", 
            mvc.uri(
                "UsersController#getUserDetails", 
                Map.of("userIdentifier", userIdentifierParam)
            )
        );
    }
    
    @POST
    @Path("{userIdentifier}/roles")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateRoleMemberships(
        @PathParam("userIdentifier") final String userIdentifierParam,
        @FormParam("userRoles") final String[] userRoles
    ) {
        // ToDo
        return String.format(
            "redirect:%s", 
            mvc.uri(
                "UsersController#getUserDetails", 
                Map.of("userIdentifier", userIdentifierParam)
            )
        );
    }

}
