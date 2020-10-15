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
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.ui.admin.AdminMessages;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.mvc.MvcContext;
import javax.mvc.binding.BindingResult;
import javax.mvc.binding.MvcBinding;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@Path("/users-groups-roles/groups/")
@RequestScoped
public class GroupFormController {

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
    private GroupManager groupManager;

    @Inject
    private GroupRepository groupRepository;

    @MvcBinding
    @FormParam("groupName")
    @NotBlank
    private String groupName;

    @POST
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createGroup() {
        if (bindingResult.isFailed()) {
            models.put("errors", bindingResult.getAllMessages());
            return "org/libreccm/ui/admin/users-groups-roles/group-form.xhtml";
        }

        final Group group = new Group();
        group.setName(groupName);
        groupRepository.save(group);

        return "redirect:users-groups-roles/groups";
    }

    @POST
    @Path("{groupIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateUser(
        @PathParam("groupIdentifier") final String groupIdentifierParam
    ) {
        if (bindingResult.isFailed()) {
            models.put("errors", bindingResult.getAllMessages());
            return "org/libreccm/ui/admin/users-groups-roles/group-form.xhtml";
        }

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
            final Group group = result.get();
            group.setName(groupName);

            groupRepository.save(group);
            return "redirect:users-groups-roles/groups";
        } else {
            models.put("errors", Arrays.asList(
                       adminMessages.getMessage(
                           "usersgroupsroles.groups.not_found.message",
                           Arrays.asList(groupIdentifierParam)
                       )
                   ));
            return "org/libreccm/ui/admin/users-groups-roles/group-form.xhtml";
        }
    }

}
