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
package org.librecms.contentsection;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.libreccm.categorization.Category;
import org.libreccm.core.CcmObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FOLDERS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "Folder.rootFolders",
        query = "SELECT f FROM Folder f "
                    + "WHERE f.parentCategory IS NULL "
                    + "  AND f.type = :type")
    ,
    @NamedQuery(
        name = "Folder.findByName",
        query = "SELECT f FROM Folder f WHERE f.name = :name")
})
public class Folder extends Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne
//    @JoinColumn(name = "CONTENT_SECTION_ID")
    @JoinTable(name = "FOLDER_CONTENT_SECTION_MAP", schema = DB_SCHEMA,
               inverseJoinColumns = {
                   @JoinColumn(name = "CONTENT_SECTION_ID")},
               joinColumns = {
                   @JoinColumn(name = "FOLDER_ID")})
    private ContentSection section;

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private FolderType type;

    public ContentSection getSection() {
        return section;
    }

    protected void setSection(final ContentSection section) {
        this.section = section;
    }

    public FolderType getType() {
        return type;
    }

    protected void setType(final FolderType type) {
        this.type = type;
    }

    /**
     * A convenient method for getting all sub folders of folder.
     *
     * @return The sub folders of this folder.
     */
    public List<Folder> getSubFolders() {
        return Collections.unmodifiableList(
            getSubCategories()
                .stream()
                .filter(subCategory -> subCategory instanceof Folder)
                .map(subCategory -> (Folder) subCategory)
                .collect(Collectors.toList()));
    }

    public Folder getParentFolder() {
        final Category parent = getParentCategory();
        if (parent == null) {
            return null;
        } else {
            return (Folder) getParentCategory();
        }
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 29 * hash + Objects.hashCode(type);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Folder)) {
            return false;
        }
        final Folder other = (Folder) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return type == other.getType();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Folder;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", type = %s%s",
                                            type,
                                            data));
    }

}
