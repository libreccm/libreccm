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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contenttypes.MultiPartArticle;
import org.librecms.contenttypes.MultiPartArticleSection;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

class SectionTableModel implements TableModel {

    private final TableColumnModel columnModel;
    private final SectionTable sectionTable;
    private final PageState pageState;
    private final StringParameter selectedLanguageParam;
    private final SectionSelectionModel<? extends MultiPartArticleSection> moveSectionModel;

    private final Iterator<MultiPartArticleSection> iterator;
    private MultiPartArticleSection currentSection;

    /**
     * Constructor.
     *
     * @param sectionTable
     * @param pageState
     * @param article
     * @param moveSectionModel
     */
    public SectionTableModel(
        final Table sectionTable,
        final PageState pageState,
        final StringParameter selectedLanguageParam,
        final MultiPartArticle article,
        final SectionSelectionModel<? extends MultiPartArticleSection> moveSectionModel) {

        this.pageState = pageState;
        this.selectedLanguageParam = selectedLanguageParam;
        this.sectionTable = (SectionTable) sectionTable;
        this.moveSectionModel = moveSectionModel;

        columnModel = sectionTable.getColumnModel();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final MultiPartArticleSectionStepController controller = cdiUtil
            .findBean(MultiPartArticleSectionStepController.class);

        final List<MultiPartArticleSection> sections = controller
            .retrieveSections(article);
        iterator = sections.iterator();
    }

    /**
     * Return the number of columns this TableModel has.
     */
    @Override
    public int getColumnCount() {
        return columnModel.size();
    }

    /**
     * Move to the next row and return true if the model is now positioned on a
     * valid row.
     */
    @Override
    public boolean nextRow() {

        if (iterator.hasNext()) {
            currentSection = iterator.next();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the data element for the given column and the current row.
     */
    @Override
    public Object getElementAt(final int columnIndex) {

        if (columnModel == null) {
            return null;
        }
        
        final MultiPartArticleSectionStepController controller = CdiUtil
        .createCdiUtil()
        .findBean(MultiPartArticleSectionStepController.class);

        switch (columnIndex) {
            case SectionTable.COL_INDEX_TITLE:
                return controller.getSectionTitle(
                    currentSection, 
                    SelectedLanguageUtil.selectedLocale(
                        pageState, selectedLanguageParam
                    )
                )
                    ;
            case SectionTable.COL_PAGE_BREAK:
                if (currentSection.isPageBreak()) {
                    return new Label(
                        new GlobalizedMessage("cms.ui.yes",
                                              CmsConstants.CMS_BUNDLE));
                } else {
                    return new Label(
                        new GlobalizedMessage("cms.ui.no",
                                              CmsConstants.CMS_BUNDLE));
                }
            case SectionTable.COL_INDEX_EDIT:
                return new Label(new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle.section_table.link_edit",
                    CmsConstants.CMS_BUNDLE));
            case SectionTable.COL_INDEX_DELETE:
                return new Label(new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle.section_table.link_delete",
                    CmsConstants.CMS_BUNDLE));
            case SectionTable.COL_INDEX_MOVE:
                if (moveSectionModel.getSelectedKey(pageState) == null) {
                    return new Label(new GlobalizedMessage(
                        "cms.contenttypes.ui.mparticle.section_table.link_move",
                        CmsConstants.CMS_BUNDLE));
                } else {
                    return new Label(new GlobalizedMessage(
                        "cms.contenttypes.ui.mparticle.section_table.link_move_below",
                        CmsConstants.CMS_BUNDLE));
                }
            default:
                return null;
        }
    }

    /**
     * Return the key for the given column and the current row.
     */
    @Override
    public Object getKeyAt(final int columnIndex) {
        return currentSection.getSectionId();
    }

}
