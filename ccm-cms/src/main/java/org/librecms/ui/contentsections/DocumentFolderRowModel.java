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
package org.librecms.ui.contentsections;

import java.util.Collections;
import java.util.SortedSet;

/**
 * Model for a row in the document folder browser.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DocumentFolderRowModel {

    /**
     * ISO formatted creation date of the item represented by the row. May be
     * {@code null}.
     */
    private String created;

    /**
     * Can the object represented by the row by deleted?
     */
    private boolean deletable;

    /**
     * Does the row represent a folder?
     */
    private boolean folder;

    /**
     * The the row represents a folder: The path of the folder.
     */
    private String folderPath;

    /**
     * If the row representents a {@link ContentItem}: The languages in which
     * the item is available.
     */
    private SortedSet<String> languages;

    /**
     * ISO formatted date of the last edit of the item represented by the row.
     * May be {@code null}.
     */
    private String lastEdited;

    /**
     * ISO formatted date of the last publication of the item represented by the
     * row. May be {@code null}.
     */
    private boolean lastEditPublished;

    /**
     * The name of the entry.
     */
    private String name;

    /**
     * The row represents a none CMS object does has been assigned to the
     * category backing the folder.
     */
    private boolean noneCmsObject;

    /**
     * The title of the item represented by the row.
     */
    private String title;

    /**
     * The type of the item represented by the row.
     */
    private String type;

    /**
     * The {@link DocumentPermissionsModel} for the entry.
     */
    private DocumentPermissionsModel permissions;

    public String getCreated() {
        return created;
    }

    public void setCreated(final String created) {
        this.created = created;
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

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(final String folderPath) {
        this.folderPath = folderPath;
    }

    public void setFolder(final boolean folder) {
        this.folder = folder;
    }

    public SortedSet<String> getLanguages() {
        return Collections.unmodifiableSortedSet(languages);
    }

    public String getLanguagesAsString() {
        return String.join(", ", languages);
    }

    public void setLanguages(final SortedSet<String> languages) {
        this.languages = languages;
    }

    public String getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(final String lastEdited) {
        this.lastEdited = lastEdited;
    }

    public boolean isLastEditPublished() {
        return lastEditPublished;
    }

    public void setLastEditPublished(final boolean lastEditPublished) {
        this.lastEditPublished = lastEditPublished;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isNoneCmsObject() {
        return noneCmsObject;
    }

    public void setNoneCmsObject(boolean noneCmsObject) {
        this.noneCmsObject = noneCmsObject;
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

    public DocumentPermissionsModel getPermissions() {
        return permissions;
    }

    public void setPermissions(
        final DocumentPermissionsModel permissions
    ) {
        this.permissions = permissions;
    }

}
