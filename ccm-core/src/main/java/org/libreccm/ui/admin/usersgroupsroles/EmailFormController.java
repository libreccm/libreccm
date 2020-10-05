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
import org.libreccm.core.EmailAddress;
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
import javax.mvc.Models;
import javax.mvc.binding.BindingResult;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path(
    "/users-groups-roles/users/{userIdentifier}/email-addresses/{emailIdentifier}/save")
public class EmailFormController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private BindingResult bindingResult;

    @Inject
    private EmailFormModel emailFormModel;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private UserDetailsModel userDetailsModel;

    @Inject
    private UserRepository userRepository;

    // MVC does not work with Krazo 1.1.0-M1
//    @MvcBinding 
    @FormParam("address")
//    @NotBlank
//    @Email
    private String address;

    @FormParam("bouncing")
    private String bouncingParam;

    @FormParam("verified")
    private String verifiedParam;

    @POST
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String saveEmailAddress(
        @PathParam("userIdentifier") final String userIdentifierParam,
        @PathParam("emailIdentifier") final String emailIdentifierParam
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

            // MVC Binding does not work with Krazo 1.1.0-M1
//            if (bindingResult.isFailed()) {
//                models.put("errors", bindingResult.getAllMessages());
//              emailFormModel.setUserIdentifier(userIdentifierParam);
//                emailFormModel.setAddress(address);
//                emailFormModel.setBouncing(bouncing);
//                emailFormModel.setVerified(verified);
//
//                return "org/libreccm/ui/admin/users-groups-roles/email-form.xhtml";
//            }
            if (address == null || address.matches("\\s*")) {
                emailFormModel.addMessage(
                    new Message(
                        "usergroupsroles.users.user_details.email_addresses.errors.address_empty",
                        MessageType.DANGER)
                );
                emailFormModel.setUserIdentifier(userIdentifierParam);
                emailFormModel.setAddress(address);
                emailFormModel.setBouncing(bouncingParam != null);
                emailFormModel.setVerified(verifiedParam != null);

                return "org/libreccm/ui/admin/users-groups-roles/email-form.xhtml";
            }

            if ("new".equals(emailIdentifierParam)) {
                return addEmailAddress(user);
            } else {
                return updateEmailAddress(
                    userIdentifierParam,
                    user,
                    Integer.parseInt(emailIdentifierParam)
                );
            }

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

    private String addEmailAddress(final User user) {
        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress(address);
        emailAddress.setBouncing(bouncingParam != null);
        emailAddress.setVerified(verifiedParam != null);
        user.addEmailAddress(emailAddress);

        userRepository.save(user);

        return String.format(
            "redirect:/users-groups-roles/users/%s/details",
            user.getName()
        );
    }

    private String updateEmailAddress(
        final String userIdentifierParam,
        final User user,
        final int emailId
    ) {
        if (user.getEmailAddresses().size() <= emailId) {
            models.put("error.userIdentifier", userIdentifierParam);
            models.put("error.emailId", emailId);
            return "org/libreccm/ui/admin/users-groups-roles/email-not-found.xhtml";
        } else {
            final EmailAddress emailAddress = user
                .getEmailAddresses()
                .get(emailId);

            emailAddress.setAddress(address);
            emailAddress.setBouncing(bouncingParam != null);
            emailAddress.setVerified(verifiedParam != null);

            userRepository.save(user);

            return String.format(
                "redirect:/users-groups-roles/users/%s/details",
                user.getName()
            );
        }
    }

//    @POST
//    @Path("/users-groups-roles/users/{userIdentifier}/email-addresses/new")
//    @AuthorizationRequired
//    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
//    @Transactional(Transactional.TxType.REQUIRED)
//    public String addNewEmailAddress(
//        @PathParam("userIdentifier") final String userIdentifierParam
//    ) {
//        final Identifier identifier = identifierParser.parseIdentifier(
//            userIdentifierParam
//        );
//        final Optional<User> result;
//        switch (identifier.getType()) {
//            case ID:
//                result = userRepository.findById(
//                    Long.parseLong(identifier.getIdentifier())
//                );
//                break;
//            case UUID:
//                result = userRepository.findByUuid(
//                    identifier.getIdentifier()
//                );
//                break;
//            default:
//                result = userRepository.findByName(identifier.getIdentifier());
//                break;
//        }
//
//        if (result.isPresent()) {
//            final User user = result.get();
//
//            if (bindingResult.isFailed()) {
//                models.put("errors", bindingResult.getAllMessages());
//                emailFormModel.setUserIdentifier(userIdentifierParam);
//                emailFormModel.setAddress(address);
//                emailFormModel.setBouncing(bouncing);
//                emailFormModel.setVerified(verified);
//
//                return "org/libreccm/ui/admin/users-groups-roles/email-form.xhtml";
//            }
//
//            final EmailAddress emailAddress = new EmailAddress();
//            emailAddress.setAddress(address);
//            emailAddress.setBouncing(bouncing);
//            emailAddress.setVerified(verified);
//            user.addEmailAddress(emailAddress);
//
//            userRepository.save(user);
//
//            return String.format(
//                "redirect:/users-groups-roles/users/%s/details",
//                user.getName()
//            );
//        } else {
//            userDetailsModel.addMessage(
//                new Message(
//                    adminMessages.getMessage(
//                        "usersgroupsroles.users.not_found.message",
//                        Arrays.asList(userIdentifierParam)
//                    ),
//                    MessageType.WARNING
//                )
//            );
//            return "org/libreccm/ui/admin/users-groups-roles/user-not-found.xhtml";
//        }
//    }
//
//    @POST
//    @Path("/users-groups-roles/users/{userIdentifier}/email-addresses/{emailId}")
//    @AuthorizationRequired
//    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
//    @Transactional(Transactional.TxType.REQUIRED)
//    public String updateEmailAddress(
//        @PathParam("userIdentifier") final String userIdentifierParam,
//        @PathParam("emailId") final int emailId
//    ) {
//        final Identifier identifier = identifierParser.parseIdentifier(
//            userIdentifierParam
//        );
//        final Optional<User> result;
//        switch (identifier.getType()) {
//            case ID:
//                result = userRepository.findById(
//                    Long.parseLong(identifier.getIdentifier())
//                );
//                break;
//            case UUID:
//                result = userRepository.findByUuid(
//                    identifier.getIdentifier()
//                );
//                break;
//            default:
//                result = userRepository.findByName(identifier.getIdentifier());
//                break;
//        }
//
//        if (result.isPresent()) {
//            final User user = result.get();
//
//            if (bindingResult.isFailed()) {
//                models.put("errors", bindingResult.getAllMessages());
//                emailFormModel.setUserIdentifier(userIdentifierParam);
//                emailFormModel.setEmailId(emailId);
//                emailFormModel.setAddress(address);
//                emailFormModel.setBouncing(bouncing);
//                emailFormModel.setVerified(verified);
//
//                return "org/libreccm/ui/admin/users-groups-roles/email-form.xhtml";
//            }
//
//            if (user.getEmailAddresses().size() <= emailId) {
//                models.put("error.userIdentifier", userIdentifierParam);
//                models.put("error.emailId", emailId);
//                return "org/libreccm/ui/admin/users-groups-roles/email-not-found.xhtml";
//            } else {
//                final EmailAddress emailAddress = user
//                    .getEmailAddresses()
//                    .get(emailId);
//
//                emailAddress.setAddress(address);
//                emailAddress.setBouncing(bouncing);
//                emailAddress.setVerified(verified);
//
//                userRepository.save(user);
//
//                return "org/libreccm/ui/admin/users-groups-roles/email-form.xhtml";
//            }
//        } else {
//            userDetailsModel.addMessage(
//                new Message(
//                    adminMessages.getMessage(
//                        "usersgroupsroles.users.not_found.message",
//                        Arrays.asList(userIdentifierParam)
//                    ),
//                    MessageType.WARNING
//                )
//            );
//
//            return "org/libreccm/ui/admin/users-groups-roles/user-not-found.xhtml";
//        }
//    }
}
