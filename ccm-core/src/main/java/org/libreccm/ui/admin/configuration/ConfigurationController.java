/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.configuration;

import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Controller for the UI for managing the configuration of CCM.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/configuration")
public class ConfigurationController {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    /**
     * Show all available configurations (groups of settings).
     *
     * @return The template to use.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String getSettings() {
        final List<ConfigurationTableEntry> configurationClasses = confManager
            .findAllConfigurations()
            .stream()
            .map(confManager::getConfigurationInfo)
            .map(this::buildTableEntry)
            .sorted()
            .collect(Collectors.toList());

        models.put("configurationClasses", configurationClasses);

        return "org/libreccm/ui/admin/configuration/configuration.xhtml";
    }

    /**
     * Helper method for converting a
     * {@link org.libreccm.configuration.ConfigurationInfo} instance into a
     * {@link org.libreccm.ui.admin.configuration.ConfigurationTableEntry}
     * instance.
     *
     * @param confInfo Configuration info to convert.
     *
     * @return A {@link ConfigurationTableEntry} for the configuration.
     */
    private ConfigurationTableEntry buildTableEntry(
        final ConfigurationInfo confInfo
    ) {
        Objects.requireNonNull(confInfo);
        final ConfigurationTableEntry entry = new ConfigurationTableEntry();
        entry.setName(confInfo.getName());
        final LocalizedTextsUtil util = globalizationHelper
            .getLocalizedTextsUtil(confInfo.getDescBundle());
        entry.setTitle(util.getText(confInfo.getTitleKey()));
        entry.setDescription(util.getText(confInfo.getDescKey()));

        return entry;
    }

}
