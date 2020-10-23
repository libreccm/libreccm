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
import org.libreccm.configuration.SettingInfo;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;

import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@RequestScoped
@Path("/configuration/{configurationClass}")
public class SettingsController {
    
    @Inject
    private ConfigurationManager confManager;
    
    @Inject
    private GlobalizationHelper globalizationHelper;
    
    @Inject
    private Models models;
    
    @Path("/")
    public String showSettings(
        @PathParam("configurationClass") final String configurationClass
    ) {
        final Class<?> confClass;
        try {
        confClass = Class.forName(configurationClass);
        } catch(ClassNotFoundException ex) {
            models.put("configurationClass", configurationClass);
            return "org/libreccm/ui/admin/configuration/configuration-class-not-found.xhtml";
        }
        
        final Object conf = confManager.findConfiguration(confClass);
        final ConfigurationInfo confInfo = confManager.getConfigurationInfo(
            confClass
        );
        
        confInfo
            .getSettings()
            .entrySet()
            .stream()
            .map(info -> buildSettingsTableEntry(info.getValue(), conf))
            .sorted()
            .collect(Collectors.toList());
        
        
        return "org/libreccm/ui/admin/configuration/settings.xhtml";
    }
    
    private SettingsTableEntry buildSettingsTableEntry(
        final SettingInfo settingInfo, final Object conf
    ) {
        Objects.requireNonNull(settingInfo);
        
        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(settingInfo.getDescBundle());
        
        final SettingsTableEntry entry = new SettingsTableEntry();
        entry.setName(settingInfo.getName());
        entry.setValueType(settingInfo.getValueType());
        entry.setDefaultValue(settingInfo.getDefaultValue());
        entry.setLabel(textsUtil.getText(settingInfo.getLabelKey()));
        entry.setDescription(textsUtil.getText(settingInfo.getDescKey()));
        
        return entry;
    }
    
}
