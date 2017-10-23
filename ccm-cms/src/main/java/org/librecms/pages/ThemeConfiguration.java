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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Configuration for the {@link Page} and a {@link Site}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Embeddable
public class ThemeConfiguration implements Serializable {

    private static final long serialVersionUID = -989896906728810984L;

    /**
     * The theme associated with this configuration.
     */
    @Column(name = "THEME")
    private String theme;

    /**
     * The template provided by the theme to use for index pages.
     */
    @Column(name = "INDEX_PAGE_TEMPLATE")
    private String indexPageTemplate;

    /**
     * The template provided by the theme to use for item pages.
     */
    @Column(name = "ITEM_PAGE_TEMPLATE")
    private String itemPageTemplate;

    public String getTheme() {
        return theme;
    }

    public void setTheme(final String theme) {
        this.theme = theme;
    }

    public String getIndexPageTemplate() {
        return indexPageTemplate;
    }

    public void setIndexPageTemplate(final String indexPageTemplate) {
        this.indexPageTemplate = indexPageTemplate;
    }

    public String getItemPageTemplate() {
        return itemPageTemplate;
    }

    public void setItemPageTemplate(final String itemPageTemplate) {
        this.itemPageTemplate = itemPageTemplate;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(theme);
        hash = 59 * hash + Objects.hashCode(indexPageTemplate);
        hash = 59 * hash + Objects.hashCode(itemPageTemplate);
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
        if (!(obj instanceof ThemeConfiguration)) {
            return false;
        }
        final ThemeConfiguration other = (ThemeConfiguration) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(theme, other.getTheme())) {
            return false;
        }
        if (!Objects.equals(indexPageTemplate, other.getIndexPageTemplate())) {
            return false;
        }
        return Objects.equals(itemPageTemplate, other.getItemPageTemplate());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ThemeConfiguration;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "theme = \"%s\","
                                 + "indexPageTemplate = \"%s\", "
                                 + "itemPageTemplate = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             theme,
                             indexPageTemplate,
                             itemPageTemplate,
                             data);
    }

}
