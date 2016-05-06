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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractSettingFormSingleValue<T> extends Form {

    private static final Logger LOGGER = LogManager.getLogger(
        AbstractSettingFormSingleValue.class);

    private static final String VALUE_FIELD = "valueField";

    public AbstractSettingFormSingleValue(
        final ConfigurationTab configurationTab,
        final ParameterSingleSelectionModel<String> selectedConf,
        final ParameterSingleSelectionModel<String> selectedSetting) {

        super("settingFormSingleValue", new BoxPanel(BoxPanel.VERTICAL));

        add(new SettingFormHeader(selectedConf, selectedSetting));

        add(new SettingFormCurrentValuePanel(selectedConf, selectedSetting));

        final TextField valueField = new TextField(VALUE_FIELD);
        valueField.setLabel(new GlobalizedMessage(
            "ui.admin.configuration.setting.edit.new_value", ADMIN_BUNDLE));
        add(valueField);

        final SaveCancelSection saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(new InitListener(selectedConf,
                                         selectedSetting,
                                         valueField));

        addValidationListener(new ValidationListener());

        addProcessListener(new ProcessListener(configurationTab,
                                               selectedConf,
                                               selectedSetting));

    }

    abstract T convertValue(final String valueData);

    private class InitListener implements FormInitListener {

        private final ParameterSingleSelectionModel<String> selectedConf;
        private final ParameterSingleSelectionModel<String> selectedSetting;
        private final TextField valueField;

        public InitListener(
            final ParameterSingleSelectionModel<String> selectedConf,
            final ParameterSingleSelectionModel<String> selectedSetting,
            final TextField valueField) {
            this.selectedConf = selectedConf;
            this.selectedSetting = selectedSetting;
            this.valueField = valueField;
        }

        @Override
        public void init(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
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

            final Object value;
            try {
                value = confClass.getField(selectedSetting
                    .getSelectedKey(state)).get(config);
            } catch (NoSuchFieldException | SecurityException |
                     IllegalAccessException | ClassCastException ex) {
                LOGGER.warn("Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                LOGGER.warn(ex);
                return;
            }

            valueField.setValue(state, value.toString());
        }

    }

    private class ValidationListener implements FormValidationListener {

        @Override
        public void validate(final FormSectionEvent event) throws
            FormProcessException {
            final FormData data = event.getFormData();

            final String valueData = data.getString(VALUE_FIELD);
            if (Strings.isBlank(valueData)) {
                data.addError(VALUE_FIELD,
                              new GlobalizedMessage(
                                  "ui.admin.configuration.setting.error.blank",
                                  ADMIN_BUNDLE));
            }
            try {
                final T value = convertValue(valueData);
                LOGGER.debug("New value {} is a valid BigDecimal.", value);
            } catch (NumberFormatException ex) {
                data.addError(VALUE_FIELD,
                              new GlobalizedMessage(
                                  "ui.admin.configuration.setting.error.not_a_bigdecimal",
                                  ADMIN_BUNDLE));
            }
        }

    }

    private class ProcessListener implements FormProcessListener {

        private final ConfigurationTab configurationTab;
        private final ParameterSingleSelectionModel<String> selectedConf;
        private final ParameterSingleSelectionModel<String> selectedSetting;

        public ProcessListener(
            final ConfigurationTab configurationTab,
            final ParameterSingleSelectionModel<String> selectedConf,
            final ParameterSingleSelectionModel<String> selectedSetting) {
            this.configurationTab = configurationTab;
            this.selectedConf = selectedConf;
            this.selectedSetting = selectedSetting;
        }

        @Override
        public void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final FormData data = event.getFormData();
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

            final String valueData = data.getString(VALUE_FIELD);

            final T value = convertValue(valueData);

            try {
                field.set(config, value);
                configurationTab.hideSettingForms(state);
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
        }

    }

}
