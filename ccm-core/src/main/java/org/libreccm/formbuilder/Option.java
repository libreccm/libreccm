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

import static org.libreccm.core.CoreConstants.*;

import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FORMBUILDER_OPTIONS", schema = DB_SCHEMA)
public class Option extends Component implements Serializable {

    private static final long serialVersionUID = -7528058391772415511L;

    @Column(name = "PARAMETER_VALUE")
    private String parameterValue;

    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "FORMBUILDER_OPTION_LABELS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OPTION_ID")}))
    private LocalizedString label;

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(final String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 67 * hash + Objects.hashCode(parameterValue);
        hash = 67 * hash + Objects.hashCode(label);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Option)) {
            return false;
        }
        final Option other = (Option) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(parameterValue, other.getParameterValue())) {
            return false;
        }
        return Objects.equals(label, other.getLabel());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Option;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", parameterValue = \"%s\", "
                                                + "label = %s%s",
                                            parameterValue,
                                            Objects.toString(label),
                                            data));
    }

}
