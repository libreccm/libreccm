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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Basic class for files of theme stored in the database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "THEME_FILES", schema = CoreConstants.DB_SCHEMA)
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(name = "ThemeFile.findByUuid",
                query = "SELECT f FROM ThemeFile f "
                            + "WHERE f.uuid = :uuid "
                            + "AND f.version = :version")
    ,
    @NamedQuery(name = "ThemeFile.findByPath",
                query = "SELECT f FROM ThemeFile f "
                            + "WHERE f.path = :path "
                            + "AND f.version = :version")
    ,
    @NamedQuery(name = "ThemeFile.findByNameAndParent",
                query = "SELECT f FROM ThemeFile f "
                            + "WHERE f.name = :name "
                            + "AND f.parent = :parent")
})
public class ThemeFile implements Serializable {

    private static final long serialVersionUID = -622867075507267065L;

    @Id
    @Column(name = "FILE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long fileId;

    @Column(name = "UUID", nullable = false)
    @NotNull
    private String uuid;

    @Column(name = "NAME", length = 255, nullable = false)
    @NotNull
    private String name;

    @Column(name = "VERSION")
    @Enumerated(EnumType.STRING)
    private ThemeVersion version;

    @Column(name = "FILE_PATH", length = 8192, nullable = false)
    @NotNull
    private String path;

    @ManyToOne
    @JoinColumn(name = "PARENT_DIRECTORY_ID")
    private Directory parent;

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    protected void setPath(final String path) {
        this.path = path;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public ThemeVersion getVersion() {
        return version;
    }

    protected void setVersion(final ThemeVersion version) {
        this.version = version;
    }

    public Directory getParent() {
        return parent;
    }

    protected void setParent(final Directory parent) {
        this.parent = parent;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (int) (fileId ^ (fileId >>> 32));
        hash = 37 * hash + Objects.hashCode(name);
        hash = 37 * hash + Objects.hashCode(path);
        hash = 37 * hash + Objects.hashCode(uuid);
        hash = 37 * hash + Objects.hashCode(parent);
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
        if (!(obj instanceof ThemeFile)) {
            return false;
        }
        final ThemeFile other = (ThemeFile) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (fileId != other.getFileId()) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(path, other.getPath())) {
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }
        return Objects.equals(parent, other.getParent());
    }

    public boolean canEqual(final Object other) {
        return other instanceof ThemeFile;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "fileId = %d, "
                                 + "name = \"%s\", "
                                 + "path = \"%s\", "
                                 + "uuid = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             fileId,
                             name,
                             path,
                             uuid,
                             data);
    }

}
