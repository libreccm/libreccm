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
package org.librecms.ui;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class FolderBrowserItem implements
    Comparable<FolderBrowserItem> {

    private long itemId;

    private String name;

    private String title;

    private String type;

    private boolean folder;

    private Date creationDate;

    private Date lastModified;

    public long getItemId() {
        return itemId;
    }

    public void setItemId(final long itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(final boolean folder) {
        this.folder = folder;
    }

    public Date getCreationDate() {
        if (creationDate == null) {
            return null;
        } else {
            return new Date(creationDate.getTime());
        }
    }

    public void setCreationDate(final Date creationDate) {
        if (creationDate != null) {
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

    public void setLastModified(final Date lastModified) {
        if (lastModified != null) {
            this.lastModified = new Date(lastModified.getTime());
        }
    }

    @Override
    public int compareTo(final FolderBrowserItem other) {

        int result = title.compareTo(other.getTitle());
        if (result != 0) {
            return result;
        }

        result = name.compareTo(other.getName());
        if (result != 0) {
            return result;
        }

        result = type.compareTo(other.getType());
        if (result != 0) {
            return result;
        }

        result = lastModified.compareTo(other.getLastModified());
        if (result != 0) {
            return result;
        }

        return creationDate.compareTo(other.getCreationDate());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (int) (itemId ^ (itemId >>> 32));
        hash = 53 * hash + Objects.hashCode(name);
        hash = 53 * hash + Objects.hashCode(title);
        hash = 53 * hash + Objects.hashCode(type);
        hash = 53 * hash + (folder ? 1 : 0);
        hash = 53 * hash + Objects.hashCode(creationDate);
        hash = 53 * hash + Objects.hashCode(lastModified);
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
        if (!(obj instanceof FolderBrowserItem)) {
            return false;
        }
        final FolderBrowserItem other = (FolderBrowserItem) obj;
        if (itemId != other.getItemId()) {
            return false;
        }
        if (folder != other.isFolder()) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(type, other.getType())) {
            return false;
        }
        if (!Objects.equals(creationDate, other.getCreationDate())) {
            return false;
        }
        return Objects.equals(lastModified, other.getLastModified());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "itemId = %d, "
                                 + "name = \"%s\", "
                                 + "title = \"%s\", "
                                 + "type = \"%s\", "
                                 + "creationDate = %s, "
                                 + "lastChanged = %s"
                                 + " }",
                             super.toString(),
                             itemId,
                             name,
                             title,
                             type,
                             Objects.toString(creationDate),
                             Objects.toString(lastModified));
    }

}
