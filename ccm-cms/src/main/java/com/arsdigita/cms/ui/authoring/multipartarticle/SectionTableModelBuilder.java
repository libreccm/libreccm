/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.authoring.multipartarticle;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.util.LockableImpl;

import org.librecms.contenttypes.MultiPartArticle;
import org.librecms.contenttypes.MultiPartArticleSection;

/**
 * The model builder to generate a suitable model for the SectionTable
 */
class SectionTableModelBuilder extends LockableImpl implements TableModelBuilder {

    private final ItemSelectionModel selectedArticleModel;
    private final SectionSelectionModel<? extends MultiPartArticleSection> moveSectionModel;

    private final StringParameter selectedLanguageParam;

    /**
     * Private class constructor.
     *
     * @param selectedArticleModel
     * @param moveSectionModel
     */
    public SectionTableModelBuilder(
        final ItemSelectionModel selectedArticleModel,
        final SectionSelectionModel<? extends MultiPartArticleSection> moveSectionModel,
        final StringParameter selectedLanguageParam) {

        this.selectedArticleModel = selectedArticleModel;
        this.moveSectionModel = moveSectionModel;
        this.selectedLanguageParam = selectedLanguageParam;
    }

    /**
     *
     * @param table
     * @param state
     *
     * @return
     */
    @Override
    public TableModel makeModel(final Table table,
                                final PageState state) {

        table.getRowSelectionModel().clearSelection(state);
        MultiPartArticle article
                             = (MultiPartArticle) selectedArticleModel
                .getSelectedObject(state);
        return new SectionTableModel(table,
                                     state,
                                     selectedLanguageParam,
                                     article,
                                     moveSectionModel);
    }

}
