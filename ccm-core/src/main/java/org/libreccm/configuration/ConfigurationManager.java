/*
 * Copyright (C) 2015 LibreCCM Foundation.
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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;

import static org.libreccm.configuration.ConfigurationConstants.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.web.CcmApplication;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Maps between configuration classes and the values stored in the registry.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ConfigurationManager {

    private static final Logger LOGGER = LogManager.getLogger(
        ConfigurationManager.class);

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private DomainRepository domainRepository;

    @Inject
    private EntityManager entityManager;

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

        final Field[] fields = configuration.getClass().getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
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
     * Finds an application instance specific configuration and loads it values
     * from the registry.
     *
     * @param <T>       The type of the configuration.
     * @param confClass The configuration class.
     * @param instance  The application instance for which the settings are
     *                  loaded.
     *
     * @return The configuration for the provided application instance.
     */
    public <T extends ApplicationConfiguration> T findConfiguration(
        final Class<T> confClass, final CcmApplication instance) {
        if (confClass == null) {
            throw new IllegalArgumentException("confClass can't be null");
        }

        if (instance == null) {
            throw new IllegalArgumentException("instance can't be null");
        }

        if (confClass.getAnnotation(Configuration.class) == null) {
            throw new IllegalArgumentException(String.format(
                "Provided class \"%s\" is not annotated with \"%s\".",
                confClass.getName(),
                Configuration.class.getName()));
        }

        final String confName = String.format("%s.%s",
                                              confClass.getName(),
                                              instance.getPrimaryUrl());

        return findConfiguration(confName, confClass);
    }

    /**
     * Saves a application instance configuration.
     *
     * @param configuration The configuration to save.
     * @param instance      The application instance of which the configuration
     *                      stores the settings.
     */
    public void saveConfiguration(final ApplicationConfiguration configuration,
                                  final CcmApplication instance) {
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

        if (instance == null) {
            throw new IllegalArgumentException("Instance can't be null");
        }

        final Field[] fields = configuration.getClass().getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            try {
                setSettingValue(configuration,
                                instance,
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
    public ConfigurationInfo getConfigurationInfo(final Class<?> configuration) {
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
        confInfo.setName(configuration.getClass().getName());
        confInfo.setDescBundle(annotation.descBundle());
        confInfo.setDescKey(annotation.descKey());

        final Field[] fields = configuration.getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(Setting.class) != null) {
                confInfo.addSetting(getSettingInfo(configuration,
                                                   field.getName()));
            }
        }

        return confInfo;
    }

    /**
     * Create a {@link SettingInfo} instance for a setting.
     *
     * @param configuration The configuration class to which the settings
     *                      belongs.
     * @param name          The name of the setting for which the
     *                      {@link SettingInfo} is generated.
     *
     * @return The {@link SettingInfo} for the provided configuration class.
     */
    public SettingInfo getSettingInfo(final Class<?> configuration,
                                      final String name) {
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

        final Configuration confAnnotation = configuration.getAnnotation(
            Configuration.class);
        final String descBundle = confAnnotation.descBundle();

        final Field field;
        try {
            field = configuration.getDeclaredField(name);
        } catch (SecurityException | NoSuchFieldException ex) {
            LOGGER.warn(String.format(
                "Failed to generate SettingInfo for field \"%s\" of "
                    + "configuration \"%s\". Ignoring field.",
                configuration.getClass().getName(),
                name),
                        ex);
            return null;
        }

        if (field.getAnnotation(Setting.class) == null) {
            return null;
        }

        final Setting settingAnnotation = field.getAnnotation(Setting.class);
        final SettingInfo settingInfo = new SettingInfo();
        if (settingAnnotation.name() == null
                || settingAnnotation.name().isEmpty()) {
            settingInfo.setName(field.getName());
        } else {
            settingInfo.setName(settingAnnotation.name());
        }

        settingInfo.setValueType(field.getType().getName());

        try {
            final Object conf = configuration.newInstance();
            settingInfo.setDefaultValue(field.get(conf).toString());
        } catch (InstantiationException | IllegalAccessException ex) {
            LOGGER.warn(String.format("Failed to create instance of \"%s\" to "
                                          + "get default values.",
                                      configuration.getName()),
                        ex);
        }

        settingInfo.setConfigurationClass(configuration.getName());

        settingInfo.setDescBundle(descBundle);
        settingInfo.setDescKey(settingAnnotation.descKey());

        return settingInfo;
    }

    /**
     * A low level method for finding a setting in the registry.
     *
     * @param <T>   Type of the value of the setting
     * @param name  The fully qualified name of the setting.
     * @param clazz The class of the setting.
     *
     * @return The requested setting if it exists in the registry, {@code null}
     *         otherwise.
     */
    public <T> AbstractSetting<T> findSetting(final String name,
                                              final Class<T> clazz) {
        final String[] tokens = name.split(".");
        final String[] categoryTokens = Arrays.copyOfRange(tokens,
                                                           0,
                                                           tokens.length - 1);
        final String categoryPath = String.join(".", categoryTokens);

        final Domain registry = domainRepository
            .findByDomainKey(REGISTRY_DOMAIN);
        final Category category = categoryRepository.findByPath(registry,
                                                                categoryPath);
        if (category == null) {
            return null;
        }

        final Optional<Categorization> result = category
            .getObjects()
            .stream()
            .filter((Categorization c)
                -> c.getCategorizedObject() instanceof AbstractSetting)
            .filter((Categorization c)
                -> ((AbstractSetting<?>) c.getCategorizedObject())
                .getName()
                .equals(tokens[tokens.length - 1]))
            .findFirst();

        if (result.isPresent()) {
            final CcmObject object = result.get().getCategorizedObject();
            final AbstractSetting<?> entry = (AbstractSetting<?>) object;

            if (clazz.isInstance(entry.getValue())) {
                @SuppressWarnings("unchecked")
                final AbstractSetting<T> resultEntry
                                             = (AbstractSetting<T>) entry;
                return resultEntry;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Low level method of saving a setting.
     * 
     * @param setting The setting to save.
     */
    public void saveSetting(final AbstractSetting<?> setting) {
        if (setting.getObjectId() == 0) {
            entityManager.persist(setting);
        } else {
            entityManager.merge(setting);
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
    @SuppressWarnings("unchecked")
    private <T> AbstractSetting<T> createSettingForValueType(
        final Class<T> valueType) {

        final String valueTypeName = valueType.getName();
        if (BigDecimal.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new BigDecimalSetting();
        } else if (Boolean.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new BooleanSetting();
        } else if (Double.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new DoubleSetting();
        } else if (List.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new EnumSetting();
        } else if (LocalizedString.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new LocalizedStringSetting();
        } else if (LongSetting.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new LongSetting();
        } else if (String.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new StringSetting();
        } else {
            throw new IllegalArgumentException(String.format(
                "No setting type for value type \"s\".", valueTypeName));
        }
    }

    /**
     * Sets a value on a setting in the registry.
     *
     * @param <T>           The value type of the setting.
     * @param configuration The configuration to which the settings belongs.
     * @param settingName   The name of the setting.
     * @param valueType     The type of the value of the setting.
     * @param value         The value to set.
     */
    private <T> void setSettingValue(final Object configuration,
                                     final String settingName,
                                     final Class<T> valueType,
                                     final Object value) {
        final String settingPath = String.format(
            "%s.%s",
            configuration.getClass().getName(),
            settingName);
        AbstractSetting<T> setting = findSetting(settingPath, valueType);
        if (setting == null) {
            setting = createSettingForValueType(valueType);
            setting.setName(settingName);
            final Domain registry = domainRepository
                .findByDomainKey(REGISTRY_DOMAIN);
            final Category category = categoryRepository
                .findByPath(registry, configuration.getClass().getName());
            categoryManager.addObjectToCategory(setting, category);
        }

        @SuppressWarnings("unchecked")
        final T settingValue = (T) value;
        setting.setValue(settingValue);

        entityManager.merge(setting);
    }

    /**
     * Sets the value of a setting of application instance configuration.
     *
     * @param <T>           The value type of the setting.
     * @param configuration The configuration to which the settings belongs.
     * @param instance      The application instance to which
     * @param settingName   The name of the setting.
     * @param valueType     The type of the value of the setting.
     * @param value         The value to set.
     */
    private <T> void setSettingValue(final Object configuration,
                                     final CcmApplication instance,
                                     final String settingName,
                                     final Class<T> valueType,
                                     final Object value) {
        final String settingPath = String.format(
            "%s.%s.%s",
            configuration.getClass().getName(),
            instance.getPrimaryUrl(),
            settingName);
        AbstractSetting<T> setting = findSetting(settingPath, valueType);
        if (setting == null) {
            setting = createSettingForValueType(valueType);
            setting.setName(settingName);
            final Domain registry = domainRepository
                .findByDomainKey(REGISTRY_DOMAIN);
            final Category category = categoryRepository
                .findByPath(registry, configuration.getClass().getName());
            categoryManager.addObjectToCategory(setting, category);
        }

        @SuppressWarnings("unchecked")
        final T settingValue = (T) value;
        setting.setValue(settingValue);

        entityManager.merge(setting);
    }

    /**
     * Helper method for loading a configuration from the registry.
     *
     * @param <T>       The type of the configuration.
     * @param confName  The fully qualified name of the configuration in the
     *                  registry. For normal configuration this is the fully
     *                  qualified name of the configuration class. For
     *                  application instance configurations this is the fully
     *                  qualified name of the configuration class joined with
     *                  the primary URL of the application instance, separated
     *                  with a dot.
     * @param confClass The configuration class.
     *
     * @return An instance of the configuration class with all setting fields
     *         set to the values stored in the registry.
     */
    private <T> T findConfiguration(final String confName,
                                    final Class<T> confClass) {
        final Domain registry = domainRepository
            .findByDomainKey(REGISTRY_DOMAIN);
        final Category category = categoryRepository.findByPath(registry,
                                                                confName);

        if (category == null) {
            return null;
        }

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

            final String settingPath = String.format("%s.%s",
                                                     confClass.getName(),
                                                     getSettingName(field));
            final Class<?> settingType = field.getType();
            final AbstractSetting<?> setting = findSetting(settingPath,
                                                           settingType);
            if (setting != null) {
                try {
                    field.set(conf, setting.getValue());
                } catch (IllegalAccessException ex) {
                    LOGGER.warn(String.format(
                        "Failed to set value of configuration class \"%s\". "
                            + "Ignoring.",
                        confClass.getName()),
                                ex);
                }
            }
        }

        return conf;
    }

}
