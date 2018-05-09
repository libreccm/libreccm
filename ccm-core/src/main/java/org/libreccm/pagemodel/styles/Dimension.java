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
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Embeddable
public class Dimension implements Serializable {

    private static final long serialVersionUID = 44299305931240403L;

    @Column(name = "DIMENSION_VALUE")
    private double value;

    @Column(name = "UNIT")
    @Enumerated(EnumType.STRING)
    private Unit unit;

    public double getValue() {
        return value;
    }

    public void setValue(final double value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(final Unit unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash
                   + (int) (Double.doubleToLongBits(value)
                            ^ (Double.doubleToLongBits(value) >>> 32));
        hash = 37 * hash + Objects.hashCode(unit);
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
        if (!(obj instanceof Dimension)) {
            return false;
        }
        final Dimension other = (Dimension) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (Double.doubleToLongBits(value)
                != Double.doubleToLongBits(other.getValue())) {
            return false;
        }
        return unit == other.getUnit();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Dimension;
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "value = %f, "
                                 + "unit = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             value,
                             Objects.toString(unit),
                             data);
    }

    @Override
    public final String toString() {

        return toString("");
    }

    public String toCss() {
        return String.format("%s%s", value, unit.toString().toLowerCase());
    }

}
