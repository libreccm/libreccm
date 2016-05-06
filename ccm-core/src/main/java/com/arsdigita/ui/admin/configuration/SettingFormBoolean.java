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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SettingFormBoolean extends Form {

    private static final String VALUE_FIELD_GROUP = "valueFieldGroup";
    private static final String VALUE_FIELD = "valueField";

    private static final Logger LOGGER = LogManager.getLogger(
        SettingFormBoolean.class);

    public SettingFormBoolean(
        final ConfigurationTab configurationTab,
        final ParameterSingleSelectionModel<String> selectedConf,
        final ParameterSingleSelectionModel<String> selectedSetting) {

        super("settingFormBoolean", new BoxPanel(BoxPanel.VERTICAL));

        add(new SettingFormHeader(selectedConf, selectedSetting));

        add(new SettingFormCurrentValuePanel(selectedConf, selectedSetting));

        final CheckboxGroup valueFieldGroup = new CheckboxGroup(
            VALUE_FIELD_GROUP);
        valueFieldGroup.addOption(
            new Option(VALUE_FIELD,
                       new Label(new GlobalizedMessage(
                           "ui.admin.configuration.setting.edit.new_value",
                           ADMIN_BUNDLE))));

        final SaveCancelSection saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(e -> {
            final PageState state = e.getPageState();
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

            final Boolean value;
            try {
                value = (Boolean) confClass.getField(selectedSetting
                    .getSelectedKey(state)).get(config);
            } catch (NoSuchFieldException | SecurityException |
                     IllegalAccessException | ClassCastException ex) {
                LOGGER.warn("Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                LOGGER.warn(ex);
                return;
            }

            if (value) {
                valueFieldGroup.setValue(state, VALUE_FIELD);
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();
            final FormData data = e.getFormData();
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
            final String settingName = selectedSetting.getSelectedKey(state);

            final Object config = confManager.findConfiguration(confClass);

            final Field field;
            try {
                field = confClass.getField(settingName);
            } catch (NoSuchFieldException | SecurityException ex) {
                throw new FormProcessException(
                    String.format(
                        "Failed to retrieve field \"%s\" "
                            + "from configuration class \"%s\".",
                        settingName,
                        confClass.getName()),
                    new GlobalizedMessage(
                        "ui.admin.configuration.setting.failed_to_set_value",
                        ADMIN_BUNDLE),
                    ex);
            }

            try {
                final String[] valueData = (String[]) data.
                    get(VALUE_FIELD_GROUP);
                if (valueData != null && valueData.length > 0) {
                    if (VALUE_FIELD.equals(valueData[0])) {
                        field.set(config, Boolean.TRUE);
                    } else {
                        field.set(config, Boolean.FALSE);
                    }
                    configurationTab.hideSettingForms(state);
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new FormProcessException(
                    String.format(
                        "Failed to change value of field \"%s\" "
                            + "of configuration class \"%s\".",
                        settingName,
                        confClass.getName()),
                    new GlobalizedMessage(
                        "ui.admin.configuration.setting.failed_to_set_value",
                        ADMIN_BUNDLE),
                    ex);
            }

        });

    }

}
