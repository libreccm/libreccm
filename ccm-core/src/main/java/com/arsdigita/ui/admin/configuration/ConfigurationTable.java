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
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.SettingInfo;
import org.libreccm.configuration.SettingManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * This table lists all settings of a configuration class. The table shows the
 * current value and the description of each setting. If there is localised
 * label for the setting this label is used, otherwise the name of the setting
 * is used.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ConfigurationTable extends Table {

    private static final Logger LOGGER = LogManager.getLogger(
        ConfigurationTable.class);

    private static final int COL_SETTING_LABEL = 0;
    private static final int COL_SETTING_VALUE = 1;
    private static final int COL_SETTING_DESC = 2;
    private static final int COL_EDIT_SETTING = 3;

    private ParameterSingleSelectionModel<String> selectedConf;
    private ParameterSingleSelectionModel<String> selectedSetting;

    public ConfigurationTable(
        final ConfigurationTab configurationTab,
        final ParameterSingleSelectionModel<String> selectedConf,
        final ParameterSingleSelectionModel<String> selectedSetting) {

        super();

        setIdAttr("configurationTable");

        this.selectedConf = selectedConf;
        this.selectedSetting = selectedSetting;

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.configuration.settings.none", ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_SETTING_LABEL,
            new Label(new GlobalizedMessage(
                "ui.admin.configuration.settings.table.col_setting_label.header",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_SETTING_VALUE,
            new Label(new GlobalizedMessage(
                "ui.admin.configuration.settings.table.col_setting_value.header",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_SETTING_DESC,
            new Label(new GlobalizedMessage(
                "ui.admin.configuration.settings.table.col_setting_desc.header",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_EDIT_SETTING,
            new Label(new GlobalizedMessage(
                "ui.admin.configuration.settings.table.col_edit_setting.header",
                ADMIN_BUNDLE))));

        columnModel.get(COL_EDIT_SETTING).setCellRenderer(
            new TableCellRenderer() {

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

                if (event.getColumn() == COL_EDIT_SETTING) {
                    final String settingName = (String) event.getRowKey();
                    selectedSetting.setSelectedKey(state, settingName);

                    switch (getTypeOfSelectedSetting(state)) {
                        case "boolean":
                            LOGGER.debug("Setting is of type boolean");
                            configurationTab.showBooleanSettingForm(state);
                            break;
                        case "long":
                            LOGGER.debug("Setting is of type long");
                            configurationTab.showLongSettingForm(state);
                            break;
                        case "double":
                            LOGGER.debug("Setting is of type double");
                            configurationTab.showDoubleSettingForm(state);
                            break;
                        case "java.math.BigDecimal":
                            LOGGER.debug("Setting is of type BigDecimal");
                            configurationTab.showBigDecimalSettingForm(state);
                            break;
                        case "org.libreccm.l10n.LocalizedString":
                            LOGGER.debug("Setting is of type LocalizedString");
                            configurationTab.showLocalizedStringSettingForm(
                                state);
                            break;
                        case "java.lang.String":
                            LOGGER.debug("Setting is of type String");
                            configurationTab.showStringSettingForm(state);
                            break;
                        case "java.util.Set":
                            LOGGER.debug("Setting is of type Enum");
                            configurationTab.showEnumSettingForm(state);
                            break;
                        case "java.util.List":
                            LOGGER.debug("Setting is of type List");
                            configurationTab.showStringListSettingForm(state);
                            break;
                        default:
                            throw new IllegalArgumentException(String.format(
                                "Unknown setting type \"%s\".",
                                getTypeOfSelectedSetting(state)));
                    }

                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        setModelBuilder(new ConfigurationTableModelBuilder());
    }

    private String getTypeOfSelectedSetting(final PageState state) {
        final Class<?> confClass;
        try {
            confClass = Class.forName(selectedConf.getSelectedKey(state));
        } catch (ClassNotFoundException ex) {
            LOGGER.error("Configuration class '{}' not found.",
                         selectedConf.getSelectedKey(state));
            throw new UncheckedWrapperException(String.format(
                "Configuration class '%s not found'",
                selectedConf.getSelectedKey(state)), ex);
        }

        final SettingManager settingManager = CdiUtil.createCdiUtil().findBean(
            SettingManager.class);
        final SettingInfo info = settingManager.getSettingInfo(confClass,
                                                               selectedSetting.
                                                               getSelectedKey(
                                                                   state));

        return info.getValueType();

    }

    private class ConfigurationTableModelBuilder
        extends LockableImpl implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            final ConfigurationManager confManager = CdiUtil.createCdiUtil()
                .findBean(ConfigurationManager.class);
            final Class<?> confClass;
            try {
                confClass = Class.forName(selectedConf.getSelectedKey(state));
            } catch (ClassNotFoundException ex) {
                LOGGER.error("Configuration class '{}' not found.",
                             selectedConf.getSelectedKey(state));
                throw new UncheckedWrapperException(String.format(
                    "Configuration class '%s not found'",
                    selectedConf.getSelectedKey(state)), ex);
            }

            final Object configuration = confManager
                .findConfiguration(confClass);

            return new ConfigurationTableModel(configuration, state);
        }

    }

    private class ConfigurationTableModel implements TableModel {

        private final Object configuration;

        private final ConfigurationManager confManager;
        private final SettingManager settingManager;
        private final GlobalizationHelper globalizationHelper;

        private final List<String> settings;
        private int index = -1;

        public ConfigurationTableModel(final Object configuration,
                                       final PageState state) {
            this.configuration = configuration;

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            confManager = cdiUtil.findBean(ConfigurationManager.class);
            settingManager = cdiUtil.findBean(SettingManager.class);
            globalizationHelper = cdiUtil.findBean(GlobalizationHelper.class);

            settings = settingManager.getAllSettings(configuration.getClass());
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < settings.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final String setting = settings.get(index);
            final SettingInfo settingInfo = settingManager.getSettingInfo(
                configuration.getClass(), setting);

            switch (columnIndex) {
                case COL_SETTING_LABEL:
                    return settingInfo.getLabel(globalizationHelper
                        .getNegotiatedLocale());
                case COL_SETTING_VALUE: {
                    try {
                        final Field field = configuration.getClass().
                            getDeclaredField(setting);
                        field.setAccessible(true);
                        return buildValueColumn(settingInfo,
                                                field.get(configuration));
                    } catch (NoSuchFieldException |
                             SecurityException |
                             IllegalAccessException ex) {
                        LOGGER.error("Failed to read value from configuration.",
                                     ex);
                        return new Label(new GlobalizedMessage(
                            "ui.admin.configuration.settings.read_error",
                            ADMIN_BUNDLE));
                    }
                }
                case COL_SETTING_DESC:
                    return settingInfo.getDescription(globalizationHelper
                        .getNegotiatedLocale());
                case COL_EDIT_SETTING:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.configuration.settings.edit", ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException("Illegal column index");
            }
        }

        private String buildValueColumn(final SettingInfo settingInfo,
                                        final Object settingValue) {
            switch (settingInfo.getValueType()) {
                case "java.util.List": {
                    @SuppressWarnings("unchecked")
                    final List<String> value = (List<String>) settingValue;
                    return String.join(", ", value);
                }
                case "java.util.Set": {
                    @SuppressWarnings("unchecked")
                    final Set<String> value = (Set<String>) settingValue;
                    return String.join(", ", value);
                }
                case "org.libreccm.l10n.LocalizedString": {
                    final LocalizedString value = (LocalizedString) settingValue;
                    final List<String> entries = new ArrayList<>();
                    value.getValues().entrySet().forEach(e -> {
                        entries.add(String.format("%s: %s",
                                                  e.getKey(),
                                                  e.getValue()));
                    });

                    return String.join("; ", entries);
                }
                default: {
                    return Objects.toString(settingValue);
                }
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex
        ) {
            return settings.get(index);
        }

    }

}
