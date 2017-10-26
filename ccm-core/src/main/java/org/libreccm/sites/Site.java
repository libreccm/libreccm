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

import org.libreccm.core.CcmObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "SITES", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "Site.findByDomain",
        query = "SELECT s FROM Site s WHERE s.domainOfSite = :domain"
    )
    ,
    @NamedQuery(
        name = "Site.findDefaultSite",
        query = "SELECT s FROM Site s WHERE s.defaultSite = true"
    )
    ,
    @NamedQuery(
        name = "Site.hasSiteForDomain",
        query = "SELECT (CASE WHEN COUNT(s) > 0 THEN true ELSE false END) "
                    + "FROM Site s "
                    + "WHERE s.domainOfSite = :domain")
})
public class Site extends CcmObject {

    private static final long serialVersionUID = 7993361616050713139L;

    @Column(name = "DOMAIN_OF_SITE", unique = true)
    private String domainOfSite;

    @Column(name = "DEFAULT_SITE")
    private boolean defaultSite;

    @Column(name = "DEFAULT_THEME")
    private String defaultTheme;

    @OneToMany(mappedBy = "site")
    private List<SiteAwareApplication> applications;
    
    public Site() {
        super();
        applications = new ArrayList<>();
    }
    
    public String getDomainOfSite() {
        return domainOfSite;
    }

    public void setDomainOfSite(final String domainOfSite) {
        this.domainOfSite = domainOfSite;
    }

    public boolean isDefaultSite() {
        return defaultSite;
    }

    public void setDefaultSite(final boolean defaultSite) {
        this.defaultSite = defaultSite;
    }

    public String getDefaultTheme() {
        return defaultTheme;
    }

    public void setDefaultTheme(final String defaultTheme) {
        this.defaultTheme = defaultTheme;
    }
    
    public List<SiteAwareApplication> getApplications() {
        return Collections.unmodifiableList(applications);
    }
    
    protected void setApplications(final List<SiteAwareApplication> applications) {
        this.applications = new ArrayList<>(applications);
    }

    protected void addApplication(final SiteAwareApplication application) {
        applications.add(application);
    }
    
    protected void removeApplication(final SiteAwareApplication application) {
        applications.remove(application);
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(domainOfSite);
        hash = 67 * hash + (defaultSite ? 1 : 0);
        hash = 67 * hash + Objects.hashCode(defaultTheme);
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

        if (!super.canEqual(obj)) {
            return false;
        }

        if (!(obj instanceof Site)) {
            return false;
        }
        final Site other = (Site) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        if (defaultSite != other.isDefaultSite()) {
            return false;
        }
        if (!Objects.equals(domainOfSite, other.getDomainOfSite())) {
            return false;
        }
        return Objects.equals(defaultTheme, other.getDefaultTheme());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Site;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", domainOfSite = \"%s\", "
                                                + "defaultSite = %b, "
                                                + "defaultTheme = \"%s\"%s",
                                            domainOfSite,
                                            defaultSite,
                                            defaultTheme,
                                            data));
    }

}
