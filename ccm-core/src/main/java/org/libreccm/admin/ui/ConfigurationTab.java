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

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.SettingManager;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ConfigurationTab extends CustomComponent {

    private static final long serialVersionUID = 7642627611731762410L;

    private static final String COL_CONF_TITLE = "title";
    private static final String COL_CONF_CLASS = "configuration_class";
    private static final String COL_CONF_DESC = "description";

    private ConfigurationsTabController controller;

    private final Grid<ConfigurationsGridRowData> configurationsGrid;

    public ConfigurationTab() {

        super();
        configurationsGrid = new Grid<>();

        configurationsGrid.addComponentColumn(rowData -> {
            final Button button = new Button(
                rowData.getTitle(),
                event -> {
                    final ConfigurationSettingsWindow window
                                                          = new ConfigurationSettingsWindow(
                        rowData.getConfigurationClass(),
                        rowData.getConfigurationInfo(),
                        this);
                    window.setWidth("70%");
                    window.center();
                    UI.getCurrent().addWindow(window);
                });
            button.setStyleName(ValoTheme.BUTTON_LINK);

            return button;
        })
            .setId(COL_CONF_TITLE);

        configurationsGrid.addColumn(
            ConfigurationsGridRowData::getName)
            .setId(COL_CONF_CLASS)
            .setCaption("Configuration class");

        configurationsGrid.addColumn(
            ConfigurationsGridRowData::getDescription)
            .setCaption(COL_CONF_DESC)
            .setCaption("Description");

        configurationsGrid.setWidth("100%");
        
        super.setCompositionRoot(configurationsGrid);

    }

    protected void init(final ConfigurationsTabController controller) {
        
        this.controller = controller;
        
        configurationsGrid
            .setDataProvider(controller.getConfigurationsTableDataProvider());
    }

    protected GlobalizationHelper getGlobalizationHelper() {
        return controller.getGlobalizationHelper();
    }
    
    protected Locale getDefaultLocale() {
        return controller.getDefaultLocale();
    }

    protected ConfigurationManager getConfigurationManager() {
        return controller.getConfigurationManager();
    }

    protected SettingManager getSettingManager() {
        return controller.getSettingManager();
    }

}
