/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.shortcuts.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
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
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.shortcuts.Shortcut;
import org.libreccm.shortcuts.ShortcutRepository;
import org.libreccm.shortcuts.ShortcutsConstants;

import java.util.List;

/**
 * Table which lists all shortcuts.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ShortcutsTable extends Table {

    private static final int COL_URL_KEY = 0;
    private static final int COL_REDIRECT = 1;
    private static final int COL_EDIT = 2;
    private static final int COL_DELETE = 3;

    private final ShortcutsSettingsPane shortcutsPane;

    public ShortcutsTable(
        final ShortcutsSettingsPane shortcutsPane,
        final ParameterSingleSelectionModel<String> selectedShortcut) {

        super();

        this.shortcutsPane = shortcutsPane;

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_URL_KEY,
            new Label(new GlobalizedMessage(
                "shortcuts.ui.admin.shortcuts_table.col_url_key.header",
                ShortcutsConstants.SHORTCUTS_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_REDIRECT,
            new Label(new GlobalizedMessage(
                "shortcuts.ui.admin.shortcuts_table.col_redirect.header",
                ShortcutsConstants.SHORTCUTS_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_EDIT,
            new Label(new GlobalizedMessage(
                "shortcuts.ui.admin.shortcuts_table.col_edit.header",
                ShortcutsConstants.SHORTCUTS_BUNDLE))
        ));
        columnModel.add(new TableColumn(
            COL_DELETE,
            new Label(new GlobalizedMessage(
                "shortcuts.ui.admin.shortcuts_table.col_delete.header",
                ShortcutsConstants.SHORTCUTS_BUNDLE))
        ));

        columnModel.get(COL_EDIT).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                return new ControlLink((Component) value);
            }

        });

        columnModel.get(COL_DELETE).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                return new ControlLink((Component) value);
            }

        });

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();

                switch (event.getColumn()) {
                    case COL_EDIT:
                        selectedShortcut.setSelectedKey(state,
                                                        event.getRowKey());
                        shortcutsPane.showShortcutForm(state);
                        break;
                    case COL_DELETE: {
                        final ShortcutRepository repo = CdiUtil.createCdiUtil()
                            .findBean(ShortcutRepository.class);
                        final Shortcut shortcut = repo.findById(Long.parseLong(
                            (String) event.getRowKey()));
                        repo.delete(shortcut);
                        break;
                    }
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //nothing
            }

        });

        setModelBuilder(new ShortcutsTableModelBuilder());

        setEmptyView(new Label(new GlobalizedMessage(
            "shortcuts.ui.admin.table.empty",
            ShortcutsConstants.SHORTCUTS_BUNDLE)));

    }

    private class ShortcutsTableModelBuilder extends LockableImpl implements
        TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            return new ShortcutsTableModel();
        }

    }

    private class ShortcutsTableModel implements TableModel {

        private final List<Shortcut> shortcuts;
        private int index = -1;

        public ShortcutsTableModel() {
            final ShortcutRepository repo = CdiUtil.createCdiUtil().findBean(
                ShortcutRepository.class);
            shortcuts = repo.findAll();
            shortcuts.sort((s1, s2) -> {
                return s1.getUrlKey().compareTo(s2.getUrlKey());
            });
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < shortcuts.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Shortcut shortcut = shortcuts.get(index);

            switch (columnIndex) {
                case COL_URL_KEY:
                    return shortcut.getUrlKey();
                case COL_REDIRECT:
                    return shortcut.getRedirect();
                case COL_EDIT:
                    return new Label(new GlobalizedMessage(
                        "shortcuts.ui.admin.shortcuts_table.edit",
                        ShortcutsConstants.SHORTCUTS_BUNDLE));
                case COL_DELETE:
                    return new Label(new GlobalizedMessage(
                        "shortcuts.ui.admin.shortcuts_table.delete",
                        ShortcutsConstants.SHORTCUTS_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return Long.toString(shortcuts.get(index).getShortcutId());
        }

    }

}
