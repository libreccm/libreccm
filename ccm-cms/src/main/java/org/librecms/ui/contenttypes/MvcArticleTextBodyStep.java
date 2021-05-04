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
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contenttypes.Article;
import org.librecms.ui.contentsections.ItemPermissionChecker;
import org.librecms.ui.contentsections.documents.AbstractMvcAuthoringStep;
import org.librecms.ui.contentsections.documents.ContentSectionNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentUi;

import javax.mvc.Controller;
import javax.ws.rs.Path;

import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

import org.librecms.ui.contentsections.documents.MvcAuthoringStepDef;

import java.util.Collections;

/**
 * Authoring step for editing the main text of an {@link Article}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "article-text")
@Controller
@Named("CmsArticleTextBodyStep")
@MvcAuthoringStepDef(
    bundle = ArticleStepsConstants.BUNDLE,
    descriptionKey = "authoringsteps.text.description",
    labelKey = "authoringsteps.text.label",
    supportedDocumentType = Article.class
)
public class MvcArticleTextBodyStep extends AbstractMvcAuthoringStep {

    @Inject
    private ArticleMessageBundle articleMessageBundle;

    /**
     * Used for retrieving and saving the article.
     */
    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private DocumentUi documentUi;

    /**
     * Provides functions for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ItemPermissionChecker itemPermissionChecker;

    private Map<String, String> textValues;

    private List<String> unusedLocales;

    @Override
    public Class<MvcArticleTextBodyStep> getStepClass() {
        return MvcArticleTextBodyStep.class;
    }

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
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (itemPermissionChecker.canEditItem(getArticle())) {
            textValues = getArticle()
                .getText()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                );

            final Set<Locale> locales = getArticle()
                .getText()
                .getAvailableLocales();
            unusedLocales = globalizationHelper
                .getAvailableLocales()
                .stream()
                .filter(locale -> !locales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList());

            return "org/librecms/ui/contenttypes/article/article-text.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getArticle(),
                articleMessageBundle.getMessage("article.edit.denied")
            );
        }
    }

    /**
     * Get all localized values of the main text.
     *
     * @return The localized values of the main text.
     */
    public Map<String, String> getTextValues() {
        return Collections.unmodifiableMap(textValues);
    }

    /**
     * Gets the locales for which the main text has not been defined yet.
     *
     * @return The locales for which the main text has not been defined yet.
     */
    public List<String> getUnusedLocales() {
        return Collections.unmodifiableList(unusedLocales);
    }

    /**
     * Adds a localized main text.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale of the text.
     * @param value             The text.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTextValue(
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

        if (itemPermissionChecker.canEditItem(getArticle())) {
            final Locale locale = new Locale(localeParam);
            getArticle().getText().addValue(locale, value);
            itemRepo.save(getArticle());

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getArticle(),
                getLabel()
            );
        }
    }

    /**
     * Updates a localized main text.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale of the text.
     * @param value             The text.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTextValue(
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

        if (itemPermissionChecker.canEditItem(getArticle())) {
            final Locale locale = new Locale(localeParam);
            getArticle().getText().addValue(locale, value);
            itemRepo.save(getArticle());

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getArticle(),
                getLabel()
            );
        }
    }

    /**
     * Removes a localized main text.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale of the text.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTextValue(
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

        if (itemPermissionChecker.canEditItem(getArticle())) {
            final Locale locale = new Locale(localeParam);
            getArticle().getText().removeValue(locale);
            itemRepo.save(getArticle());

            return buildRedirectPathForStep();
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getArticle(),
                getLabel()
            );
        }
    }

    private Article getArticle() {
        return (Article) getDocument();
    }

}
