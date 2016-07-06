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
package org.libreccm.modules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.configuration.AbstractSetting;
import org.libreccm.configuration.BigDecimalSetting;
import org.libreccm.configuration.BooleanSetting;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.DoubleSetting;
import org.libreccm.configuration.EnumSetting;
import org.libreccm.configuration.LocalizedStringSetting;
import org.libreccm.configuration.LongSetting;
import org.libreccm.configuration.Setting;
import org.libreccm.configuration.StringListSetting;
import org.libreccm.configuration.StringSetting;
import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.LocalizedString;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;

/**
 * A helper class used by the {@link ModuleManager} to load the initial
 * configuration values for the configurations of a module from the bundle and
 * store them in the database.
 *
 * If no entry for a setting is found in the integration properties the default
 * value defined in the configuration class is not changed. If value defined in
 * the bundle can not be converted to the correct type an warning is written
 * into the Log and the value is ignored.
 *
 * If the bundle does not provide integration properties, a warning is written
 * into the log.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ConfigurationLoader {

    private static final Logger LOGGER = LogManager.getLogger(
        ConfigurationLoader.class);

    private final EntityManager entityManager;
    private final Properties integration;

    public ConfigurationLoader(final EntityManager entityManager) {
        this.entityManager = entityManager;

        integration = new Properties();
        try (final InputStream inputStream = getClass().getResourceAsStream(
            CoreConstants.INTEGRATION_PROPS)) {

            if (inputStream == null) {
                LOGGER.warn("No integration properties found. Using empty "
                                + "properties.");
            } else {
                LOGGER.info("Loading integration properties.");
                integration.load(inputStream);
            }

        } catch (IOException ex) {
            LOGGER.warn(
                "Failed to load integration properties file. Using empty"
                    + "integration properties", ex);
        }
    }

    public void loadConfigurations(final CcmModule ccmModule) {
        final Module module = ccmModule.getClass().getAnnotation(Module.class);

        final Class<?>[] configurations = module.configurations();
        for (final Class<?> configuration : configurations) {
            loadConfiguration(configuration);
        }
    }

    /**
     * Loads the values for a specific configuration class from the integration
     * properties. The method will iterate over all settings defined in the
     * provided configuration class and try to find an entry for the setting in
     * the integration properties. If an entry in the integration properties is
     * found the method will try to convert the value to the type of the
     * setting. If this fails a warning is written to the log.
     *
     * @param confClass
     */
    public void loadConfiguration(final Class<?> confClass) {
        if (confClass.getAnnotation(Configuration.class) == null) {
            throw new IllegalArgumentException(String.format(
                "Provided class \"%s\" is not annotated with \"%s\".",
                confClass.getName(),
                Configuration.class.getName()));
        }

        final String confName = confClass.getName();

        final Field[] fields = confClass.getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(Setting.class) == null) {
                continue;
            }

            final String settingName = getSettingName(field);
            final String propertyName = String.join(".", confName, settingName);
            if (integration.containsKey(propertyName)) {
                createSetting(confName,
                              settingName,
                              field,
                              integration.getProperty(propertyName));
            }

        }
    }

    /**
     * Helper method for generating the name of a setting. This method does not
     * check if the provided field is annotated with {@link Setting}. The caller
     * is responsible to do that. Passing a field without the {@code Setting}
     * annotation to this method will result in a {@code NullPointerException}.
     *
     * @param field The setting field.
     *
     * @return The name of the field or if the {@link Setting} annotation of the
     *         field has a name value, the value of that field.
     */
    private String getSettingName(final Field field) {
        LOGGER.debug(String.format("Trying to get setting name from field: "
                                       + "\"%s\"",
                                   field.getName()));
        final Setting annotation = field.getAnnotation(Setting.class);

        if (annotation.name() == null || annotation.name().isEmpty()) {
            return field.getName();
        } else {
            return annotation.name();
        }
    }

    private void createSetting(final String confName,
                               final String settingName,
                               final Field field,
                               final String valueStr) {
        final String settingType = field.getType().getName();

        final AbstractSetting<?> setting;
        if (BigDecimal.class.getName().equals(settingType)) {
            try {
                final BigDecimal value = new BigDecimal(valueStr);
                final BigDecimalSetting bigDecimalSetting
                                            = new BigDecimalSetting();
                bigDecimalSetting.setValue(value);
                setting = bigDecimalSetting;
            } catch (NumberFormatException ex) {
                LOGGER.warn("Can't convert value \"{}\" for setting {}.{} from "
                                + "integration properties to BigDecimal.",
                            valueStr, confName, settingName);
                return;
            }
        } else if (Boolean.class.getName().equals(settingType)
                       || "boolean".equals(settingType)) {
            final Boolean value = Boolean.valueOf(valueStr);
            final BooleanSetting booleanSetting = new BooleanSetting();
            booleanSetting.setValue(value);
            setting = booleanSetting;
        } else if (Double.class.getName().equals(settingType)
                       || "double".equals(settingType)) {
            try {
                final Double value = Double.valueOf(valueStr);
                final DoubleSetting doubleSetting = new DoubleSetting();
                doubleSetting.setValue(value);
                setting = doubleSetting;
            } catch (NumberFormatException ex) {
                LOGGER.warn("Can't convert value \"{}\" for setting {}.{} from "
                                + "integration properties to Double.",
                            valueStr, confName, settingName);
                return;
            }
        } else if (Set.class.getName().equals(settingType)) {
            final String[] tokens = valueStr.split(",");
            final EnumSetting enumSetting = new EnumSetting();
            for (final String token : tokens) {
                enumSetting.addEnumValue(token.trim());
            }
            setting = enumSetting;
        } else if (LocalizedString.class.getName().equals(settingType)) {
            final String[] tokens = valueStr.split(",");
            final LocalizedStringSetting l10nSetting
                                             = new LocalizedStringSetting();
            for (final String token : tokens) {
                final String[] parts = token.split(":");
                if (parts.length != 2) {
                    LOGGER.warn("Error reading values for "
                                    + "LocalizedStringSetting {}. Token \"{}\" "
                                    + "does not have the correct format "
                                    + "\"locale:value\".",
                                confName, settingName, token);
                    continue;
                }
                l10nSetting.getValue().addValue(new Locale(parts[0]), parts[1]);
            }
            setting = l10nSetting;
        } else if (Long.class.getName().equals(settingType)
                       || "long".equals(settingType)) {
            try {
                final Long value = Long.parseLong(valueStr);
                final LongSetting longSetting = new LongSetting();
                longSetting.setValue(value);
                setting = longSetting;
            } catch (NumberFormatException ex) {
                LOGGER.warn("Can't convert value \"{}\" for setting {}.{} from "
                                + "integration properties to Long.",
                            valueStr, confName, settingName);
                return;
            }
        } else if (List.class.getName().equals(settingType)) {
            final String[] tokens = valueStr.split(",");
            final StringListSetting listSetting = new StringListSetting();
            for (final String token : tokens) {
                listSetting.addListValue(token.trim());
            }
            setting = listSetting;
        } else if (String.class.getName().equals(settingType)) {
            final StringSetting strSetting = new StringSetting();
            strSetting.setValue(valueStr);
            setting = strSetting;
        } else {
            throw new IllegalArgumentException(String.format(
                "No setting type for field type \"%s\" available.",
                settingType));
        }

        setting.setConfigurationClass(confName);
        setting.setName(settingName);
        entityManager.persist(setting);
    }

}
