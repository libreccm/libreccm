/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.assets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsPostalAddressEditStep")
public class PostalAddressEditStepModel {

    private String address;

    private String postalCode;

    private String city;

    private String state;

    private String isoCountryCode;

    private String country;

    private Map<String, String> countries;

    public String getAddress() {
        return address;
    }

    protected void setAddress(final String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    protected void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    protected void setCity(final String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    protected void setState(final String state) {
        this.state = state;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    protected void setIsoCountryCode(final String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
    }

    public String getCountry() {
        return country;
    }

    protected void setCountry(final String country) {
        this.country = country;
    }

    public Map<String, String> getCountries() {
        return Collections.unmodifiableMap(countries);
    }

    public void setCountries(final Map<String, String> countries) {
        this.countries = new HashMap<>(countries);
    }

}
