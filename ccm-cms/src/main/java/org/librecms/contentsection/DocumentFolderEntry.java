/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.contentsection;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DocumentFolderEntry {
    
    private long entryId;
    
    private String entryUuid;
    
    private String displayName;
    
    private String itemClass;
    
    private Date creationDate;
    
    private Date lastModified;
    
    private String version;
    
    private boolean folder;
    
    public DocumentFolderEntry() {
        
    }
    
    public DocumentFolderEntry(
        final long entryId,
        final String entryUuid,
        final String displayName,
        final String itemClass,
        final Date creationDate,
        final Date lastModified,
        final String version,
        final boolean folder
    ) {
        this.entryId = entryId;
        this.entryUuid = entryUuid;;
        this.displayName = displayName;
        this.itemClass = itemClass;
        this.creationDate = creationDate;
        this.lastModified = lastModified;
        this.version = version;
        this.folder = folder;
    }

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(final long entryId) {
        this.entryId = entryId;
    }

    public String getEntryUuid() {
        return entryUuid;
    }

    public void setEntryUuid(final String entryUuid) {
        this.entryUuid = entryUuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getItemClass() {
        return itemClass;
    }

    public void setItemClass(final String itemClass) {
        this.itemClass = itemClass;
    }

    public Date getCreationDate() {
        return new Date(creationDate.getTime());
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = new Date(creationDate.getTime());
    }

    public Date getLastModified() {
        return new Date(lastModified.getTime());
    }

    public void setLastModified(final Date lastModified) {
        this.lastModified = new Date(lastModified.getTime());
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(final boolean folder) {
        this.folder = folder;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (int) (entryId ^ (entryId >>> 32));
        hash = 29 * hash + Objects.hashCode(entryUuid);
        hash = 29 * hash + Objects.hashCode(displayName);
        hash = 29 * hash + Objects.hashCode(itemClass);
        hash = 29 * hash + Objects.hashCode(creationDate);
        hash = 29 * hash + Objects.hashCode(lastModified);
        hash = 29 * hash + Objects.hashCode(version);
        hash = 29 * hash + (folder ? 1 : 0);
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
        if (!(obj instanceof DocumentFolderEntry)) {
            return false;
        }
        final DocumentFolderEntry other = (DocumentFolderEntry) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (entryId != other.getEntryId()) {
            return false;
        }
        if (folder != other.isFolder()) {
            return false;
        }
        if (!Objects.equals(entryUuid, other.getEntryUuid())) {
            return false;
        }
        if (!Objects.equals(itemClass, other.getItemClass())) {
            return false;
        }
        if (!Objects.equals(displayName, other.getDisplayName())) {
            return false;
        }
        return Objects.equals(version, other.getVersion());
    }
    
    public boolean canEqual(final Object obj) {
        return obj instanceof DocumentFolderEntry;
    }
    
}
