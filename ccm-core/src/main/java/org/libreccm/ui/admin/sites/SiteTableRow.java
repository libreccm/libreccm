/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.sites;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SiteTableRow {

    private long siteId;

    private String uuid;

    private String domain;

    private boolean defaultSite;

    private String defaultTheme;

    public long getSiteId() {
        return siteId;
    }

    protected void setSiteId(final long siteId) {
        this.siteId = siteId;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getIdentifier() {
        return String.format("ID-%d", siteId);
    }

    public String getDomain() {
        return domain;
    }

    protected void setDomain(final String domain) {
        this.domain = domain;
    }

    public boolean isDefaultSite() {
        return defaultSite;
    }

    protected void setDefaultSite(final boolean defaultSite) {
        this.defaultSite = defaultSite;
    }

    public String getDefaultTheme() {
        return defaultTheme;
    }

    protected void setDefaultTheme(final String defaultTheme) {
        this.defaultTheme = defaultTheme;
    }

}
