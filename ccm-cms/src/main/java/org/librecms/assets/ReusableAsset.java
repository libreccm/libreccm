/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.assets;

import org.hibernate.envers.Audited;
import org.libreccm.core.CcmObject;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
@Entity
@Table(schema = DB_SCHEMA, name = "REUSABLE_ASSETS")
@Audited
public class ReusableAsset<T extends Asset> extends CcmObject
    implements Serializable {

    private static final long serialVersionUID = 1341326042963088198L;

    @OneToOne(targetEntity = Asset.class)
    @JoinColumn(name = "ASSET_ID")
    private T asset;

    public T getAsset() {
        return asset;
    }

    protected void setAsset(final T asset) {
        this.asset = asset;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 11 * hash + Objects.hashCode(asset);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof ReusableAsset)) {
            return false;
        }
        final ReusableAsset<?> other = (ReusableAsset<?>) obj;
        if (!other.canEqual(obj)) {
            return false;
        }

        return Objects.equals(asset, other.getAsset());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ReusableAsset;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(
            ", asset = { %s }%s",
            Objects.toString(asset),
            data
        ));
    }

}
