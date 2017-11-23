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
import com.arsdigita.bebop.Text;
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

import java.util.Iterator;
import java.util.List;

/**
 * Table showing all available {@link PageModels}. 
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelsTable extends Table {

    public static final int COL_MODEL_APPLICATION = 0;
    public static final int COL_MODEL_NAME = 1;
    public static final int COL_MODEL_TITLE = 2;
    public static final int COL_MODEL_DESC = 3;
    public static final int COL_REMOVE = 4;

    public PageModelsTable(
        final PageModelsTab parent,
        final ParameterSingleSelectionModel<String> selectedPageModelId) {

        super();

        super.setIdAttr("pageModelsTable");
        super.setStyleAttr("wdith: 30em");

        setEmptyView(new Label(
            new GlobalizedMessage("ui.admin.pagemodels.table.empty_view",
                                  AdminUiConstants.ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_MODEL_APPLICATION,
            new Label(new GlobalizedMessage(
                "ui.admin.pagemodels.table.columns.headers.application",
                AdminUiConstants.ADMIN_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_MODEL_NAME,
            new Label(new GlobalizedMessage(
                "ui.admin.pagemodels.table.columns.headers.name",
                AdminUiConstants.ADMIN_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_MODEL_TITLE,
            new Label(new GlobalizedMessage(
                "ui.admin.pagemodels.table.columns.headers.title",
                AdminUiConstants.ADMIN_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_MODEL_DESC,
            new Label(new GlobalizedMessage(
                "ui.admin.pagemodels.table.columns.headers.desc",
                AdminUiConstants.ADMIN_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_REMOVE,
            new Label(new GlobalizedMessage(
                "ui.admin.pagemodels.table.columns.headers.remove",
                AdminUiConstants.ADMIN_BUNDLE))
        ));

        columnModel
            .get(COL_MODEL_NAME)
            .setCellRenderer(new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {

                    return new ControlLink((String) value);
                }

            });

        columnModel
            .get(COL_REMOVE)
            .setCellRenderer(new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {

                    if (value == null) {
                        return new Text("");
                    } else {
                        final ControlLink link = new ControlLink(
                            (Component) value);
                        link.setConfirmation(new GlobalizedMessage(
                            "ui.admin.pagemodels.delete.confirm",
                            AdminUiConstants.ADMIN_BUNDLE));
                        return link;
                    }
                }

            });

        super.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();
                final String key = (String) event.getRowKey();

                switch (event.getColumn()) {
                    case COL_MODEL_NAME:
                        selectedPageModelId.setSelectedKey(state, key);
                        parent.showPageModelDetails(state);
                        break;
                    case COL_REMOVE:
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final PageModelsController controller = cdiUtil
                            .findBean(PageModelsController.class);
                        controller.deletePageModel(Long.parseLong(key));
                        break;
                    default:
                        throw new IllegalArgumentException(
                            "Invalid value for column.");
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {

                // Nothing
            }

        });

        super.setModelBuilder(new PageModelsTableModelBuilder());
    }

    private class PageModelsTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PageModelsController controller = cdiUtil
                .findBean(PageModelsController.class);
            return new PageModelsTableModel(controller.findPageModels());
        }

    }

    private class PageModelsTableModel implements TableModel {

        private final Iterator<PageModelsTableRow> iterator;
        private PageModelsTableRow currentRow;

        public PageModelsTableModel(final List<PageModelsTableRow> rows) {
            iterator = rows.iterator();
        }

        @Override
        public int getColumnCount() {
            return 5;
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
                case COL_MODEL_APPLICATION:
                    return currentRow.getApplicationName();
                case COL_MODEL_DESC:
                    return currentRow.getDescription();
                case COL_MODEL_NAME:
                    return currentRow.getName();
                case COL_MODEL_TITLE:
                    return currentRow.getTitle();
                case COL_REMOVE:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.pagemodels.table.columns.remove.label",
                        AdminUiConstants.ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException("No a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {

            return currentRow.getModelId();
        }

    }

}
