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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Setting for storing a long value.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "SETTINGS_LONG", schema = DB_SCHEMA)
public class LongSetting 
    extends AbstractSetting<Long> implements Serializable{

    private static final long serialVersionUID = 818622372461020368L;
    
    @Column(name = "setting_value")
    private long value;

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public void setValue(final Long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + Long.hashCode(value);
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
        if (!(obj instanceof LongSetting)) {
            return false;
        }
        final LongSetting other
                                         = (LongSetting) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        return this.value == other.getValue();
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof LongSetting;
    }
    
    @Override
    public String toString(final String data) {
        return super.toString(String.format(", value = %d%s",
                                            value,
                                            data));
    }
    
}
