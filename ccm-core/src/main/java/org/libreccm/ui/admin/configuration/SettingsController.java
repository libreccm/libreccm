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
import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.GET;
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
    
    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
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
        
        final ConfigurationInfo confInfo = confManager.getConfigurationInfo(
            confClass
        );
        
        final LocalizedTextsUtil textUtil = globalizationHelper.getLocalizedTextsUtil(confInfo.getDescBundle());
        models.put(
            "confLabel", 
            textUtil.getText(confInfo.getTitleKey())
        );
        models.put(
            "configurationDesc", 
            textUtil.getText(confInfo.getDescKey())
        );
        
        final List<SettingsTableEntry> settings = confInfo
            .getSettings()
            .entrySet()
            .stream()
            .map(Map.Entry::getValue)
            .map(this::buildSettingsTableEntry)
            .sorted()
            .collect(Collectors.toList());
        
        models.put("settings", settings);
        
        return "org/libreccm/ui/admin/configuration/settings.xhtml";
    }
    
    private SettingsTableEntry buildSettingsTableEntry(
        final SettingInfo settingInfo
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
