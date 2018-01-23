/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.category;

import java.util.Locale;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CategoryLocalizationTableRow
    implements Comparable<CategoryLocalizationTableRow> {

    private final Locale locale;
    private String title;
    private String description;

    public CategoryLocalizationTableRow(final Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public int compareTo(final CategoryLocalizationTableRow other) {
        return locale.toString().compareTo(other.getLocale().toString());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(locale);
        hash = 53 * hash + Objects.hashCode(title);
        hash = 53 * hash + Objects.hashCode(description);
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
        if (!(obj instanceof CategoryLocalizationTableRow)) {
            return false;
        }
        final CategoryLocalizationTableRow other
                                               = (CategoryLocalizationTableRow) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        return Objects.equals(locale, other.getLocale());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof CategoryLocalizationTableRow;
    }

    @Override
    public String toString() {
        return toString("");
    }
    
    public String toString(final String data) {
        return String.format("%s{ "
                                 + "locale = \"%s\", "
                                 + "title = \"%s\", "
                                 + "description = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             Objects.toString(locale),
                             title,
                             description,
                             data);
    }

}
