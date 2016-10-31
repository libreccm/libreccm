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
package org.librecms.assets;

import org.librecms.contentsection.Asset;

import java.io.Serializable;
import java.net.URL;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;
import org.libreccm.l10n.LocalizedString;

import static org.librecms.CmsConstants.*;

/**
 * An assets for managing bookmarks which can be used to create links. Useful
 * if the same link appears in multiple places.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "BOOKMARKS", schema = DB_SCHEMA)
@Audited
public class Bookmark extends Asset implements Serializable {

    private static final long serialVersionUID = -2077380735104791483L;

    @Embedded
    @AssociationOverride(
            name = "values",
            joinTable = @JoinTable(name = "BOOKMARK_DESCRIPTIONS",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "ASSET_ID")
                                   }
            )
    )
    private LocalizedString description;

    @Column(name = "URL", length = 2048, nullable = false)
    @NotEmpty
    private URL url;

    public Bookmark() {
        super();
        description = new LocalizedString();
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(final URL url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 19 * hash + Objects.hashCode(description);
        hash = 19 * hash + Objects.hashCode(url);
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

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Bookmark)) {
            return false;
        }
        final Bookmark other = (Bookmark) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        return Objects.equals(url, other.getUrl());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Bookmark;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", description = %s, "
                                                    + "url = %s%s",
                                            Objects.toString(description),
                                            Objects.toString(url),
                                            data));
    }

}
