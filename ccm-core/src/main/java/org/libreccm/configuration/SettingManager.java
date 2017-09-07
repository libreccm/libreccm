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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * Manages settings in the database. Normally there should be no need to use
 * this class directly because the {@link ConfigurationManager} provides the
 * same public methods for accessing settings than this class. The purpose of
 * this class is only to separate the logic for managing settings from the logic
 * for managing configuration classes and to reduce the complexity of the
 * {@link ConfigurationManager} class
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class SettingManager {

    private static final Logger LOGGER = LogManager.getLogger(
        SettingManager.class);

    @Inject
    private EntityManager entityManager;

    /**
     * Get the names of all settings of a configuration class.
     *
     * @param configuration The configuration class for which the settings are
     *                      retrieved.
     *
     * @return A list with the names of all settings provided by the
     *         configuration class.
     */
    public List<String> getAllSettings(final Class<?> configuration) {
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

        final List<String> settings = new ArrayList<>();
        final Field[] fields = configuration.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Setting.class) != null) {
                settings.add(field.getName());
            }
        }

        settings.sort((s1, s2) -> {
            return s1.compareTo(s2);
        });

        return settings;
    }

    public List<AbstractSetting> retrieveAllSettings(final String confName) {
        Objects.requireNonNull(confName);

        final TypedQuery<AbstractSetting> query = entityManager
            .createNamedQuery("AbstractSetting.findAllForClass",
                              AbstractSetting.class);
        query.setParameter("class", confName);

        return query.getResultList();
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
    @SuppressWarnings({"PMD.NPathComplexity",
                       "PMD.CyclomaticComplexity",
                       "PMD.StandardCyclomaticComplexity"})
    public SettingInfo getSettingInfo(
        final Class<?> configuration,
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

        //Make the field accessible even if it has a private modifier
        field.setAccessible(true);

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
            settingInfo.setDefaultValue(Objects.toString(field.get(conf)));
        } catch (InstantiationException | IllegalAccessException ex) {
            LOGGER.warn(String.format("Failed to create instance of \"%s\" to "
                                          + "get default values.",
                                      configuration.getName()),
                        ex);
        }

        settingInfo.setConfClass(configuration.getName());
        settingInfo.setDescBundle(getDescBundle(configuration));

        if (Strings.isBlank(settingAnnotation.labelKey())) {
            settingInfo.setLabelKey(String.join(".", field.getName(),
                                                "label"));
        } else {
            settingInfo.setLabelKey(name);
        }

        if (Strings.isBlank(settingAnnotation.descKey())) {
            settingInfo.setDescKey(String.join(".",
                                               field.getName(),
                                               "description"));
        } else {
            settingInfo.setDescKey(settingAnnotation.descKey());
        }

        return settingInfo;
    }

    /**
     * A low level method for finding a setting in the registry.
     *
     * @param <T>      Type of the value of the setting
     * @param confName Name of the configuration to which the setting belongs
     * @param name     The fully qualified name of the setting.
     * @param clazz    The class of the setting.
     *
     * @return The requested setting if it exists in the registry, {@code null}
     *         otherwise.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("unchecked")
    public <T> AbstractSetting<T> findSetting(final String confName,
                                              final String name,
                                              final Class<T> clazz) {
        LOGGER.debug(String.format(
            "Trying to find setting \"%s\" of type \"%s\"",
            name,
            clazz.getName()));

        final TypedQuery<AbstractSetting> query = entityManager.
            createNamedQuery("AbstractSetting.findByClassAndName",
                             AbstractSetting.class);
        query.setParameter("class", confName);
        query.setParameter("name", name);
        final List<AbstractSetting> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * Low level method of saving a setting.
     *
     * @param setting The setting to save.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void saveSetting(final AbstractSetting<?> setting) {
        if (setting.getSettingId() == 0) {
            entityManager.persist(setting);
        } else {
            entityManager.merge(setting);
        }
    }

    private String getDescBundle(final Class<?> configuration) {
        final Configuration confAnnotation = configuration.getAnnotation(
            Configuration.class);
        if (confAnnotation.descBundle() == null
                || confAnnotation.descBundle().isEmpty()) {
            return String.join("",
                               configuration.getName(),
                               "Description");
        } else {
            return confAnnotation.descBundle();
        }
    }

}
