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

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ConfigurationTab extends LayoutPanel {

    private static final String CONF_CLASSES_FILTER = "confClassesFilter";

    private final StringParameter selectedConfParam;
    private final ParameterSingleSelectionModel<String> selectedConf;

    private final StringParameter selectedSettingParam;
    private final ParameterSingleSelectionModel<String> selectedSetting;

    private final Label confClassesFilterHeading;
    private final Form confClassesFilterForm;
    private final ConfigurationsTable configurationsTable;

    public ConfigurationTab() {
        super();

        setClassAttr("sidebarNavPanel");

        selectedConfParam = new StringParameter("selectedConfiguration");
        selectedConf = new ParameterSingleSelectionModel<>(selectedConfParam);

        selectedSettingParam = new StringParameter("selectedSetting");
        selectedSetting = new ParameterSingleSelectionModel<>(
            selectedSettingParam);

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

        setBody(body);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(confClassesFilterHeading, true);
        page.setVisibleDefault(confClassesFilterForm, true);
        page.setVisibleDefault(configurationsTable, true);
    }

    protected void showConfigurationsTable(final PageState state) {
        confClassesFilterHeading.setVisible(state, true);
        confClassesFilterForm.setVisible(state, true);
        configurationsTable.setVisible(state, true);
    }

    protected void hideConfigurationsTable(final PageState state) {
        confClassesFilterHeading.setVisible(state, false);
        confClassesFilterForm.setVisible(state, false);
        configurationsTable.setVisible(state, false);
    }

    protected void showConfiguration(final PageState state) {
        hideConfigurationsTable(state);

    }

    protected void hideConfiguration(final PageState state) {

        showConfigurationsTable(state);
    }

}
