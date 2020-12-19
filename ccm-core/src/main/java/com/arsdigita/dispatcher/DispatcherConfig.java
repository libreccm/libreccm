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
package com.arsdigita.dispatcher;

import java.util.Objects;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration(
    descBundle = "com.arsdigita.dispatcher.DispatcherConfig",
    descKey = "description",
    titleKey = "title"
)
public final class DispatcherConfig {

    @Setting(
        descKey = "cachingActive.desc",
        labelKey = "cachingActive.label"
    )
    private Boolean cachingActive = true;

    @Setting(
        descKey = "defaultExpiry.desc",
        labelKey = "defaultExpiry.label"
    )
    private Integer defaultExpiry = 259200;

    @Setting(
        descKey = "staticUrlPrefix.desc",
        labelKey = "statusUrlPrefix.label"
    )
    private String staticUrlPrefix = "/STATICII/";

    @Setting(
        descKey = "defaultPageClass.desc",
        labelKey = "defaultPageClass.label"
    )
    private String defaultPageClass = "com.arsdigita.bebop.Page";

    public static DispatcherConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(DispatcherConfig.class);
    }

    public Boolean isCachingActive() {
        return cachingActive;
    }

    public void setCachingActive(final Boolean cachingActive) {
        this.cachingActive = cachingActive;
    }

    public Integer getDefaultExpiry() {
        return defaultExpiry;
    }

    public void setDefaultExpiry(final Integer defaultExpiry) {
        this.defaultExpiry = defaultExpiry;
    }

    public String getStaticUrlPrefix() {
        return staticUrlPrefix;
    }

    public void setStaticUrlPrefix(final String staticUrlPrefix) {
        this.staticUrlPrefix = staticUrlPrefix;
    }

    public String getDefaultPageClass() {
        return defaultPageClass;
    }

    public void setDefaultPageClass(final String defaultPageClass) {
        this.defaultPageClass = defaultPageClass;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(cachingActive);
        hash = 97 * hash + Objects.hashCode(defaultExpiry);
        hash = 97 * hash + Objects.hashCode(staticUrlPrefix);
        hash = 97 * hash + Objects.hashCode(defaultPageClass);
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
        if (!(obj instanceof DispatcherConfig)) {
            return false;
        }
        final DispatcherConfig other = (DispatcherConfig) obj;
        if (!Objects.equals(staticUrlPrefix, other.getStaticUrlPrefix())) {
            return false;
        }
        if (!Objects.equals(defaultPageClass, other.getDefaultPageClass())) {
            return false;
        }
        if (!Objects.equals(cachingActive, other.isCachingActive())) {
            return false;
        }
        return Objects.equals(defaultExpiry, other.getDefaultExpiry());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "cachingActive = %b, "
                                 + "defaultExpiry = %d, "
                                 + "staticUrlPrefix  = \"%s\", "
                                 + "defaultPageClass = \"%s\""
                                 + " }",
                             super.toString(),
                             cachingActive,
                             defaultExpiry,
                             staticUrlPrefix,
                             defaultPageClass);
    }

}
