/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CONF_ENTRIES_BIG_DECIMAL")
public class BigDecimalConfigurationEntry
    extends AbstractConfigurationEntry<BigDecimal> implements Serializable {

    private static final long serialVersionUID = 1869044294174385532L;
    
    @Column(name = "entry_value")
    private BigDecimal value;
    
    @Override
    public BigDecimal getValue() {
        return value;
    }
    
    @Override
    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + Objects.hashCode(value);
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
        
        if (!(obj instanceof BigDecimalConfigurationEntry)) {
            return false;
        }
        final BigDecimalConfigurationEntry other
                                           = (BigDecimalConfigurationEntry) obj;
        if (!other.canEqual(this)) {
            return false;
        }
         
        return Objects.equals(value, other.getValue());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof BigDecimalConfigurationEntry;
    }
    
    @Override
    public String toString(final String data) {
        return super.toString(String.format(", value = %s%s",
                                            value,
                                            data));
    }


}
