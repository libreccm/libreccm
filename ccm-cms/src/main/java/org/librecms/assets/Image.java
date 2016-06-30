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

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import org.libreccm.l10n.LocalizedString;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "IMAGES", schema = DB_SCHEMA)
@Audited
public class Image extends BinaryAsset implements Serializable {

    private static final long serialVersionUID = -8095106228017573785L;

    @Column(name = "WIDTH")
    private long width;

    @Column(name = "HEIGHT")
    private long height;

    @Embedded
    @AssociationOverride(
            name = "values",
            joinTable = @JoinTable(name = "IMAGE_COPYRIGHT_NOTICES",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "ASSET_ID")
                                   }
            )
    )
    private LocalizedString copyrightNotice;

    public Image() {
        super();
        copyrightNotice = new LocalizedString();
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(final long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(final long height) {
        this.height = height;
    }

    public LocalizedString getCopyrightNotice() {
        return copyrightNotice;
    }

    public void setCopyrightNotice(final LocalizedString copyrightNotice) {
        this.copyrightNotice = copyrightNotice;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + (int) (width ^ (width >>> 32));
        hash = 89 * hash + (int) (height ^ (height >>> 32));
        hash = 89 * hash + Objects.hashCode(copyrightNotice);
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

        if (!(obj instanceof Image)) {
            return false;
        }
        final Image other = (Image) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (width != other.getWidth()) {
            return false;
        }
        if (height != other.getHeight()) {
            return false;
        }
        return Objects.equals(copyrightNotice, other.getCopyrightNotice());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Image;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", width = %d, "
                                                    + "height = %d, "
                                                    + "copyrightNotice = %s%s",
                                            width,
                                            height,
                                            Objects.toString(copyrightNotice),
                                            data));
    }

}
