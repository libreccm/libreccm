/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.db;

import org.libreccm.core.CoreConstants;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A file inside the directory structure of a theme stored in the database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "THEME_DATA_FILES", schema = CoreConstants.DB_SCHEMA)
public class DataFile extends ThemeFile {

    private static final long serialVersionUID = 7513785608453872667L;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "FILE_SIZE")
    private long size;

    @Column(name = "CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "LAST_MODIFIED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    @Column(name = "FILE_DATA")
    @Lob
    private byte[] data;

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public Date getCreationDate() {
        if (creationDate == null) {
            return null;
        } else {
            return new Date(creationDate.getTime());
        }
    }

    protected void setCreationDate(final Date creationDate) {
        if (creationDate == null) {
            this.creationDate = null;
        } else {
            this.creationDate = new Date(creationDate.getTime());
        }
    }

    public Date getLastModified() {
        if (lastModified == null) {
            return null;
        } else {
            return new Date(lastModified.getTime());
        }
    }

    protected void setLastModified(final Date lastModified) {
        if (lastModified == null) {
            this.lastModified = null;
        } else {
            this.lastModified = new Date(lastModified.getTime());
        }
    }

    public byte[] getData() {
        if (data == null) {
            return null;
        } else {
            return Arrays.copyOf(data, data.length);
        }
    }

    public void setData(final byte[] data) {
        if (data == null) {
            this.data = null;
        } else {
            this.data = Arrays.copyOf(data, data.length);
        }
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 47 * hash + Objects.hashCode(type);
        hash = 47 * hash + (int) (size ^ (size >>> 32));
        if (creationDate == null) {
            hash = 47 * hash + 0;
        } else {
            hash = 47 * hash + Objects.hashCode(creationDate.getTime());
        }
        if (lastModified == null) {
            hash = 47 * hash + 0;
        } else {
            hash = 47 * hash + Objects.hashCode(lastModified.getTime());
        }
        hash = 47 * hash + Arrays.hashCode(data);
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
        if (!(obj instanceof DataFile)) {
            return false;
        }
        final DataFile other = (DataFile) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (size != other.getSize()) {
            return false;
        }
        if (!Objects.equals(type, other.getType())) {
            return false;
        }
        if (!Objects.equals(creationDate, other.getCreationDate())) {
            return false;
        }
        if (!Objects.equals(lastModified, other.getLastModified())) {
            return false;
        }
        return Arrays.equals(data, other.getData());
    }

    @Override
    public boolean canEqual(final Object other) {
        return other instanceof DataFile;
    }

    @Override
    public String toString(final String toStringData) {
        return super.toString(String.format(", size = %d, "
                                                + "type = \"%s\", "
                                                + "creationDate = %tF %<tT, "
                                                + "lastModified = %tF %<tT%s",
                                            size,
                                            type,
                                            creationDate,
                                            lastModified,
                                            toStringData));
    }

}
