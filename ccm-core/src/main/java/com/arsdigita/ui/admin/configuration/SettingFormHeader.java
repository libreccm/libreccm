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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.SettingInfo;
import org.libreccm.configuration.SettingManager;
import org.libreccm.l10n.GlobalizationHelper;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Header used by all setting forms/editors. The header contains a link to go
 * back to the list of settings and heading for contains the title of the
 * configuration class and the label of the setting which is edited.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SettingFormHeader extends BoxPanel {

    public SettingFormHeader(
        final ConfigurationTab configurationTab,
        final ParameterSingleSelectionModel<String> selectedConf,
        final ParameterSingleSelectionModel<String> selectedSetting) {

        super(BoxPanel.VERTICAL);

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.configuration.setting.edit.back", ADMIN_BUNDLE));
        backLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            configurationTab.hideSettingForms(state);
        });
        add(backLink);

        final Label heading = new Label(new HeadingPrintListener(
            selectedConf, selectedSetting));
        heading.setClassAttr("heading");

        add(heading);

        final Text desc = new Text(new DescPrintListener(selectedConf,
                                                         selectedSetting));

        add(desc);
    }

    private class HeadingPrintListener implements PrintListener {

        private final ParameterSingleSelectionModel<String> selectedConf;
        private final ParameterSingleSelectionModel<String> selectedSetting;

        public HeadingPrintListener(
            final ParameterSingleSelectionModel<String> selectedConf,
            final ParameterSingleSelectionModel<String> selectedSetting) {

            this.selectedConf = selectedConf;
            this.selectedSetting = selectedSetting;

        }

        @Override
        public void prepare(final PrintEvent event) {

            final PageState state = event.getPageState();
            final Label target = (Label) event.getTarget();

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
        }

    }

    private class DescPrintListener implements PrintListener {

        private final ParameterSingleSelectionModel<String> selectedConf;
        private final ParameterSingleSelectionModel<String> selectedSetting;

        public DescPrintListener(
            final ParameterSingleSelectionModel<String> selectedConf,
            final ParameterSingleSelectionModel<String> selectedSetting) {

            this.selectedConf = selectedConf;
            this.selectedSetting = selectedSetting;

        }

        @Override
        public void prepare(final PrintEvent event) {
            final PageState state = event.getPageState();
            final Text target = (Text) event.getTarget();

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
        }

    }

}
