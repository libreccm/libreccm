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
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.SettingInfo;
import org.libreccm.configuration.SettingManager;
import org.libreccm.l10n.GlobalizationHelper;

import java.math.BigDecimal;
import java.util.Objects;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SettingFormBigDecimal extends Form {

    private static final Logger LOGGER = LogManager.getLogger(
        SettingFormBigDecimal.class);

    private static final String VALUE_FIELD = "valueField";

    public SettingFormBigDecimal(
        final ConfigurationTab configurationTab,
        final ParameterSingleSelectionModel<String> selectedConf,
        final ParameterSingleSelectionModel<String> selectedSetting) {

        super("settingFormBigDecimal", new BoxPanel(BoxPanel.VERTICAL));

        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);
            final SettingManager settingManager = cdiUtil.findBean(
                SettingManager.class);
            final GlobalizationHelper globalizationHelper = cdiUtil
                .findBean(GlobalizationHelper.class);

            final Class<?> confClass;
            try {
                confClass = Class
                    .forName(selectedConf.getSelectedKey(state));
            } catch (ClassNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }

            final ConfigurationInfo confInfo = confManager
                .getConfigurationInfo(confClass);
            final SettingInfo settingInfo = settingManager.getSettingInfo(
                confClass, selectedSetting.getSelectedKey(state));

            final String confTitle = confInfo.getTitle(globalizationHelper
                .getNegotiatedLocale());
            final String settingLabel = settingInfo.getLabel(
                globalizationHelper.getNegotiatedLocale());

            target.setLabel(new GlobalizedMessage(
                "ui.admin.configuration.setting.edit.heading",
                ADMIN_BUNDLE,
                new String[]{confTitle, settingLabel}));
        });

        heading.setClassAttr("heading");

        add(heading);

        final Text desc = new Text(e -> {
            final PageState state = e.getPageState();
            final Text target = (Text) e.getTarget();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final SettingManager settingManager = cdiUtil.findBean(
                SettingManager.class);
            final GlobalizationHelper globalizationHelper = cdiUtil
                .findBean(GlobalizationHelper.class);

            final Class<?> confClass;
            try {
                confClass = Class
                    .forName(selectedConf.getSelectedKey(state));
            } catch (ClassNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }

            final SettingInfo settingInfo = settingManager.getSettingInfo(
                confClass, selectedSetting.getSelectedKey(state));

            target.setText(settingInfo.getDescription(globalizationHelper
                .getNegotiatedLocale()));
        });

        add(desc);

        final GridPanel gridPanel = new GridPanel(2);

        gridPanel.add(new Label(new GlobalizedMessage(
            "ui.admin.configuration.setting.edit.current_value",
            ADMIN_BUNDLE)));

        gridPanel.add(new Text(e -> {
            final PageState state = e.getPageState();
            final Text target = (Text) e.getTarget();

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

            final BigDecimal value;
            try {
                value = (BigDecimal) confClass.getField(selectedSetting
                    .getSelectedKey(state)).get(config);
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException | ClassCastException ex) {
                LOGGER.warn("Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                LOGGER.warn(ex);
                target.setText("Failed to read setting value.");
                return;
            }

            target.setText(Objects.toString(value));
        }));

        add(gridPanel);

        final TextField valueField = new TextField(VALUE_FIELD);
        valueField.setLabel(new GlobalizedMessage(
            "ui.admin.configuration.setting.edit.new_value", ADMIN_BUNDLE));
        add(valueField);

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

            final BigDecimal value;
            try {
                value = (BigDecimal) confClass.getField(selectedSetting
                    .getSelectedKey(state)).get(config);
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException | ClassCastException ex) {
                LOGGER.warn("Failed to read setting {} from configuration {}",
                            selectedSetting.getSelectedKey(state),
                            selectedConf.getSelectedKey(state));
                LOGGER.warn(ex);
                return;
            }

            valueField.setValue(state, value);
        });

        addValidationListener(e -> {
            final PageState state = e.getPageState();
            final FormData data = e.getFormData();

            final String valueData = data.getString(VALUE_FIELD);

            if (Strings.isBlank(valueData)) {
                data.addError(VALUE_FIELD,
                              new GlobalizedMessage(
                                  "ui.admin.configuration.setting.error.blank",
                                  ADMIN_BUNDLE));
            }

            try {
                final BigDecimal value = new BigDecimal(valueData);
                LOGGER.debug("New value {} is a valid BigDecimal.", value);
            } catch (NumberFormatException ex) {
                data.addError(VALUE_FIELD,
                              new GlobalizedMessage(
                                  "ui.admin.configuration.setting.error.not_a_bigdecimal",
                                  ADMIN_BUNDLE));
            }
        });

        addProcessListener(e -> {
            throw new UnsupportedOperationException();
        });
    }
    
   

}
