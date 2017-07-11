/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui.mparticle;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.ArticleSection;
import com.arsdigita.cms.contenttypes.ArticleSectionCollection;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.cms.contenttypes.util.MPArticleGlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;

import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * A table that displays the sections for the currently
 * selected MultiPartArticle.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @version $Id: SectionTable.java 2099 2010-04-17 15:35:14Z pboy $
 */
public class SectionTable extends Table {

    private static final Logger s_log = Logger.getLogger(SectionTable.class);
    // match columns by (symbolic) index, makes for easier reordering
    public static final int COL_INDEX_TITLE = 0;   // "Section";
    public static final int COL_INDEX_EDIT = 1;   // "Edit";
    public static final int COL_INDEX_MOVE = 2;   // "Move"; 
    public static final int COL_INDEX_DELETE = 3;   // "Delete"; 
    private ItemSelectionModel m_selArticle;
    private ItemSelectionModel m_selSection;
    private ItemSelectionModel m_moveSection;

    /**
     * Constructor.  Create an instance of this class.
     *
     * @param selArticle a selection model that returns the MultiPartArticle
     *                   which holds the sections to display.
     * @param moveSection
     */
    public SectionTable(ItemSelectionModel selArticle,
                        ItemSelectionModel moveSection) {

        super();
        m_selArticle = selArticle;
        m_moveSection = moveSection;

        TableColumnModel model = getColumnModel();
        model.add(new TableColumn(
                COL_INDEX_TITLE,
                new Label(MPArticleGlobalizationUtil.globalize(
                "cms.contenttypes.ui.mparticle.section_table.header_section"))));
        model.add(new TableColumn(
                COL_INDEX_EDIT,
                new Label(MPArticleGlobalizationUtil.globalize(
                "cms.contenttypes.ui.mparticle.section_table.header_edit"))));
        model.add(new TableColumn(
                COL_INDEX_MOVE,
                new Label(MPArticleGlobalizationUtil.globalize(
                "cms.contenttypes.ui.mparticle.section_table.header_move"))));
        model.add(new TableColumn(
                COL_INDEX_DELETE,
                new Label(MPArticleGlobalizationUtil.globalize(
                "cms.contenttypes.ui.mparticle.section_table.header_delete"))));

        model.get(1).setCellRenderer(new SectionTableCellRenderer(true));
        model.get(2).setCellRenderer(new SectionTableCellRenderer(true));
        model.get(3).setCellRenderer(new SectionTableCellRenderer(true));


        setModelBuilder(new SectionTableModelBuilder(m_selArticle, m_moveSection));

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(TableActionEvent event) {
                PageState state = event.getPageState();

                TableColumn col = getColumnModel().get(event.getColumn()
                        .intValue());

                if (col.getModelIndex() == COL_INDEX_MOVE) {
                    if (m_moveSection.getSelectedKey(state) == null) {
                        m_moveSection.setSelectedKey(state,
                                                     m_selSection
                                .getSelectedKey(state));
                    } else {
                        MultiPartArticle article = (MultiPartArticle) m_selArticle.
                                getSelectedObject(state);

                        BigDecimal id = (BigDecimal) m_moveSection.getSelectedKey(state);
                        ArticleSection sect = (ArticleSection) DomainObjectFactory.newInstance(
                                new OID(ArticleSection.BASE_DATA_OBJECT_TYPE, id));

                        BigDecimal dest =
                                   new BigDecimal((String) event.getRowKey());
                        ArticleSection destSect = (ArticleSection) DomainObjectFactory.newInstance(
                                new OID(ArticleSection.BASE_DATA_OBJECT_TYPE, dest));

                        // if sect is lower in rank than the dest
                        // then move below is default behavior
                        int rank = destSect.getRank().intValue();
                        if (sect.getRank().intValue() > rank) {
                            // otherwise, add one to get "move below"
                            rank++;
                        }

                        article.changeSectionRank(sect, rank);
                        m_moveSection.setSelectedKey(state, null);
                    }
                }
            }

            @Override
            public void headSelected(TableActionEvent event) {
                // do nothing
            }

        });
    }

    public void setSectionModel(ItemSelectionModel selSection) {
        if (selSection == null) {
            s_log.warn("null item model");
        }
        m_selSection = selSection;
    }

    /**
     * The model builder to generate a suitable model for the SectionTable
     */
    protected class SectionTableModelBuilder extends LockableImpl
            implements TableModelBuilder {

        protected ItemSelectionModel m_selArticle;
        protected ItemSelectionModel m_moveSection;

        /**
         * Private class constructor.
         * @param selArticle
         * @param moveSection 
         */
        public SectionTableModelBuilder(ItemSelectionModel selArticle,
                                        ItemSelectionModel moveSection) {
            m_selArticle = selArticle;
            m_moveSection = moveSection;
        }

        /**
         * 
         * @param table
         * @param state
         * @return 
         */
        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            MultiPartArticle article = (MultiPartArticle) m_selArticle
                    .getSelectedObject(state);

            return new SectionTableModel(table, state, article, m_moveSection);
        }

    }

    /**
     * Internal class
     */
    protected class SectionTableModel implements TableModel {

        private TableColumnModel m_colModel;
        private SectionTable m_table;
        private PageState m_state;
        private ArticleSectionCollection m_sections;
        private ItemSelectionModel m_moveSection;
        private ArticleSection m_section;

        /** Constructor.
         * @param table
         * @param state
         * @param article
         * @param moveSection
         */
        public SectionTableModel(Table table, PageState state,
                                 MultiPartArticle article,
                                 ItemSelectionModel moveSection) {
            m_colModel = table.getColumnModel();
            m_state = state;
            m_sections = article.getSections();
            m_table = (SectionTable) table;
            m_moveSection = moveSection;
        }

        /** Return the number of columsn this TableModel has. */
        @Override
        public int getColumnCount() {
            return m_colModel.size();
        }

        /** Move to the next row and return true if the model is now positioned 
         *  on a valid row.
         */
        @Override
        public boolean nextRow() {
            if (m_sections.next()) {
                m_section = (ArticleSection) m_sections.getArticleSection();
                return true;
            }
            return false;
        }

        /** 
         * Return the data element for the given column and the current row. 
         */
        @Override
        public Object getElementAt(int columnIndex) {

            if (m_colModel == null) {
                return null;
            }

            // match columns by (symbolic) index, makes for easier reordering
            if (columnIndex == COL_INDEX_TITLE) {
                return m_section.getTitle();
            } else if (columnIndex == COL_INDEX_EDIT) {
                //return "edit";
                return new Label(
                        MPArticleGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.mparticle.section_table.link_edit"));
            } else if (columnIndex == COL_INDEX_DELETE) {
                //  return "delete";
                return new Label(
                        MPArticleGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.mparticle.section_table.link_delete"));
            } else if (columnIndex == COL_INDEX_MOVE) {
                if (m_moveSection.getSelectedKey(m_state) == null) {
                    //  return "move";
                    return new Label(
                            MPArticleGlobalizationUtil.globalize(
                            "cms.contenttypes.ui.mparticle.section_table.link_move"));
                } else {
                    //  return "move below here";
                    return new Label(
                            MPArticleGlobalizationUtil.globalize(
                            "cms.contenttypes.ui.mparticle.section_table.link_move_below"));
                }
            }

            return null;
        }

        /** 
         * Return the key for the given column and the current row. 
         */
        @Override
        public Object getKeyAt(int columnIndex) {
            return m_section.getID();
        }

    }

    /**
     * 
     */
    public class SectionTableCellRenderer extends LockableImpl
            implements TableCellRenderer {

        private boolean m_active;

        public SectionTableCellRenderer() {
            this(false);
        }

        public SectionTableCellRenderer(boolean active) {
            m_active = active;
        }

        @Override
        public Component getComponent(Table table, PageState state,
                                      Object value, boolean isSelected,
                                      Object key, int row, int column) {

            Component ret;
            SecurityManager sm = CMS.getSecurityManager(state);
            ContentItem item = (ContentItem) m_selArticle.getSelectedObject(state);

            boolean active = m_active && sm.canAccess(state.getRequest(),
                                                      SecurityManager.EDIT_ITEM,
                                                      item);

            if (value instanceof Label) {
                if (active) {
                    ret = new ControlLink((Component) value);
                } else {
                    ret = (Component) value;
                }

            } else if (value instanceof String) {
                // Backwards compatibility, should be removed asap!
                if (active) {
                    ret = new ControlLink(value.toString());
                } else {
                    ret = new Label(value.toString());
                }
            } else {
                ret = new Label(MPArticleGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.mparticle.section_table.link_not_defined"),
                                false);
            }

            return ret;
        }

    }
}
