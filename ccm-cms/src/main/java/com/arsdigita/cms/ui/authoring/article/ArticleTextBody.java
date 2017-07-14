/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.authoring.article;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;
import com.arsdigita.cms.ui.authoring.TextBody;

import org.librecms.contenttypes.Article;

import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItemRepository;

import java.util.Locale;

/**
 * Displays the current text body of the article and allows the user to edit it,
 * by uploading a file or entering text in a text box.
 *
 * The {@link com.arsdigita.bebop.PropertySheet} class is often used as the
 * display component in the default authoring kit steps of this class.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ArticleTextBody extends TextBody {

    private final AuthoringKitWizard authoringKitWizard;
    private final ItemSelectionModel itemSelectionModel;
    private final StringParameter selectedLanguageParam;

    /**
     * Construct a new GenericArticleBody component
     *
     * @param itemSelectionModel    The {@link ItemSelectionModel} which will be
     *                              responsible for loading the current item
     *
     * @param authoringKitWizard    The parent wizard which contains the form.
     *                              The form may use the wizard's methods, such
     *                              as stepForward and stepBack, in its process
     *                              listener.
     * @param selectedLanguageParam
     */
    public ArticleTextBody(final ItemSelectionModel itemSelectionModel,
                           final AuthoringKitWizard authoringKitWizard,
                           final StringParameter selectedLanguageParam) {

        super(itemSelectionModel, selectedLanguageParam);
        this.itemSelectionModel = itemSelectionModel;
        this.authoringKitWizard = authoringKitWizard;
        this.selectedLanguageParam = selectedLanguageParam;

        // Rest the component when it is hidden
        authoringKitWizard
            .getList()
            .addActionListener(event -> reset(event.getPageState()));

        // Set the right component access on the forms
        final Component uploadComponent = getComponent(FILE_UPLOAD);
        if (uploadComponent != null) {
            setComponentAccess(FILE_UPLOAD,
                               new WorkflowLockedComponentAccess(
                                   uploadComponent, itemSelectionModel));
        }
        final Component textEntryComponent = getComponent(TEXT_ENTRY);
        setComponentAccess(TEXT_ENTRY,
                           new WorkflowLockedComponentAccess(
                               textEntryComponent, itemSelectionModel));
    }

    /**
     * Adds the options for the mime type select widget of
     * <code>GenericArticleForm</code> and sets the default mime type.
     *
     * @param mimeSelect
     */
    @Override
    protected void setMimeTypeOptions(final SingleSelect mimeSelect) {
        mimeSelect.addOption(new Option("text/html", "HTML Text"));
        mimeSelect.setOptionSelected("text/html");
    }

    /**
     * Updates the text for the currently selected locale.
     *
     * @param state the current page state
     * @param text  the new text for the currently selected locale
     */
    @Override
    protected void updateText(final PageState state,
                              final String text) {

        final Article article = getSelectedArticle(state);

        final Locale selectedLocale = SelectedLanguageUtil
            .selectedLocale(state, selectedLanguageParam);

        article.getText().addValue(selectedLocale, text);

        final ContentItemRepository itemRepo = CdiUtil
            .createCdiUtil()
            .findBean(ContentItemRepository.class);
        itemRepo.save(article);
    }

    /**
     * Get the current {@link Article}
     *
     * @param state The current page state.
     *
     * @return The currently selected article.
     */
    protected Article getSelectedArticle(final PageState state) {

        return (Article) itemSelectionModel.getSelectedObject(state);
    }

    @Override
    protected String getTextPropertyName() {
        return "text";
    }

    @Override
    public String getText(final PageState state) {

        final Article article = getSelectedArticle(state);

        final Locale selectedLocale = SelectedLanguageUtil
            .selectedLocale(state, selectedLanguageParam);

        return article.getText().getValue(selectedLocale);
    }

}
