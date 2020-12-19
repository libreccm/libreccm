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
package org.libreccm.ui.admin.themes;

import org.libreccm.theming.ThemeVersion;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Data of theme for displaying in the table of available themes.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ThemesTableRow implements Comparable<ThemesTableRow>, Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String type;

    private ThemeVersion themeVersion;

    private String version;

    private String provider;

    private String title;

    private String description;

    private boolean editable;

    private boolean publishable;

    private boolean published;

    public String getName() {
        return name;
    }

    protected void setName(final String name) {
        this.name = name;
    }

    public ThemeVersion getThemeVersion() {
        return themeVersion;
    }

    protected void setThemeVersion(final ThemeVersion themeVersion) {
        this.themeVersion = themeVersion;
    }

    public String getType() {
        return type;
    }

    protected void setType(final String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    protected void setVersion(final String version) {
        this.version = version;
    }

    public String getProvider() {
        return provider;
    }

    protected void setProvider(final String provider) {
        this.provider = provider;
    }

    public String getTitle() {
        return title;
    }

    protected void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(final String description) {
        this.description = description;
    }

    public boolean isEditable() {
        return editable;
    }

    protected void setEditable(final boolean editable) {
        this.editable = editable;
    }

    public boolean isPublishable() {
        return publishable;
    }

    protected void setPublishable(final boolean publishable) {
        this.publishable = publishable;
    }

    public boolean isPublished() {
        return published;
    }

    protected void setPublished(final boolean published) {
        this.published = published;
    }

    @Override
    public int compareTo(final ThemesTableRow other) {
        return Comparator.nullsFirst(Comparator
            .comparing(ThemesTableRow::getTitle)
            .thenComparing(ThemesTableRow::getName)
            .thenComparing(ThemesTableRow::getVersion)
            .thenComparing(ThemesTableRow::getType)
            .thenComparing(ThemesTableRow::getProvider)
            .thenComparing(ThemesTableRow::isPublishable)
            .thenComparing(ThemesTableRow::isPublished)
            .thenComparing(ThemesTableRow::isEditable)
        ).compare(this, other);
    }

}
