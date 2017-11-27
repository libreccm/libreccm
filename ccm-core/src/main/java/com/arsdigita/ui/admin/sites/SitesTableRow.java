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
package com.arsdigita.ui.admin.sites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains all data for one row of the {@link SitesTable}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class SitesTableRow implements Comparable<SitesTableRow>, Serializable {

    private static final long serialVersionUID = -8913595737414248135L;
    
    private String siteId;
    
    private String domainOfSite;

    private boolean defaultSite;

    private String defaultTheme;

    private boolean deletable;

    private List<String> applications;

    protected SitesTableRow() {
        applications = new ArrayList<>();
    }
    
    public String getSiteId() {
        return siteId;
    }
    
    public void setSiteId(final String siteId) {
        this.siteId = siteId;
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

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }

    public List<String> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    protected void setApplications(final List<String> applications) {
        this.applications = new ArrayList<>(applications);
    }
    
    protected void addApplication(final String application) {
        applications.add(application);
    }
    
    @Override
    public int compareTo(final SitesTableRow other) {
        return domainOfSite.compareTo(other.getDomainOfSite());
    }

}
