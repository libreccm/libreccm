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

import org.hibernate.validator.constraints.NotBlank;
import org.libreccm.core.CcmObject;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

/**
 * Abstract base class for all settings.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The value type of the setting.
 */
@Entity
@Table(name = "SETTINGS", schema = DB_SCHEMA)
public abstract class AbstractSetting<T>
    extends CcmObject implements Serializable {

    private static final long serialVersionUID = -839223659103128135L;

    /**
     * The name of the setting. The string must be a valid URL fragment.
     */
    @Column(name = "name", nullable = false, length = 512)
    @NotBlank
    @Pattern(regexp = "[\\w-.]*")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for the value of the setting.
     * 
     * @return The value of the setting.
     */
    public abstract T getValue();

    /**
     * Setter for the value of the setting.
     * 
     * @param value The new value of the setting.
     */
    public abstract void setValue(T value);

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 47 * hash + Objects.hashCode(name);
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

        if (!(obj instanceof AbstractSetting)) {
            return false;
        }

        final AbstractSetting<?> other
                                                = (AbstractSetting) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(name, other.getName());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof AbstractSetting;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", name = \"%s\"%s",
                                            name,
                                            data));
    }

}
