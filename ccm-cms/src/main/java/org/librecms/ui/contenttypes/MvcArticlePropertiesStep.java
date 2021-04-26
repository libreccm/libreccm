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
package org.librecms.ui.contenttypes;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.FolderManager;
import org.librecms.contenttypes.Article;
import org.librecms.ui.contentsections.ItemPermissionChecker;
import org.librecms.ui.contentsections.documents.ContentSectionNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentUi;
import org.librecms.ui.contentsections.documents.MvcAuthoringStep;
import org.librecms.ui.contentsections.documents.MvcAuthoringStepService;
import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;


/**
 * Authoring step for editing the basic properties of an a {@link Article}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "basicproperties")
@Controller
@Named("CmsArticlePropertiesStep")
@MvcAuthoringStep(
    bundle = ArticleStepsConstants.BUNDLE,
    descriptionKey = "authoringsteps.basicproperties.description",
    labelKey = "authoringsteps.basicproperties.label",
    supportedDocumentType = Article.class
)
public class MvcArticlePropertiesStep {

    @Inject
    private ArticleMessageBundle articleMessageBundle;

    /**
     * Used for retrieving and saving the article.
     */
    @Inject
    private ContentItemRepository itemRepo;

    /**
     * Provides functions for working with content items.
     */
    @Inject
    private ContentItemManager itemManager;

    @Inject
    private DocumentUi documentUi;

    /**
     * Provides functions for working with folders.
     */
    @Inject
    private FolderManager folderManager;

    /**
     * Provides functions for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ItemPermissionChecker itemPermissionChecker;

    @Inject
    private Models models;

    @Inject
    private MvcAuthoringStepService stepService;

    @GET
    @Path("/")
    @Transactional(Transactional.TxType.REQUIRED)
    public String showStep(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath
    ) {
        try {
            stepService.setSectionAndDocument(sectionIdentifier, documentPath);
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(stepService.getDocument())) {
            return "org/librecms/ui/contenttypes/article/article-basic-properties.xhtml";
        } else {
            return documentUi.showAccessDenied(
                stepService.getContentSection(),
                stepService.getDocument(),
                articleMessageBundle.getMessage("article.edit.denied")
            );
        }
    }

    /**
     * Gets the display name of the current article.
     *
     * @return The display name of the current article.
     */
    public String getName() {
        return stepService.getDocument().getDisplayName();
    }

    /**
     * Updates the name of the current article.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param name
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/name")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateName(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @FormParam("name") @DefaultValue("") final String name
    ) {
        try {
            stepService.setSectionAndDocument(sectionIdentifier, documentPath);
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(stepService.getDocument())) {
            if (name.isEmpty() || name.matches("\\s*")) {
                models.put("nameMissing", true);

                return showStep(sectionIdentifier, documentPath);
            }

            stepService.getDocument().setDisplayName(name);
            itemRepo.save(stepService.getDocument());

            return stepService.buildRedirectPathForStep(getClass());
        } else {
            return documentUi.showAccessDenied(
                stepService.getContentSection(),
                stepService.getDocument(),
                stepService.getLabel(getClass())
            );
        }
    }

    /**
     * Get the values of the localized title of the article.
     *
     * @return The values of the localized title of the article.
     */
    public Map<String, String> getTitleValues() {
        return stepService
            .getDocument()
            .getTitle()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
    }

    /**
     * Get the locales for which no localized title has been defined yet.
     *
     * @return The locales for which no localized title has been defined yet.
     */
    public List<String> getUnusedTitleLocales() {
        final Set<Locale> titleLocales = stepService
            .getDocument()
            .getTitle()
            .getAvailableLocales();
        return globalizationHelper
            .getAvailableLocales()
            .stream()
            .filter(locale -> !titleLocales.contains(locale))
            .map(Locale::toString)
            .collect(Collectors.toList());
    }

    /**
     * Updates a localized title of the article.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale to update.
     * @param value             The updated title value.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        try {
            stepService.setSectionAndDocument(sectionIdentifier, documentPath);
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(stepService.getDocument())) {
            final Locale locale = new Locale(localeParam);
            stepService.getDocument().getTitle().addValue(locale, value);
            itemRepo.save(stepService.getDocument());

            return stepService.buildRedirectPathForStep(getClass());
        } else {
            return documentUi.showAccessDenied(
                stepService.getContentSection(),
                stepService.getDocument(),
                stepService.getLabel(getClass())
            );
        }
    }

    /**
     * Updates a localized title of the article.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale to update.
     * @param value             The updated title value.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTitle(
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        try {
            stepService.setSectionAndDocument(sectionIdentifier, documentPath);
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(stepService.getDocument())) {
            final Locale locale = new Locale(localeParam);
            stepService.getDocument().getTitle().removeValue(locale);
            itemRepo.save(stepService.getDocument());

            return stepService.buildRedirectPathForStep(getClass());
        } else {
            return documentUi.showAccessDenied(
                stepService.getContentSection(),
                stepService.getDocument(),
                stepService.getLabel(getClass())
            );
        }
    }

    /**
     * Removes a localized title of the article.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale to remove.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("locale") final String localeParam
    ) {
        try {
            stepService.setSectionAndDocument(sectionIdentifier, documentPath);
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(stepService.getDocument())) {
            final Locale locale = new Locale(localeParam);
            stepService.getDocument().getTitle().removeValue(locale);
            itemRepo.save(stepService.getDocument());

            return stepService.buildRedirectPathForStep(getClass());
        } else {
            return documentUi.showAccessDenied(
                stepService.getContentSection(),
                stepService.getDocument(),
                stepService.getLabel(getClass())
            );
        }
    }

    /**
     * Get the locales for which no localized description has been defined yet.
     *
     * @return The locales for which no localized description has been defined
     *         yet.
     */
    public List<String> getUnusedDescriptionLocales() {
        final Set<Locale> descriptionLocales = stepService
            .getDocument()
            .getDescription()
            .getAvailableLocales();
        return globalizationHelper
            .getAvailableLocales()
            .stream()
            .filter(locale -> !descriptionLocales.contains(locale))
            .map(Locale::toString)
            .collect(Collectors.toList());
    }

    /**
     * Get the values of the localized decrription of the article.
     *
     * @return The values of the localized description of the article.
     */
    public Map<String, String> getDescriptionValues() {
        return stepService
            .getDocument()
            .getDescription()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
    }

    /**
     * Adds a localized description to the article.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale of the description.
     * @param value             The description value.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addDescription(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        try {
            stepService.setSectionAndDocument(sectionIdentifier, documentPath);
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(stepService.getDocument())) {
            final Locale locale = new Locale(localeParam);
            stepService.getDocument().getDescription().addValue(locale, value);
            itemRepo.save(stepService.getDocument());

            return stepService.buildRedirectPathForStep(getClass());
        } else {
            return documentUi.showAccessDenied(
                stepService.getContentSection(),
                stepService.getDocument(),
                stepService.getLabel(getClass())
            );
        }
    }

    /**
     * Updates a localized description of the article.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale to update.
     * @param value             The updated description value.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDescription(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        try {
            stepService.setSectionAndDocument(sectionIdentifier, documentPath);
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(stepService.getDocument())) {
            final Locale locale = new Locale(localeParam);
            stepService.getDocument().getDescription().addValue(locale, value);
            itemRepo.save(stepService.getDocument());

            return stepService.buildRedirectPathForStep(getClass());
        } else {
            return documentUi.showAccessDenied(
                stepService.getContentSection(),
                stepService.getDocument(),
                stepService.getLabel(getClass())
            );
        }
    }

    /**
     * Removes a localized description of the article.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam The locale to remove.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
          @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("locale") final String localeParam
    ) {
         try {
            stepService.setSectionAndDocument(sectionIdentifier, documentPath);
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(stepService.getDocument())) {
        final Locale locale = new Locale(localeParam);
        stepService.getDocument().getDescription().removeValue(locale);
        itemRepo.save(stepService.getDocument());

         return stepService.buildRedirectPathForStep(getClass());
        } else {
            return documentUi.showAccessDenied(
                stepService.getContentSection(),
                stepService.getDocument(),
                stepService.getLabel(getClass())
            );
        }
    }

}
