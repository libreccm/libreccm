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
package com.arsdigita.ui.admin.pagemodels;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.AdminUiConstants;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.pagemodel.ComponentModel;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ComponentsTable extends Table {

    protected static final int COL_COMPONENT_KEY = 0;
    protected static final int COL_COMPONENT_TYPE = 1;
    protected static final int COL_EDIT = 2;
    protected static final int COL_DELETE = 3;

    public ComponentsTable(
        final PageModelTab pageModelTab,
        final ParameterSingleSelectionModel<String> selectedModelId,
        final ParameterSingleSelectionModel<String> selectedComponentId) {

        super();
        super.setIdAttr("pageModelComponentModelsTable");

        super.setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.pagemodels.componentmodels.none",
            AdminUiConstants.ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_COMPONENT_KEY,
            new Label(new GlobalizedMessage(
                "ui.admin.pagemodels.componentmodels.cols.key.heading",
                AdminUiConstants.ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_COMPONENT_TYPE,
            new Label(new GlobalizedMessage(
                "ui.admin.pagemodels.componentmodels.cols.type.heading",
                AdminUiConstants.ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_EDIT,
            new Label(new GlobalizedMessage(
                "ui.admin.pagemodels.componentmodels.cols.edit.heading",
                AdminUiConstants.ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DELETE,
            new Label(new GlobalizedMessage(
                "ui.admin.pagemodels.componentmodels.cols.delete.heading",
                AdminUiConstants.ADMIN_BUNDLE))));

        columnModel.get(COL_EDIT).setCellRenderer(
            new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {

                final ControlLink link = new ControlLink((Component) value);
                return link;
            }

        });

        columnModel.get(COL_DELETE).setCellRenderer(
            new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {

                final ControlLink link = new ControlLink((Component) value);
                link.setConfirmation(new GlobalizedMessage(
                    "ui.admin.pagemodels.componentmodels.cols.delete.confirmation",
                    AdminUiConstants.ADMIN_BUNDLE));
                return link;
            }

        });

        super.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();
                final String selectedModelIdStr = selectedModelId
                    .getSelectedKey(state);
                final String key = (String) event.getRowKey();

                switch (event.getColumn()) {
                    case COL_EDIT:
                        selectedComponentId.setSelectedKey(state, key);
                        pageModelTab.showComponentForm(state);
                        break;
                    case COL_DELETE:
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final PageModelsController controller = cdiUtil
                            .findBean(PageModelsController.class);
                        controller.removeComponentModel(
                            Long.parseLong(selectedModelIdStr),
                            Long.parseLong(key));
                        break;
                    default:
                        throw new IllegalArgumentException(
                            "Invalid value for column");
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        super.setModelBuilder(new ComponentsTableModelBuilder(
            selectedModelId));
    }

    private class ComponentsTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        private final ParameterSingleSelectionModel<String> selectedModelId;

        public ComponentsTableModelBuilder(
            final ParameterSingleSelectionModel<String> selectedModelId) {

            this.selectedModelId = selectedModelId;
        }

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PageModelsController controller = cdiUtil
                .findBean(PageModelsController.class);

            final String selectedModelIdStr = selectedModelId
                .getSelectedKey(state);

            final List<ComponentModel> components = controller
                .retrieveComponents(Long.parseLong(selectedModelIdStr));

            return new ComponentsTableModel(components);
        }

    }

    private class ComponentsTableModel implements TableModel {

        private final Iterator<ComponentModel> iterator;
        private ComponentModel currentComponent;

        public ComponentsTableModel(
            final List<ComponentModel> components) {

            iterator = components.iterator();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public boolean nextRow() {

            if (iterator.hasNext()) {
                currentComponent = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {

            switch (columnIndex) {
                case ComponentsTable.COL_COMPONENT_KEY:
                    return currentComponent.getKey();
                case ComponentsTable.COL_COMPONENT_TYPE:
                    return currentComponent.getClass().getName();
                case ComponentsTable.COL_EDIT:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.pagemodels.components.edit",
                        AdminUiConstants.ADMIN_BUNDLE));
                case ComponentsTable.COL_DELETE:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.pagemodels.components.delete",
                        AdminUiConstants.ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {

            return currentComponent.getComponentModelId();
        }

    }

}
