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

import com.arsdigita.kernel.KernelConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contenttypes.Article;
import org.librecms.ui.contentsections.ItemPermissionChecker;
import org.librecms.ui.contentsections.documents.AbstractMvcAuthoringStep;
import org.librecms.ui.contentsections.documents.CmsEditorLocaleVariantRow;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentNotFoundException;
import org.librecms.ui.contentsections.documents.DocumentUi;

import javax.mvc.Controller;
import javax.ws.rs.Path;

import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

import org.librecms.ui.contentsections.documents.MvcAuthoringStepDef;

import java.util.StringTokenizer;

/**
 * Authoring step for editing the main text of an {@link Article}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "article-text")
@Controller
@MvcAuthoringStepDef(
    bundle = ArticleStepsConstants.BUNDLE,
    descriptionKey = "authoringsteps.text.description",
    labelKey = "authoringsteps.text.label",
    supportedDocumentType = Article.class
)
public class MvcArticleTextBodyStep extends AbstractMvcAuthoringStep {

    @Inject
    private ArticleMessageBundle articleMessageBundle;

    @Inject
    private ConfigurationManager confManager;

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
    private MvcArticleTextBodyStepModel articleTextBodyStepModel;

//    private Map<String, String> textValues;
//
//    private List<CmsEditorLocaleVariantRow> variants;
//
//    private List<String> unusedLocales;
//
//    private String selectedLocale;
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
            return "org/librecms/ui/contenttypes/article/article-text/available-languages.xhtml";
//            return "org/librecms/ui/contenttypes/article/article-text/available-languages.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getArticle(),
                articleMessageBundle.getMessage("article.edit.denied")
            );
        }
    }

//    /**
//     * Get all localized values of the main text.
//     *
//     * @return The localized values of the main text.
//     */
//    public Map<String, String> getTextValues() {
//        return Collections.unmodifiableMap(textValues);
//    }
//
//    public List<CmsEditorLocaleVariantRow> getVariants() {
//        return Collections.unmodifiableList(variants);
//    }
//
//    /**
//     * Gets the locales for which the main text has not been defined yet.
//     *
//     * @return The locales for which the main text has not been defined yet.
//     */
//    public List<String> getUnusedLocales() {
//        return Collections.unmodifiableList(unusedLocales);
//    }
//
//    public String getSelectedLocale() {
//        return selectedLocale;
//    }
    /**
     * Adds a localized main text.
     *
     * @param sectionIdentifier
     * @param documentPath
     * @param localeParam       The locale of the text.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTextValue(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath,
        @FormParam("locale") final String localeParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class
        );
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        if (itemPermissionChecker.canEditItem(getArticle())) {
            final String value;
            if (getArticle().getText().getAvailableLocales().isEmpty()) {
                value = "";
            } else {
                value = globalizationHelper.getValueFromLocalizedString(
                    getArticle().getText()
                );
            }
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

//    @GET
////    @Path("/{locale}/@view")
//    @Path("/variants/{locale}")
//    @Transactional(Transactional.TxType.REQUIRED)
//    public String viewTextValue(
//        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
//        final String sectionIdentifier,
//        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
//        final String documentPath,
//        @PathParam("locale") final String localeParam
//    ) {
//          try {
//            init();
//        } catch (ContentSectionNotFoundException ex) {
//            return ex.showErrorMessage();
//        } catch (DocumentNotFoundException ex) {
//            return ex.showErrorMessage();
//        }
//
//        if (itemPermissionChecker.canEditItem(getArticle())) {
//            selectedLocale = new Locale(localeParam).toString();
//            
////            return "org/librecms/ui/contenttypes/article/article-text/view.xhtml";
//            return getTextValues().get(localeParam);
//        } else {
//            return documentUi.showAccessDenied(
//                getContentSection(),
//                getArticle(),
//                articleMessageBundle.getMessage("article.edit.denied")
//            );
//        }
//    }
    @GET
    @Path("/edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTextValue(
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
            articleTextBodyStepModel.setSelectedLocale(
                new Locale(localeParam).toString()
            );

            return "org/librecms/ui/contenttypes/article/article-text/edit.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getArticle(),
                articleMessageBundle.getMessage("article.edit.denied")
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
    @Path("/edit/{locale}")
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
//    @Path("/{locale}/@remove")
    @Path("/remove/{locale}")
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

    @Override
    protected void init() throws ContentSectionNotFoundException,
                                 DocumentNotFoundException {
        super.init();

        if (itemPermissionChecker.canEditItem(getArticle())) {
            articleTextBodyStepModel.setCanEdit(
                itemPermissionChecker.canEditItem(getArticle())
            );
            articleTextBodyStepModel.setTextValues(
                getArticle()
                    .getText()
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

            articleTextBodyStepModel.setVariants(
                getArticle()
                    .getText()
                    .getValues()
                    .entrySet()
                    .stream()
                    .map(this::buildVariantRow)
                    .collect(Collectors.toList())
            );

            final Set<Locale> locales = getArticle()
                .getText()
                .getAvailableLocales();
            articleTextBodyStepModel.setUnusedLocales(
                globalizationHelper
                    .getAvailableLocales()
                    .stream()
                    .filter(locale -> !locales.contains(locale))
                    .map(Locale::toString)
                    .collect(Collectors.toList())
            );
        }
    }

    private Article getArticle() {
        return (Article) getDocument();
    }

    private CmsEditorLocaleVariantRow buildVariantRow(
        final Map.Entry<Locale, String> entry
    ) {
        final CmsEditorLocaleVariantRow variant
            = new CmsEditorLocaleVariantRow();
        variant.setLocale(entry.getKey().toString());
        final Document document = Jsoup.parseBodyFragment(entry.getValue());
        variant.setWordCount(
            new StringTokenizer(document.body().text()).countTokens()
        );

        return variant;
    }

}
