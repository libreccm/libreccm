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

import org.libreccm.core.CcmObject;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
@Entity
@Table(name = "CONFIGURATION_ENTRIES", schema = DB_SCHEMA)
public abstract class AbstractConfigurationEntry<T>
    extends CcmObject implements Serializable {

    private static final long serialVersionUID = -839223659103128135L;

    @Column(name = "comment", length = 2048)
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 47 * hash + Objects.hashCode(comment);
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

        if (!(obj instanceof AbstractConfigurationEntry)) {
            return false;
        }

        final AbstractConfigurationEntry<?> other
                                            = (AbstractConfigurationEntry) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(comment, other.getComment());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof AbstractConfigurationEntry;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", comment = \"%s\"%s",
                                            comment,
                                            data));
    }

}
