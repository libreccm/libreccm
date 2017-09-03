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

import com.arsdigita.kernel.KernelConfig;

import com.vaadin.cdi.ViewScoped;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.SettingManager;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.Locale;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class ConfigurationsTabController {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ConfigurationsTableDataProvider configurationsTableDataProvider;

    @Inject
    private ConfigurationManager configurationManager;

    @Inject
    private SettingManager settingManager;

    protected GlobalizationHelper getGlobalizationHelper() {
        return globalizationHelper;
    }

    protected Locale getDefaultLocale() {
        return confManager
            .findConfiguration(KernelConfig.class)
            .getDefaultLocale();
    }

    protected ConfigurationsTableDataProvider getConfigurationsTableDataProvider() {
        return configurationsTableDataProvider;
    }

    protected ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    protected SettingManager getSettingManager() {
        return settingManager;
    }

}
