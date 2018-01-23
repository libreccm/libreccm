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
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.GlobalizedMessagesUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.Iterator;
import java.util.List;

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

    private static final int COL_LOCALE = 0;
    private static final int COL_TITLE = 1;
    private static final int COL_DESCRIPTION = 2;
    private static final int COL_EDIT = 3;
    private static final int COL_DEL = 4;

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
            COL_LOCALE,
            messagesUtil.getGlobalizedMessage(
                "cms.ui.category.localization.locale"),
            TABLE_COL_LANG));
        columnModel.add(new TableColumn(
            COL_TITLE,
            messagesUtil
                .getGlobalizedMessage("cms.ui.category.localization_title")));
        columnModel.add(new TableColumn(
            COL_DESCRIPTION,
            messagesUtil.getGlobalizedMessage(
                "cms.ui.category.localization_description")));
        columnModel.add(new TableColumn(
            COL_EDIT,
            messagesUtil
                .getGlobalizedMessage("cms.ui.category.localization_edit")));
        columnModel.add(new TableColumn(
            COL_DEL,
            messagesUtil
                .getGlobalizedMessage("cms.ui.category.localization_action"),
            TABLE_COL_DEL));

        super.setModelBuilder(new CategoryLocalizationTableModelBuilder());

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(4).setCellRenderer(new DeleteCellRenderer());

        super.addTableActionListener(this);

    }

    private class CategoryLocalizationTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            
            final Category category = m_category.getCategory(state);

            if (category == null) {
                return Table.EMPTY_MODEL;
            } else {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final CategoryLocalizationTableController controller = cdiUtil
                .findBean(CategoryLocalizationTableController.class);
                
                final List<CategoryLocalizationTableRow> rows = controller
                .getCategoryLocalizations(category);
                return new CategoryLocalizationTableModel(table, rows);
            }
        }

    }

    private class CategoryLocalizationTableModel implements TableModel {

        private final Table table;
        private final Iterator<CategoryLocalizationTableRow> iterator;
        private CategoryLocalizationTableRow currentRow;

        private CategoryLocalizationTableModel(
            final Table table,
            final List<CategoryLocalizationTableRow> rows) {

            this.table = table;
            iterator = rows.iterator();
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {

            if (iterator.hasNext()) {
                currentRow = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {

            switch (columnIndex) {
                case COL_LOCALE:
                    return currentRow.getLocale();
                case COL_TITLE:
                    return currentRow.getTitle();
                case COL_DESCRIPTION:
                    return currentRow.getDescription();
                case COL_EDIT:
                    return new GlobalizedMessage("cms.ui.edit",
                                                 CmsConstants.CMS_BUNDLE);
                case COL_DEL:
                    return new GlobalizedMessage("cms.ui.delete",
                                                 CmsConstants.CMS_BUNDLE);
                default:
                    throw new IllegalArgumentException("Illegal Column Index");
            }
        }

        /**
         *
         * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
         */
        @Override
        public Object getKeyAt(final int columnIndex) {
            return currentRow.getLocale();
        }

    }

    private class EditCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            
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

        @Override
        public Component getComponent(final Table table, 
                                      final PageState state, 
                                      final Object value,
                                      final boolean isSelected, 
                                      final Object key,
                                      final int row, 
                                      final int column) {
            
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
    @Override
    public void cellSelected(final TableActionEvent event) {

        PageState state = event.getPageState();

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
    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing
    }

}
