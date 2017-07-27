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
package com.arsdigita.cms.ui.assets;

import java.util.Date;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AssetFolderBrowserTableRow {
    
    private long objectId;
    private String objectUuid;
    private String name;
    private String title;
    private String thumbnailUrl;
    private String typeLabelBundle;
    private String typeLabelKey;
    private Date created;
    private Date lastModified;
    private boolean deletable;
    private boolean folder;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(final long objectId) {
        this.objectId = objectId;
    }

    public String getObjectUuid() {
        return objectUuid;
    }

    public void setObjectUuid(final String objectUuid) {
        this.objectUuid = objectUuid;
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
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(final String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTypeLabelBundle() {
        return typeLabelBundle;
    }

    public void setTypeLabelBundle(final String typeLabelBundle) {
        this.typeLabelBundle = typeLabelBundle;
    }

    public String getTypeLabelKey() {
        return typeLabelKey;
    }

    public void setTypeLabelKey(final String typeLabelKey) {
        this.typeLabelKey = typeLabelKey;
    }

      public Date getCreated() {
        if (created == null) {
            return null;
        } else {
            return new Date(created.getTime());
        }
    }

    protected void setCreated(final Date created) {
        if (created == null) {
            this.created = null;
        } else {
            this.created = new Date(created.getTime());
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

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(final boolean folder) {
        this.folder = folder;
    }
    
    
    
    
}
