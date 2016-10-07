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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

/**
 * An example configuration used by {@link ConfigurationManagerTest}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public class TestExampleConfiguration {

    @Setting
    private BigDecimal price;

    @Setting
    private Boolean enabled;

    @Setting
    private Double minTemperature;

    @Setting
    private Long itemsPerPage;

    @Setting
    private String helpUrl;

    @Setting
    private Set<String> languages;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(final double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public long getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(final long itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public String getHelpUrl() {
        return helpUrl;
    }

    public void setHelpUrl(final String helpUrl) {
        this.helpUrl = helpUrl;
    }

    public Set<String> getLanguages() {
        return Collections.unmodifiableSet(languages);
    }

    private void setLanguages(final Set<String> languages) {
        this.languages = languages;
    }

    public void addLanguage(final String language) {
        languages.add(language);
    }

    public void removeLanguage(final String language) {
        languages.remove(language);
    }

}
