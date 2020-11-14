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
package org.libreccm.ui.admin.categories;

import org.apache.commons.validator.routines.UrlValidator;
import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainManager;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.ui.Message;
import org.libreccm.ui.MessageType;
import org.libreccm.ui.admin.AdminMessages;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@Path("/categorymanager/categorysystems")
@RequestScoped
public class CategorySystemFormController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private CategorySystemDetailsModel categorySystemDetailsModel;

    @Inject
    private DomainManager domainManager;

    @Inject
    private DomainRepository domainRepository;

    @Inject
    private IdentifierParser identifierParser;

    @FormParam("domainKey")
    private String domainKey;

    @FormParam("uri")
    private String uri;

    @FormParam("version")
    private String version;

    @FormParam("released")
    private String released;

    @POST
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createCategorySystem() {

        if (!isValidUri()) {
            categorySystemDetailsModel.setDomainKey(domainKey);
            categorySystemDetailsModel.setUri(uri);
            categorySystemDetailsModel.setVersion(version);
            categorySystemDetailsModel.setReleased(released);

            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.get("categorysystems.form.errors.uri_invalid"),
                    MessageType.DANGER)
            );
            categorySystemDetailsModel.addInvalidField("uri");
            return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
        }

        final Domain domain = domainManager.createDomain(domainKey, domainKey);
        domain.setUri(uri);
        domain.setVersion(version);
        if (released == null || released.isEmpty()) {
            domain.setReleased(null);
        } else {
            domain.setReleased(convertReleased());
        }
        domainRepository.save(domain);

        return "redirect:/categorymanager/categorysystems";
    }

    @POST
    @Path("{categorySystemIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateCategorySystem(
        @PathParam("categorySystemIdentifier")
        final String identifierParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            identifierParam
        );
        final Optional<Domain> result;
        switch (identifier.getType()) {
            case ID:
                result = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                result = domainRepository.findByUuid(identifier.getIdentifier());
                break;
            default:
                result = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            if (!isValidUri()) {
                categorySystemDetailsModel.setDomainKey(domainKey);
                categorySystemDetailsModel.setUri(uri);
                categorySystemDetailsModel.setVersion(version);
                categorySystemDetailsModel.setReleased(released);

                categorySystemDetailsModel.addMessage(
                    new Message(
                        adminMessages.get(
                            "categorysystems.form.errors.uri_invalid"),
                        MessageType.DANGER)
                );
                categorySystemDetailsModel.addInvalidField("uri");
                return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
            }
            final Domain domain = result.get();
            domain.setDomainKey(domainKey);
            domain.setUri(uri);
            domain.setVersion(version);
            if (released == null || released.isEmpty()) {
                domain.setReleased(null);
            } else {
                domain.setReleased(convertReleased());
            }
            domainRepository.save(domain);

            return String.format(
                "redirect:/categorymanager/categorysystems/ID-%d/details",
                domain.getObjectId()
            );
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(identifierParam)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    private LocalDate convertReleased() {
        return LocalDate.parse(
            released,
            DateTimeFormatter.ISO_DATE.withZone(ZoneOffset.systemDefault())
        );
    }

    private boolean isValidUri() {
        final UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(uri);
    }

}
