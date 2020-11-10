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
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/categorymanager")
public class CategorySystemsController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private CategorySystemDetailsModel categorySystemDetailsModel;

    @Inject
    private ApplicationRepository applicationRepository;

    @Inject
    private DomainManager domainManager;

    @Inject
    private DomainRepository domainRepository;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getCategoryManager() {
        return getCategorySystems();
    }

    @GET
    @Path("/categorysystems")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getCategorySystems() {
        return "org/libreccm/ui/admin/categories/categorysystems.xhtml";
    }

    @GET
    @Path("/categorysystems/{categorySystemIdentifier}/details")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getCategorySystemDetails(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> result;
        switch (identifier.getType()) {
            case ID:
                result = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                result = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            categorySystemDetailsModel.setCategorySystem(result.get());
            return "org/libreccm/ui/admin/categories/categorysystem-details.xhtml";
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    @GET
    @Path("/categorysystems/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String newCategorySystem() {
        return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
    }

    @GET
    @Path("/categorysystems/{categorySystemIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String editCategorySystem(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> result;
        switch (identifier.getType()) {
            case ID:
                result = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                result = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            categorySystemDetailsModel.setCategorySystem(result.get());
            return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/delete")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteCategorySystem(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @FormParam("confirmed") final String confirmed
    ) {
        if (Objects.equals(confirmed, "true")) {
            final Identifier identifier = identifierParser.parseIdentifier(
                categorySystemIdentifier
            );
            final Optional<Domain> result;
            switch (identifier.getType()) {
                case ID:
                    result = domainRepository.findById(
                        Long.parseLong(identifier.getIdentifier()
                        )
                    );
                    break;
                case UUID:
                    result = domainRepository.findByUuid(
                        identifier.getIdentifier()
                    );
                    break;
                default:
                    result = domainRepository.findByDomainKey(
                        identifier.getIdentifier()
                    );
                    break;
            }

            if (result.isPresent()) {
                domainRepository.delete(result.get());
            } else {
                categorySystemDetailsModel.addMessage(
                    new Message(
                        adminMessages.getMessage(
                            "categorysystems.not_found.message",
                            Arrays.asList(categorySystemIdentifier)
                        ),
                        MessageType.WARNING
                    )
                );
                return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
            }
        }
        return "redirect:categorymanager/categorysystems";
    }

    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/title/add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTitle(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> result;
        switch (identifier.getType()) {
            case ID:
                result = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                result = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Domain domain = result.get();

            final Locale locale = new Locale(localeParam);
            domain.getTitle().addValue(locale, value);
            domainRepository.save(domain);
            categorySystemDetailsModel.setCategorySystem(domain);
            return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/title/${locale}/edit")
    @AuthorizationRequired
    public String editTitle(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> result;
        switch (identifier.getType()) {
            case ID:
                result = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                result = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Domain domain = result.get();

            final Locale locale = new Locale(localeParam);
            domain.getTitle().addValue(locale, value);
            domainRepository.save(domain);
            categorySystemDetailsModel.setCategorySystem(domain);
            return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/title/${locale}/remove")
    @AuthorizationRequired
    public String removeTitle(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @PathParam("locale") final String localeParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> result;
        switch (identifier.getType()) {
            case ID:
                result = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                result = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Domain domain = result.get();

            final Locale locale = new Locale(localeParam);
            domain.getTitle().removeValue(locale);
            domainRepository.save(domain);
            categorySystemDetailsModel.setCategorySystem(domain);
            return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/description/add")
    @AuthorizationRequired
    public String addDescription(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> result;
        switch (identifier.getType()) {
            case ID:
                result = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                result = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Domain domain = result.get();

            final Locale locale = new Locale(localeParam);
            domain.getDescription().addValue(locale, value);
            domainRepository.save(domain);
            categorySystemDetailsModel.setCategorySystem(domain);
            return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    @POST
    @Path(
        "/categorysystems/{categorySystemIdentifier}/description/${locale}/edit")
    @AuthorizationRequired
    public String editDescription(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> result;
        switch (identifier.getType()) {
            case ID:
                result = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                result = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Domain domain = result.get();

            final Locale locale = new Locale(localeParam);
            domain.getDescription().addValue(locale, value);
            domainRepository.save(domain);
            categorySystemDetailsModel.setCategorySystem(domain);
            return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    @POST
    @Path(
        "/categorysystems/{categorySystemIdentifier}/description/${locale}/remove")
    @AuthorizationRequired
    public String removeDescription(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @PathParam("locale") final String localeParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> result;
        switch (identifier.getType()) {
            case ID:
                result = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                result = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                result = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Domain domain = result.get();

            final Locale locale = new Locale(localeParam);
            domain.getDescription().removeValue(locale);
            domainRepository.save(domain);
            categorySystemDetailsModel.setCategorySystem(domain);
            return "org/libreccm/ui/admin/categories/categorysystem-form.xhtml";
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/owners/add")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String addOwner(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @FormParam("applicationUuid") final String applicationUuid
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> domainResult;
        switch (identifier.getType()) {
            case ID:
                domainResult = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                domainResult = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                domainResult = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (domainResult.isPresent()) {
            final Domain domain = domainResult.get();

            final Optional<CcmApplication> appResult = applicationRepository
                .findByUuid(applicationUuid);
            if (!appResult.isPresent()) {
                categorySystemDetailsModel.addMessage(
                    new Message(
                        adminMessages.getMessage(
                            "categorysystems.add_owner.not_found.message",
                            Arrays.asList(applicationRepository)
                        ),
                        MessageType.WARNING
                    )
                );
                return "org/libreccm/ui/admin/categories/application-not-found.xhtml";
            }

            final CcmApplication owner = appResult.get();

            domainManager.addDomainOwner(owner, domain);

            return String.format(
                "redirect:categorymanager/categorysystems/%s",
                categorySystemIdentifier
            );
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

    @POST
    @Path("/categorysystems/{categorySystemIdentifier}/owners/remove")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String removeOwner(
        @PathParam("categorySystemIdentifier")
        final String categorySystemIdentifier,
        @FormParam("applicationUuid") final String applicationUuid
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categorySystemIdentifier
        );
        final Optional<Domain> domainResult;
        switch (identifier.getType()) {
            case ID:
                domainResult = domainRepository.findById(
                    Long.parseLong(identifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                domainResult = domainRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
            default:
                domainResult = domainRepository.findByDomainKey(
                    identifier.getIdentifier()
                );
                break;
        }

        if (domainResult.isPresent()) {
            final Domain domain = domainResult.get();

            final Optional<CcmApplication> appResult = applicationRepository
                .findByUuid(applicationUuid);
            if (!appResult.isPresent()) {
                categorySystemDetailsModel.addMessage(
                    new Message(
                        adminMessages.getMessage(
                            "categorysystems.add_owner.not_found.message",
                            Arrays.asList(applicationRepository)
                        ),
                        MessageType.WARNING
                    )
                );
                return "org/libreccm/ui/admin/categories/application-not-found.xhtml";
            }

            final CcmApplication owner = appResult.get();

            domainManager.removeDomainOwner(owner, domain);

            return String.format(
                "redirect:categorymanager/categorysystems/%s",
                categorySystemIdentifier
            );
        } else {
            categorySystemDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categorysystems.not_found.message",
                        Arrays.asList(categorySystemIdentifier)
                    ),
                    MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/categorysystem-not-found.xhtml";
        }
    }

}
