/*
 * Copyright (C) 2008 Sören Bernstein All Rights Reserved.
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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;

import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.GlobalizedMessagesUtil;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

/**
 * Lists all existing localizations for a selected category.
 *
 * This class is part of the admin GUI of CCM and extends the standard form in
 * order to present forms for managing the multi-language categories.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class CategoryLocalizationTable extends Table implements
    TableActionListener {

    private static final String TABLE_COL_LANG = "table_col_lang";
    private static final String TABLE_COL_DEL = "table_col_del";

    private final CategoryRequestLocal m_category;
    private final SingleSelectionModel m_model;
    private final SingleSelectionModel m_catLocale;

    /**
     * Creates a new instance of CategoryLocalizationTable
     */
    public CategoryLocalizationTable(final CategoryRequestLocal category,
                                     final SingleSelectionModel model,
                                     final SingleSelectionModel catLocale) {

        super();

        m_category = category;
        m_model = model;
        m_catLocale = catLocale;

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final GlobalizationHelper globalizationHelper = cdiUtil
            .findBean(GlobalizationHelper.class);
        final GlobalizedMessagesUtil messagesUtil = globalizationHelper
            .getGlobalizedMessagesUtil(CmsConstants.CMS_BUNDLE);

        // if table is empty:
        setEmptyView(new Label(messagesUtil
            .getGlobalizedMessage("cms.ui.category.localization_none")));
        final TableColumnModel columnModel = getColumnModel();

        // define columns
        columnModel.add(new TableColumn(
            0,
            messagesUtil.getGlobalizedMessage(
                "cms.ui.category.localization.locale")
                .localize(),
            TABLE_COL_LANG));
        columnModel.add(new TableColumn(
            1,
            messagesUtil
                .getGlobalizedMessage("cms.ui.category.localization_name")
                .localize()));
        columnModel.add(new TableColumn(
            2,
            messagesUtil.getGlobalizedMessage(
                "cms.ui.category.localization_description")
                .localize()));
        columnModel.add(new TableColumn(
            3,
            messagesUtil
                .getGlobalizedMessage("cms.ui.category.localization_url")
                .localize()));
        columnModel.add(new TableColumn(
            4,
            messagesUtil
                .getGlobalizedMessage("cms.ui.category.localization_action")
                .localize(),
            TABLE_COL_DEL));

        super.setModelBuilder(new CategoryLocalizationTableModelBuilder());

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(4).setCellRenderer(new DeleteCellRenderer());

        super.addTableActionListener(this);

    }

    /**
     * XXXX
     *
     */
    private class CategoryLocalizationTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        public TableModel makeModel(Table table, PageState state) {
            final Category category = m_category.getCategory(state);

            if (category != null) {
                return new CategoryLocalizationTableModel(table, state, category);
            } else {
                return Table.EMPTY_MODEL;
            }
        }

    }

    /**
     * XXX
     *
     */
    private class CategoryLocalizationTableModel implements TableModel {

        private Table m_table;
        private ArrayList<LocalizedString> localizedStringCollection;
        private LocalizedString m_categoryLocalization;

        private CategoryLocalizationTableModel(Table t, PageState ps,
                                               Category category) {
            m_table = t;
            localizedStringCollection = new ArrayList<>();
            localizedStringCollection.add(category.getTitle());
            localizedStringCollection.add(category.getDescription());
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        /**
         * Check collection for the existence of another row.
         *
         * If exists, fetch the value of current CategoryLocalization object
         * into m_categoryLocalization class variable.
         */
        public boolean nextRow() {
            return false;
//            if (m_categoryLocalizations != null && m_categoryLocalizations.next()) {
//                m_categoryLocalization = m_categoryLocalizations.getCategoryLocalization();
//                return true;
//
//            } else {
//
//                return false;
//
//            }
        }

        /**
         * Return the
         *
         * @see com.arsdigita.bebop.table.TableModel#getElementAt(int)
         */
        public Object getElementAt(int columnIndex) {
            return null;
//            switch (columnIndex) {
//                case 0:
//                    Locale clLocale = new Locale(m_categoryLocalization.getLocale());
//                    return clLocale.getDisplayLanguage(/*Locale*/);
//                case 1:
//                    return m_categoryLocalization.getName();
//                case 2:
//                    String desc = m_categoryLocalization.getDescription();
//                    if (desc != null && desc.length() > MAX_DESC_LENGTH) {
//                        desc = desc.substring(MAX_DESC_LENGTH - 3).concat("...");
//                    }
//                    return desc;
//                case 3:
//                    return m_categoryLocalization.getURL();
//                case 4:
//                    return GlobalizationUtil.globalize("cms.ui.delete").localize();
//                default:
//                    return null;
//            }
        }

        /**
         *
         * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
         */
        public Object getKeyAt(int columnIndex) {
            return null;
//          return m_categoryLocalization.getID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements
        TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, final Object key,
                                      int row, int column) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);

            if (permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_CATEGORIES, m_category.getCategory(
                    state))) {
                return new ControlLink(value.toString());
            } else {
                return new Label(GlobalizationUtil.globalize(value.toString()));
            }
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements
        TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);

            if (permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_CATEGORIES, m_category.getCategory(
                    state))) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation(GlobalizationUtil.globalize(
                    "cms.ui.category.localization_confirm_delete"));
                return link;
            } else {
                return null;
            }
        }

    }

    /**
     * Provide implementation to TableActionListener method. Code that comes
     * into picture when a link on the table is clicked. Handles edit and delete
     * event.
     */
    public void cellSelected(TableActionEvent evt) {

        PageState state = evt.getPageState();

//        // Get selected CategoryLocalization
//        CategoryLocalization categoryLocalization =
//                new CategoryLocalization(new BigDecimal(evt.getRowKey().toString()));
//
//        // Get Category
//        Category category = m_category.getCategory(state);
//
//        // Get selected column
//        TableColumn col = getColumnModel().get(evt.getColumn().intValue());
//
//        // Edit
//        if (col.getHeaderKey().toString().equals(TABLE_COL_LANG)) {
//            m_catLocale.setSelectedKey(state, categoryLocalization.getLocale());
//        }
//
//        // Delete
//        if (col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
//            category.delLanguage(categoryLocalization.getLocale());
//        }
    }

    /**
     * provide Implementation to TableActionListener method. Does nothing in our
     * case.
     */
    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException("Not Implemented");
    }

}
