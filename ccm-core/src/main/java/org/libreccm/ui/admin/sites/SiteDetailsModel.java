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

import org.libreccm.sites.Site;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.ui.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SiteDetailsModel")
public class SiteDetailsModel {

    private long siteId;

    private String uuid;

    private String identifier;

    private String domain;

    private boolean defaultSite;

    private String defaultTheme;

    private Map<String, String> availableThemes;

    private List<Message> messages;

    public long getSiteId() {
        return siteId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isNew() {
        return siteId == 0;
    }

    public String getDomain() {
        return domain;
    }

    public boolean isDefaultSite() {
        return defaultSite;
    }

    public String getDefaultTheme() {
        return defaultTheme;
    }

    public Map<String, String> getAvailableThemes() {
        return Collections.unmodifiableMap(availableThemes);
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    protected void addMessage(final Message message) {
        messages.add(message);
    }

    protected void setSite(final Site site) {
        Objects.requireNonNull(site);

        siteId = site.getObjectId();
        uuid = site.getUuid();
        identifier = String.format("ID-%d", siteId);
        domain = site.getDomainOfSite();
        defaultSite = site.isDefaultSite();
        defaultTheme = site.getDefaultTheme();
    }

    protected void setAvailableThemes(final List<ThemeInfo> availableThemes) {
        this.availableThemes = availableThemes
            .stream()
            .collect(
                Collectors.toMap(
                    themeInfo -> themeInfo.getName(),
                    themeInfo -> themeInfo.getName()
                )
            );
    }

}
