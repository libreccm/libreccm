/*
 * Copyright (C) 2015 LibreCCM Foundation.
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

import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.ParameterSingleSelectionModel;
import org.libreccm.shortcuts.Shortcut;
//import com.arsdigita.shortcuts.ShortcutCollection;
import java.math.BigDecimal;

import org.apache.log4j.Category;

import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.Component;
import java.util.List;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.shortcuts.ShortcutRepository;

/**
 *
 *
 */
public class ShortcutsTable extends Table {

    private static final Category log
                                      = Category.getInstance(
            ShortcutsTable.class.getName());

    public static final String headers[] = {"URL Key", "Redirect", "", ""};

    public ShortcutsTable(final ParameterSingleSelectionModel selected_shortcut) {
        super(new ShortcutsModelBuilder(), headers);
        setDefaultCellRenderer(new ShortcutsCellRenderer());
        final CdiUtil cdiUtil = new CdiUtil();
        final ShortcutRepository shortcutsRepo = cdiUtil.findBean(
            ShortcutRepository.class);

        addTableActionListener(new TableActionListener() {

            public void cellSelected(TableActionEvent e) {
                selected_shortcut.clearSelection(e.getPageState());
                String row = (String) e.getRowKey();
                if (e.getColumn().intValue() == 2) {
                    // edit selected
                    log.debug("selected edit shortcut " + row);
                    selected_shortcut.setSelectedKey(e.getPageState(),
                                                     new BigDecimal(row));

                } else if (e.getColumn().intValue() == 3) {
                    // delete selected
                    log.fatal("selected delete shortcut " + row);

                    Shortcut shortcut = shortcutsRepo.findById(
                        (Long) selected_shortcut
                        .getSelectedKey(e.getPageState()));

                    if (shortcut != null) {
                        log.info("delete shortcut " + shortcut.getUrlKey());
                        shortcutsRepo.delete(shortcut);
                    }
                }
            }

            public void headSelected(TableActionEvent e) {
            }

        });
    }

    protected static class ShortcutsModelBuilder extends LockableImpl implements
        TableModelBuilder {

        public TableModel makeModel(Table table, PageState ps) {
            return new ShortcutsModel();
        }

        protected class ShortcutsModel implements TableModel {

            private ShortcutRepository shortcutRepo = new ShortcutRepository();
            private List<Shortcut> m_shortcuts = null;
            private int index = 0;
            private Shortcut m_shortcut;

            public ShortcutsModel() {
//                m_shortcuts = Shortcut.retrieveAll();
                m_shortcuts = shortcutRepo.findAll();
            }

            public int getColumnCount() {
                return headers.length;
            }

            public boolean nextRow() {
                if (index < m_shortcuts.size()) {
                    index++;
                    m_shortcut = m_shortcuts.get(index);
                    return true;
                } else {
                    return false;
                }
            }

            public Object getElementAt(int col) {
                return m_shortcut;
            }

            public Long getKeyAt(int col) {
                Long id = m_shortcut.getShortcutId();
                return id;
            }

        }

    }

    protected static class ShortcutsCellRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state,
                                      Object value, boolean isSelected,
                                      Object key, int row,
                                      int column) {
            Shortcut shortcut = (Shortcut) value;

            switch (column) {
//                case 0:
//                    return new ExternalLink(shortcut.getUrlKey(), shortcut
//                            .getUrlKey());
//                case 1:
//                    return new ExternalLink(shortcut.getRedirect(), shortcut
//                            .getRedirect());
                case 2:
                    return new ControlLink(" edit ");
                case 3:
                    return new ControlLink(" delete ");
                default:
                    throw new UncheckedWrapperException("Column out of bounds");
            }
        }

    }

}
