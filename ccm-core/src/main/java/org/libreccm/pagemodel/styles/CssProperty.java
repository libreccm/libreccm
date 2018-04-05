/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.pagemodel.styles;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A single CSS property like {@code font-weight: bold}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "STYLE_PROPERTIES")
public class CssProperty implements Serializable {

    private static final long serialVersionUID = -4697757123207731769L;

    @Id
    @Column(name = "PROPERTY_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long propertyId;

    @Column(name = "NAME", length = 256)
    private String name;

    @Column(name = "PROPERTY_VALUE", length = 4096)
    private String value;

    public long getPropertyId() {
        return propertyId;
    }

    protected void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int) (propertyId ^ (propertyId >>> 32));
        hash = 17 * hash + Objects.hashCode(name);
        hash = 17 * hash + Objects.hashCode(value);
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
        if (!(obj instanceof CssProperty)) {
            return false;
        }
        final CssProperty other = (CssProperty) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        return Objects.equals(value, other.getValue());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof CssProperty;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "name = \"%s\", "
                                 + "value = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             name,
                             value,
                             data);
    }

    public String toCss() {
        return String.format("%s: %s",
                             name,
                             value);
    }

}
