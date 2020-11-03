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

import org.libreccm.configuration.AbstractSetting;
import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.SettingInfo;
import org.libreccm.configuration.SettingManager;
import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

    @Inject
    private SettingManager settingManager;

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
        } catch (ClassNotFoundException ex) {
            models.put("configurationClass", configurationClass);
            return "org/libreccm/ui/admin/configuration/configuration-class-not-found.xhtml";
        }

        final ConfigurationInfo confInfo = confManager.getConfigurationInfo(
            confClass
        );

        final LocalizedTextsUtil textUtil = globalizationHelper
            .getLocalizedTextsUtil(confInfo.getDescBundle());
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

    @POST
    @Path("/{settingName}")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String updateSettingValue(
        @PathParam("configurationClass") final String configurationClassName,
        @PathParam("settingName") final String settingName,
        @FormParam("settingValue") final String valueParam
    ) {
        final Class<?> confClass;
        try {
            confClass = Class.forName(configurationClassName);
        } catch (ClassNotFoundException ex) {
            models.put("configurationClass", configurationClassName);
            return "org/libreccm/ui/admin/configuration/configuration-class-not-found.xhtml";
        }
        final SettingInfo settingInfo = settingManager.getSettingInfo(
            confClass, settingName
        );

        final String valueType = settingInfo.getValueType();
        if (valueType.equals(BigDecimal.class.getName())) {
            return updateBigDecimalSetting(
                configurationClassName, settingName, valueType, valueParam
            );
        } else if (valueType.equals(Boolean.class.getName())
                       || valueType.equals("boolean")) {
            return updateBooleanSetting(
                configurationClassName, settingName, valueType, valueParam
            );
        } else if (valueType.equals(Double.class.getName())
                       || valueType.equals("double")) {
            return updateDoubleSetting(
                configurationClassName, settingName, valueType, valueParam
            );
        } else if (valueType.equals(LocalizedString.class.getName())) {
            return updateLocalizedStringSetting(
                configurationClassName, settingName, valueType, valueParam
            );
        } else if (valueType.equals(Long.class.getName())
                       || valueType.equals("long")) {
            return updateLongSetting(
                configurationClassName, settingName, valueType, valueParam
            );
        } else if (valueType.equals(List.class.getName())) {
            return updateStringListSetting(
                configurationClassName, settingName, valueType, valueParam
            );
        } else if (valueType.equals(Set.class.getName())) {
            return updateStringListSetting(
                configurationClassName, settingName, valueType, valueParam
            );
        } else if (valueType.equals(String.class.getName())) {
            return updateStringSetting(
                configurationClassName, settingName, valueType, valueParam
            );
        } else {
            models.put("configurationClass", configurationClassName);
            models.put("settingName", settingName);
            models.put("valueType", valueType);
            return "org/libreccm/ui/admin/configuration/unsupported-setting-type.xhtml";
        }
    }

    private String updateBigDecimalSetting(
        final String configurationClassName,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        final BigDecimal value;
        try {
            value = new BigDecimal(valueParam);
        } catch (NumberFormatException ex) {
            return buildInvalidTypeErrorTarget(
                configurationClassName,
                settingName,
                valueType,
                valueParam
            );
        }

        final AbstractSetting<BigDecimal> setting = settingManager
            .findSetting(
                configurationClassName, settingName, BigDecimal.class
            );

        if (setting == null) {
            return buildSettingNotFoundErrorTarget(
                configurationClassName,
                settingName,
                valueType
            );
        } else {
            setting.setValue(value);
            settingManager.saveSetting(setting);
            return buildRedirectAfterUpdateSettingTarget(
                configurationClassName
            );
        }
    }

    private String updateBooleanSetting(
        final String configurationClassName,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        final Boolean value = Boolean.valueOf(valueParam);

        final AbstractSetting<Boolean> setting = settingManager.findSetting(
            configurationClassName, settingName, Boolean.class
        );

        if (setting == null) {
            return buildSettingNotFoundErrorTarget(
                configurationClassName, settingName, valueType
            );
        } else {
            setting.setValue(value);
            settingManager.saveSetting(setting);
            return buildRedirectAfterUpdateSettingTarget(
                configurationClassName
            );
        }
    }

    private String updateDoubleSetting(
        final String configurationClassName,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        final Double value;
        try {
            value = Double.valueOf(valueParam);
        } catch (NumberFormatException ex) {
            return buildInvalidTypeErrorTarget(
                configurationClassName,
                settingName,
                valueType,
                valueParam
            );
        }

        final AbstractSetting<Double> setting = settingManager.findSetting(
            configurationClassName, settingName, Double.class
        );

        if (setting == null) {
            return buildSettingNotFoundErrorTarget(
                configurationClassName, settingName, valueType
            );
        } else {
            setting.setValue(value);
            settingManager.saveSetting(setting);
            return buildRedirectAfterUpdateSettingTarget(
                configurationClassName
            );
        }
    }

    private String updateLocalizedStringSetting(
        final String configurationClassName,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        final LocalizedString value = new LocalizedString();
        final String[] lines = valueParam.split("\n");
        for (final String line : lines) {
            final String[] tokens = line.split(":");
            if (tokens.length != 2) {
                continue;
            }
            final Locale locale = new Locale(tokens[0]);
            final String localeValue = tokens[1];
            value.addValue(locale, localeValue);
        }

        final AbstractSetting<LocalizedString> setting = settingManager
            .findSetting(
                configurationClassName, settingName, LocalizedString.class
            );

        if (setting == null) {
            return buildSettingNotFoundErrorTarget(
                configurationClassName, settingName, valueType
            );
        } else {
            setting.setValue(value);
            settingManager.saveSetting(setting);
            return buildRedirectAfterUpdateSettingTarget(
                configurationClassName
            );
        }
    }

    private String updateLongSetting(
        final String configurationClassName,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        final Long value;
        try {
            value = Long.valueOf(valueParam, 10);
        } catch (NumberFormatException ex) {
            return buildInvalidTypeErrorTarget(
                configurationClassName,
                settingName,
                valueType,
                valueParam
            );
        }

        final AbstractSetting<Long> setting = settingManager.findSetting(
            configurationClassName, settingName, Long.class
        );

        if (setting == null) {
            return buildSettingNotFoundErrorTarget(
                configurationClassName, settingName, valueType
            );
        } else {
            setting.setValue(value);
            settingManager.saveSetting(setting);
            return buildRedirectAfterUpdateSettingTarget(
                configurationClassName
            );
        }
    }

    private String updateStringListSetting(
        final String configurationClassName,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        final String[] tokens = valueParam.split(",");
        final List<String> value = Arrays.asList(tokens);

        final AbstractSetting<List> setting = settingManager.findSetting(
            configurationClassName, settingName, List.class
        );

        if (setting == null) {
            return buildSettingNotFoundErrorTarget(
                configurationClassName, settingName, valueType
            );
        } else {
            setting.setValue(value);
            settingManager.saveSetting(setting);
            return buildRedirectAfterUpdateSettingTarget(
                configurationClassName
            );
        }
    }

    private String updateStringSetting(
        final String configurationClassName,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        final AbstractSetting<String> setting = settingManager.findSetting(
            configurationClassName, settingName, String.class
        );

        if (setting == null) {
            return buildSettingNotFoundErrorTarget(
                configurationClassName, settingName, valueType
            );
        } else {
            setting.setValue(valueParam);
            settingManager.saveSetting(setting);
            return buildRedirectAfterUpdateSettingTarget(
                configurationClassName
            );
        }
    }

    private String buildInvalidTypeErrorTarget(
        final String configurationClassName,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        models.put("configurationClass", configurationClassName);
        models.put("settingName", settingName);
        models.put("valueType", valueType);
        models.put("valueParam", valueParam);
        return "org/libreccm/ui/admin/configuration/invalid-setting-value.xhtml";
    }

    private String buildSettingNotFoundErrorTarget(
        final String configurationClassName,
        final String settingName,
        final String valueType
    ) {
        models.put("configurationClass", configurationClassName);
        models.put("settingName", settingName);
        models.put("valueType", valueType);
        return "org/libreccm/ui/admin/configuration/setting-not-found.xhtml";
    }

    private String buildRedirectAfterUpdateSettingTarget(
        final String configurationClassName
    ) {
        return String.format(
            "redirect:configuration/%s", configurationClassName
        );
    }

}
