/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package org.librecms.assets;

import org.hibernate.envers.Audited;
import org.librecms.contentsection.Asset;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * A reuable postal address.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "POSTAL_ADDRESSES", schema = DB_SCHEMA)
public class PostalAddress extends Asset {

    private static final long serialVersionUID = 1L;

    @Column(name = "ADDRESS", length = 2048)
    private String address;

    @Column(name = "POSTAL_CODE", length = 255)
    private String postalCode;

    @Column(name = "CITY", length = 512)
    private String city;

    @Column(name = "ADDRESS_STATE", length = 255)
    private String state;

    @Column(name = "ISO_COUNTRY_CODE", length = 10)
    private String isoCountryCode;

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public void setIsoCountryCode(final String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 67 * hash + Objects.hashCode(address);
        hash = 67 * hash + Objects.hashCode(postalCode);
        hash = 67 * hash + Objects.hashCode(city);
        hash = 67 * hash + Objects.hashCode(state);
        hash = 67 * hash + Objects.hashCode(isoCountryCode);
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
        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof PostalAddress)) {
            return false;
        }
        final PostalAddress other = (PostalAddress) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(address, other.getAddress())) {
            return false;
        }
        if (!Objects.equals(postalCode, other.getPostalCode())) {
            return false;
        }
        if (!Objects.equals(city, other.getCity())) {
            return false;
        }
        if (!Objects.equals(state, other.getState())) {
            return false;
        }
        return Objects.equals(isoCountryCode, other.getIsoCountryCode());
    }

    @Override
    public String toString(final String data) {

        return super.toString(String.format(
            "address = \"%s\", "
                + "postalCode = \"%s\", "
                + "city = \"%s\", "
                + "state = \"%s\", "
                + "isoCountryCode = \"%s\"%s",
            address,
            postalCode,
            city,
            state,
            isoCountryCode,
            data));
    }

}
