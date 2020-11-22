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
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.ui.Message;
import org.libreccm.ui.MessageType;
import org.libreccm.ui.admin.AdminMessages;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/categorymanager/categories")
public class CategoriesController {

    @Inject
    private AdminMessages adminMessages;

    @Inject
    private CategoryDetailsModel categoryDetailsModel;

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private DomainRepository domainRepository;
    
    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @GET
    @Path("/{categoryIdentifier}")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categoryIdentifier
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            categoryDetailsModel.setCategory(result.get());
            return "org/libreccm/ui/admin/categories/category-details.xhtml";
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(categoryIdentifier)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @GET
    @Path("/{categoryIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String editCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categoryIdentifier
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            categoryDetailsModel.setCategory(result.get());
            return "org/libreccm/ui/admin/categories/category-form.xhtml";
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(categoryIdentifier)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @GET
    @Path("/{categoryIdentifier}/subcategories/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String newSubCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categoryIdentifier
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            categoryDetailsModel.setParentCategory(result.get());
            return "org/libreccm/ui/admin/categories/category-form.xhtml";
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(categoryIdentifier)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @POST
    @Path("/{categoryIdentifier}/subcategories/move")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveSubCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifierParam,
        @FormParam("targetIdentifier") final String targetIdentifierParam
    ) {
        final Identifier categoryIdentifier = identifierParser.parseIdentifier(
            categoryIdentifierParam
        );
        final Optional<Category> categoryResult;
        switch (categoryIdentifier.getType()) {
            case ID:
                categoryResult = categoryRepository.findById(
                    Long.parseLong(categoryIdentifier.getIdentifier())
                );
                break;
            default:
                categoryResult = categoryRepository.findByUuid(
                    categoryIdentifier.getIdentifier()
                );
                break;
        }
        if (!categoryResult.isPresent()) {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(categoryIdentifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }

        final Identifier targetIdentifier = identifierParser.parseIdentifier(
            targetIdentifierParam
        );

        final Optional<Category> targetResult;
        switch (targetIdentifier.getType()) {
            case ID:
                targetResult = categoryRepository.findById(
                    Long.parseLong(targetIdentifier.getIdentifier())
                );
                break;
            default:
                targetResult = categoryRepository.findByUuid(
                    targetIdentifier.getIdentifier()
                );
                break;
        }
        if (!categoryResult.isPresent()) {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(targetIdentifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }

        final Category category = categoryResult.get();
        final Category oldParent = category.getParentCategory();
        if (oldParent == null) {
            return String.format(
                "redirect:categorymanager/categories/ID-%d",
                category.getObjectId()
            );
        }
        final Category target = targetResult.get();

        categoryManager.removeSubCategoryFromCategory(category, oldParent);
        categoryManager.addSubCategoryToCategory(category, target);

        return String.format(
            "redirect:categorymanager/categories/ID-%d", target.getObjectId()
        );
    }

    @POST
    @Path("/{categoryIdentifier}/subcategories/remove")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeSubCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categoryIdentifier
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Category category = result.get();
            final Category parentCategory = category.getParentCategory();
            if (parentCategory == null) {
                return String.format(
                    "redirect:categorymanager/categories/ID-%d",
                    category.getObjectId()
                );
            }
            categoryManager.removeSubCategoryFromCategory(category,
                                                          parentCategory
            );
            categoryRepository.delete(category);
            return String.format(
                "redirect:categorymanager/categories/ID-%d",
                parentCategory.getObjectId()
            );
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(categoryIdentifier)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @POST
    @Path("/{identifier}/title/add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTitle(
        @PathParam("identifier") final String identifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            identifierParam
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Category category = result.get();

            final Locale locale = new Locale(localeParam);
            category.getTitle().addValue(locale, value);
            categoryRepository.save(category);
            return String.format(
                "redirect:categorymanager/categories/ID-%d",
                category.getObjectId()
            );
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(identifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @POST
    @Path("/{identifier}/title/{locale}/edit")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTitle(
        @PathParam("identifier") final String identifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            identifierParam
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Category category = result.get();

            final Locale locale = new Locale(localeParam);
            category.getTitle().addValue(locale, value);
            categoryRepository.save(category);
            return String.format(
                "redirect:categorymanager/categories/ID-%d",
                category.getObjectId()
            );
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(identifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @POST
    @Path("/{identifier}/title/{locale}/remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTitle(
        @PathParam("identifier")
        final String categoryIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categoryIdentifierParam
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Category category = result.get();

            final Locale locale = new Locale(localeParam);
            category.getTitle().removeValue(locale);
            categoryRepository.save(category);
            return String.format(
                "redirect:categorymanager/categories/ID-%d",
                category.getObjectId()
            );
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(categoryIdentifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @POST
    @Path("/{identifier}decsription/add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addDescription(
        @PathParam("identifier") final String identifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            identifierParam
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Category category = result.get();

            final Locale locale = new Locale(localeParam);
            category.getDescription().addValue(locale, value);
            categoryRepository.save(category);
            return String.format(
                "redirect:categorymanager/categories/ID-%d",
                category.getObjectId()
            );
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(identifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @POST
    @Path("/{identifier}/description/{locale}/edit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDescription(
        @PathParam("identifier") final String identifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            identifierParam
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Category category = result.get();

            final Locale locale = new Locale(localeParam);
            category.getDescription().addValue(locale, value);
            categoryRepository.save(category);
            return String.format(
                "redirect:categorymanager/categories/ID-%d",
                category.getObjectId()
            );
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(identifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @POST
    @Path("/{identifier}/description/{locale}/remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam("identifier") final String identifierParam,
        @PathParam("locale") final String localeParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            identifierParam
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            default:
                result = categoryRepository.findByUuid(
                    identifier.getIdentifier()
                );
                break;
        }

        if (result.isPresent()) {
            final Category category = result.get();

            final Locale locale = new Locale(localeParam);
            category.getDescription().removeValue(locale);
            categoryRepository.save(category);
            return String.format(
                "redirect:categorymanager/categories/ID-%d",
                category.getObjectId()
            );
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(identifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    @POST
    @Path("/{categoryIdentifier}/subcategories/{subCategoryIdentifier}/reorder")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String reorderSubCategory(
        @PathParam("categoryIdentifier") final String categoryIdentifierParam,
        @PathParam("subCategoryIdentifier") final String subCategoryIdentifierParam,
        @FormParam("direction") final String direction
    ) {
        final Identifier categoryIdentifier = identifierParser.parseIdentifier(
            categoryIdentifierParam
        );
        final Identifier subCategoryIdentifier = identifierParser
            .parseIdentifier(subCategoryIdentifierParam);

        final Optional<Category> categoryResult;
        switch (categoryIdentifier.getType()) {
            case ID:
                categoryResult = categoryRepository.findById(
                    Long.parseLong(categoryIdentifier.getIdentifier())
                );
                break;
            default:
                categoryResult = categoryRepository.findByUuid(
                    categoryIdentifier.getIdentifier()
                );
                break;
        }
        final Category category;
        if (categoryResult.isPresent()) {
            category = categoryResult.get();
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(categoryIdentifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }

        final Optional<Category> subCategoryResult;
        switch (subCategoryIdentifier.getType()) {
            case ID:
                subCategoryResult = categoryRepository.findById(
                    Long.parseLong(subCategoryIdentifier.getIdentifier())
                );
                break;
            default:
                subCategoryResult = categoryRepository.findByUuid(
                    subCategoryIdentifier.getIdentifier()
                );
                break;
        }
        final Category subCategory;
        if (subCategoryResult.isPresent()) {
            subCategory = subCategoryResult.get();
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(subCategoryIdentifierParam)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }

        switch (direction) {
            case "DECREASE":
                categoryManager.decreaseCategoryOrder(subCategory, category);
                break;
            case "INCREASE":
                categoryManager.increaseCategoryOrder(subCategory, category);
                break;
            default:
                categoryDetailsModel.addMessage(
                    new Message(
                        adminMessages.getMessage(
                            "categories.invalid_direction.message",
                            Arrays.asList(direction)),
                        MessageType.WARNING
                    )
                );
        }
        
        if (category.getParentCategory() == null) {
            final Optional<Domain> categorySystem = domainRepository
                .findByRootCategory(category);
            if (categorySystem.isPresent()) {
                return String.format(
                    "redirect:categorymanager/categorysystems/ID-%d/details",
                    categorySystem.get().getObjectId()
                );
            } else {
                return String.format(
                    "redirect:categorymanager/categories/ID-%d", 
                    category.getObjectId()
                );
            }
        } else {
            return String.format(
                "redirect:categorymanager/categories/ID-%d",
                category.getObjectId()
            );
        }
    }

}
