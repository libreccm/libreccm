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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Editor for {@link EnumSetting}s. The editor consists of the usual header (see
 * {@link SettingFormHeader}) which is used by all setting forms/editors, a
 * table which displays all current values together with links for editing and
 * deleting the values and a form for adding and editing values.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SettingEditorEnum extends BoxPanel {

    private static final Logger LOGGER = LogManager.getLogger(
        SettingEditorEnum.class);

    private static final int COL_VALUE = 0;
    private static final int COL_EDIT = 1;
    private static final int COL_DEL = 2;

    private final ConfigurationTab configurationTab;
    private final ParameterSingleSelectionModel<String> selectedConf;
    private final ParameterSingleSelectionModel<String> selectedSetting;
    private final ParameterSingleSelectionModel<String> selectedValue;

    public SettingEditorEnum(
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

            setIdAttr("enumValues");

            setEmptyView(new Label(new GlobalizedMessage(
                "ui.admin.configuration.setting.enum.no_values", ADMIN_BUNDLE)));

            final TableColumnModel columnModel = getColumnModel();
            columnModel.add(new TableColumn(
                COL_VALUE,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.enum.col_value",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_EDIT,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.enum.col_edit",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_DEL,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.enum.col_del",
                    ADMIN_BUNDLE))));

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
                            "ui.admin.configuration.setting.enum.del_confirm",
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

                    final Set<String> values;
                    try {
                        final Field field = confClass.getDeclaredField(
                            selectedSetting.getSelectedKey(state));
                        field.setAccessible(true);
                        values = (Set<String>) field.get(config);
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

                    switch (event.getColumn()) {
                        case COL_EDIT: {
                            selectedValue.setSelectedKey(state, event
                                                         .getRowKey());

                            break;
                        }
                        case COL_DEL: {
                            values.remove(event.getRowKey());

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
                @SuppressWarnings("unchecked")
                final Field field = confClass.getDeclaredField(selectedSetting
                    .getSelectedKey(state));
                field.setAccessible(true);
                final Set<String> valuesSet = (Set<String>) field.get(config);

                values = new ArrayList<>(valuesSet);

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
            return 3;
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
                case COL_EDIT:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.configuration.setting.enum.value.edit",
                        ADMIN_BUNDLE));
                case COL_DEL:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.configuration.setting.enum.value.del",
                        ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return values.get(index);
        }

    }

    private class ValueForm extends Form {

        private final String VALUE = "value";

        @SuppressWarnings("unchecked")
        public ValueForm() {

            super("settingEnumValueForm");

            final TextField valueField = new TextField(VALUE);
            valueField.setLabel(new GlobalizedMessage(
                "ui.admin.configuration.setting.enum.value.label",
                ADMIN_BUNDLE));
            add(valueField);

            final SaveCancelSection saveCancelSection = new SaveCancelSection();
            add(saveCancelSection);

            addInitListener(e -> {
                final PageState state = e.getPageState();

                if (selectedValue.getSelectedKey(state) != null) {

                    valueField.setValue(state, selectedValue.getSelectedKey(
                                        state));
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

                    final Set<String> enumSetting;
                    try {
                        final Field field = confClass.getDeclaredField(
                            selectedSetting.getSelectedKey(state));
                        field.setAccessible(true);
                        final Object value = field.get(config);

                        if (value == null) {
                            enumSetting = new HashSet<>();
                            confClass.getDeclaredField(selectedSetting
                                .getSelectedKey(state)).set(config,
                                                            enumSetting);
                        } else {
                            enumSetting = (Set<String>) value;
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
                        enumSetting.add(valueData);
                    } else {
                        final String selected = selectedValue.getSelectedKey(
                            state);
                        enumSetting.remove(selected);
                        enumSetting.add(valueData);
                    }
                    confManager.saveConfiguration(config);
                }

                selectedValue.clearSelection(state);
                valueField.setValue(state, null);
            });
        }

    }

}
