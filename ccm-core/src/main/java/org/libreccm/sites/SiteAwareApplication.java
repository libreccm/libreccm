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
package org.libreccm.sites;

import static org.libreccm.core.CoreConstants.*;

import org.libreccm.web.CcmApplication;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "SITE_AWARE_APPLICATIONS", schema = DB_SCHEMA)
public class SiteAwareApplication extends CcmApplication {

    private static final long serialVersionUID = -8892544588904174406L;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "SITE_ID")
    private Site site;

    public Site getSite() {
        return site;
    }

    protected void setSite(final Site site) {
        this.site = site;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        if (site != null) {
            hash = 59 * hash + Objects.hashCode(site.getDomainOfSite());
            hash = 59 * hash + Objects.hashCode(site.isDefaultSite());
            hash = 59 * hash + Objects.hashCode(site.getDefaultTheme());
        }
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
        if (!(obj instanceof SiteAwareApplication)) {
            return false;
        }
        final SiteAwareApplication other = (SiteAwareApplication) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (site == null && other.getSite() != null) {
            return false;
        } else if (site != null && other.getSite() == null) {
            return false;
        } else {
            if (!Objects.equals(site.getDomainOfSite(),
                                other.getSite().getDomainOfSite())) {
                return false;
            }
            if (site.isDefaultSite() != other.getSite().isDefaultSite()) {
                return false;
            }
            if (!Objects.equals(site.getDefaultTheme(),
                                other.getSite().getDefaultTheme())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof SiteAwareApplication;
    }

    @Override
    public String toString(final String data) {

        if (site == null) {
            return super.toString(String.format(", site = null%d", data));
        } else {
            return super.toString(String.format(", site = { "
                                                    + "domainOfSite = \"%s\", "
                                                    + "isDefaultSite = %b,"
                                                    + "defaultTheme = \"%s\" }%s",
                                                site.getDomainOfSite(),
                                                site.isDefaultSite(),
                                                site.getDefaultTheme(),
                                                data));
        }
    }

}
