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

import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.FolderManager;
import org.librecms.contenttypes.Article;
import org.librecms.ui.contentsections.documents.AuthoringStepPathFragment;

import javax.enterprise.context.RequestScoped;
import javax.mvc.Controller;
import javax.ws.rs.Path;

import org.librecms.ui.contentsections.documents.MvcAuthoringStep;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

/**
 * Authoring step for editing the main text of an {@link Article}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/")
@AuthoringStepPathFragment(MvcArticleTextBodyStep.PATH_FRAGMENT)
@Named("CmsArticleTextBodyStep")
public class MvcArticleTextBodyStep implements MvcAuthoringStep {

    /**
     * The path fragment of the step.
     */
    static final String PATH_FRAGMENT = "text";

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

    /**
     * Provides functions for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * The current content section.
     */
    private ContentSection section;

    /**
     * The {@link Article} to edit.
     */
    private Article document;

    @Override
    public Class<? extends ContentItem> supportedDocumentType() {
        return Article.class;
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.text.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.text.description");
    }

    @Override
    public String getBundle() {
        return ArticleStepsConstants.BUNDLE;
    }

    @Override
    public ContentSection getContentSection() {
        return section;
    }

    @Override
    public String getContentSectionLabel() {
        return section.getLabel();
    }

    @Override
    public String getContentSectionTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(section.getTitle());
    }

    @Override
    public void setContentSection(final ContentSection section) {
        this.section = section;
    }

    @Override
    public ContentItem getContentItem() {
        return document;
    }

    @Override
    public String getContentItemPath() {
        return itemManager.getItemPath(document);
    }

    @Override
    public String getContentItemTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(document.getTitle());
    }

    @Override
    public void setContentItem(final ContentItem document) {
        if (!(document instanceof Article)) {
            throw new UnexpectedErrorException("Not an article.");
        }
        this.document = (Article) document;
    }

    @Override
    public String showStep() {
        return "org/librecms/ui/contenttypes/article/article-text.xhtml";
    }

    /**
     * Get all localized values of the main text.
     *
     * @return The localized values of the main text.
     */
    public Map<String, String> getTextValues() {
        return document
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
        final Set<Locale> locales = document
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
     * @param localeParam The locale of the text.
     * @param value       The text.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/@add")
    public String addTextValue(
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getText().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:/@documents/%s/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Updates a localized main text.
     *
     * @param localeParam The locale of the text.
     * @param value       The text.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/@edit/{locale}")
    public String editTextValue(
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getText().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:/@documents/%s/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Removes a localized main text.
     *
     * @param localeParam The locale of the text.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/@remove/{locale}")
    public String remvoeTextValue(
        @PathParam("locale") final String localeParam
    ) {
        final Locale locale = new Locale(localeParam);
        document.getText().removeValue(locale);
        itemRepo.save(document);

        return String.format(
            "redirect:/@documents/%s/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

}
