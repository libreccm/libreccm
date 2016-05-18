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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Tab for the admin application containing the UI to manage the settings in the
 * various configuration classes.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ConfigurationTab extends LayoutPanel {

    private static final String CONF_CLASSES_FILTER = "confClassesFilter";

    /**
     * Parameter for the selected configuration.
     */
    private final StringParameter selectedConfParam;
    private final ParameterSingleSelectionModel<String> selectedConf;

    /**
     * Parameter for the selected setting.
     */
    private final StringParameter selectedSettingParam;
    private final ParameterSingleSelectionModel<String> selectedSetting;

    /**
     * Parameter for the selected value of multi-value settings.
     */
    private final StringParameter selectedValueParam;
    private final ParameterSingleSelectionModel<String> selectedValue;

    /**
     * Heading of the table of configurations.
     */
    private final Label confClassesFilterHeading;
    /**
     * Form for filtering the table of configurations.
     */
    private final Form confClassesFilterForm;
    /**
     * The table which lists all available configurations.
     */
    private final ConfigurationsTable configurationsTable;

    /**
     * Link to go back to the listing of configurations.
     */
    private final ActionLink configurationBackLink;
    /**
     * Heading for the list of settings of a configuration.
     */
    private final Label configurationHeading;
    /**
     * The table which lists a settings of configuration.
     */
    private final ConfigurationTable configurationTable;

    /**
     * The form for editing a setting of the type {@code boolean}.
     */
    private final SettingFormBoolean settingFormBoolean;
    /**
     * The form for editing a setting of the type {@code long}.
     */
    private final SettingFormLong settingFormLong;
    /**
     * The form for editing a setting of the type {@code double}.
     */
    private final SettingFormDouble settingFormDouble;
    /**
     * The form for editing a setting of the type {@code BigDecimal}.
     */
    private final SettingFormBigDecimal settingFormBigDecimal;
    /**
     * The form for editing a setting of the type {@code String}.
     */
    private final SettingFormString settingFormString;
    /**
     * The form for editing a setting of the type {@code LocalizedString}.
     */
    private final SettingEditorLocalizedString settingEditorLocalizedString;
    /**
     * The form for editing a setting of the type {@code List<String>}.
     */
    private final SettingEditorStringList settingEditorStringList;
    /**
     * The form for editing a setting of the type {@code Set<String>}.
     */
    private final SettingEditorEnum settingEditorEnum;

    /**
     * Initialises the parameters, widgets and forms.
     */
    public ConfigurationTab() {
        super();

        setClassAttr("sidebarNavPanel");

        selectedConfParam = new StringParameter("selectedConfiguration");
        selectedConf = new ParameterSingleSelectionModel<>(selectedConfParam);

        selectedSettingParam = new StringParameter("selectedSetting");
        selectedSetting = new ParameterSingleSelectionModel<>(
            selectedSettingParam);

        selectedValueParam = new StringParameter("selectedValue");
        selectedValue = new ParameterSingleSelectionModel<>(selectedValueParam);

        final SegmentedPanel left = new SegmentedPanel();

        confClassesFilterHeading = new Label(new GlobalizedMessage(
            "ui.admin.configuration.classes.filter.heading", ADMIN_BUNDLE));

        confClassesFilterForm = new Form("confClassesForm");
        final TextField confClassesFilter = new TextField(CONF_CLASSES_FILTER);
        confClassesFilterForm.add(confClassesFilter);
        confClassesFilterForm.add(new Submit(new GlobalizedMessage(
            "ui.admin.configuration.classes.filter.submit", ADMIN_BUNDLE)));
        final ActionLink clearLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.configuration.classes.filter.clear", ADMIN_BUNDLE));
        clearLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            confClassesFilter.setValue(state, null);
        });
        confClassesFilterForm.add(clearLink);
        left.addSegment(confClassesFilterHeading, confClassesFilterForm);

        setLeft(left);

        final BoxPanel body = new BoxPanel(BoxPanel.VERTICAL);
        configurationsTable = new ConfigurationsTable(
            this, selectedConf, confClassesFilter);
        body.add(configurationsTable);

        configurationBackLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.configuration.back_to_configurations",
            ADMIN_BUNDLE));
        configurationBackLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            hideConfiguration(state);
        });
        body.add(configurationBackLink);
        configurationHeading = new Label(e -> {
            //This print listener includes the (localised title) of the selected 
            //configuration in the heading
            final PageState state = e.getPageState();

            final Class<?> confClass;
            try {
                confClass = Class.forName(selectedConf.getSelectedKey(state));
            } catch (ClassNotFoundException ex) {
                throw new UncheckedWrapperException(
                    String.format("Configuration class \"%s\" not found.",
                                  selectedConf.getSelectedKey(state)),
                    ex);
            }
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);
            final ConfigurationInfo confInfo = confManager.getConfigurationInfo(
                confClass);
            final GlobalizationHelper globalizationHelper = cdiUtil.findBean(
                GlobalizationHelper.class);
            final Label target = (Label) e.getTarget();
            final String confTitle = confInfo.getTitle(globalizationHelper
                .getNegotiatedLocale());
            target.setLabel(new GlobalizedMessage(
                "ui.admin.configuration.editing_configuration",
                ADMIN_BUNDLE,
                new String[]{confTitle}));
        });
        configurationHeading.setClassAttr("heading");
        body.add(configurationHeading);
        configurationTable = new ConfigurationTable(this,
                                                    selectedConf,
                                                    selectedSetting);
        body.add(configurationTable);

        settingFormBoolean = new SettingFormBoolean(this,
                                                    selectedConf,
                                                    selectedSetting);
        body.add(settingFormBoolean);

        settingFormLong = new SettingFormLong(this,
                                              selectedConf,
                                              selectedSetting);
        body.add(settingFormLong);

        settingFormDouble = new SettingFormDouble(this,
                                                  selectedConf,
                                                  selectedSetting);
        body.add(settingFormDouble);

        settingFormBigDecimal = new SettingFormBigDecimal(this,
                                                          selectedConf,
                                                          selectedSetting);
        body.add(settingFormBigDecimal);

        settingFormString = new SettingFormString(this,
                                                  selectedConf,
                                                  selectedSetting);
        body.add(settingFormString);

        settingEditorLocalizedString = new SettingEditorLocalizedString(
            this, selectedConf, selectedSetting, selectedValue);
        body.add(settingEditorLocalizedString);

        settingEditorStringList = new SettingEditorStringList(
            this, selectedConf, selectedSetting, selectedValue);
        body.add(settingEditorStringList);

        settingEditorEnum = new SettingEditorEnum(
            this, selectedConf, selectedSetting, selectedValue);
        body.add(settingEditorEnum);

        setBody(body);
    }

    /**
     * Registers all forms and widgets in the page for visibility management and
     * registers all parameters. The method is called by Bebop when the tab is
     * rendered.
     *
     * @param page The page which this tab is part of.
     */
    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(selectedConfParam);
        page.addGlobalStateParam(selectedSettingParam);
        page.addGlobalStateParam(selectedValueParam);

        page.setVisibleDefault(confClassesFilterHeading, true);
        page.setVisibleDefault(confClassesFilterForm, true);
        page.setVisibleDefault(configurationsTable, true);

        page.setVisibleDefault(configurationBackLink, false);
        page.setVisibleDefault(configurationHeading, false);
        page.setVisibleDefault(configurationTable, false);

        page.setVisibleDefault(settingFormBoolean, false);
        page.setVisibleDefault(settingFormLong, false);
        page.setVisibleDefault(settingFormDouble, false);
        page.setVisibleDefault(settingFormBigDecimal, false);
        page.setVisibleDefault(settingFormString, false);
        page.setVisibleDefault(settingEditorLocalizedString, false);
        page.setVisibleDefault(settingEditorStringList, false);
        page.setVisibleDefault(settingEditorEnum, false);
    }

    /**
     * Shows the {@link #configurationsTable} and hides all other widgets and
     * forms.
     *
     * @param state The current {@link PageState}.
     */
    protected void showConfigurationsTable(final PageState state) {
        confClassesFilterHeading.setVisible(state, true);
        confClassesFilterForm.setVisible(state, true);
        configurationsTable.setVisible(state, true);

        configurationTable.setVisible(state, false);

        settingFormBoolean.setVisible(state, false);
        settingFormLong.setVisible(state, false);
        settingFormDouble.setVisible(state, false);
        settingFormBigDecimal.setVisible(state, false);
        settingFormString.setVisible(state, false);
        settingEditorLocalizedString.setVisible(state, false);
        settingEditorStringList.setVisible(state, false);
        settingEditorEnum.setVisible(state, false);
    }

    /**
     * Hides the {@link #configurationsTable} and its supporting widgets.
     *
     * @param state The current {@link PageState}.
     */
    protected void hideConfigurationsTable(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);
    }

    /**
     * Shows the {@link #configurationTable} which lists all settings of a
     * configuration.
     *
     * @param state The current {@link PageState}.
     */
    protected void showConfiguration(final PageState state) {
        hideConfigurationsTable(state);
        hideSettingForms(state);

        configurationBackLink.setVisible(state, true);
        configurationHeading.setVisible(state, true);
        configurationTable.setVisible(state, true);
    }

    /**
     * Hides the configuration table and resets the {@link #selectedConf}
     * parameter.
     *
     * @param state The current {@link PageState}.
     */
    protected void hideConfiguration(final PageState state) {
        configurationBackLink.setVisible(state, false);
        configurationHeading.setVisible(state, false);
        configurationTable.setVisible(state, false);

        selectedConf.clearSelection(state);

        showConfigurationsTable(state);
    }

    /**
     * Shows the {@link #settingFormBigDecimal}.
     *
     * @param state The current {@link PageState}.
     */
    protected void showBigDecimalSettingForm(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);

        configurationBackLink.setVisible(state, false);
        configurationHeading.setVisible(state, false);
        configurationTable.setVisible(state, false);

        settingFormBoolean.setVisible(state, false);
        settingFormLong.setVisible(state, false);
        settingFormDouble.setVisible(state, false);
        settingFormBigDecimal.setVisible(state, true);
        settingFormString.setVisible(state, false);
        settingEditorLocalizedString.setVisible(state, false);
        settingEditorStringList.setVisible(state, false);
        settingEditorEnum.setVisible(state, false);
    }

    /**
     * Shows the {@link #settingFormBoolean}.
     *
     * @param state The current {@link PageState}.
     */
    protected void showBooleanSettingForm(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);

        configurationBackLink.setVisible(state, false);
        configurationHeading.setVisible(state, false);
        configurationTable.setVisible(state, false);

        settingFormBoolean.setVisible(state, true);
        settingFormLong.setVisible(state, false);
        settingFormDouble.setVisible(state, false);
        settingFormBigDecimal.setVisible(state, false);
        settingFormString.setVisible(state, false);
        settingEditorLocalizedString.setVisible(state, false);
        settingEditorStringList.setVisible(state, false);
        settingEditorEnum.setVisible(state, false);
    }

    /**
     * Shows the {@link #settingFormDouble}.
     *
     * @param state The current {@link PageState}.
     */
    protected void showDoubleSettingForm(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);

        configurationBackLink.setVisible(state, false);
        configurationHeading.setVisible(state, false);
        configurationTable.setVisible(state, false);

        settingFormBoolean.setVisible(state, false);
        settingFormLong.setVisible(state, false);
        settingFormDouble.setVisible(state, true);
        settingFormBigDecimal.setVisible(state, false);
        settingFormString.setVisible(state, false);
        settingEditorLocalizedString.setVisible(state, false);
        settingEditorStringList.setVisible(state, false);
        settingEditorEnum.setVisible(state, false);
    }

    /**
     * Shows the {@link #settingEditorEnum}.
     *
     * @param state The current {@link PageState}.
     */
    protected void showEnumSettingForm(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);

        configurationBackLink.setVisible(state, false);
        configurationHeading.setVisible(state, false);
        configurationTable.setVisible(state, false);

        settingFormBoolean.setVisible(state, false);
        settingFormLong.setVisible(state, false);
        settingFormDouble.setVisible(state, false);
        settingFormBigDecimal.setVisible(state, false);
        settingFormString.setVisible(state, false);
        settingEditorLocalizedString.setVisible(state, false);
        settingEditorStringList.setVisible(state, false);
        settingEditorEnum.setVisible(state, true);
    }

    /**
     * Show the {@link #settingEditorLocalizedString}.
     *
     * @param state The current {@link PageState}.
     */
    protected void showLocalizedStringSettingForm(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);

        configurationBackLink.setVisible(state, false);
        configurationHeading.setVisible(state, false);
        configurationTable.setVisible(state, false);

        settingFormBoolean.setVisible(state, false);
        settingFormLong.setVisible(state, false);
        settingFormDouble.setVisible(state, false);
        settingFormBigDecimal.setVisible(state, false);
        settingFormString.setVisible(state, false);
        settingEditorLocalizedString.setVisible(state, true);
        settingEditorStringList.setVisible(state, false);
        settingEditorEnum.setVisible(state, false);
    }

    /**
     * Shows the {@link #settingFormLong}.
     *
     * @param state The current {@link PageState}.
     */
    protected void showLongSettingForm(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);

        configurationBackLink.setVisible(state, false);
        configurationHeading.setVisible(state, false);
        configurationTable.setVisible(state, false);

        settingFormBoolean.setVisible(state, false);
        settingFormLong.setVisible(state, true);
        settingFormDouble.setVisible(state, false);
        settingFormBigDecimal.setVisible(state, false);
        settingFormString.setVisible(state, false);
        settingEditorLocalizedString.setVisible(state, false);
        settingEditorStringList.setVisible(state, false);
        settingEditorEnum.setVisible(state, false);
    }

    /**
     * Shows the {@link #settingEditorStringList}.
     *
     * @param state The current {@link PageState}.
     */
    protected void showStringListSettingForm(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);

        configurationBackLink.setVisible(state, false);
        configurationHeading.setVisible(state, false);
        configurationTable.setVisible(state, false);

        settingFormBoolean.setVisible(state, false);
        settingFormLong.setVisible(state, false);
        settingFormDouble.setVisible(state, false);
        settingFormBigDecimal.setVisible(state, false);
        settingFormString.setVisible(state, false);
        settingEditorLocalizedString.setVisible(state, false);
        settingEditorStringList.setVisible(state, true);
        settingEditorEnum.setVisible(state, false);
    }

    /**
     * Shows the {@link #settingFormString}.
     *
     * @param state The current {@link PageState}.
     */
    protected void showStringSettingForm(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);

        configurationBackLink.setVisible(state, false);
        configurationHeading.setVisible(state, false);
        configurationTable.setVisible(state, false);

        settingFormBoolean.setVisible(state, false);
        settingFormLong.setVisible(state, false);
        settingFormDouble.setVisible(state, false);
        settingFormBigDecimal.setVisible(state, false);
        settingFormString.setVisible(state, true);
        settingEditorLocalizedString.setVisible(state, false);
        settingEditorStringList.setVisible(state, false);
        settingEditorEnum.setVisible(state, false);
    }

    /**
     * Hides all settings forms/editors and resets the {@link #selectedSetting}
     * and {@link #selectedValue} parameters.
     *
     * @param state The current {@link PageState}.
     */
    protected void hideSettingForms(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);

        configurationBackLink.setVisible(state, true);
        configurationHeading.setVisible(state, true);
        configurationTable.setVisible(state, true);

        settingFormBoolean.setVisible(state, false);
        settingFormLong.setVisible(state, false);
        settingFormDouble.setVisible(state, false);
        settingFormBigDecimal.setVisible(state, false);
        settingFormString.setVisible(state, false);
        settingEditorLocalizedString.setVisible(state, false);
        settingEditorStringList.setVisible(state, false);
        settingEditorEnum.setVisible(state, false);

        selectedSetting.clearSelection(state);
        selectedValue.clearSelection(state);
    }

}
