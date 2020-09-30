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
import org.libreccm.security.UserRepository;
import org.libreccm.ui.Message;
import org.libreccm.ui.MessageType;
import org.libreccm.ui.admin.AdminMessages;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.MvcContext;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/users-groups-roles/users")
public class UsersController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private MvcContext mvc;

    @Inject
    private UserDetailsModel userDetailsModel;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UsersTableModel usersTableModel;

    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getUsers(
        @QueryParam("filterterm") @DefaultValue("") final String filterTerm
    ) {
        usersTableModel.setFilterTerm(filterTerm);
        return "org/libreccm/ui/admin/users-groups-roles/users.xhtml";
    }

    @GET
    @Path("/{userIdentifier}/details")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getUserDetails(
        @PathParam("userIdentifier") final String userIdentifierParam
    ) {
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
            userDetailsModel.setUser(result.get());
            return "org/libreccm/ui/admin/users-groups-roles/user-details.xhtml";
        } else {
            userDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "usersgroupsroles.users.not_found.message",
                        Arrays.asList(userIdentifierParam)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/user-not-found.xhtml";
        }
    }

    @GET
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String newUser(
        @PathParam("userIdentifier") final String userIdentifier
    ) {
        return "org/libreccm/ui/admin/users-groups-roles/user-form.xhtml";
    }

    @GET
    @Path("/{userIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String editUser(
        @PathParam("userIdentifier") final String userIdentifierParam
    ) {
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
            userDetailsModel.setUser(result.get());
            return "org/libreccm/ui/admin/users-groups-roles/user-form.xhtml";
        } else {
            userDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "usersgroupsroles.users.not_found.message",
                        Arrays.asList(userIdentifierParam)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/user-not-found.xhtml";
        }
    }

    @POST
    @Path("/{userIdentifier}/disable")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String disableUser(
        @PathParam("userIdentifier") final String userIdentifierParam,
        @FormParam("confirmed") final boolean confirmed
    ) {
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
            user.setBanned(true);
            userRepository.save(user);
            return String.format(
                "redirect:%s", mvc.uri("UsersController#getUsers")
            );
        } else {
            userDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "usersgroupsroles.users.not_found.message",
                        Arrays.asList(userIdentifierParam)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/user-not-found.xhtml";
        }
    }

    @POST
    @Path("/{userIdentifier}/email-addresses/{emailId}/remove")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String removeEmailAddress(
        @PathParam("userIdentifier") final String userIdentifierParam,
        @PathParam("emailId") final int emailId,
        @FormParam("confirmed") final boolean confirmed
    ) {
        if (!confirmed) {
            return String.format(
                "redirect:%s",
                mvc.uri(
                    String.format(
                        "UsersController#getUserDetails",
                        "{ userIdentifier: %s}",
                        userIdentifierParam
                    )
                )
            );
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
            if (user.getEmailAddresses().size() <= emailId) {
                return String.format(
                    "redirect:%s",
                    mvc.uri(
                        String.format(
                            "UsersController#getUserDetails",
                            "{ userIdentifier: %s}",
                            userIdentifierParam
                        )
                    )
                );
            }
            user.getEmailAddresses().remove(emailId);
            userRepository.save(user);
            return String.format(
                "redirect:%s",
                mvc.uri(
                    String.format(
                        "UsersController#getUserDetails",
                        "{ userIdentifier: %s}",
                        userIdentifierParam
                    )
                )
            );
        } else {
            userDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "usersgroupsroles.users.not_found.message",
                        Arrays.asList(userIdentifierParam)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/user-not-found.xhtml";
        }
    }

}
