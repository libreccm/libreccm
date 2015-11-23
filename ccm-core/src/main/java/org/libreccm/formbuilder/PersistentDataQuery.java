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

import org.libreccm.core.CcmObject;

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
@Table(name = "FORMBUILDER_DATA_QUERIES", schema = DB_SCHEMA)
public class PersistentDataQuery extends CcmObject implements Serializable {

    private static final long serialVersionUID = -7344153915501267752L;

    @Column(name = "QUERY_ID")
    private String queryId;

    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "FORMBUILDER_DATA_QUERY_NAMES",
            schema = DB_SCHEMA,
            joinColumns = {
                @JoinColumn(name = "DATA_QUERY_ID")}))
    private LocalizedString name;

    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "FORMBUILDER_DATA_QUERY_DESCRIPTIONS",
            schema = DB_SCHEMA,
            joinColumns = {
                @JoinColumn(name = "DATA_QUERY_ID")}))
    private LocalizedString description;

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(final String queryId) {
        this.queryId = queryId;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(final LocalizedString name) {
        this.name = name;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 23 * hash + Objects.hashCode(queryId);
        hash = 23 * hash + Objects.hashCode(name);
        hash = 23 * hash + Objects.hashCode(description);
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

        if (!(obj instanceof PersistentDataQuery)) {
            return false;
        }
        final PersistentDataQuery other = (PersistentDataQuery) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(queryId, other.getQueryId())) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        return Objects.equals(description, other.getDescription());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof PersistentDataQuery;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", queryId = \"%s\", "
                                                + "name = %s, "
                                                + "description = %s%s",
                                            queryId,
                                            Objects.toString(name),
                                            Objects.toString(description),
                                            data));
    }

}
