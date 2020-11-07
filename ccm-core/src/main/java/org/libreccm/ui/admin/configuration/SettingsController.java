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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
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
import javax.transaction.Transactional;
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

    private static final Logger LOGGER = LogManager.getLogger(
        SettingsController.class
    );

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
    @Transactional(Transactional.TxType.REQUIRED)
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

        final Object configuration = confManager.findConfiguration(confClass);

        final List<SettingsTableEntry> settings = confInfo
            .getSettings()
            .entrySet()
            .stream()
            .map(Map.Entry::getValue)
            .map(settingInfo -> buildSettingsTableEntry(settingInfo,
                                                        configuration))
            .sorted()
            .collect(Collectors.toList());

        models.put("configurationClass", configurationClass);

        models.put("settings", settings);

        models.put("BigDecimalClassName", BigDecimal.class.getName());
        models.put("BooleanClassName", Boolean.class.getName());
        models.put("DoubleClassName", Double.class.getName());
        models.put("FloatClassName", Float.class.getName());
        models.put("IntegerClassName", Integer.class.getName());
        models.put("ListClassName", List.class.getName());
        models.put("LongClassName", Long.class.getName());
        models.put("LocalizedStringClassName", LocalizedString.class.getName());
        models.put("SetClassName", Set.class.getName());
        models.put("StringClassName", String.class.getName());
        
        models.put("IntegerMaxValue", Integer.toString(Integer.MAX_VALUE));
        models.put("IntegerMinValue", Integer.toString(Integer.MIN_VALUE));
        models.put("LongMaxValue", Long.toString(Long.MAX_VALUE));
        models.put("LongMinValue", Long.toString(Long.MIN_VALUE));
        models.put("DoubleMaxValue", Double.toString(Double.MAX_VALUE));
        models.put("DoubleMinValue", Double.toString(Double.MIN_VALUE));

        return "org/libreccm/ui/admin/configuration/settings.xhtml";
    }

    @Transactional(Transactional.TxType.REQUIRED)
    private SettingsTableEntry buildSettingsTableEntry(
        final SettingInfo settingInfo,
        final Object configuration
    ) {
        Objects.requireNonNull(settingInfo);
        Objects.requireNonNull(configuration);

        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(settingInfo.getDescBundle());

        String value;
        try {
            final Field field = configuration
                .getClass()
                .getDeclaredField(settingInfo.getName());
            field.setAccessible(true);
            final Object valueObj = field.get(configuration);
            if (valueObj instanceof List) {
                @SuppressWarnings("unchecked")
                final List<String> list = (List<String>) valueObj;
                value = list
                    .stream()
                    .collect(Collectors.joining("\n"));
            } else if (valueObj instanceof LocalizedString) {
                final LocalizedString localizedStr = (LocalizedString) valueObj;
                value = localizedStr
                    .getValues()
                    .entrySet()
                    .stream()
                    .map(
                        entry -> String.format(
                            "%s: %s",
                            entry.getKey().toString(), entry.getValue()
                        )
                    )
                    .sorted()
                    .collect(Collectors.joining("\n"));
            } else if (valueObj instanceof Set) {
                @SuppressWarnings("unchecked")
                final Set<String> set = (Set<String>) valueObj;
                value = set
                    .stream()
                    .collect(Collectors.joining("\n"));
            } else {
                value = Objects.toString(valueObj);
            }
        } catch (NoSuchFieldException | IllegalAccessException | SecurityException ex) {
            LOGGER.error(
                "Failed to get value for field {} of configuration {}.",
                settingInfo.getName(),
                configuration.getClass().getName()
            );
            LOGGER.error(ex);
            value = "?err?";
        }
        final SettingsTableEntry entry = new SettingsTableEntry();
        entry.setName(settingInfo.getName());
        entry.setValue(value);
        entry.setValueType(settingInfo.getValueType());
        entry.setDefaultValue(settingInfo.getDefaultValue());
        entry.setLabel(textsUtil.getText(settingInfo.getLabelKey()));
        entry.setDescription(textsUtil.getText(settingInfo.getDescKey()));

        return entry;
    }

    @POST
    @Path("/{settingName}")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public String updateSettingValue(
        @PathParam("configurationClass")
        final String configurationClassName,
        @PathParam("settingName")
        final String settingName,
        @FormParam("settingValue")
        final String valueParam
    ) {
        final Class<?> confClass;
        try {
            confClass = Class.forName(configurationClassName);
        } catch (ClassNotFoundException ex) {
            models.put("configurationClass", configurationClassName);
            return "org/libreccm/ui/admin/configuration/configuration-class-not-found.xhtml";
        }
        final Object conf = confManager.findConfiguration(confClass);
        final SettingInfo settingInfo = settingManager.getSettingInfo(
            confClass, settingName
        );

        final String valueType = settingInfo.getValueType();
        if (valueType.equals(BigDecimal.class.getName())) {
            return updateBigDecimalSetting(
                configurationClassName,
                confClass,
                conf,
                settingName,
                valueType,
                valueParam
            );
        } else if (valueType.equals(Boolean.class.getName())
                       || valueType.equals("boolean")) {
            final boolean value = valueParam != null;
            return updateBooleanSetting(
                configurationClassName,
                confClass,
                conf,
                settingName,
                valueType,
                value
            );
        } else if (valueType.equals(Double.class.getName())
                       || valueType.equals("double")) {
            return updateDoubleSetting(
                configurationClassName,
                confClass,
                conf,
                settingName,
                valueType,
                valueParam
            );
        } else if (valueType.equals(LocalizedString.class.getName())) {
            return updateLocalizedStringSetting(
                configurationClassName,
                confClass,
                conf,
                settingName,
                valueType,
                valueParam
            );
        } else if (valueType.equals(Long.class.getName())
                       || valueType.equals("long")) {
            return updateLongSetting(
                configurationClassName,
                confClass,
                conf,
                settingName,
                valueType,
                valueParam
            );
        } else if (valueType.equals(List.class.getName())) {
            return updateStringListSetting(
                configurationClassName,
                confClass,
                conf,
                settingName,
                valueType,
                valueParam
            );
        } else if (valueType.equals(Set.class.getName())) {
            return updateStringSetSetting(
                configurationClassName,
                confClass,
                conf,
                settingName,
                valueType,
                valueParam
            );
        } else if (valueType.equals(String.class.getName())) {
            return updateStringSetting(
                configurationClassName,
                confClass,
                conf,
                settingName,
                valueType,
                valueParam
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
        final Class<?> configurationClass,
        final Object configuration,
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
        return updateSetting(
            configurationClassName,
            configurationClass,
            configuration,
            settingName,
            valueType,
            value
        );
    }

    private String updateBooleanSetting(
        final String configurationClassName,
        final Class<?> configurationClass,
        final Object configuration,
        final String settingName,
        final String valueType,
        final boolean value
    ) {
        return updateSetting(
            configurationClassName,
            configurationClass,
            configuration,
            settingName,
            valueType,
            value
        );
    }

    private String updateDoubleSetting(
        final String configurationClassName,
        final Class<?> configurationClass,
        final Object configuration,
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
        return updateSetting(
            configurationClassName,
            configurationClass,
            configuration,
            settingName,
            valueType,
            value
        );
    }

    private String updateLocalizedStringSetting(
        final String configurationClassName,
        final Class<?> configurationClass,
        final Object configuration,
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
            final String localeValue = tokens[1].trim();
            value.addValue(locale, localeValue);
        }
        return updateSetting(
            configurationClassName,
            configurationClass,
            configuration,
            settingName,
            valueType,
            value
        );
    }

    private String updateLongSetting(
        final String configurationClassName,
        final Class<?> configurationClass,
        final Object configuration,
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
        return updateSetting(
            configurationClassName,
            configurationClass,
            configuration,
            settingName,
            valueType,
            value
        );
    }

    private String updateStringListSetting(
        final String configurationClassName,
        final Class<?> configurationClass,
        final Object configuration,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        final String[] tokens = valueParam.split("\n");
        final List<String> value = Arrays
            .asList(tokens)
            .stream()
            .map(String::trim)
            .collect(Collectors.toList());
        return updateSetting(
            configurationClassName,
            configurationClass,
            configuration,
            settingName,
            valueType,
            value
        );
    }
    
     private String updateStringSetSetting(
        final String configurationClassName,
        final Class<?> configurationClass,
        final Object configuration,
        final String settingName,
        final String valueType,
        final String valueParam
    ) {
        final String[] tokens = valueParam.split(",");
        final Set<String> value = new HashSet<>(Arrays.asList(tokens));
        return updateSetting(
            configurationClassName,
            configurationClass,
            configuration,
            settingName,
            valueType,
            value
        );
    }

    private String updateStringSetting(
        final String configurationClassName,
        final Class<?> configurationClass,
        final Object configuration,
        final String settingName,
        final String valueType,
        final String value
    ) {
        return updateSetting(
            configurationClassName,
            configurationClass,
            configuration,
            settingName,
            valueType,
            value
        );
    }

    private String updateSetting(
        final String configurationClassName,
        final Class<?> configurationClass,
        final Object configuration,
        final String settingName,
        final String valueType,
        final Object value
    ) {
        try {
            final Field field = configurationClass.getDeclaredField(
                settingName
            );
            field.setAccessible(true);
            field.set(configuration, value);
            confManager.saveConfiguration(configuration);
            return buildRedirectAfterUpdateSettingTarget(
                configurationClassName
            );
        } catch (NoSuchFieldException ex) {
            return buildSettingNotFoundErrorTarget(
                configurationClassName,
                settingName,
                valueType);
        } catch (SecurityException | IllegalAccessException ex) {
            LOGGER.error("Failed to update setting.", ex);
            models.put("configurationClass", configurationClassName);
            models.put("settingName", settingName);
            return "org/libreccm/ui/admin/configuration/failed-to-update-setting.xhtml";
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
