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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
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
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.EnumSetting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Editor for {@link EnumSetting}s. The editor consists of the usual header (see
 * {@link SettingFormHeader}) which is used by all setting forms/editors, a
 * table which displays all current values together with links for moving,
 * editing and deleting the values and a form for adding and editing values.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SettingEditorStringList extends BoxPanel {

    private static final Logger LOGGER = LogManager.getLogger(
        SettingEditorStringList.class);

    private static final int COL_VALUE = 0;
    private static final int COL_UP = 1;
    private static final int COL_DOWN = 2;
    private static final int COL_EDIT = 3;
    private static final int COL_DEL = 4;

    private final ConfigurationTab configurationTab;
    private final ParameterSingleSelectionModel<String> selectedConf;
    private final ParameterSingleSelectionModel<String> selectedSetting;
    private final ParameterSingleSelectionModel<String> selectedValue;

    public SettingEditorStringList(
        final ConfigurationTab configurationTab,
        final ParameterSingleSelectionModel<String> selectedConf,
        final ParameterSingleSelectionModel<String> selectedSetting,
        final ParameterSingleSelectionModel<String> selectedValue) {

        super(BoxPanel.VERTICAL);

        this.configurationTab = configurationTab;
        this.selectedConf = selectedConf;
        this.selectedSetting = selectedSetting;
        this.selectedValue = selectedValue;

        add(new SettingFormHeader(configurationTab,
                                  selectedConf,
                                  selectedSetting));

        add(new ValuesTable());

        add(new ValueForm());

    }

    private class ValuesTable extends Table {

        public ValuesTable() {

            super();

            setIdAttr("stringListValues");

            setEmptyView(new Label(new GlobalizedMessage(
                "ui.admin.configuration.setting.stringlist.no_values",
                ADMIN_BUNDLE)));

            final TableColumnModel columnModel = getColumnModel();
            columnModel.add(new TableColumn(
                COL_VALUE,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.stringlist.col_value",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_UP,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.stringlist.col_up",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_DOWN,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.stringlist.col_down",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_EDIT,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.stringlist.col_edit",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_DEL,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.stringlist.col_delete",
                    ADMIN_BUNDLE))));

            columnModel.get(COL_UP).setCellRenderer(new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    if (row > 0) {
                        return new ControlLink((Component) value);
                    } else {
                        return new Text("");
                    }
                }

            });

            columnModel.get(COL_DOWN).setCellRenderer(new TableCellRenderer() {

                @Override
                @SuppressWarnings("unchecked")
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

                    final Class<?> confClass;
                    try {
                        confClass = Class
                            .forName(selectedConf.getSelectedKey(state));
                    } catch (ClassNotFoundException ex) {
                        throw new UncheckedWrapperException(ex);
                    }

                    final ConfigurationManager confManager = cdiUtil
                        .findBean(
                            ConfigurationManager.class);

                    final Object config = confManager.findConfiguration(
                        confClass);

                    final List<String> values;
                    try {
                        final Field field = confClass.getDeclaredField(
                            selectedSetting.getSelectedKey(state));
                        field.setAccessible(true);
                        values = (List<String>) field.get(config);
                    } catch (NoSuchFieldException |
                             SecurityException |
                             IllegalAccessException |
                             ClassCastException ex) {
                        LOGGER.warn(
                            "Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                        LOGGER.warn(ex);

                        throw new UncheckedWrapperException(ex);
                    }

                    if (row < values.size()) {
                        return new ControlLink((Component) value);
                    } else {
                        return new Text("");
                    }
                }

            });

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

            columnModel.get(COL_DEL).setCellRenderer(new TableCellRenderer() {

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
                            "ui.admin.configuration.setting.stringlist.del_confirm",
                            ADMIN_BUNDLE));
                        return link;
                    }
                }

            });

            addTableActionListener(new TableActionListener() {

                @Override
                @SuppressWarnings("unchecked")
                public void cellSelected(final TableActionEvent event) {
                    final PageState state = event.getPageState();

                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

                    final Class<?> confClass;
                    try {
                        confClass = Class
                            .forName(selectedConf.getSelectedKey(state));
                    } catch (ClassNotFoundException ex) {
                        throw new UncheckedWrapperException(ex);
                    }

                    final ConfigurationManager confManager = cdiUtil
                        .findBean(
                            ConfigurationManager.class);

                    final Object config = confManager.findConfiguration(
                        confClass);

                    final List<String> values;
                    try {
                        final Field field = confClass.getDeclaredField(
                            selectedSetting.getSelectedKey(state));
                        field.setAccessible(true);
                        values = (List<String>) field.get(config);
                    } catch (NoSuchFieldException | SecurityException |
                             IllegalAccessException | ClassCastException ex) {
                        LOGGER.warn(
                            "Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                        LOGGER.warn(ex);

                        throw new UncheckedWrapperException(ex);
                    }

                    switch (event.getColumn()) {
                        case COL_UP: {

                            final int currentIndex = Integer.parseInt(
                                (String) event.getRowKey());
                            final int previousIndex = currentIndex - 1;

                            final String currentValue = values.get(currentIndex);
                            final String previousValue = values.get(
                                previousIndex);

                            values.set(previousIndex, currentValue);
                            values.set(currentIndex, previousValue);

                            confManager.saveConfiguration(config);

                            break;
                        }
                        case COL_DOWN: {

                            final int currentIndex = Integer.parseInt(
                                (String) event.getRowKey());
                            final int nextIndex = currentIndex + 1;

                            final String currentValue = values.get(currentIndex);
                            final String nextValue = values.get(nextIndex);

                            values.set(nextIndex, currentValue);
                            values.set(currentIndex, nextValue);

                            confManager.saveConfiguration(config);

                            break;
                        }
                        case COL_EDIT: {
                            selectedValue.setSelectedKey(state,
                                                         event.getRowKey());
                            break;
                        }
                        case COL_DEL: {
                            values.remove(Integer.parseInt((String) event
                                .getRowKey()));

                            confManager.saveConfiguration(config);

                            break;
                        }
                    }

                }

                @Override
                public void headSelected(final TableActionEvent event) {
                    //Notthing
                }

            });

            setModelBuilder(new ValuesTableModelBuilder());
        }

    }

    private class ValuesTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new ValuesTableModel(state);
        }

    }

    private class ValuesTableModel implements TableModel {

        private final List<String> values;
        private int index = -1;

        @SuppressWarnings("unchecked")
        public ValuesTableModel(final PageState state) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

            final Class<?> confClass;
            try {
                confClass = Class
                    .forName(selectedConf.getSelectedKey(state));
            } catch (ClassNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }

            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);

            final Object config = confManager.findConfiguration(confClass);

            try {
                final Field field = confClass.getDeclaredField(selectedSetting
                    .getSelectedKey(state));
                field.setAccessible(true);
                values = (List<String>) field.get(config);
            } catch (NoSuchFieldException |
                     SecurityException |
                     IllegalAccessException |
                     ClassCastException ex) {
                LOGGER.warn("Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                LOGGER.warn(ex);
                throw new UncheckedWrapperException(ex);
            }

        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < values.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case COL_VALUE:
                    return values.get(index);
                case COL_UP:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.configuration.setting.stringlist.value.up",
                        ADMIN_BUNDLE));
                case COL_DOWN:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.configuration.setting.stringlist.value.down",
                        ADMIN_BUNDLE));
                case COL_EDIT:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.configuration.setting.stringlist.value.edit",
                        ADMIN_BUNDLE));
                case COL_DEL:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.configuration.setting.stringlist.value.del",
                        ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return index;
        }

    }

    private class ValueForm extends Form {

        private final String VALUE = "value";

        @SuppressWarnings("unchecked")
        public ValueForm() {
            super("settingStringListValueForm");

            final TextField valueField = new TextField(VALUE);
            valueField.setLabel(new GlobalizedMessage(
                "ui.admin.configuration.setting.stringlist.value.label",
                ADMIN_BUNDLE));
            add(valueField);

            final SaveCancelSection saveCancelSection = new SaveCancelSection();
            add(saveCancelSection);

            addInitListener(e -> {
                final PageState state = e.getPageState();

                if (selectedValue.getSelectedKey(state) != null) {
                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

                    final Class<?> confClass;
                    try {
                        confClass = Class
                            .forName(selectedConf.getSelectedKey(state));
                    } catch (ClassNotFoundException ex) {
                        throw new UncheckedWrapperException(ex);
                    }

                    final ConfigurationManager confManager = cdiUtil.findBean(
                        ConfigurationManager.class);

                    final Object config = confManager.findConfiguration(
                        confClass);

                    try {
                        @SuppressWarnings("unchecked")
                        final Field field = confClass.getDeclaredField(
                            selectedSetting.getSelectedKey(state));
                        field.setAccessible(true);
                        final List<String> stringList = (List<String>) field
                            .get(config);

                        final String str = stringList.get(Integer.parseInt(
                            selectedValue.getSelectedKey(state)));

                        valueField.setValue(state, str);

                    } catch (NoSuchFieldException | SecurityException |
                             IllegalAccessException | ClassCastException ex) {
                        LOGGER.warn(
                            "Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                        LOGGER.warn(ex);

                        throw new UncheckedWrapperException(ex);
                    }
                }
            });

            addProcessListener(e -> {
                final PageState state = e.getPageState();

                if (saveCancelSection.getSaveButton().isSelected(state)) {

                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

                    final Class<?> confClass;
                    try {
                        confClass = Class
                            .forName(selectedConf.getSelectedKey(state));
                    } catch (ClassNotFoundException ex) {
                        throw new UncheckedWrapperException(ex);
                    }

                    final ConfigurationManager confManager = cdiUtil.findBean(
                        ConfigurationManager.class);

                    final Object config = confManager.findConfiguration(
                        confClass);

                    final List<String> stringList;
                    try {
                        final Field field = confClass.getDeclaredField(
                            selectedSetting.getSelectedKey(state));
                        field.setAccessible(true);
                        final Object value = field.get(config);

                        if (value == null) {
                            stringList = new ArrayList<>();
                            field.set(config, stringList);
                        } else {
                            stringList = (List<String>) value;
                        }
                    } catch (NoSuchFieldException |
                             SecurityException |
                             IllegalAccessException |
                             ClassCastException ex) {
                        LOGGER.warn(
                            "Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                        LOGGER.warn(ex);

                        throw new UncheckedWrapperException(ex);
                    }

                    final FormData data = e.getFormData();
                    final String valueData = data.getString(VALUE);

                    if (selectedValue.getSelectedKey(state) == null) {
                        stringList.add(valueData);
                    } else {
                        final int selected = Integer.parseInt(selectedValue
                            .getSelectedKey(state));
                        stringList.set(selected, valueData);
                    }

                    confManager.saveConfiguration(config);
                }

                selectedValue.clearSelection(state);
                valueField.setValue(state, null);
            });
        }

    }

}
