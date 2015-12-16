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

import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

/**
 * A setting which stores a {@link LocalizedString} . This can be used for
 * storing values for text in the user interface which should be customisable by
 * the administrator.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "SETTINGS_L10N_STRING", schema = DB_SCHEMA)
public class LocalizedStringSetting
    extends AbstractSetting<LocalizedString> implements Serializable {

    private static final long serialVersionUID = -5854552013878000164L;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CONF_ENTRIES_L10N_STR_VALUES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "ENTRY_ID")}))
    private LocalizedString value;

    @Override
    public LocalizedString getValue() {
        return value;
    }

    @Override
    public void setValue(final LocalizedString value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 53 * hash + Objects.hashCode(value);
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

        if (!(obj instanceof LocalizedStringSetting)) {
            return false;
        }
        final LocalizedStringSetting other
                                                    = (LocalizedStringSetting) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(value, other.getValue());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof LocalizedStringSetting;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", value = %s%s",
                                            Objects.toString(value),
                                            data));
    }

}
