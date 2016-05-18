/*p
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
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TooManyListenersException;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Editor for {@link LocalizedStringSetting}s. The editor consists of the usual
 * header (see {@link SettingFormHeader}) which is used by all setting
 * forms/editors, a table which displays all current values together with links
 * for editing and deleting the values and a form for adding and editing values.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SettingEditorLocalizedString extends BoxPanel {

    private final static Logger LOGGER = LogManager.getLogger(
        SettingEditorLocalizedString.class);

    private static final int COL_LOCALE = 0;
    private static final int COL_VALUE = 1;
    private static final int COL_EDIT = 2;
    private static final int COL_DEL = 3;

    private final ConfigurationTab configurationTab;
    private final ParameterSingleSelectionModel<String> selectedConf;
    private final ParameterSingleSelectionModel<String> selectedSetting;
    private final ParameterSingleSelectionModel<String> selectedValue;

    public SettingEditorLocalizedString(
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

            setIdAttr("localizedStringSettingValues");

            setEmptyView(new Label(new GlobalizedMessage(
                "ui.admin.configuration.setting.localized_string.no_values",
                ADMIN_BUNDLE)));

            final TableColumnModel columnModel = getColumnModel();
            columnModel.add(new TableColumn(
                COL_LOCALE,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.localized_string.col_lang",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_VALUE,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.localized_string.col_value",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_EDIT,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.localized_string.col_edit",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_DEL,
                new Label(new GlobalizedMessage(
                    "ui.admin.configuration.setting.localized_string.col_del",
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
                            "ui.admin.configuration.setting.localized_string.del_confirm",
                            ADMIN_BUNDLE));
                        return link;
                    }
                }

            });

            addTableActionListener(new TableActionListener() {

                @Override
                public void cellSelected(final TableActionEvent event) {
                    final PageState state = event.getPageState();

                    switch (event.getColumn()) {
                        case COL_EDIT:
                            selectedValue.setSelectedKey(state,
                                                         event.getRowKey());
                            break;
                        case COL_DEL:
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

                            final LocalizedString localizedStr;
                            try {
                                final Field field = confClass.getDeclaredField(
                                    selectedSetting.getSelectedKey(state));
                                field.setAccessible(true);
                                final Object value = field.get(config);

                                if (value == null) {
                                    localizedStr = new LocalizedString();
                                    field.set(config, localizedStr);
                                } else {
                                    localizedStr = (LocalizedString) value;
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

                            final Locale locale = new Locale((String) event
                                .getRowKey());

                            localizedStr.removeValue(locale);

                            confManager.saveConfiguration(config);

                            break;
                    }
                }

                @Override
                public void headSelected(final TableActionEvent event) {
                    //Nothing
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

        private final LocalizedString value;
        private final List<Locale> locales;
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
                final Field field = confClass.getDeclaredField(selectedSetting
                    .getSelectedKey(state));
                field.setAccessible(true);
                value = (LocalizedString) field.get(config);

                locales = new ArrayList<>();
                locales.addAll(value.getAvailableLocales());
                locales.sort((s1, s2) -> {
                    return s1.toString().compareTo(s2.toString());
                });
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
            return 4;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < locales.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Locale locale = locales.get(index);

            switch (columnIndex) {
                case COL_LOCALE:
                    return locale.toString();
                case COL_VALUE:
                    return value.getValue(locale);
                case COL_EDIT:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.configuration.setting.localized_string.value.edit",
                        ADMIN_BUNDLE));
                case COL_DEL:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.configuration.setting.localized_string.title.del",
                        ADMIN_BUNDLE
                    ));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return locales.get(index);
        }

    }

    private class ValueForm extends Form {

        private static final String LOCALE_SELECT = "localeSelect";
        private static final String VALUE = "value";

        private final SingleSelect localeSelect;

        public ValueForm() {
            super("settingLocalizedStringValueForm");

            localeSelect = new SingleSelect(LOCALE_SELECT);
            localeSelect.setLabel(new GlobalizedMessage(
                "ui.admin.configuration.setting.localized_string.locale.label",
                ADMIN_BUNDLE));

            try {
                localeSelect.addPrintListener(e -> {
                    final PageState state = e.getPageState();

                    if (selectedValue.getSelectedKey(state) == null) {
                        final ConfigurationManager confManager = CdiUtil
                            .createCdiUtil()
                            .findBean(ConfigurationManager.class);

                        final Class<?> confClass;
                        try {
                            confClass = Class
                                .forName(selectedConf.getSelectedKey(state));
                        } catch (ClassNotFoundException ex) {
                            throw new UncheckedWrapperException(ex);
                        }

                        final Object config = confManager.findConfiguration(
                            confClass);

                        final LocalizedString value;
                        try {
                            final Field field = confClass.getDeclaredField(
                                selectedSetting.getSelectedKey(state));
                            field.setAccessible(true);
                            value = (LocalizedString) field.get(config);
                        } catch (NoSuchFieldException |
                                 SecurityException |
                                 IllegalAccessException |
                                 ClassCastException ex) {
                            LOGGER.warn(
                                "Failed to read setting {} from configuration {}",
                                selectedSetting.getSelectedKey(state),
                                selectedConf.getSelectedKey(state));
                            LOGGER.warn(ex);
                            throw new UncheckedWrapperException(
                                String.format(
                                    "Failed to read setting %s from configuration %s",
                                    selectedSetting.getSelectedKey(state),
                                    selectedConf.getSelectedKey(state)),
                                ex);
                        }

                        final Set<String> supportedLanguages = KernelConfig
                            .getConfig().getSupportedLanguages();
                        final Set<String> assignedLanguages = new HashSet<>();
                        value.getAvailableLocales().forEach(l -> {
                            assignedLanguages.add(l.toString());
                        });

                        final SingleSelect target = (SingleSelect) e.getTarget();

                        target.clearOptions();

                        supportedLanguages.forEach(l -> {
                            if (!assignedLanguages.contains(l)) {
                                target.addOption(new Option(l, new Text(l)));
                            }
                        });
                    } else {
                        final SingleSelect target = (SingleSelect) e.getTarget();

                        target.clearOptions();

                        final String language = selectedValue.getSelectedKey(
                            state);
                        target.addOption(
                            new Option(language, new Text(language)));
                    }
                });
            } catch (TooManyListenersException ex) {
                //We are in big trouble...
                throw new UncheckedWrapperException(ex);
            }

            add(localeSelect);

            final TextField localizedValue = new TextField(VALUE);
            localizedValue.setLabel(new GlobalizedMessage(
                "ui.admin.configuration.setting.localized_string.value.label",
                ADMIN_BUNDLE));
            add(localizedValue);

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
                        final Field field = confClass.getDeclaredField(
                            selectedSetting.getSelectedKey(state));
                        field.setAccessible(true);
                        final LocalizedString localizedStr
                                                  = (LocalizedString) field.get(
                                config);

                        final String value = localizedStr.getValue(new Locale(
                            selectedValue.getSelectedKey(state)));

                        localizedValue.setValue(state, value);

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

                    final LocalizedString localizedStr;
                    try {
                        final Field field = confClass.getDeclaredField(
                            selectedSetting.getSelectedKey(state));
                        field.setAccessible(true);
                        final Object value = field.get(config);

                        if (value == null) {
                            localizedStr = new LocalizedString();

                            field.set(config, localizedStr);
                        } else {
                            localizedStr = (LocalizedString) value;
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
                    final Locale locale = new Locale(data.getString(
                        LOCALE_SELECT));
                    final String valueData = data.getString(VALUE);
                    localizedStr.addValue(locale, valueData);

                    confManager.saveConfiguration(config);
                }

                selectedValue.clearSelection(state);
                localeSelect.setValue(state, null);
                localizedValue.setValue(state, null);
            });
        }

        @Override
        public boolean isVisible(final PageState state) {
            if (super.isVisible(state)) {

                if (selectedValue.getSelectedKey(state) == null) {
                    final ConfigurationManager confManager = CdiUtil
                        .createCdiUtil()
                        .findBean(ConfigurationManager.class);

                    final Class<?> confClass;
                    try {
                        confClass = Class
                            .forName(selectedConf.getSelectedKey(state));
                    } catch (ClassNotFoundException ex) {
                        throw new UncheckedWrapperException(ex);
                    }

                    final Object config = confManager.findConfiguration(
                        confClass);

                    final LocalizedString value;
                    try {
                        final Field field = confClass.getDeclaredField(
                            selectedSetting.getSelectedKey(state));
                        field.setAccessible(true);
                        value = (LocalizedString) field.get(config);
                    } catch (NoSuchFieldException |
                             SecurityException |
                             IllegalAccessException |
                             ClassCastException ex) {
                        LOGGER.warn(
                            "Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                        LOGGER.warn(ex);
                        throw new UncheckedWrapperException(
                            String.format(
                                "Failed to read setting %s from configuration %s",
                                selectedSetting.getSelectedKey(state),
                                selectedConf.getSelectedKey(state)),
                            ex);
                    }

                    final Set<String> supportedLanguages = KernelConfig
                        .getConfig().getSupportedLanguages();
                    final Set<String> assignedLanguages = new HashSet<>();
                    value.getAvailableLocales().forEach(l -> {
                        assignedLanguages.add(l.toString());
                    });

                    return !assignedLanguages.equals(supportedLanguages);
                } else {
                    return true;
                }

            } else {
                return false;
            }
        }

    }

}
