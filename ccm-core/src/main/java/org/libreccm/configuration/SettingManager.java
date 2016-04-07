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
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.core.CcmObject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Manages settings in the registry. Normally there should be no need to use
 * this class directly because the {@link ConfigurationManager} provide the same
 * public methods for accessing settings than this class. The purpose of this
 * class is only to separate the logic for managing settings from the logic for
 * managing configuration classes and to reduce the complexity of the
 * {@link ConfigurationManager} class
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class SettingManager {

    private static final Logger LOGGER = LogManager.getLogger(
        SettingManager.class);

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private DomainRepository domainRepo;
    
    @Inject
    private EntityManager entityManager;

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

//        final Configuration confAnnotation = configuration.getAnnotation(
//            Configuration.class);
//        final String descBundle;
//        if (confAnnotation.descBundle() == null
//                || confAnnotation.descBundle().isEmpty()) {
//            descBundle = String.join("",
//                                     configuration.getClass().getName(),
//                                     "Description");
//        } else {
//            descBundle = confAnnotation.descBundle();
//        }

        

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
        settingInfo.setDescBundle(getDescBundle(configuration));

        if (settingAnnotation.labelKey() == null
                || settingAnnotation.labelKey().isEmpty()) {
            settingInfo.setLabelKey(String.join(".", field.getName(),
                                                "label"));
        } else {
            settingInfo.setLabelKey(name);
        }

        if (settingAnnotation.descKey() == null
                || settingAnnotation.descKey().isEmpty()) {
            settingInfo.setDescKey(String.join(".",
                                               field.getName(),
                                               "descripotion"));
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

        final Domain registry = domainRepo
            .findByDomainKey(REGISTRY_DOMAIN);
        final Category category = categoryRepo.findByPath(registry,
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
    @Transactional(Transactional.TxType.REQUIRED)
    public void saveSetting(final AbstractSetting<?> setting) {
        if (setting.getObjectId() == 0) {
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
                                     configuration.getClass().getName(),
                                     "Description");
        } else {
            return confAnnotation.descBundle();
        }
    }
    
}
