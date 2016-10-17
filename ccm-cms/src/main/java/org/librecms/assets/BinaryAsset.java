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
import java.util.Arrays;
import java.util.Objects;

import javax.activation.MimeType;
import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;
import org.libreccm.jpa.utils.MimeTypeConverter;
import org.libreccm.l10n.LocalizedString;

import javax.persistence.Convert;

import static org.librecms.CmsConstants.*;

/**
 * Base class for all assets storing binary information, like videos.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "BINARY_ASSETS", schema = DB_SCHEMA)
@Audited
public class BinaryAsset extends Asset implements Serializable {

    private static final long serialVersionUID = -8540922051232103527L;

    @Embedded
    @AssociationOverride(
            name = "values",
            joinTable = @JoinTable(name = "BINARY_ASSET_DESCRIPTIONS",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "ASSET_ID")
                                   }
            )
    )
    private LocalizedString description;

    @Column(name = "FILENAME", length = 512, nullable = false)
    @NotEmpty
    private String fileName;

    @Column(name = "MIME_TYPE", length = 512, nullable = false)
    @Convert(converter = MimeTypeConverter.class)
    @NotEmpty
    private MimeType mimeType;

    @Column(name = "ASSET_DATA")
    @Lob
    private byte[] data;

    @Column(name = "DATA_SIZE")
    private long size;

    public BinaryAsset() {
        super();
        description = new LocalizedString();
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(final MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public void setData(final byte[] data) {
        this.data = Arrays.copyOf(data, data.length);
        size = data.length;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + Objects.hashCode(description);
        hash = 59 * hash + Objects.hashCode(fileName);
        hash = 59 * hash + Objects.hashCode(mimeType);
        hash = 59 * hash + Arrays.hashCode(data);
        hash = 59 * hash + (int) (size ^ (size >>> 32));
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

        if (!(obj instanceof BinaryAsset)) {
            return false;
        }
        final BinaryAsset other = (BinaryAsset) obj;
        if (!(other.canEqual(this))) {
            return false;
        }
        if (size != other.getSize()) {
            return false;
        }
        if (!Objects.equals(fileName, other.getFileName())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (!Objects.equals(mimeType, other.getMimeType())) {
            return false;
        }
        return Arrays.equals(data, other.getData());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof BinaryAsset;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", description = %s, "
                                                    + "fileName = \"%s\", "
                                                    + "mimeType = \"%s\", "
                                                    + "size = %d%s",
                                            Objects.toString(description),
                                            fileName,
                                            Objects.toString(mimeType),
                                            size,
                                            data));
    }
}
