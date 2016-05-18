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
import org.libreccm.l10n.LocalizedString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;

/**
 * Helper class for converting values to settings of the appropriate type.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class SettingConverter {

    private static final Logger LOGGER = LogManager.getLogger(
        SettingConverter.class);

    /**
     * Stores a map of the Java types and the corresponding subtypes
     * {@link AbstractSetting}. We are using a map here to avoid a large if or
     * switch statement. 
     */
    private final Map<String, Class<? extends AbstractSetting<?>>> typeMap;

    public SettingConverter() {
        typeMap = new HashMap<>();
        typeMap.put(BigDecimal.class.getName(), BigDecimalSetting.class);
        typeMap.put(Boolean.class.getName(), BooleanSetting.class);
        typeMap.put("boolean", BooleanSetting.class);
        typeMap.put(Double.class.getName(), DoubleSetting.class);
        typeMap.put("double", DoubleSetting.class);
        typeMap.put(List.class.getName(), StringListSetting.class);
        typeMap.put(LocalizedString.class.getName(),
                    LocalizedStringSetting.class);
        typeMap.put(Long.class.getName(), LongSetting.class);
        typeMap.put("long", LongSetting.class);
        typeMap.put(Set.class.getName(), EnumSetting.class);
        typeMap.put(String.class.getName(), StringSetting.class);
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
    <T> AbstractSetting<T> createSettingForValueType(final Class<T> valueType) {

        final String valueTypeName = valueType.getName();
        final Class<? extends AbstractSetting<?>> clazz = typeMap.get(
            valueTypeName);

        if (clazz == null) {
            LOGGER.error("No setting type for value type \"{}\".",
                         valueTypeName);
            throw new IllegalArgumentException(String.format(
                "No setting type for value type \"%s\".", valueTypeName));
        } else {
            try {
                return (AbstractSetting<T>) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                LOGGER.error("Failed to create setting instance.", ex);
                throw new IllegalStateException(
                    "Failed to create setting instance.", ex);
            }
        }
    }

}
