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
 * Controller processing the POST requests from the form for creating and
 * editing categories.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/categorymanager/categories")
public class CategoryFormController {

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

    @FormParam("uniqueId")
    private String uniqueId;

    @FormParam("name")
    private String name;

    @FormParam("enabled")
    private String enabled;

    @FormParam("visible")
    private String visible;

    @FormParam("abstractCategory")
    private String abstractCategory;

    /**
     * Create a new category.
     * 
     * @param parentCategoryIdentifier Identifier of the parent category.
     * @return Redirect to the details page of the parent category.
     */
    @POST
    @Path("/{parentCategoryIdentifier}/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createCategory(
        @PathParam("parentCategoryIdentifier") final String parentCategoryIdentifier
    ) {
        final Identifier parentIdentifier = identifierParser.parseIdentifier(
            parentCategoryIdentifier
        );
        final Optional<Category> parentResult;
        switch (parentIdentifier.getType()) {
            case ID:
                parentResult = categoryRepository.findById(
                    Long.parseLong(
                        parentIdentifier.getIdentifier()
                    )
                );
                break;
            default:
                parentResult = categoryRepository.findByUuid(
                    parentIdentifier.getIdentifier()
                );
                break;
        }

        if (parentResult.isPresent()) {
            final Category parentCategory = parentResult.get();
            final Category category = new Category();
            category.setUniqueId(uniqueId);
            category.setName(name);
            category.setEnabled(enabled != null);
            category.setVisible(visible != null);
            category.setAbstractCategory(abstractCategory != null);

            categoryRepository.save(category);
            categoryManager.addSubCategoryToCategory(category, parentCategory);

            if (parentCategory.getParentCategory() == null) {
                final Optional<Domain> categorySystem = domainRepository
                    .findByRootCategory(parentCategory);
                if (categorySystem.isPresent()) {
                    return String.format(
                        "redirect:categorymanager/categorysystems/ID-%d/details",
                        categorySystem.get().getObjectId()
                    );
                } else {
                    return String.format(
                        "redirect:categorymanager/categories/ID-%d",
                        parentCategory.getObjectId()
                    );
                }
            } else {
                return String.format(
                    "redirect:categorymanager/categories/ID-%d",
                    parentCategory.getObjectId()
                );
            }
        } else {
            categoryDetailsModel.addMessage(
                new Message(
                    adminMessages.getMessage(
                        "categories.not_found.message",
                        Arrays.asList(parentCategoryIdentifier)
                    ), MessageType.WARNING
                )
            );
            return "org/libreccm/ui/admin/categories/category-not-found.xhtml";
        }
    }

    /**
     * Updates a category with the data from the form.
     * 
     * @param categoryIdentifierParam Identifier of the category to update.
     * @return Redirect to the details page of the category.
     */
    @POST
    @Path("/{categoryIdentifier}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateCategory(
        @PathParam("categoryIdentifier")
        final String categoryIdentifierParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            categoryIdentifierParam
        );
        final Optional<Category> result;
        switch (identifier.getType()) {
            case ID:
                result = categoryRepository.findById(
                    Long.parseLong(
                        identifier.getIdentifier()
                    )
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
            category.setUniqueId(uniqueId);
            category.setName(name);
            category.setEnabled(enabled != null);
            category.setVisible(visible != null);
            category.setAbstractCategory(abstractCategory != null);

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

}
