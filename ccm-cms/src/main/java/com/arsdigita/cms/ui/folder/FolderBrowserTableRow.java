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

import org.libreccm.l10n.LocalizedString;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class FolderBrowserTableRow {
    
    private long objectId;
    private String objectUuid;
    private String name;
    private List<Locale> languages;
    private String title;
    private String type;
    private Date created;
    private Date lastModified;
    private boolean deletable;

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

    public String getType() {
        return type;
    }

    protected void setType(final String type) {
        this.type = type;
    }

    public Date getCreated() {
        return new Date(created.getTime());
    }

    protected void setCreated(final Date created) {
        this.created = new Date(created.getTime());
    }

    public Date getLastModified() {
        return new Date(lastModified.getTime());
    }

    protected void setLastModified(final Date lastModified) {
        this.lastModified = new Date(lastModified.getTime());
    }

    public boolean isDeletable() {
        return deletable;
    }

    protected void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }
    
    
    
}
