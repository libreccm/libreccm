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
@Table(name = "formbuilder_data_queries", schema = "ccm_core")
public class PersistentDataQuery extends CcmObject implements Serializable {

    private static final long serialVersionUID = -7344153915501267752L;

    @Column(name = "query_id")
    private String queryId;

    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "formbuilder_data_query_names",
            schema = "ccm_core",
            joinColumns = {
                @JoinColumn(name = "data_query_id")}))
    private LocalizedString name;

    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "formbuilder_data_query_descriptions",
            schema = "ccm_core",
            joinColumns = {
                @JoinColumn(name = "data_query_id")}))
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
