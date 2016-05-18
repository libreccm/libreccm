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

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Setting for values for type double (rational numbers). Be aware: Because how
 * double values are handled by Java (and all other programming languages) the
 * precision of a value of the type {@code double} can be guaranteed. If full
 * precision is required use {@link BigDecimal} and {@link BigDecimalSetting}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
public class DoubleSetting
    extends AbstractSetting<Double> implements Serializable {

    private static final long serialVersionUID = 4698940335480821950L;

    @Column(name = "SETTING_VALUE_DOUBLE")
    private double value;

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(final Double value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 71 * hash + Double.hashCode(value);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DoubleSetting)) {
            return false;
        }
        final DoubleSetting other = (DoubleSetting) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Double.doubleToLongBits(value) == Double.doubleToLongBits(other
            .getValue());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof DoubleSetting;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", value = %f%s",
                                            value,
                                            data));
    }

}
