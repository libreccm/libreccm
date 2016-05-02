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
package com.arsdigita.ui.admin.configuration;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.apache.logging.log4j.util.Strings;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.stream.Collectors;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ConfigurationsTable extends Table {

    private static final int COL_TITLE = 0;
    private static final int COL_DESC = 1;

    public ConfigurationsTable(
        final ConfigurationTab configurationTab,
        final ParameterSingleSelectionModel<String> selectedConf,
        final TextField confClassesFilter) {

        super();

        setIdAttr("configurationsTable");

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.configuration.configurations.none", ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_TITLE,
            new Label(new GlobalizedMessage(
                "ui.admin.configuration.configurations.table.name",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DESC,
            new Label(new GlobalizedMessage(
                "ui.admin.configuration.configurations.table.desc",
                ADMIN_BUNDLE))));

        columnModel.get(COL_TITLE).setCellRenderer(new TableCellRenderer() {

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

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();

                if (event.getColumn() == COL_TITLE) {
                    final String confClassName = (String) event.getRowKey();
                    selectedConf.setSelectedKey(state, confClassName);
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        setModelBuilder(new ConfigurationsTableModelBuilder(confClassesFilter));
    }

    private class ConfigurationsTableModelBuilder
        extends LockableImpl implements TableModelBuilder {

        private final TextField confClassesFilter;

        public ConfigurationsTableModelBuilder(
            final TextField confClassesField) {

            this.confClassesFilter = confClassesField;
        }

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new ConfigurationsTableModel(confClassesFilter, state);
        }

    }

    private class ConfigurationsTableModel implements TableModel {

        private final List<Class<?>> configurations;
        private int index = -1;
        private final ConfigurationManager confManager;
        private final Locale negoiatedLocale;

        public ConfigurationsTableModel(final TextField confClassesFilter,
                                        final PageState state) {
            final String filterTerm = (String) confClassesFilter
                .getValue(state);
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            confManager = cdiUtil.findBean(ConfigurationManager.class);
            final GlobalizationHelper globalizationHelper = cdiUtil.findBean(
                GlobalizationHelper.class);
            negoiatedLocale = globalizationHelper.getNegotiatedLocale();
            final SortedSet<Class<?>> confs = confManager
                .findAllConfigurations();
            configurations = confs.stream()
                .filter(c -> {
                    final ConfigurationInfo info = confManager
                        .getConfigurationInfo(c);
//                    return c.getName().startsWith(filterTerm);
                    return info.getTitle(negoiatedLocale).startsWith(filterTerm);
                })
                .collect(Collectors.toCollection(ArrayList::new));
            configurations.sort((c1, c2) -> {
//                return c1.getName().compareTo(c2.getName());
                final ConfigurationInfo info1 = confManager
                    .getConfigurationInfo(c1);
                final ConfigurationInfo info2 = confManager
                    .getConfigurationInfo(c2);

                return info1.getTitle(negoiatedLocale)
                    .compareTo(info2.getTitle(negoiatedLocale));
            });
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < configurations.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final ConfigurationInfo info = confManager.getConfigurationInfo(
                configurations.get(index));
            switch (columnIndex) {
                case COL_TITLE:
                    if (Strings.isBlank(info.getTitle(negoiatedLocale))) {
                        return configurations.get(index).getSimpleName();
                    } else {
                        return info.getTitle(negoiatedLocale);
                    }
                case COL_DESC:
                    return info.getDescription(negoiatedLocale);
                default:
                    throw new IllegalArgumentException("Illegal column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return configurations.get(index).getName();
        }

    }

}
