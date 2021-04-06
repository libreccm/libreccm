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
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.FolderManager;
import org.librecms.contenttypes.Article;
import org.librecms.ui.contentsections.ItemPermissionChecker;
import org.librecms.ui.contentsections.documents.AuthoringStepPathFragment;
import org.librecms.ui.contentsections.documents.DocumentUi;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import org.librecms.ui.contentsections.documents.MvcAuthoringStep;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

/**
 * Authoring step for editing the basic properties of an a {@link Article}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
@AuthoringStepPathFragment(MvcArticlePropertiesStep.PATH_FRAGMENT)
@Named("CmsArticlePropertiesStep")
public class MvcArticlePropertiesStep implements MvcAuthoringStep {

    @Inject
    private ArticleMessageBundle articleMessageBundle;

    /**
     * The path fragment of the step.
     */
    static final String PATH_FRAGMENT = "basicproperties";

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
            .getText("authoringsteps.basicproperties.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.basicproperties.description");
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
        if (itemPermissionChecker.canEditItem(document)) {
            return "org/librecms/ui/contenttypes/article/article-basic-properties.xhtml";
        } else {
            return documentUi.showAccessDenied(
                section,
                document,
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
        return document.getDisplayName();
    }

    /**
     * Updates the name of the current article.
     *
     * @param name The new name of the article.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/name")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateName(@FormParam("name") final String name) {
        document.setDisplayName(name);
        itemRepo.save(document);
        return String.format(
            "redirect:/@documents/%s/%s/%s/@edit/%s",
            section.getLabel(),
            folderManager.getFolderPath(
                itemManager.getItemFolder(document).get()
            ),
            name,
            PATH_FRAGMENT
        );
    }

    /**
     * Get the values of the localized title of the article.
     *
     * @return The values of the localized title of the article.
     */
    public Map<String, String> getTitleValues() {
        return document
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
        final Set<Locale> titleLocales = document
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
     * Adds a localized title to the article.
     *
     * @param localeParam The locale of the title.
     * @param value       The title value.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTitle(
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getTitle().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:%s/@documents/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Updates a localized title of the article.
     *
     * @param localeParam The locale to update.
     * @param value       The updated title value.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTitle(
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getTitle().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:%s/@documents/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Removes a localized title of the article.
     *
     * @param localeParam The locale to remove.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTitle(
        @PathParam("locale") final String localeParam
    ) {
        final Locale locale = new Locale(localeParam);
        document.getTitle().removeValue(locale);
        itemRepo.save(document);

        return String.format(
            "redirect:%s/@documents/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Get the locales for which no localized description has been defined yet.
     *
     * @return The locales for which no localized description has been defined
     *         yet.
     */
    public List<String> getUnusedDescriptionLocales() {
        final Set<Locale> descriptionLocales = document
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
        return document
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
     * @param localeParam The locale of the description.
     * @param value       The description value.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@add")
    @Transactional(Transactional.TxType.REQUIRED)
    public String addDescription(
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getDescription().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:%s/@documents/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Updates a localized description of the article.
     *
     * @param localeParam The locale to update.
     * @param value       The updated description value.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@edit/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDescription(
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Locale locale = new Locale(localeParam);
        document.getDescription().addValue(locale, value);
        itemRepo.save(document);

        return String.format(
            "redirect:%s/@documents/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Removes a localized description of the article.
     *
     * @param localeParam The locale to remove.
     *
     * @return A redirect to this authoring step.
     */
    @POST
    @Path("/title/@remove/{locale}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam("locale") final String localeParam
    ) {
        final Locale locale = new Locale(localeParam);
        document.getDescription().removeValue(locale);
        itemRepo.save(document);

        return String.format(
            "redirect:%s/@documents/%s/@edit/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

}
