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
package org.libreccm.configuration;

import java.lang.reflect.Field;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.Strings;
import org.libreccm.modules.CcmModule;
import org.libreccm.modules.Module;

import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Maps between configuration classes and the settings stored in the database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ConfigurationManager {

    private static final Logger LOGGER = LogManager.getLogger(
        ConfigurationManager.class);

    @Inject
    private SettingManager settingManager;

    @Inject
    private SettingConverter settingConverter;

    /**
     * Finds all configuration classes listed by the installed modules.
     *
     * @return A sorted set containing all configuration classes.
     *
     * @see Module#configurations()
     */
    public SortedSet<Class<?>> findAllConfigurations() {
        final ServiceLoader<CcmModule> modules = ServiceLoader.load(
            CcmModule.class);

        final SortedSet<Class<?>> configurations = new TreeSet<>((c1, c2) -> {
            return c1.getName().compareTo(c2.getName());
        });

        for (CcmModule module : modules) {
            final Module annotation = module.getClass().getAnnotation(
                Module.class);

            if (annotation == null) {
                continue;
            }

            Arrays.stream(annotation.configurations()).forEach(c -> {
                configurations.add(c);
            });
        }

        return configurations;
    }

    /**
     * Load all settings of the provided configuration class.
     *
     * @param <T>       Type of the configuration class.
     * @param confClass The configuration class.
     *
     * @return An instance of the configuration class with all settings set to
     *         the values stored in the registry.
     */
    public <T> T findConfiguration(final Class<T> confClass) {
        if (confClass == null) {
            throw new IllegalArgumentException("confClass can't be null");
        }

        if (confClass.getAnnotation(Configuration.class) == null) {
            throw new IllegalArgumentException(String.format(
                "Provided class \"%s\" is not annotated with \"%s\".",
                confClass.getName(),
                Configuration.class.getName()));
        }

        final String confName = confClass.getName();

        return findConfiguration(confName, confClass);
    }

    /**
     * Saves a configuration by writing the values of all setting fields to the
     * registry.
     *
     * @param configuration The configuration to save. The class of the provided
     *                      object must be annotation with
     *                      {@link Configuration}.
     *
     * @throws IllegalArgumentException If the {@code configuration} parameter
     *                                  is {@code null} or if the class of the
     *                                  provided object is not annotation with
     *                                  {@link Configuration}.
     */
    public void saveConfiguration(final Object configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration can't be null");
        }

        if (configuration.getClass().getAnnotation(Configuration.class) == null) {
            throw new IllegalArgumentException(String.format(
                "The class \"%s\" of the provided object is not annotated "
                    + "with \"%s\".",
                configuration.getClass().getName(),
                Configuration.class.getName()));
        }

        LOGGER.debug(String.format("Saving configuration \"%s\"...",
                                   configuration.getClass().getName()));
        final Field[] fields = configuration.getClass().getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);

            if (field.getAnnotation(Setting.class) == null) {
                LOGGER.debug(String.format(
                    "Field \"%s\" of class \"%s\" is not "
                        + "a setting. Ignoring it.",
                    configuration.getClass().getName(),
                    field.getName()));
                continue;
            }

            try {
                setSettingValue(configuration,
                                getSettingName(field),
                                field.getType(),
                                field.get(configuration));
            } catch (IllegalAccessException ex) {
                LOGGER.error(String.format(
                    "Failed to write setting value for setting \"%s\" "
                        + "of configuration \"%s\"",
                    getSettingName(field),
                    configuration.getClass().getName()),
                             ex);
                throw new IllegalStateException(String.format(
                    "Failed to write setting value for setting \"%s\" "
                        + "of configuration \"%s\"",
                    getSettingName(field),
                    configuration.getClass().getName()),
                                                ex);
            }
        }
    }

    /**
     * Get the {@link ConfigurationInfo} for a configuration.
     *
     * @param configuration The configuration for which the info is generated.
     *
     * @return a {@link ConfigurationInfo} instance describing the provided
     *         configuration.
     */
    public ConfigurationInfo getConfigurationInfo(
        final Class<?> configuration) {

        if (configuration == null) {
            throw new IllegalArgumentException("Configuration can't be null");
        }

        if (configuration.getAnnotation(Configuration.class) == null) {
            throw new IllegalArgumentException(String.format(
                "The class \"%s\" of the provided object is not annotated "
                    + "with \"%s\".",
                configuration.getClass().getName(),
                Configuration.class.getName()));
        }

        final Configuration annotation = configuration.getAnnotation(
            Configuration.class);

        final ConfigurationInfo confInfo = new ConfigurationInfo();
        confInfo.setName(configuration.getName());
        if (annotation.descBundle() == null
                || annotation.descBundle().isEmpty()) {
            confInfo.setDescBundle(
                String.join("",
                            configuration.getName(),
                            "Description"));
        } else {
            confInfo.setDescBundle(annotation.descBundle());
        }
        if (Strings.isBlank(annotation.titleKey())) {
            confInfo.setTitleKey("title");
        } else {
            confInfo.setTitleKey(annotation.titleKey());
        }
        if (Strings.isBlank(annotation.descKey())) {
            confInfo.setDescKey("description");
        } else {
            confInfo.setDescKey(annotation.descKey());
        }

        final Field[] fields = configuration.getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(Setting.class) != null) {
                confInfo.addSetting(settingManager.getSettingInfo(
                    configuration, field.getName()));
            }
        }

        return confInfo;
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
    String getSettingName(final Field field) {
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

    /**
     * Create a setting instance of a specific value type.
     *
     * @param <T>       Type variable.
     * @param valueType The type of the value of the setting to create.
     *
     * @return An setting instance of the provided value type.
     *
     * @throws IllegalArgumentException If there is not setting type for the
     *                                  provided value type.
     */
//    @SuppressWarnings("unchecked")
//    <T> AbstractSetting<T> createSettingForValueType(
//        final Class<T> valueType) {
//
//        final String valueTypeName = valueType.getName();
//        if (BigDecimal.class.getName().equals(valueTypeName)) {
//            return (AbstractSetting<T>) new BigDecimalSetting();
//        } else if (Boolean.class.getName().equals(valueTypeName)) {
//            return (AbstractSetting<T>) new BooleanSetting();
//        } else if (Double.class.getName().equals(valueTypeName)) {
//            return (AbstractSetting<T>) new DoubleSetting();
//        } else if (List.class.getName().equals(valueTypeName)) {
//            return (AbstractSetting<T>) new StringListSetting();
//        } else if (LocalizedString.class.getName().equals(valueTypeName)) {
//            return (AbstractSetting<T>) new LocalizedStringSetting();
//        } else if (Long.class.getName().equals(valueTypeName)) {
//            return (AbstractSetting<T>) new LongSetting();
//        } else if (Set.class.getName().equals(valueTypeName)) {
//            return (AbstractSetting<T>) new EnumSetting();
//        } else if (String.class.getName().equals(valueTypeName)) {
//            return (AbstractSetting<T>) new StringSetting();
//        } else {
//            throw new IllegalArgumentException(String.format(
//                "No setting type for value type \"%s\".", valueTypeName));
//        }
//    }
    /**
     * Sets a value on a setting in the registry.
     *
     * @param <T>           The value type of the setting.
     * @param configuration The configuration to which the settings belongs.
     * @param settingName   The name of the setting.
     * @param valueType     The type of the value of the setting.
     * @param value         The value to set.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    private <T> void setSettingValue(final Object configuration,
                                     final String settingName,
                                     final Class<T> valueType,
                                     final Object value) {
        final String confClassName = configuration.getClass().getName();

        AbstractSetting<T> setting = settingManager.findSetting(confClassName,
                                                                settingName,
                                                                valueType);
        if (setting == null) {
            LOGGER.debug(String.format(
                "Setting \"%s#%s\" does not yet exist in "
                    + "database. Creating new setting.",
                confClassName,
                settingName));
            setting = settingConverter.createSettingForValueType(valueType);
        }
        setting.setConfigurationClass(confClassName);
        setting.setName(settingName);

        LOGGER.debug(String.format(
            "New value of setting \"%s#%s\" is: \"%s\"",
            confClassName,
            settingName,
            value.toString()));
        @SuppressWarnings("unchecked")
        final T settingValue = (T) value;
        setting.setValue(settingValue);
        LOGGER.debug(String.format(
            "Value of setting \"%s#%s\" is now: \"%s\"",
            confClassName,
            settingName,
            setting.getValue().toString()
        ));
        LOGGER.debug("Saving changed setting to DB...");
        settingManager.saveSetting(setting);
    }

    /**
     * Helper method for loading a configuration from the database.
     *
     * @param <T>       The type of the configuration.
     * @param confClass The configuration class.
     *
     * @return An instance of the configuration class with all setting fields
     *         set to the values stored in the registry.
     */
    <T> T findConfiguration(final String confName, final Class<T> confClass) {
        final T conf;

        try {
            conf = confClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            LOGGER.warn(String.format(
                "Failed to instantiate configuration \"%s\".",
                confClass.getName()),
                        ex);
            return null;
        }

        final Field[] fields = confClass.getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(Setting.class) == null) {
                continue;
            }

            final String settingName = getSettingName(field);

            final Class<?> settingType = field.getType();
            final AbstractSetting<?> setting = settingManager.findSetting(
                confName, settingName, settingType);
            if (setting != null) {
                try {
                    LOGGER.debug("Setting \"{}#{}\" found. Value: {}",
                                 confName,
                                 settingName,
                                 setting.getValue().toString());
                    field.set(conf, setting.getValue());
                } catch (IllegalAccessException ex) {
                    LOGGER.warn(
                        "Failed to set value of configuration class \"{}\". "
                            + "Ignoring.",
                        confClass.getName(),
                        ex);
                }
            }
        }

        return conf;
    }

}
