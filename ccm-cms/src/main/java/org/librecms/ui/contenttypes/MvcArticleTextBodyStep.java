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
import org.librecms.ui.contentsections.documents.ContentSectionNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentUi;

import javax.mvc.Controller;
import javax.ws.rs.Path;

import org.librecms.ui.contentsections.documents.MvcAuthoringStep;
import org.librecms.ui.contentsections.documents.MvcAuthoringStepService;
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

/**
 * Authoring step for editing the main text of an {@link Article}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "/@text")
@Controller
@Named("CmsArticleTextBodyStep")
@MvcAuthoringStep(
    bundle = ArticleStepsConstants.BUNDLE,
    descriptionKey = "authoringsteps.text.description",
    labelKey = "authoringsteps.text.label",
    supportedDocumentType = Article.class
)
public class MvcArticleTextBodyStep {

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

    @Inject
    private MvcAuthoringStepService stepService;

    @GET
    @Path("/")
    @Transactional(Transactional.TxType.REQUIRED)
    public String showStep(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM)
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
            return "org/librecms/ui/contenttypes/article/article-text.xhtml";
        } else {
            return documentUi.showAccessDenied(
                stepService.getContentSection(),
                stepService.getDocument(),
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
        return getDocument()
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
    }

    /**
     * Gets the locales for which the main text has not been defined yet.
     *
     * @return The locales for which the main text has not been defined yet.
     */
    public List<String> getUnusedLocales() {
        final Set<Locale> locales = getDocument()
            .getText()
            .getAvailableLocales();
        return globalizationHelper
            .getAvailableLocales()
            .stream()
            .filter(locale -> !locales.contains(locale))
            .map(Locale::toString)
            .collect(Collectors.toList());
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
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM)
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
            getDocument().getText().addValue(locale, value);
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
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM)
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
            getDocument().getText().addValue(locale, value);
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
    public String remvoeTextValue(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM)
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
            getDocument().getText().removeValue(locale);
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

    private Article getDocument() {
        return (Article) stepService.getDocument();
    }

}
