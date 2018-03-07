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
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "THEMES", schema = CoreConstants.DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "Theme.findByUuid",
                query = "SELECT t FROM Theme t "
                            + "WHERE t.uuid = :uuid "
                            + "AND t.version = :version")
    ,
    @NamedQuery(name = "Theme.findByName",
                query = "SELECT t FROM Theme t "
                + "WHERE t.name = :name "
                + "AND t.version = :version")
})
public class Theme implements Serializable {

    private static final long serialVersionUID = -5229641158755727717L;

    @Id
    @Column(name = "THEME_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long themeId;

    @Column(name = "NAME", length = 255)
    private String name;

    @Column(name = "UUID")
    private String uuid;

    @Column(name = "VERSION")
    @Enumerated(EnumType.STRING)
    private ThemeVersion version;

    @OneToOne
    @JoinColumn(name = "ROOT_DIRECTORY_ID")
    private Directory rootDirectory;

    public long getThemeId() {
        return themeId;
    }

    protected void setThemeId(final long themeId) {
        this.themeId = themeId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public Directory getRootDirectory() {
        return rootDirectory;
    }

    protected void setRootDirectory(final Directory rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (themeId ^ (themeId >>> 32));
        hash = 79 * hash + Objects.hashCode(name);
        hash = 79 * hash + Objects.hashCode(uuid);
        hash = 79 * hash + Objects.hashCode(version);
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
        if (!(obj instanceof Theme)) {
            return false;
        }
        final Theme other = (Theme) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (themeId != other.getThemeId()) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }
        return version == other.getVersion();
    }

    public boolean canEqual(final Object other) {
        return other instanceof Theme;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "themeId = %d, "
                                 + "name = \"%s\", "
                                 + "uuid = %s, "
                                 + "version = %s%s"
                                 + " }",
                             super.toString(),
                             themeId,
                             name,
                             uuid,
                             Objects.toString(version),
                             data);
    }

}
