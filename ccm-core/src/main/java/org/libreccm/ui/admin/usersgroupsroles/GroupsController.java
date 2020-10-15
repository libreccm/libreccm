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
import org.libreccm.security.Group;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.ui.Message;
import org.libreccm.ui.MessageType;
import org.libreccm.ui.admin.AdminMessages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/users-groups-roles/groups")
public class GroupsController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private GroupDetailsModel groupDetailsModel;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getGroups() {
        final List<Group> groups = groupRepository.findAll();
        models.put("groups", groups);

        return "org/libreccm/ui/admin/users-groups-roles/groups.xhtml";
    }

    @GET
    @Path("/{groupIdentifier}/details")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getGroupDetails(
        @PathParam("groupIdentifier") final String groupIdentifierParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            groupIdentifierParam
        );
        final Optional<Group> result;
        switch (identifier.getType()) {
            case ID:
                result = groupRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                result = groupRepository.findByUuid(identifier.getIdentifier());
                break;
            default:
                result = groupRepository.findByName(identifier.getIdentifier());
                break;
        }

        if (result.isPresent()) {
            groupDetailsModel.setGroup(result.get());
            return "org/libreccm/ui/admin/users-groups-roles/group-details.xhtml";
        } else {
            groupDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "usersgroupsroles.groups.not_found.message",
                        Arrays.asList(groupIdentifierParam)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/group-not-found.xhtml";
        }
    }

    @GET
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String newGroup() {
        return "org/libreccm/ui/admin/users-groups-roles/group-form.xhtml";
    }

    @GET
    @Path("/{groupIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String editGroup(
        @PathParam("groupIdentifier") final String groupIdentifierParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            groupIdentifierParam
        );
        final Optional<Group> result;
        switch (identifier.getType()) {
            case ID:
                result = groupRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                result = groupRepository.findByUuid(identifier.getIdentifier());
                break;
            default:
                result = groupRepository.findByName(identifier.getIdentifier());
                break;
        }

        if (result.isPresent()) {
            groupDetailsModel.setGroup(result.get());
            return "org/libreccm/ui/admin/users-groups-roles/group-form.xhtml";
        } else {
            groupDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "usersgroupsroles.groups.not_found.message",
                        Arrays.asList(groupIdentifierParam)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/users-groups-roles/group-not-found.xhtml";
        }
    }

}
