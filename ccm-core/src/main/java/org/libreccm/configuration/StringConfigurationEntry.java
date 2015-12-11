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

import static org.libreccm.core.CoreConstants.*;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CONF_ENTRIES_STRING", schema = DB_SCHEMA)
public class StringConfigurationEntry 
    extends AbstractConfigurationEntry<String> implements Serializable {

    private static final long serialVersionUID = -8564570962027541731L;
    
    @Column(name = "entry_value", length = 1024)
    private String value;
    
    @Override
    public String getValue() {
        return value;
    }
    
    @Override
    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 67 * hash + Objects.hashCode(value);
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
        
        if (!(obj instanceof StringConfigurationEntry)) {
            return false;
        }
        final StringConfigurationEntry other = (StringConfigurationEntry) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        return Objects.equals(value, other.getValue());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof StringConfigurationEntry;
    }
    
    @Override
    public String toString(final String data) {
        return super.toString(String.format(", value = %s%s",
                                            value,
                                            data));
    }
    
}
