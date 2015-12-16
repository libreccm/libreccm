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
import java.util.Collections;
import java.util.Set;
import java.util.Objects;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

/**
 * A setting class for storing a list a strings. This can be used to generate
 * enums which can be configured by the administrator.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "SETTINGS_ENUM", schema = DB_SCHEMA)
public class EnumSetting
    extends AbstractSetting<Set<String>> implements Serializable {

    private static final long serialVersionUID = 8506016944203102813L;

    @ElementCollection
    @JoinTable(name = "ENUM_CONFIGURATION_ENTRIES_VALUES",
               schema = DB_SCHEMA,
               joinColumns = {@JoinColumn(name = "ENUM_ID")})
    private Set<String> value;

    @Override
    public Set<String> getValue() {
        if (value == null) {
            return null;
        } else {
            return Collections.unmodifiableSet(value);
        }
    }

    @Override
    public void setValue(final Set<String> value) {
        this.value = value;
    }

    public void addEnumValue(final String value) {
        this.value.add(value);
    }

    public void removeEnumValue(final String value) {
        this.value.remove(value);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + Objects.hashCode(value);
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

        if (!(obj instanceof EnumSetting)) {
            return false;
        }
        final EnumSetting other = (EnumSetting) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(value, other.getValue());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof EnumSetting;
    }

    @Override
    public String toString(final String data) {
        final StringBuffer enumValues = new StringBuffer();
        enumValues.append("{ ");
        if (value != null) {
            value.forEach((String v) -> {
                enumValues.append('\"').append(v).append('\"');
                if (enumValues.indexOf(v) != enumValues.length() - 1) {
                    enumValues.append(", ");
                }
            });
            enumValues.append(" }");
        }

        return super.toString(String.format(", value = %s%s",
                                            enumValues.toString(),
                                            data));
    }

}
