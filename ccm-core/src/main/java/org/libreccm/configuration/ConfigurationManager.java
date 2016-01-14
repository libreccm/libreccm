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

import org.apache.logging.log4j.message.FormattedMessage;

import java.util.Set;
import java.util.StringJoiner;

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
        if (annotation.descBundle() == null
                || annotation.descBundle().isEmpty()) {
            confInfo.setDescBundle(String.join("",
                                               configuration.getClass()
                                               .getName(),
                                               "Description"));
        } else {
            confInfo.setDescBundle(annotation.descBundle());
        }
        if (annotation.descKey() == null
                || annotation.descKey().isEmpty()) {
            confInfo.setDescKey("description");
        } else {
            confInfo.setDescKey(annotation.descKey());
        }

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
        final String descBundle;
        if (confAnnotation.descBundle() == null
                || confAnnotation.descBundle().isEmpty()) {
            descBundle = String.join("",
                                     configuration.getClass().getName(),
                                     "Description");
        } else {
            descBundle = confAnnotation.descBundle();
        }

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

        settingInfo.setConfClass(configuration.getName());

        settingInfo.setDescBundle(descBundle);
        if (settingAnnotation.descKey() == null
                || settingAnnotation.descKey().isEmpty()) {
            settingInfo.setDescKey(field.getName());
        } else {
            settingInfo.setDescKey(settingAnnotation.descKey());
        }

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
        LOGGER.debug(String.format(
            "Trying to find setting \"%s\" of type \"%s\"",
            name,
            clazz.getName()));
        final String[] tokens = name.split("\\.");
        LOGGER.debug(String.format("Setting name \"%s\" has %d tokens.",
                                   name,
                                   tokens.length));
        final String[] categoryTokens = Arrays.copyOfRange(tokens,
                                                           0,
                                                           tokens.length - 1);
        final String categoryPath = String.join(".", categoryTokens);
        LOGGER.debug(String.format("categoryPath for setting is \"%s\".",
                                   categoryPath));

        final Domain registry = domainRepository
            .findByDomainKey(REGISTRY_DOMAIN);
        final Category category = categoryRepository.findByPath(registry,
                                                                categoryPath);
        if (category == null) {
            LOGGER.warn(String.format(String.format(
                "Category \"%s\" for setting \"%s\" not found.",
                categoryPath,
                name)));
            return null;
        }

        LOGGER.debug(String.format("Category has %d objects. Filtering.",
                                   category.getObjects().size()));
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
                LOGGER.warn(String.format("Setting \"%s\" found but is not of "
                                              + "the requested type \"%s\".",
                                          name,
                                          clazz.getName()));
                return null;
            }
        } else {
            LOGGER.warn(String.format(
                "Setting \"%s\" was not found in category \"%s\".",
                name,
                categoryPath));
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
            return (AbstractSetting<T>) new StringListSetting();
        } else if (LocalizedString.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new LocalizedStringSetting();
        } else if (Long.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new LongSetting();
        } else if (Set.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new EnumSetting();
        } else if (String.class.getName().equals(valueTypeName)) {
            return (AbstractSetting<T>) new StringSetting();
        } else {
            throw new IllegalArgumentException(String.format(
                "No setting type for value type \"%s\".", valueTypeName));
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
        LOGGER.debug(new FormattedMessage(
            "Saving setting \"%s\" of type \"%s\"...",
            settingPath,
            valueType.getName()));
        AbstractSetting<T> setting = findSetting(settingPath, valueType);
        if (setting == null) {
            LOGGER.debug(String.format("Setting \"%s\" does not yet exist in "
                                           + "database. Creating new setting.",
                                       settingPath));
            setting = createSettingForValueType(valueType);
            setting.setName(settingName);
            setting.setDisplayName(settingName);
            final Category category = findCategoryForNewSetting(configuration);
            categoryManager.addObjectToCategory(setting, category);

//            final Domain registry = domainRepository
//                    .findByDomainKey(REGISTRY_DOMAIN);
//            Category category = categoryRepository
//                    .findByPath(registry, configuration.getClass().getName());
//            if (category == null) {
//                final String[] tokens = configuration.getClass().getName().
//                        split("\\.");
//                final StringBuilder categoryPath = new StringBuilder(
//                        configuration.getClass().getName().length());
//                for (String token : tokens) {
//                    if (categoryPath.length() > 0) {
//                        categoryPath.append('.');
//                    }
//                    categoryPath.append(token);
//                    category = createCategoryIfNotExists(categoryPath.toString());
//                }
//            }
//            categoryManager.addObjectToCategory(setting, category);
        }

        LOGGER.debug(String.format("New value of setting \"%s\" is: \"%s\"",
                                   settingPath,
                                   value.toString()));
        @SuppressWarnings("unchecked")
        final T settingValue = (T) value;
        setting.setValue(settingValue);
        LOGGER.debug(String.format("Value of setting \"%s\" is now: \"%s\"",
                                   settingPath,
                                   setting.getValue().toString()));

        LOGGER.debug("Saving changed setting to DB...");
        entityManager.merge(setting);
        entityManager.flush();
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
                    LOGGER.debug(String.
                        format("Setting \"%s\" found. Value: %s",
                               settingPath,
                               setting.getValue().toString()));
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

    private Category findCategoryForNewSetting(final Object configuration) {
        LOGGER.debug(new FormattedMessage(
            "#findCategoryForNewSetting: Looking for category for "
                + "configuration \"%s\"...",
            configuration.getClass().getName()));
        final String categoryPath = configuration.getClass().getName();
        final String[] tokens = categoryPath.split("\\.");
        final Domain registry = domainRepository
            .findByDomainKey(REGISTRY_DOMAIN);

        final Category[] categories = new Category[tokens.length];

        //Check which of the categories in the categoryPath exist already
        final boolean[] exists = new boolean[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            final String path = buildCategoryPath(tokens, i);
            LOGGER.debug(new FormattedMessage(
                "#findCategoryForNewSetting: Checking if category \"%s\" exists.",
                path));
            final Category category = categoryRepository.findByPath(registry,
                                                                    path);
            if (category == null) {
                LOGGER.debug(new FormattedMessage(
                    "#findCategoryForNewSetting: Category \"%s\" does not exist.",
                    path));
                exists[i] = false;
            } else {
                LOGGER.debug(new FormattedMessage(
                    "#findCategoryForNewSetting: Category \"%s\" exists.",
                    path));
                exists[i] = true;
                categories[i] = category;
            }
        }

        LOGGER.debug(
            "#findCategoryForNewSetting: Creating missing categories...");
        for (int i = 0; i < tokens.length; i++) {
            LOGGER.debug(new FormattedMessage(
                "#findCategoryForNewSetting: Checking for category \"%s\"...",
                tokens[i]));
            if (!exists[i]) {

                if (i == 0) {
                    LOGGER.debug(new FormattedMessage(
                        "#findCategoryForNewSetting: Category \"%s\" does not exist, "
                        + "creating as subcategory of the registry root category.",
                        tokens[i]));
                    categories[i] = createNewCategory(tokens[i],
                                                      registry.getRoot());
                } else {
                    LOGGER.debug(new FormattedMessage(
                        "#findCategoryForNewSetting: Category \"%s\" does not exist, "
                        + "creating as subcategory of \"%s\"",
                        tokens[i],
                        categories[i - 1].getName()));
                    categories[i] = createNewCategory(tokens[i],
                                                      categories[i - 1]);
                }
            }
        }

        LOGGER.debug(new FormattedMessage(
            "#findCategoryForNewSetting: Found/Created category \"%s\".",
            categoryPath));
        return categories[categories.length - 1];
    }

    private String buildCategoryPath(final String[] tokens,
                                     final int index) {
        final StringJoiner joiner = new StringJoiner(".");
        for (int i = 0; i <= index; i++) {
            joiner.add(tokens[i]);
        }

        return joiner.toString();
    }

    private Category createNewCategory(final String name,
                                       final Category parent) {
        final Category category = new Category();
        category.setName(name);
        category.setDisplayName(name);
        categoryRepository.save(category);
        entityManager.flush();
        categoryManager.addSubCategoryToCategory(category, parent);

        return category;
    }

}
