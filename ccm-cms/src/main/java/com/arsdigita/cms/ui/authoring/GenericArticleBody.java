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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.cms.ItemSelectionModel;

import org.librecms.contenttypes.Article;

import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.util.Assert;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItemRepository;

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
public class GenericArticleBody extends TextAssetBody {

    protected AuthoringKitWizard authoringKitWizard;
    protected ItemSelectionModel itemSelectionModel;

    /**
     * Construct a new GenericArticleBody component
     *
     * @param itemSelectionModel The {@link ItemSelectionModel} which will be
     *                           responsible for loading the current item
     *
     * @param authoringKitWizard The parent wizard which contains the form. The
     *                           form may use the wizard's methods, such as
     *                           stepForward and stepBack, in its process
     *                           listener.
     */
    public GenericArticleBody(final ItemSelectionModel itemSelectionModel,
                              final AuthoringKitWizard authoringKitWizard) {

        super(new ItemAssetModel(null));
        this.itemSelectionModel = itemSelectionModel;
        this.authoringKitWizard = authoringKitWizard;

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
     */
    @Override
    protected void setMimeTypeOptions(final SingleSelect mimeSelect) {
        mimeSelect.addOption(new Option("text/html", "HTML Text"));
        mimeSelect.setOptionSelected("text/html");
    }

    /**
     * Create a new text asset and associate it with the current item
     *
     * @param state the current page state
     *
     * @return a valid TextAsset
     */
    @Override
    protected LocalizedString createTextAsset(final PageState state) {

        final Article article = getGenericArticle(state);
        final LocalizedString text = article.getText();

        // no need - cg. Text doesn't need a security context,
        // and ownership of text is recorded in text_pages
        // t.setParent(item);
        return text;
    }

    /**
     * Set additional parameters of a brand new text asset, such as the parent
     * ID, after the asset has been successfully uploaded
     *
     * @param state the current page state
     * @param text  the new <code>TextAsset</code>
     */
    @Override
    protected void updateTextAsset(final PageState state,
                                   final LocalizedString text) {

        final Article article = getGenericArticle(state);

        //  a.setParent(t);
        article.setText(text);
        final ContentItemRepository itemRepo = CdiUtil
            .createCdiUtil()
            .findBean(ContentItemRepository.class);
        itemRepo.save(article);
    }

    /**
     * Get the current GenericArticle
     *
     * @param state
     */
    protected Article getGenericArticle(final PageState state) {

        return (Article) itemSelectionModel.getSelectedObject(state);
    }

    @Override
    protected String getTextAssetName() {
       return "text";
    }

    @Override
    public LocalizedString getTextAsset(final PageState state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * An ACSObjectSelectionModel that selects the current text asset for the
     * text page
     */
    private static class ItemAssetModel extends ItemSelectionModel {

        private RequestLocal requestLocal;

        public ItemAssetModel(final LongParameter parameter) {

            super(parameter);

            requestLocal = new RequestLocal() {

                @Override
                protected Object initialValue(final PageState state) {
//                    final Article t
//                                       = (Article) ((ItemSelectionModel) getSingleSelectionModel())
//                            .getSelectedObject(state);
//                    Assert.exists(t);
//                    return t.getTextAsset();

                    throw new UnsupportedOperationException("ToDo");
                }

            };
        }

       
        @Override
        public boolean isSelected(PageState s) {
            return (getSelectedObject(s) != null);
        }

    }

}
