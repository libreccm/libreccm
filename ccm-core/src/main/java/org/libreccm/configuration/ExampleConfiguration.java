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

import org.libreccm.l10n.LocalizedString;

import java.math.BigDecimal;
import java.util.*;

/**
 * Example of configuration with all available setting types. Not used anywhere
 * else, only for testing.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class ExampleConfiguration {

    @Setting
    private boolean enabled = false;

    @Setting
    private long itemsPerPage = 42;

    @Setting
    private double minTemperature = 23.42;

    @Setting
    private BigDecimal price = new BigDecimal("10.42");

    @Setting
    private String hostname = "srv-01.example.org";

    @Setting
    private LocalizedString title = new LocalizedString();

    @Setting
    private List<String> components = Arrays.asList(new String[]{"component1",
                                                                 "component2",
                                                                 "component3"});

    @Setting
    private Set<String> supportedLanguages = new HashSet<>(Arrays.asList(
        new String[]{"en", "de"}));

    public ExampleConfiguration() {
        title.addValue(Locale.GERMAN, "Dies ist ein Test");
        title.addValue(Locale.ENGLISH, "This is a test");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public long getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(final long itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(final double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(LocalizedString title) {
        this.title = title;
    }

    public List<String> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public void setComponents(final List<String> components) {
        this.components = components;
    }

    public Set<String> getSupportedLanguages() {
        return Collections.unmodifiableSet(supportedLanguages);
    }

    public void setSupportedLanguages(final Set<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        if (enabled) {
            hash = 13 * hash + 1;
        } else {
            hash = 13 * hash;
        }
        hash
            = 13 * hash + (int) (itemsPerPage ^ (itemsPerPage >>> 32));
        hash
            = 13 * hash + (int) (Double.doubleToLongBits(minTemperature)
                                 ^ (Double.doubleToLongBits(minTemperature)
                                    >>> 32));
        hash = 13 * hash + Objects.hashCode(price);
        hash = 13 * hash + Objects.hashCode(hostname);
        hash = 13 * hash + Objects.hashCode(title);
        hash = 13 * hash + Objects.hashCode(components);
        hash = 13 * hash + Objects.hashCode(supportedLanguages);
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
        if (!(obj instanceof ExampleConfiguration)) {
            return false;
        }
        final ExampleConfiguration other = (ExampleConfiguration) obj;
        if (enabled != other.isEnabled()) {
            return false;
        }
        if (itemsPerPage != other.getItemsPerPage()) {
            return false;
        }
        if (Double.doubleToLongBits(minTemperature) != Double.doubleToLongBits(
            other.getMinTemperature())) {
            return false;
        }
        if (!Objects.equals(hostname, other.getHostname())) {
            return false;
        }
        if (!Objects.equals(price, other.getPrice())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(components, other.getComponents())) {
            return false;
        }
        return Objects.equals(supportedLanguages, other.getSupportedLanguages());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "enabled = %b, "
                                 + "itemsPerPage = %d, "
                                 + "minTemperature = %f, "
                                 + "hostname = %s, "
                                 + "price = %f, "
                                 + "title = %s, "
                                 + "components = %s, "
                                 + "supportedLanguages = %s"
                                 + " }",
                             super.toString(),
                             enabled,
                             itemsPerPage,
                             minTemperature,
                             hostname,
                             price,
                             Objects.toString(title),
                             Objects.toString(components),
                             Objects.toString(supportedLanguages));
    }

}
