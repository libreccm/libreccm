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
package com.arsdigita.templating;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.ApplicationFileResolver;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class TemplatingConfig {

    @Setting
    private String stylesheetPaths = "stylesheet-paths.txt";

    @Setting
    private String stylesheetResolverClass = PatternStylesheetResolver.class
        .getName();

    @Setting
    private Integer stylesheetCacheSize = 10;

    @Setting
    private Integer stylesheetCacheAge = 3600;

    public static TemplatingConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(TemplatingConfig.class);
    }

    public String getStylesheetPaths() {
        return stylesheetPaths;
    }

    public void setStylesheetPaths(final String stylesheetPaths) {
        this.stylesheetPaths = stylesheetPaths;
    }

    public String getStylesheetResolverClass() {
        return stylesheetResolverClass;
    }

    public StylesheetResolver getStylesheetResolver() {

        try {
            @SuppressWarnings("unchecked")
            final Class<StylesheetResolver> clazz
                                                = (Class<StylesheetResolver>) Class
                .forName(stylesheetResolverClass);
            return clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new UncheckedWrapperException(
                "Unable to create configured StylesheetResolver.",
                ex);
        }
    }

    public void setStylesheetResolverClass(
        final String stylesheetResolverClass) {
        try {
            final Class<?> clazz = Class.forName(stylesheetResolverClass);
            if (!StylesheetResolver.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(String.format(
                    "Provided class \"%s\" is not an "
                        + "implementation of the interface \"%s\".",
                    stylesheetResolverClass,
                    StylesheetResolver.class.getName()));
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(
                String.format("Unable to retrieve class \"%s\".",
                              stylesheetResolverClass),
                ex);
        }

        this.stylesheetResolverClass = stylesheetResolverClass;
    }

    public Integer getStylesheetCacheSize() {
        return stylesheetCacheSize;
    }

    public void setStylesheetCacheSize(final Integer stylesheetCacheSize) {
        this.stylesheetCacheSize = stylesheetCacheSize;
    }

    public Integer getStylesheetCacheAge() {
        return stylesheetCacheAge;
    }

    public void setStylesheetCacheAge(final Integer stylesheetCacheAge) {
        this.stylesheetCacheAge = stylesheetCacheAge;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(stylesheetPaths);
        hash = 23 * hash + Objects.hashCode(stylesheetResolverClass);
        hash = 23 * hash + Objects.hashCode(stylesheetCacheSize);
        hash = 23 * hash + Objects.hashCode(stylesheetCacheAge);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof TemplatingConfig) {
            return false;
        }
        final TemplatingConfig other = (TemplatingConfig) obj;
        if (!Objects.equals(stylesheetPaths, other.getStylesheetPaths())) {
            return false;
        }
        if (!Objects.equals(stylesheetResolverClass,
                            other.getStylesheetResolverClass())) {
            return false;
        }
        if (!Objects.equals(stylesheetCacheSize,
                            other.getStylesheetCacheSize())) {
            return false;
        }
        if (!Objects.equals(stylesheetCacheAge,
                            other.getStylesheetCacheAge())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "stylesheetPaths = \"%s\", "
                                 + "stylesheetResolverClass = \"%s\", "
                                 + "stylesheetCacheSize = %d, "
                                 + "stylesheetCacheAge = %d"
                                 + " }",
                             super.toString(),
                             stylesheetPaths,
                             stylesheetResolverClass,
                             stylesheetCacheSize,
                             stylesheetCacheAge);
    }

}
