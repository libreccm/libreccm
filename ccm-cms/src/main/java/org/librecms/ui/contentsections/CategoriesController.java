/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

import com.arsdigita.cms.ui.authoring.multipartarticle.SectionPreviewPanel;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
 * Controller for managing category systems assigned to a content section and
 * their categories.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/categorysystems")
public class CategoriesController {

    /**
     * The {@link CategoryManager} to use.
     */
    @Inject
    private CategoryManager categoryManager;

    /**
     * The {@link CategoryRepository} to use.
     */
    @Inject
    private CategoryRepository categoryRepo;

    /**
     * The {@link CategorySystemModel} which stores the data of the selected
     * category system for the view.
     */
    @Inject
    private CategorySystemModel categorySystemModel;

    /**
     * The {@link ContentSectionModel} which stores the data of the current
     * content section for the view.
     */
    @Inject
    private ContentSectionModel sectionModel;

    /**
     * The {@link ContentSectionRepository} to use.
     */
    @Inject
    private ContentSectionRepository sectionRepo;

    /**
     * Provides common functions for controllers working with content sections.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * The {@link GlobalizationHelper} to use.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * The {@link IdentifierParser} to use.
     */
    @Inject
    private IdentifierParser identifierParser;

    /**
     * The {@link Models} instance to use to provide data for the view.
     */
    @Inject
    private Models models;

    /**
     * The {@link PermissionChecker} to use.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Lists all category systems ({@link Domain}s) assigned to the current
     * {@link ContentSection}.
     *
     * @param sectionIdentifier The identifier of the current content section.
     *
     * @return The template to use for generating the view.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listCategorySystems(
        @PathParam("sectionIdentifier") final String sectionIdentifier
    ) {
        final Optional<ContentSection> sectionResult = retrieveContentSection(
            sectionIdentifier);
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CATEGORIES, section
        )) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier
            );
        }

        final List<DomainListEntryModel> domains = section
            .getDomains()
            .stream()
            .map(this::buildDomainListEntryModel)
            .collect(Collectors.toList());

        models.put("categorySystems", domains);

        return "org/librecms/ui/contentsection/categorysystems/categorysystems.xhtml";
    }

    /**
     * Show the root category of a category system.
     *
     * @param sectionIdentifier The identifier of the curent content section.
     * @param context           The mapping context of the assigned category
     *                          system.
     *
     * @return The template to use for generating the view.
     */
    @GET
    @Path("/{context}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCategorySystemRoot(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context
    ) {
        return String.format(
            "redirect:/%s/categorysystems/%s/categories",
            sectionIdentifier,
            context
        );
    }

    /**
     * Show the root category of a category system.
     *
     * @param sectionIdentifier The identifier of the curent content section.
     * @param context           The mapping context of the assigned category
     *                          system.
     *
     * @return The template to use for generating the view.
     */
    @GET
    @Path("/{context}/categories")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCategorySystem(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context
    ) {
        return showCategorySystem(sectionIdentifier, context, "");
    }

    /**
     * Show a category of a category system.
     *
     * @param sectionIdentifier The identifier of the curent content section.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPath      The path of the category to show.
     *
     * @return The template to use for generating the view.
     */
    @GET
    @Path("/{context}/categories/{categoryPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCategorySystem(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPath
    ) {
        final Optional<ContentSection> sectionResult = retrieveContentSection(
            sectionIdentifier);
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CATEGORIES, section
        )) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifier
            );
        }
        
        final Optional<DomainOwnership> domainResult = section
            .getDomains()
            .stream()
            .filter(domain -> domain.getContext().equals(context))
            .findAny();
        if (!domainResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("context", context);
            return "org/librecms/ui/contentsection/categorysystems/categorysystem-not-found.xhtml";
        }
        categorySystemModel.setSelectedCategorySystem(
            domainResult
                .map(this::buildDomainListEntryModel)
                .get()
        );

        categorySystemModel
            .setCategorySystems(
                section
                    .getDomains()
                    .stream()
                    .map(this::buildDomainListEntryModel)
                    .collect(Collectors.toList())
            );

        final Domain domain = domainResult.get().getDomain();
        final String activePath;
        if (categoryPath.isEmpty()) {
            activePath = "/";
        } else {
            activePath = String.format("/%s", categoryPath);
        }
        categorySystemModel.setCategoryTree(
            buildCategoryTree(domain, activePath)
        );

        final Category category;
        if (categoryPath.isEmpty()) {
            category = domain.getRoot();
        } else {
            final Optional<Category> categoryResult = categoryRepo
                .findByPath(domain, categoryPath);
            if (!categoryResult.isPresent()) {
                models.put("sectionIdentifier", sectionIdentifier);
                models.put("context", context);
                models.put("categoryPath", categoryPath);
                return "org/librecms/ui/contentsection/categorysystems/category-not-found.xhtml";
            }
            category = categoryResult.get();
        }

        categorySystemModel.setSelectedCategory(buildCategoryModel(category));

        return "org/librecms/ui/contentsection/categorysystems/categorysystem.xhtml";
    }

    /**
     * Add a localized value to the title of the root category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/@title/add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTitle(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        return addTitle(sectionIdentifier, context, "", localeParam, value);
    }

    /**
     * Add a localized value to the title of the category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPathParam The path of the category.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/{categoryPath:(.+)?}/@title/add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTitle(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPathParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final String categoryPath;
        if (categoryPathParam == null || categoryPathParam.isEmpty()) {
            categoryPath = "/";
        } else {
            categoryPath = categoryPathParam;
        }
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Locale locale = new Locale(localeParam);
            category.getTitle().addValue(locale, value);
            categoryRepo.save(category);

            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryPathParam
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Update a localized value of the title of the root category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/@title/edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTitle(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        return editTitle(
            sectionIdentifier, context, "", localeParam, value
        );
    }

    /**
     * Edit the localized value to the title of a category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPathParam The path of the category.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/{categoryPath:(.+)?}/@title/edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTitle(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPathParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final String categoryPath;
        if (categoryPathParam == null || categoryPathParam.isEmpty()) {
            categoryPath = "/";
        } else {
            categoryPath = categoryPathParam;
        }
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Locale locale = new Locale(localeParam);
            category.getTitle().addValue(locale, value);
            categoryRepo.save(category);

            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryPathParam
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Remove a localized value from the title of the root category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/@title/remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTitle(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        return removeTitle(sectionIdentifier, context, "", localeParam, value);
    }

    /**
     * Remove a localized value from the title of the category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPathParam The path of the current category.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/{categoryPath:(.+)?}/@title/remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTitle(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPathParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final String categoryPath;
        if (categoryPathParam == null || categoryPathParam.isEmpty()) {
            categoryPath = "/";
        } else {
            categoryPath = categoryPathParam;
        }
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Locale locale = new Locale(localeParam);
            category.getTitle().removeValue(locale);
            categoryRepo.save(category);
            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryPathParam
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Add a localized value to the description of the root category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/@description/add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        return addDescription(
            sectionIdentifier, context, "", localeParam, value
        );
    }

    /**
     * Add a localized value to the description of a category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPathParam The path of the category.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/{categoryPath:(.+)?}/@description/add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPathParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final String categoryPath;
        if (categoryPathParam == null || categoryPathParam.isEmpty()) {
            categoryPath = "/";
        } else {
            categoryPath = categoryPathParam;
        }
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Locale locale = new Locale(localeParam);
            category.getDescription().addValue(locale, value);
            categoryRepo.save(category);

            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryPathParam
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Update a localized value of the description of the root category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/@description/edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        return editDescription(
            sectionIdentifier, context, "", localeParam, value
        );
    }

    /**
     * Edit the localized value to the description of a category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPathParam The path of the category.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path(
        "/{context}/categories/{categoryPath:(.+)?}/@description/edit/{locale}"
    )
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPathParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final String categoryPath;
        if (categoryPathParam == null || categoryPathParam.isEmpty()) {
            categoryPath = "/";
        } else {
            categoryPath = categoryPathParam;
        }
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Locale locale = new Locale(localeParam);
            category.getDescription().addValue(locale, value);
            categoryRepo.save(category);

            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryPathParam
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Remove a localized value from the description of the root category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path("/{context}/categories/@description/remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        return removeDescription(
            sectionIdentifier, context, "", localeParam, value
        );
    }

    /**
     * Remove a localized value from the description of the category.
     *
     * @param sectionIdentifier The identifier of the current content section .
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPathParam The path of the current category.
     * @param localeParam       The locale of the new value.
     * @param value             The new value.
     *
     * @return A redirect to the categories page or the template for generating
     *         an error message view.
     */
    @POST
    @Path(
        "/{context}/categories/{categoryPath:(.+)?}/@description/remove/{locale}"
    )
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPathParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final String categoryPath;
        if (categoryPathParam == null || categoryPathParam.isEmpty()) {
            categoryPath = "/";
        } else {
            categoryPath = categoryPathParam;
        }
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Locale locale = new Locale(localeParam);
            category.getDescription().removeValue(locale);
            categoryRepo.save(category);
            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryPathParam
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Update some properties of a category.
     *
     * @param sectionIdentifier The identifier of the current
     *                          {@link ContentSection}.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPath      The path of the current category.
     * @param categoryName      The new name of the category.
     * @param uniqueId          The unique ID of the category.
     * @param isEnabled         Is the category enabled?
     * @param isVisible         Is the category visible?
     * @param isAbstract        Is the category an abstract category?
     *
     * @return A redirect to the category detail page or a view for generating
     *         an error message.
     */
    @POST
    @Path("/{context}/categories/{categoryPath:(.+)?}/@properties")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateCategory(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPath,
        @FormParam("categoryName") final String categoryName,
        @FormParam("uniqueId") final String uniqueId,
        @FormParam("isEnabled") final String isEnabled,
        @FormParam("isVisible") final String isVisible,
        @FormParam("isAbstract") final String isAbstract
    ) {
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            category.setName(categoryName);
            category.setUniqueId(uniqueId);
            category.setEnabled(Objects.equals("true", isEnabled));
            category.setVisible(Objects.equals("true", isVisible));
            category.setAbstractCategory(Objects.equals("true", isAbstract));
            categoryRepo.save(category);

            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s/%s",
                sectionIdentifier,
                context,
                categoryManager.getCategoryPath(category.getParentCategory()),
                categoryName
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Sets the index element of a category.
     *
     * @param sectionIdentifier The identifier of the current
     *                          {@link ContentSection}.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPath      The path of the current category.
     * @param indexElementUuid  The UUID of the new index element.
     *
     * @return A redirect to the category page or the template for generating an
     *         error view.
     */
    @GET
    @Path(
        "/{context}/categories/{categoryPath:(.+)?}/@index-element/{indexElementUuid}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String setIndexElement(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPath,
        @PathParam("indexElementUuid") final String indexElementUuid
    ) {
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Optional<Categorization> categorizationResult = category
                .getObjects()
                .stream()
                .filter(
                    categorization -> Objects.equals(
                        categorization.getUuid(), indexElementUuid
                    )
                ).findAny();
            if (categorizationResult.isPresent()) {
                final CcmObject object = categorizationResult
                    .get()
                    .getCategorizedObject();
                try {
                    categoryManager.setIndexObject(category, object);
                } catch (ObjectNotAssignedToCategoryException ex) {
                    models.put("sectionIdentifier", sectionIdentifier);
                    models.put("context", context);
                    models.put("categoryPath", categoryPath);
                    models.put("categorizationUuid", indexElementUuid);
                    return "org/librecms/ui/contentsection/categorysystems/categorization-not-found.xhtml";
                }
            } else {
                models.put("sectionIdentifier", sectionIdentifier);
                models.put("context", context);
                models.put("categoryPath", categoryPath);
                models.put("categorizationUuid", indexElementUuid);
                return "org/librecms/ui/contentsection/categorysystems/categorization-not-found.xhtml";
            }
            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s#objects-sections",
                sectionIdentifier,
                context,
                categoryPath
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Rests (removes) the index element of a category.
     *
     * @param sectionIdentifier The identifier of the current
     *                          {@link ContentSection}.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPath      The path of the current category.
     *
     * @return A redirect to the category page or the template for generating an
     *         error view.
     */
    @GET
    @Path("/{context}/categories/{categoryPath:(.+)?}/@index-element/reset")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String resetIndexElement(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPath
    ) {
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            categoryManager.resetIndexObject(category);
            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s#objects-sections",
                sectionIdentifier,
                context,
                categoryPath
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Adds a new subcategory to the root assets folder of a content section.
     *
     * @param sectionIdentifier The identifier of the current
     *                          {@link ContentSection}.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryName      The name of the new category.
     * @param uniqueId          The unique ID of the new category.
     * @param isEnabled         Is the new category enabled.
     * @param isVisible         Is the new category visible?
     * @param isAbstract        Is the new category an abstract category?
     *
     * @return A redirect to the details page of the new category.
     */
    @POST
    @Path("/{context}/categories/@subcategories")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addSubcategory(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @FormParam("categoryName") final String categoryName,
        @FormParam("uniqueId") final String uniqueId,
        @FormParam("isEnabled") final String isEnabled,
        @FormParam("isVisible") final String isVisible,
        @FormParam("isAbstract") final String isAbstract
    ) {
        return addSubcategory(
            sectionIdentifier,
            context,
            "/",
            categoryName,
            uniqueId,
            isEnabled,
            isVisible,
            isAbstract
        );
    }

    /**
     * Adds a new subcategory to an assets folder of a content section.
     *
     * @param sectionIdentifier The identifier of the current
     *                          {@link ContentSection}.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPath      The path of the parent category.
     * @param categoryName      The name of the new category.
     * @param uniqueId          The unique ID of the new category.
     * @param isEnabled         Is the new category enabled.
     * @param isVisible         Is the new category visible?
     * @param isAbstract        Is the new category an abstract category?
     *
     * @return A redirect to the details page of the new category.
     */
    @POST
    @Path("/{context}/categories/{categoryPath:(.+)?}/@subcategories")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addSubcategory(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPath,
        @FormParam("categoryName") final String categoryName,
        @FormParam("uniqueId") final String uniqueId,
        @FormParam("isEnabled") final String isEnabled,
        @FormParam("isVisible") final String isVisible,
        @FormParam("isAbstract") final String isAbstract
    ) {
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Category subCategory = new Category();
            subCategory.setName(categoryName);
            subCategory.setUniqueId(uniqueId);
            subCategory.setEnabled(Objects.equals("on", isEnabled));
            subCategory.setVisible(Objects.equals("on", isVisible));
            subCategory.setAbstractCategory(Objects.equals("on", isAbstract));
            categoryRepo.save(subCategory);
            categoryManager.addSubCategoryToCategory(subCategory, category);
            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryPath
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Deletes a category.
     *
     * @param sectionIdentifier The identifier of the current
     *                          {@link ContentSection}.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPath      The path of the category to delete.
     *
     * @return A redirect to the detail page of the parent category.
     */
    @POST
    @Path("/{context}/categories/{categoryPath:(.+)?}/@delete")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteCategory(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPath
    ) {
        if (categoryPath.isEmpty()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("context", context);
            models.put("categoryPath", categoryPath);
            return "org/librecms/ui/contentsection/categorysystems/category-not-found.xhtml";
        }
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Category parentCategory = category.getParentCategory();
            categoryManager.removeSubCategoryFromCategory(
                category, parentCategory
            );
            categoryRepo.delete(category);

            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryManager.getCategoryPath(parentCategory)
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Moves a category to another super category.
     *
     ** @param sectionIdentifier     The identifier of the current
     *                                {@link ContentSection}.
     * @param context               The mapping context of the assigned category
     *                              system.
     * @param categoryPath          The path of the category to move.
     * @param targetIdentifierParam The identifier of the new parent category.
     *
     * @return A redirect to the details page of the super category, or the
     *         template for showing a error view.
     */
    @POST
    @Path("/{context}/categories/{categoryPath:(.+)?}/@move")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String moveCategory(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPath,
        @FormParam("targetIdentififer") final String targetIdentifierParam
    ) {
        if (categoryPath.isEmpty()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("context", context);
            models.put("categoryPath", categoryPath);
            return "org/librecms/ui/contentsection/categorysystems/category-not-found.xhtml";
        }
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );
        if (result.isSuccessful()) {
            final Identifier targetIdentifier = identifierParser
                .parseIdentifier(targetIdentifierParam);
            final Optional<Category> targetResult;
            switch (targetIdentifier.getType()) {
                case ID:
                    targetResult = categoryRepo.findById(
                        Long.parseLong(targetIdentifier.getIdentifier())
                    );
                    break;
                default:
                    targetResult = categoryRepo.findByUuid(
                        targetIdentifier.getIdentifier()
                    );
                    break;
            }
            if (!targetResult.isPresent()) {
                models.put("sectionIdentifier", sectionIdentifier);
                models.put("context", context);
                models.put("categoryPath", targetIdentifier);
                return "org/librecms/ui/contentsection/categorysystems/category-not-found.xhtml";
            }

            final Category category = result.getResult();
            final Category oldParent = category.getParentCategory();
            final Category target = targetResult.get();

            categoryManager.removeSubCategoryFromCategory(category, oldParent);
            categoryManager.addSubCategoryToCategory(category, target);

            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryManager.getCategoryPath(target)
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Reorders subcategories.
     *
     * @param sectionIdentifier The identifier of the current
     *                          {@link ContentSection}.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPath      The path of the category to move.
     * @param direction         The direction of the move.
     *
     * @return A redirect to the details page of the parent category or the
     *         template for building an error message.
     */
    @POST
    @Path("/{context}/categories/{categoryPath:(.+)?}/@order")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String orderSubCategories(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPath,
        @FormParam("direction") final String direction
    ) {
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );

        if (result.isSuccessful()) {
            final Category category = result.getResult();
            final Category parentCategory = category.getParentCategory();
            if (parentCategory == null) {
                return String.format(
                    "redirect:/%s/categorysystems/%s/categories/%s",
                    sectionIdentifier,
                    context,
                    categoryManager.getCategoryPath(category)
                );
            }

            switch (direction) {
                case "DECREASE":
                    categoryManager.decreaseCategoryOrder(
                        category, parentCategory
                    );
                    break;
                case "INCREASE":
                    categoryManager.increaseCategoryOrder(
                        category, parentCategory
                    );
                    break;
                default:
                    // Nothing
                    break;
            }

            final String parentCategoryPath = categoryManager
                .getCategoryPath(parentCategory);
            final String pathFragment;
            if ("/".equals(parentCategoryPath)) {
                pathFragment = "";
            } else {
                pathFragment = parentCategoryPath;
            }
            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s#subcategories-section",
                sectionIdentifier,
                context,
                pathFragment
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Reorders subcategories.
     *
     * @param sectionIdentifier The identifier of the current
     *                          {@link ContentSection}.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPath      The path of the category .
     * @param objectIdentifier  The identifier of the object to move.
     * @param direction         The direction of the move.
     *
     * @return A redirect to the details page of the parent category or the
     *         template for building an error message.
     */
    @POST
    @Path(
        "/{context}/categories/{categoryPath:(.+)?}/@objects/{objectIdentifier}/order")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String orderObjects(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("context") final String context,
        @PathParam("categoryPath") final String categoryPath,
        @PathParam("objectIdentifier") final String objectIdentifier,
        @FormParam("direction") final String direction
    ) {
        final RetrieveResult<Category> result = retrieveCategory(
            sectionIdentifier, context, categoryPath
        );

        if (result.isSuccessful()) {
            final Category category = result.getResult();

            final Optional<Categorization> categorizationResult = category
                .getObjects()
                .stream()
                .filter(
                    categorization -> Objects.equals(
                        categorization.getUuid(), objectIdentifier
                    )
                ).findAny();
            if (categorizationResult.isPresent()) {
                final CcmObject object = categorizationResult
                    .get()
                    .getCategorizedObject();
                try {
                    switch (direction) {
                        case "DECREASE":
                            categoryManager.decreaseObjectOrder(
                                object, category
                            );
                            break;
                        case "INCREASE":
                            categoryManager.increaseObjectOrder(
                                object, category
                            );
                            break;
                        default:
                            // Nothing
                            break;
                    }
                } catch (ObjectNotAssignedToCategoryException ex) {
                    return String.format(
                        "redirect:/%s/categorysystems/%s/categories/%s#objects-sections",
                        sectionIdentifier,
                        context,
                        categoryManager.getCategoryPath(category)
                    );
                }
            }

            return String.format(
                "redirect:/%s/categorysystems/%s/categories/%s",
                sectionIdentifier,
                context,
                categoryManager.getCategoryPath(category)
            );
        } else {
            return result.getFailedResponseTemplate();
        }
    }

    /**
     * Helper method for retrieving the current {@link ContentSection},
     *
     * @param sectionIdentifier The identifier of the section.
     *
     * @return A {@link Optional} with the requested {@link ContentSection}.
     */
    private Optional<ContentSection> retrieveContentSection(
        final String sectionIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            sectionIdentifier
        );

        final Optional<ContentSection> sectionResult;
        switch (identifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(identifier
                    .getIdentifier());
                break;
            default:
                sectionResult = sectionRepo.findByLabel(identifier
                    .getIdentifier());
                break;
        }

        return sectionResult;
    }

    /**
     * @param sectionIdentifier The identifier of the current
     *                          {@link ContentSection}.
     * @param context           The mapping context of the assigned category
     *                          system.
     * @param categoryPath      The path of the category .
     *
     * @return A {@link RetrieveResult}.
     */
    private RetrieveResult<Category> retrieveCategory(
        final String sectionIdentifier,
        final String context,
        final String categoryPath
    ) {
        final Optional<ContentSection> sectionResult = retrieveContentSection(
            sectionIdentifier);
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return RetrieveResult.failed(
                "org/librecms/ui/contentsection/contentsection-not-found.xhtml"
            );
        }
        final ContentSection section = sectionResult.get();
        if (permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CATEGORIES, section
        )) {
            return RetrieveResult.failed(
                "org/librecms/ui/contentsection/access-denied.xhtml"
            );
        }

        final Optional<DomainOwnership> domainResult = section
            .getDomains()
            .stream()
            .filter(domain -> domain.getContext().equals(context))
            .findAny();
        if (!domainResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("context", context);
            return RetrieveResult.failed(
                "org/librecms/ui/contentsection/categorysystems/categorysystem-not-found.xhtml"
            );
        }
        final Domain domain = domainResult.get().getDomain();

        final Optional<Category> categoryResult = categoryRepo
            .findByPath(domain, categoryPath);
        if (!categoryResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            models.put("context", context);
            models.put("categoryPath", categoryPath);
            return RetrieveResult.failed(
                "org/librecms/ui/contentsection/categorysystems/category-not-found.xhtml"
            );
        }

        return RetrieveResult.successful(categoryResult.get());
    }

    private DomainListEntryModel buildDomainListEntryModel(
        final DomainOwnership ownership
    ) {
        final Domain domain = ownership.getDomain();

        final DomainListEntryModel model = new DomainListEntryModel();
        model.setContext(ownership.getContext());
        model.setDomainKey(domain.getDomainKey());
        model.setReleased(
            DateTimeFormatter.ISO_DATE.withZone(ZoneId.systemDefault())
                .format(domain.getReleased()));
        model.setTitle(
            globalizationHelper.getValueFromLocalizedString(domain.getTitle())
        );
        model.setUri(domain.getUri());
        model.setVersion(domain.getVersion());

        return model;
    }

    private CategoryTreeNodeModel buildCategoryTree(
        final Domain domain, final String activePath
    ) {
        return buildCategoryTreeNode(domain.getRoot(), activePath);
    }

    private CategoryTreeNodeModel buildCategoryTreeNode(
        final Category category, final String activePath
    ) {
        final CategoryTreeNodeModel model = new CategoryTreeNodeModel();
        model.setUuid(category.getUuid());
        if (category.getTitle().getValues().isEmpty()) {
            model.setTitle(category.getName());
        } else {
            model.setTitle(
                globalizationHelper.getValueFromLocalizedString(
                    category.getTitle()
                )
            );
        }
        final String path = categoryManager.getCategoryPath(category);
        model.setActive(activePath.startsWith(path));
        model.setPath(path);
        if (!category.getSubCategories().isEmpty()) {
            model.setSubCategories(
                category
                    .getSubCategories()
                    .stream()
                    .map(
                        subCategory -> buildCategoryTreeNode(
                            subCategory, activePath
                        )
                    ).collect(Collectors.toList())
            );
        }

        return model;
    }

    /**
     * Helper method for building the {@link CategoryModel} for the
     * {@link Category}.
     *
     * @param category
     *
     * @return A {@link CategoryModel} for the provided category.
     */
    private CategoryModel buildCategoryModel(final Category category) {
        final CategoryModel model = new CategoryModel();
        model.setAbstractCategory(category.isAbstractCategory());
        model.setCategoryId(category.getObjectId());
        model.setCategoryOrder(category.getCategoryOrder());
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                category.getDescription()
            )
        );
        model.setEnabled(category.isEnabled());
        model.setName(category.getName());
        model.setObjects(
            category
                .getObjects()
                .stream()
                .map(this::buildCategorizedObjectModel)
                .collect(Collectors.toList())
        );
        final String categoryPath = categoryManager.getCategoryPath(category);
        if ("/".equals(categoryPath)) {
            model.setPath("");
        } else {
            model.setPath(categoryPath);
        }
        model.setSubCategories(
            category
                .getSubCategories()
                .stream()
                .map(this::buildSubCategoriesModel)
                .collect(Collectors.toList())
        );
        model.setTitle(
            globalizationHelper.getValueFromLocalizedString(
                category.getTitle()
            )
        );
        model.setUniqueId(category.getUniqueId());
        model.setUuid(category.getUuid());
        model.setVisible(category.isVisible());

        final List<Locale> availableLocales = globalizationHelper
            .getAvailableLocales();
        model.setLocalizedTitles(
            category
                .getTitle()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                )
        );
        final Set<Locale> titleLocales = category
            .getTitle()
            .getAvailableLocales();
        model.setUnusedTitleLocales(
            availableLocales
                .stream()
                .filter(locale -> !titleLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );

        model.setLocalizedDescriptions(
            category
                .getDescription()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                )
        );
        final Set<Locale> descriptionLocales = category
            .getDescription()
            .getAvailableLocales();
        model.setUnusedDescriptionLocales(
            availableLocales
                .stream()
                .filter(locale -> !descriptionLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );

        return model;
    }

    /**
     * Build a {@link CategoryModel} fro a subcategory.
     *
     * @param category The subcategory to use.
     *
     * @return A {@link CategoryModel} for the provided {@link Category}.
     */
    private CategoryModel buildSubCategoriesModel(final Category category) {
        final CategoryModel model = new CategoryModel();
        model.setAbstractCategory(category.isAbstractCategory());
        model.setCategoryId(category.getObjectId());
        model.setCategoryOrder(category.getCategoryOrder());
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                category.getDescription()
            )
        );
        model.setEnabled(category.isEnabled());
        model.setName(category.getName());
        model.setPath(categoryManager.getCategoryPath(category));
        model.setTitle(
            globalizationHelper.getValueFromLocalizedString(
                category.getTitle()
            )
        );
        model.setUniqueId(category.getUniqueId());
        model.setUuid(category.getUuid());
        model.setVisible(category.isVisible());
        return model;
    }

    /**
     * Build a {@link CategorizedObjectModel} for an object in a {@link Folder}.
     *
     * @param categorization The {@link Categorization} instance of the object
     *                       and the folder.
     *
     * @return A {@link CategorizedObjectModel} for the provided
     *         {@link Categorization}.
     */
    private CategorizedObjectModel buildCategorizedObjectModel(
        final Categorization categorization
    ) {
        final CcmObject object = categorization.getCategorizedObject();
        final CategorizedObjectModel model = new CategorizedObjectModel();
        model.setObjectId(object.getObjectId());
        model.setObjectUuid(object.getUuid());
        model.setDisplayName(object.getDisplayName());
        model.setIndexObject(categorization.isIndexObject());
        model.setObjectOrder(categorization.getObjectOrder());
        model.setType(categorization.getType());
        return model;
    }

}
