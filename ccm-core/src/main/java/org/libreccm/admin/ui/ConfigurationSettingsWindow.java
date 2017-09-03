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
package org.libreccm.admin.ui;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.SettingInfo;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.LocalizedString;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ConfigurationSettingsWindow extends Window {

    private static final long serialVersionUID = 2706661932737914254L;

    private final String COL_SETTING_LABEL = "setting_label";
    private final String COL_SETTING_EDITOR = "setting_editor";

    private final Class<?> configurationClass;
    private final ConfigurationInfo configurationInfo;
    private final ConfigurationTab configurationTab;
    private Object configuration;

    private final Button saveButton;

    public ConfigurationSettingsWindow(final Class<?> configurationClass,
                                       final ConfigurationInfo configurationInfo,
                                       final ConfigurationTab configurationTab) {

        super();

        this.configurationClass = configurationClass;
        this.configurationInfo = configurationInfo;
        this.configurationTab = configurationTab;

        final Locale locale = configurationTab
            .getGlobalizationHelper()
            .getNegotiatedLocale();

        final Grid<SettingInfo> settingsGrid = new Grid<>(
            new CallbackDataProvider<SettingInfo, String>(
                query -> fetchSettingInfos(configurationClass,
                                           configurationTab,
                                           query),
                query -> countSettings(configurationClass,
                                       configurationTab,
                                       query)));
        settingsGrid
            .addColumn(settingInfo -> settingInfo.getLabel(locale))
            .setId(COL_SETTING_LABEL)
            .setCaption("Setting");
        settingsGrid
            .addComponentColumn(settingInfo -> createSettingEditor(settingInfo))
            .setId(COL_SETTING_EDITOR)
            .setCaption("Edit");
        
        settingsGrid.setWidth("100%");

        super.setCaption(
            String.format("Edit setting of configuration \"%s\"",
                          configurationInfo
                              .getTitle(locale,
                                        configurationTab.getDefaultLocale())));

        saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            configurationTab
                .getConfigurationManager()
                .saveConfiguration(configuration);
            close();
        });
        saveButton.setEnabled(false);
        final Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> close());

        super.setContent(new VerticalLayout(
            new HorizontalLayout(saveButton, cancelButton),
            settingsGrid));
    }

    private Stream<SettingInfo> fetchSettingInfos(
        final Class<?> configurationClass,
        final ConfigurationTab configurationTab,
        final Query<SettingInfo, String> query) {

        final int fromIndex;
        final int toIndex;
        final int size = countSettings(configurationClass, 
                                       configurationTab,
                                       query);
        if (query.getOffset() > size - 1) {
            fromIndex = size -1;
        } else {
            fromIndex = query.getOffset();
        }
        
        if (query.getOffset() + query.getLimit() > size) {
            toIndex = size;
        } else {
            toIndex = query.getOffset() + query.getLimit();
        }
        
        
        return configurationTab
            .getSettingManager()
            .getAllSettings(configurationClass)
            .subList(fromIndex, toIndex)
            .stream()
            .map(settingName -> {
                return configurationTab
                    .getSettingManager()
                    .getSettingInfo(configurationClass, settingName);
            });
    }

    private int countSettings(final Class<?> configurationClass,
                              final ConfigurationTab configurationTab,
                              final Query<SettingInfo, String> query) {

        return configurationTab
            .getSettingManager()
            .getAllSettings(configurationClass)
            .size();
    }

    private Component createSettingEditor(final SettingInfo settingInfo) {

        final String type = settingInfo.getValueType();
        final String name = settingInfo.getName();

        if (Boolean.class.getName().equals(type)
                || Boolean.TYPE.getName().equals(type)) {

            final CheckBox checkBox = new CheckBox();
            checkBox.addValueChangeListener(event -> {
                updateSettingValue(name, event.getValue());
            });
            return checkBox;
        } else if (BigDecimal.class.getName().equals(type)) {

            final TextField textField = new TextField();
            textField.addValueChangeListener(event -> {
                final String value = event.getValue();
                if (value.matches("\\d*")) {
                    updateSettingValue(name, new BigDecimal(value));
                } else {
                    textField.setComponentError(new UserError(
                        "The value is not numeric"));
                }
            });
            return textField;
        } else if (Double.class.getName().equals(type)
                       || Double.TYPE.getName().equals(type)) {

            final TextField textField = new TextField();
            textField.addValueChangeListener(event -> {
                try {
                    final Double value = Double.parseDouble(event.getValue());
                    updateSettingValue(name, value);
                } catch (NumberFormatException ex) {
                    textField.setComponentError(new UserError(
                        "The value is not a double value."));
                }
            });
            return textField;
        } else if (Set.class.getName().equals(type)) {

            return new Label("Not supported yet.");

        } else if (LocalizedString.class.getName().equals(type)) {

            return new Label("Not supported yet.");

        } else if (Long.class.getName().equals(type)
                       || Long.TYPE.getName().equals(type)) {

            final TextField textField = new TextField();
            textField.addValueChangeListener(event -> {
                try {
                    final Long value = Long.parseLong(event.getValue());
                    updateSettingValue(name, value);
                } catch (NumberFormatException ex) {
                    textField.setComponentError(new UserError(
                        "The value is not a double value."));
                }
            });
            return textField;

        } else if (List.class.getName().equals(type)) {

            return new Label("Not supported yet.");

        } else if (String.class.getName().equals(type)) {

            final TextField textField = new TextField();
            textField.addValueChangeListener(event -> {
                updateSettingValue(name, event.getValue());
            });
            return textField;

        } else {
            return new Label("Unsupported value type");
        }
    }

    private void updateSettingValue(final String settingName,
                                    final Object value) {

        if (configuration == null) {
            //We can't do that in the constructor because this values are 
            //provided by CDI in other classes. Therefore they might be
            //not available when the constructors runs.
            configuration = configurationTab
                .getConfigurationManager()
                .findConfiguration(configurationClass);
            saveButton.setEnabled(true);
        }

        try {
            final Field field = configurationClass.getField(settingName);
            field.setAccessible(true);
            field.set(configuration, value);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new UnexpectedErrorException(ex);
        }

    }

}
