/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.librecms.contenttypes;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;
import org.libreccm.l10n.LocalizedString;

import static org.libreccm.core.CoreConstants.*;

/**
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "NEWS", schema = DB_SCHEMA)
public class News extends GenericArticle implements Serializable {

    private static final long serialVersionUID = -4939565845920227974L;

    /**
     * Short description of the news, usually used as teaser.
     */
    @Embedded
    @AssociationOverride(
            name = "VALUES",
            joinTable = @JoinTable(name = "NEWS_LEADS",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "OBJECT_ID")}
            ))
    private LocalizedString lead;

    /**
     * Release date of the news
     */
    @Column(name = "NEWS_DATE", nullable = false)
    @NotEmpty
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    /**
     * Should the news item be published on the portal homepage? Will be used by
     * the function recentItems.
     */
    @Column(name = "HOMEPAGE")
    @NotEmpty
    private boolean homepage;

    public LocalizedString getLead() {
        return lead;
    }

    public void setLead(final LocalizedString lead) {
        this.lead = lead;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(final Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isHomepage() {
        return homepage;
    }

    public void setHomepage(final boolean homepage) {
        this.homepage = homepage;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 11 * hash + Objects.hashCode(this.lead);
        hash = 11 * hash + Objects.hashCode(this.releaseDate);
        hash = 11 * hash + (this.homepage ? 1 : 0);
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
        if (!(obj instanceof News)) {
            return false;
        }
        final News other = (News) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (homepage != other.isHomepage()) {
            return false;
        }
        if (!Objects.equals(lead, other.getLead())) {
            return false;
        }
        return Objects.equals(releaseDate, other.getReleaseDate());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof News;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", lead = \"%s\", "
                                                    + "releaseDate = %tF %<tT, "
                                                    + "homepage = %b%d",
                                            Objects.toString(lead),
                                            releaseDate,
                                            homepage,
                                            data));
    }
}
