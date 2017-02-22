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
package com.arsdigita.cms.ui.folder;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple data transfer object containing the data required by the
 * {@link FolderBrowser} to display its rows. Used by the
 * {@link FolderBrowserTableModelBuilder} to transfer the data from the
 * {@link FolderBrowserController} to the {@link FolderBrowserTableModel}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class FolderBrowserTableRow {

    private long objectId;
    private String objectUuid;
    private String name;
    private List<Locale> languages;
    private String title;
    private String typeLabelBundle;
    private String typeLabelKey;
    private Date created;
    private Date lastModified;
    private boolean deletable;
    private boolean folder;

    public long getObjectId() {
        return objectId;
    }

    protected void setObjectId(final long objectId) {
        this.objectId = objectId;
    }

    public String getObjectUuid() {
        return objectUuid;
    }

    protected void setObjectUuid(final String objectUuid) {
        this.objectUuid = objectUuid;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public List<Locale> getLanguages() {
        return Collections.unmodifiableList(languages);
    }

    protected void setLanguages(final List<Locale> languages) {
        this.languages = languages;
    }

    public String getTitle() {
        return title;
    }

    protected void setTitle(final String title) {
        this.title = title;
    }

    public String getTypeLabelBundle() {
        return typeLabelBundle;
    }

    protected void setTypeLabelBundle(final String typeLabelBundle) {
        this.typeLabelBundle = typeLabelBundle;
    }

    public String getTypeLabelKey() {
        return typeLabelKey;
    }

    protected void setTypeLabelKey(final String typeLabelKey) {
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

    protected void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isFolder() {
        return folder;
    }

    protected void setFolder(final boolean folder) {
        this.folder = folder;
    }

}
