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

import static org.libreccm.core.CoreConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "SETTINGS_STRING_LIST", schema = DB_SCHEMA)
public class StringListSetting extends AbstractSetting<List<String>> {

    private static final long serialVersionUID = 7093818804712916413L;
    
    @ElementCollection
    @JoinTable(name = "SETTINGS_STRING_LIST",
               schema = DB_SCHEMA,
               joinColumns = {@JoinColumn(name = "LIST_ID")})
    private List<String> value;
    
    @Override
    public List<String> getValue() {
        if (value == null) {
            return null;
        } else {
            return new ArrayList<>(value);
        }
    }
    
    @Override
    public void setValue(final List<String> value) {
        this.value = value;
    }
    
    public void addListValue(final String value) {
        this.value.add(value);
    }
    
    public void removeListValue(final String value) {
        this.value.remove(value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(value);
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
        if (!(obj instanceof StringListSetting)) {
            return false;
        }
        final StringListSetting other = (StringListSetting) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        return Objects.equals(value, other.getValue());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof StringListSetting;
    }
    
    @Override
    public String toString(final String data) {
        final StringBuilder listValues = new StringBuilder();
        listValues.append("{ ");
        if (value != null) {
            value.forEach((String v) -> {
                listValues.append('\"').append(v).append('\"');
                if (value.indexOf(v) != value.size() - 1) {
                    listValues.append(", ");
                }
            });
        }
        listValues.append(" }");
        
        return super.toString(String.format(", value = %s%s",
                                            listValues.toString(),
                                            data));
    }
    
}
