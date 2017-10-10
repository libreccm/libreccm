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
package org.librecms.sites;

import org.libreccm.categorization.Domain;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "SITES", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "Site.findByName",
        query = "SELECT s FROM Site s WHERE s.name = :name"
    )
})
public class Site extends CcmApplication implements Serializable {

    private static final long serialVersionUID = -352205318143692477L;

    /**
     * The domain of the site.
     */
    @Column(name = "NAME", unique = true)
    private String name;

    /**
     * Should this be the default site which is used when there is no matching
     * site?
     */
    @Column(name = "DEFAULT_SITE")
    private boolean defaultSite;

    @OneToOne
    @JoinColumn(name = "CATEGORY_DOMAIN_ID")
    private Domain categoryDomain;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isDefaultSite() {
        return defaultSite;
    }

    public void setDefaultSite(boolean defaultSite) {
        this.defaultSite = defaultSite;
    }

    public Domain getCategoryDomain() {
        return categoryDomain;
    }

    protected void setCategoryDomain(Domain categoryDomain) {
        this.categoryDomain = categoryDomain;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(name);
        hash = 17 * hash + (defaultSite ? 1 : 0);
        hash = 17 * hash + Objects.hashCode(categoryDomain);
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

        if (!(obj instanceof Site)) {
            return false;
        }

        final Site other = (Site) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (defaultSite != other.isDefaultSite()) {
            return false;
        }

        return Objects.equals(categoryDomain, other.getCategoryDomain());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Site;
    }

    @Override
    public String toString(final String data) {

        return super.toString(String.format(
            ", name = \"%s\","
                + "defaultSite = %b%s",
            name,
            defaultSite,
            data
        ));
    }

}
