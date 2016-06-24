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
 */package org.librecms.assets;

import org.hibernate.envers.Audited;
import org.libreccm.core.Identifiable;
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(schema = DB_SCHEMA, name = "ASSETS")
@Audited
public class Asset implements Identifiable, Serializable {

    private static final long serialVersionUID = -3499741368562653529L;

    @Column(name = "ASSET_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long assetId;

    @Column(name = "UUID", unique = true)
    private String uuid;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "ASSET_TITLES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "ASSET_ID")
                               }
        )
    )
    private LocalizedString title;

    public long getAssetId() {
        return assetId;
    }

    protected void setAssetId(final long assetId) {
        this.assetId = assetId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (assetId ^ (assetId >>> 32));
        hash = 97 * hash + Objects.hashCode(uuid);
        hash = 97 * hash + Objects.hashCode(title);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Asset)) {
            return false;
        }
        final Asset other = (Asset) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (assetId != other.getAssetId()) {
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }
        return Objects.equals(title, other.getTitle());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Asset;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format(
            "%s{ "
                + "assetIdd = %d, "
                + "uuid = %s, "
                + "title = {}%s"
                + " }",
            super.toString(),
            assetId,
            uuid,
            Objects.toString(title),
            data);
    }

}
