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
package org.libreccm.formbuilder;

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
@Table(name = "formbuilder_data_driven_selects")
public class DataDrivenSelect extends Widget implements Serializable {

    private static final long serialVersionUID = -4477753441663454661L;

    @Column(name = "multiple")
    private boolean multiple;

    @Column(name = "query")
    private String query;

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(final boolean multiple) {
        this.multiple = multiple;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 41 * hash + (multiple ? 1 : 0);
        hash = 41 * hash + Objects.hashCode(query);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataDrivenSelect other = (DataDrivenSelect) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (multiple != other.isMultiple()) {
            return false;
        }
        return Objects.equals(query, other.getQuery());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof DataDrivenSelect;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", multiple = %b,"
                                                + "query = \"%s\"%s",
                                            multiple,
                                            query,
                                            data));
    }

}
