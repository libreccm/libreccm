/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.theming;

import java.io.Serializable;
import java.util.Objects;

/**
 * Informations about a file in a theme.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ThemeFileInfo implements Serializable {

    private static final long serialVersionUID = 2880986115955856570L;

    /**
     * The name of the file.
     */
    private String name;

    /**
     * Is the file a directory?
     */
    private boolean directory;

    /**
     * The type of the file (for example {@code text/xml} or {@code image/jpeg}.
     */
    private String mimeType;

    /**
     * The size of the file. For directories this will be {@code 0}.
     */
    private long size;

    /**
     * Is the file writable?
     */
    private boolean writable;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(final boolean directory) {
        this.directory = directory;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(final boolean writable) {
        this.writable = writable;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(name);
        hash = 83 * hash + (directory ? 1 : 0);
        hash = 83 * hash + Objects.hashCode(mimeType);
        hash = 83 * hash + (int) (size ^ (size >>> 32));
        hash = 83 * hash + (writable ? 1 : 0);
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
        if (!(obj instanceof ThemeFileInfo)) {
            return false;
        }
        final ThemeFileInfo other = (ThemeFileInfo) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (directory != other.isDirectory()) {
            return false;
        }
        if (size != other.getSize()) {
            return false;
        }
        if (writable != other.isWritable()) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        return Objects.equals(mimeType, other.getMimeType());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ThemeFileInfo;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "name = \"%s\", "
                                 + "directory = %b, "
                                 + "mimeType = \"%s\", "
                                 + "size = %d, "
                                 + "writable = %b%s"
                                 + " }",
                             super.toString(),
                             name,
                             directory,
                             mimeType,
                             size,
                             writable,
                             data);
    }

}
