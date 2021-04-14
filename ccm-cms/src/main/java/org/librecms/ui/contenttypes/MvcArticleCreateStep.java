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
import org.libreccm.workflow.Workflow;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contenttypes.Article;
import org.librecms.ui.contentsections.documents.AbstractMvcDocumentCreateStep;
import org.librecms.ui.contentsections.documents.CreatesDocumentOfType;

import java.util.Locale;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Describes the create step for {@link Article}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsArticleCreateStep")
@CreatesDocumentOfType(Article.class)
public class MvcArticleCreateStep
    extends AbstractMvcDocumentCreateStep<Article> {

    private static final String FORM_PARAM_NAME = "name";

    private static final String FORM_PARAM_TITLE = "title";

    private static final String FORM_PARAM_SUMMARY = "summary";

    private static final String FORM_PARAM_INITIAL_LOCALE = "initialLocale";

    private static final String FORM_PARAM_SELECTED_WORKFLOW
        = "selectedWorkflow";

    /**
     * Provides functions for working with content items.
     */
    @Inject
    private ContentItemManager itemManager;

    /**
     * Used to save the article.
     */
    @Inject
    private ContentItemRepository itemRepo;

    /**
     * Provides functions for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to provided data for the views without a named bean.
     */
    @Inject
    private Models models;

    /**
     * Name of the article.
     */
    private String name;

    /**
     * Title of the article.
     */
    private String title;

    /**
     * Summary of the article.
     */
    private String summary;

    /**
     * The initial locale of the article.
     */
    private String initialLocale;

    /**
     * The workflow to use for the new article.
     */
    private String selectedWorkflow;

    @Override
    public String getDocumentType() {
        return Article.class.getName();
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("createstep.description");
    }

    @Override
    public String getBundle() {
        return ArticleStepsConstants.BUNDLE;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getInitialLocale() {
        return initialLocale;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public String getSelectedWorkflow() {
        if (selectedWorkflow == null || selectedWorkflow.isEmpty()) {
            return getContentSection()
                .getContentTypes()
                .stream()
                .filter(
                    type -> type.getContentItemClass().equals(
                        Article.class.getName()
                    )
                )
                .findAny()
                .map(type -> type.getDefaultWorkflow())
                .map(
                    workflow -> globalizationHelper.getValueFromLocalizedString(
                        workflow.getName()
                    )
                )
                .orElse("");
        } else {
            return selectedWorkflow;
        }
    }

    @Override
    public String showCreateStep() {
        return "org/librecms/ui/contenttypes/article/create-article.xhtml";
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String createItem(final MultivaluedMap<String, String> formParams) {
        if (!formParams.containsKey(FORM_PARAM_NAME)
                || formParams.getFirst(FORM_PARAM_NAME) == null
                || formParams.getFirst(FORM_PARAM_NAME).isEmpty()) {
            addMessage(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.name.error.missing")
            );
            return showCreateStep();
        }

        name = formParams.getFirst(FORM_PARAM_NAME);
        if (!name.matches("^([a-zA-Z0-9_-]*)$")) {
            addMessage(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.name.error.invalid")
            );
            return showCreateStep();
        }

        if (!formParams.containsKey(FORM_PARAM_TITLE)
                || formParams.getFirst(FORM_PARAM_TITLE) == null
                || formParams.getFirst(FORM_PARAM_TITLE).isEmpty()) {
            addMessage(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.title.error.missing")
            );
            return showCreateStep();
        }
        title = formParams.getFirst(FORM_PARAM_TITLE);

        if (!formParams.containsKey(FORM_PARAM_SUMMARY)
                || formParams.getFirst(FORM_PARAM_SUMMARY) == null
                || formParams.getFirst(FORM_PARAM_SUMMARY).isEmpty()) {
            addMessage(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.summary.error.missing")
            );
            return showCreateStep();
        }
        summary = formParams.getFirst(FORM_PARAM_SUMMARY);

        if (!formParams.containsKey(FORM_PARAM_INITIAL_LOCALE)
                || formParams.getFirst(FORM_PARAM_INITIAL_LOCALE) == null
                || formParams.getFirst(FORM_PARAM_INITIAL_LOCALE).isEmpty()) {
            addMessage(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.initial_locale.error.missing")
            );
            return showCreateStep();
        }
        final Locale locale = new Locale(
            formParams.getFirst(FORM_PARAM_INITIAL_LOCALE)
        );

        if (!formParams.containsKey(FORM_PARAM_SELECTED_WORKFLOW)
                || formParams.getFirst(FORM_PARAM_SELECTED_WORKFLOW) == null
                || formParams.getFirst(FORM_PARAM_SELECTED_WORKFLOW).isEmpty()) {
            addMessage(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.workflow.none_selected")
            );
            return showCreateStep();
        }
        selectedWorkflow = formParams.getFirst(FORM_PARAM_SELECTED_WORKFLOW);

        final Optional<Workflow> workflowResult = getContentSection()
            .getWorkflowTemplates()
            .stream()
            .filter(template -> template.getUuid().equals(selectedWorkflow))
            .findAny();

        if (!workflowResult.isPresent()) {
            addMessage(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.workflow.error.not_available")
            );
            return showCreateStep();
        }

        if (!getMessages().isEmpty()) {
            return showCreateStep();
        }

        final Article article = itemManager.createContentItem(
            name,
            getContentSection(),
            getFolder(),
            workflowResult.get(),
            Article.class,
            locale
        );

        article.getTitle().addValue(locale, title);
        article.getDescription().addValue(locale, summary);
        itemRepo.save(article);

        return String.format(
            "redirect:/%s/documents/%s/%s/@edit/basicproperties",
            getContentSectionLabel(),
            getFolderPath(),
            name
        );
    }

    

}
