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
import org.libreccm.security.AuthorizationRequired;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.FolderManager;
import org.librecms.contenttypes.Article;
import org.librecms.ui.contentsections.ItemPermissionChecker;
import org.librecms.ui.contentsections.documents.AbstractMvcAuthoringStep;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentUi;
import org.librecms.ui.contentsections.documents.MvcAuthoringStepDef;
import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import java.util.Locale;
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
@Path(MvcAuthoringSteps.PATH_PREFIX + "article-basicproperties")
@Controller
@MvcAuthoringStepDef(
    bundle = ArticleStepsConstants.BUNDLE,
    descriptionKey = "authoringsteps.basicproperties.description",
    labelKey = "authoringsteps.basicproperties.label",
    supportedDocumentType = Article.class
)
public class MvcArticlePropertiesStep extends AbstractMvcAuthoringStep {

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
    private MvcArticlePropertiesStepModel articlePropertiesStepModel;

    @Inject
    private Models models;

//    private Map<String, String> titleValues;
//
//    private List<String> unusedTitleLocales;
//
//    private Map<String, String> descriptionValues;
//
//    private List<String> unusedDescriptionLocales;

    @Override
    public Class<MvcArticlePropertiesStep> getStepClass() {
        return MvcArticlePropertiesStep.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void init() throws ContentSectionNotFoundException,
                                 DocumentNotFoundException {
        super.init();

        articlePropertiesStepModel.setName(getDocument().getDisplayName());
        
        articlePropertiesStepModel.setCanEdit(getCanEdit());
        
        final Set<Locale> titleLocales = getDocument()
            .getTitle()
            .getAvailableLocales();

        articlePropertiesStepModel.setTitleValues(
            getDocument()
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

        articlePropertiesStepModel.setUnusedTitleLocales(
            globalizationHelper
                .getAvailableLocales()
                .stream()
                .filter(locale -> !titleLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );

        articlePropertiesStepModel.setDescriptionValues(
            getDocument()
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

        final Set<Locale> descriptionLocales = getDocument()
            .getDescription()
            .getAvailableLocales();

        articlePropertiesStepModel.setUnusedDescriptionLocales(
            globalizationHelper
                .getAvailableLocales()
                .stream()
                .filter(locale -> !descriptionLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );
    }

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showStep(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(getDocument())) {
//            titleValues = getDocument()
//                .getTitle()
//                .getValues()
//                .entrySet()
//                .stream()
//                .collect(
//                    Collectors.toMap(
//                        entry -> entry.getKey().toString(),
//                        entry -> entry.getValue()
//                    )
//                );

//            final Set<Locale> titleLocales = getDocument()
//                .getTitle()
//                .getAvailableLocales();
//
//            unusedTitleLocales = globalizationHelper
//                .getAvailableLocales()
//                .stream()
//                .filter(locale -> !titleLocales.contains(locale))
//                .map(Locale::toString)
//                .collect(Collectors.toList());
//
//            descriptionValues = getDocument()
//                .getDescription()
//                .getValues()
//                .entrySet()
//                .stream()
//                .collect(
//                    Collectors.toMap(
//                        entry -> entry.getKey().toString(),
//                        entry -> entry.getValue()
//                    )
//                );
//
//            final Set<Locale> descriptionLocales = getDocument()
//                .getDescription()
//                .getAvailableLocales();
//
//            unusedDescriptionLocales = globalizationHelper
//                .getAvailableLocales()
//                .stream()
//                .filter(locale -> !descriptionLocales.contains(locale))
//                .map(Locale::toString)
//                .collect(Collectors.toList());
            return "org/librecms/ui/contenttypes/article/article-basic-properties.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                articleMessageBundle.getMessage("article.edit.denied")
            );
        }

    }

//    /**
//     * Gets the display name of the current article.
//     *
//     * @return The display name of the current article.
//     */
//    public String getName() {
//        return getDocument().getDisplayName();
//    }

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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateName(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @FormParam("name") @DefaultValue("") final String name
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(getDocument())) {
            if (name.isEmpty() || name.matches("\\s*")) {
                models.put("nameMissing", true);

                return showStep(sectionIdentifier, documentPath);
            }

            getDocument().setDisplayName(name);
            itemRepo.save(getDocument());

            updateDocumentPath();

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
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
    @Path("/title/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(getDocument())) {
            final Locale locale = new Locale(localeParam);
            getDocument().getTitle().addValue(locale, value);
            itemRepo.save(getDocument());

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(getDocument())) {
            final Locale locale = new Locale(localeParam);
            getDocument().getTitle().addValue(locale, value);
            itemRepo.save(getDocument());

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTitle(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("locale") final String localeParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(getDocument())) {
            final Locale locale = new Locale(localeParam);
            getDocument().getTitle().removeValue(locale);
            itemRepo.save(getDocument());

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
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
    @Path("/description/@add")
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
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(getDocument())) {
            final Locale locale = new Locale(localeParam);
            getDocument().getDescription().addValue(locale, value);
            itemRepo.save(getDocument());

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
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
    @Path("/description/@edit/{locale}")
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
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(getDocument())) {
            final Locale locale = new Locale(localeParam);
            getDocument().getDescription().addValue(locale, value);
            itemRepo.save(getDocument());

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Removes a localized description of the article.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale to remove.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/description/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @PathParam("locale") final String localeParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(getDocument())) {
            final Locale locale = new Locale(localeParam);
            getDocument().getDescription().removeValue(locale);
            itemRepo.save(getDocument());

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

}
