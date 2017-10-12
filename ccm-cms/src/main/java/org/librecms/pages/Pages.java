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
package org.librecms.pages;

import org.libreccm.categorization.Domain;
import org.libreccm.sites.Site;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.Objects;

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
@Table(name = "PAGES", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "Pages.findForSite",
        query = "SELECT p FROM Pages p JOIN p.site s "
                    + "WHERE s.domainOfSite = :domain")
    ,
    @NamedQuery(
        name = "Pages.findForDefaultSite",
        query = "SELECT p FROM Pages p JOIN p.site s "
                    + "WHERE s.defaultSite = true"
    )
    ,
    @NamedQuery(
        name = "Pages.availableForSite",
        query = "SELECT (CASE WHEN COUNT(s) > 0 THEN true ELSE false END) "
                    + "FROM Pages p JOIN p.site s "
                    + "WHERE s.domainOfSite = :domain")
    ,
    @NamedQuery(
        name = "Pages.availableForDefaultSite",
        query = "SELECT (CASE WHEN COUNT(p) > 0 THEN true ELSE false END) "
                    + "FROM Pages p JOIN p.site s "
                    + "WHERE s.defaultSite = true")
})
public class Pages extends CcmApplication implements Serializable {

    private static final long serialVersionUID = -352205318143692477L;

    @OneToOne
    @JoinColumn(name = "SITE_ID")
    private Site site;

    @OneToOne
    @JoinColumn(name = "CATEGORY_DOMAIN_ID")
    private Domain categoryDomain;

    public Site getSite() {
        return site;
    }

    protected void setSite(final Site site) {
        this.site = site;
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
        hash = 17 * hash + Objects.hashCode(categoryDomain);
        hash = 17 * hash + Objects.hashCode(site);
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

        if (!(obj instanceof Pages)) {
            return false;
        }

        final Pages other = (Pages) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(site, other.getSite())) {
            return false;
        }

        return Objects.equals(categoryDomain, other.getCategoryDomain());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Pages;
    }

    @Override
    public String toString(final String data) {

        return super.toString(String.format(
            ", site = \"%s\"%s",
            Objects.toString(site),
            data
        ));
    }

}
