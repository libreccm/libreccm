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

import static org.libreccm.configuration.ConfigurationConstants.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.web.CcmApplication;

import java.lang.reflect.Field;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ApplicationConfigurationManager {

    private static final Logger LOGGER = LogManager.getLogger(
        ApplicationConfigurationManager.class
    );

    @Inject
    private ConfigurationManager confManager;
    
    @Inject
    private SettingManager settingManager;
    
    @Inject
    private SettingConverter settingConverter;
    
    @Inject
    private DomainRepository domainRepo;
    
    @Inject
    private CategoryRepository categoryRepo;
    
    @Inject
    private CategoryManager categoryManager;
    
    @Inject
    private EntityManager entityManager;

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

        return confManager.findConfiguration(confName, confClass);
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
                                confManager.getSettingName(field),
                                field.getType(),
                                field.get(configuration));
            } catch (IllegalAccessException ex) {
                LOGGER.error(String.format(
                    "Failed to write setting value for setting \"%s\" "
                        + "of configuration \"%s\"",
                    confManager.getSettingName(field),
                    configuration.getClass().getName()),
                             ex);
                throw new IllegalStateException(String.format(
                    "Failed to write setting value for setting \"%s\" "
                        + "of configuration \"%s\"",
                    confManager.getSettingName(field),
                    configuration.getClass().getName()),
                                                ex);
            }
        }
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
        AbstractSetting<T> setting = settingManager.findSetting(settingPath, valueType);
        if (setting == null) {
            setting = settingConverter.createSettingForValueType(valueType);
            setting.setName(settingName);
            final Domain registry = domainRepo
                .findByDomainKey(REGISTRY_DOMAIN);
            final Category category = categoryRepo
                .findByPath(registry, configuration.getClass().getName());
            categoryManager.addObjectToCategory(setting, category);
        }

        @SuppressWarnings("unchecked")
        final T settingValue = (T) value;
        setting.setValue(settingValue);

        entityManager.merge(setting);
    }

}
