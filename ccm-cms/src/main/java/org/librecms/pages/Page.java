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

import org.libreccm.core.CcmObject;
import org.libreccm.pagemodel.PageModel;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;

import java.util.Map;
import java.util.Objects;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;
import static org.librecms.pages.PagesConstants.*;

/**
 * A CMS page is a container which contains several data how a page is displayed.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PAGES", schema = DB_SCHEMA)
@NamedQueries(
    @NamedQuery(
        name = "Page.findForCategory",
        query = "SELECT p "
                    + "FROM Page p "
                    + "JOIN p.categories c "
                    + "WHERE c.category = :category "
                    + "AND C.type = '" + CATEGORIZATION_TYPE_PAGE_CONF + "'"
    )
)
public class Page extends CcmObject implements Serializable {

    private static final long serialVersionUID = 5108486858438122008L;

    /**
     * The page model for the index item of the associated category.
     */
    @ManyToOne
    @JoinColumn(name = "INDEX_PAGE_MODEL_ID")
    private PageModel indexPageModel;

    /**
     * The page model for other items in the associated category.
     */
    @ManyToOne
    @JoinColumn(name = "ITEM_PAGE_MODEL_ID")
    private PageModel itemPageModel;

    /**
     * The configurations for this page.
     */
    @ElementCollection
    @CollectionTable(name = "PAGE_THEME_CONFIGURATIONS", 
                     schema = DB_SCHEMA,
                     joinColumns = {
                         @JoinColumn(name = "PAGE_ID")
                     })
    @MapKeyColumn(name = "THEME")
    private Map<String, ThemeConfiguration> themeConfiguration;

    public PageModel getIndexPageModel() {
        return indexPageModel;
    }

    public void setIndexPageModel(final PageModel indexPageModel) {
        this.indexPageModel = indexPageModel;
    }

    public PageModel getItemPageModel() {
        return itemPageModel;
    }

    public void setItemPageModel(final PageModel itemPageModel) {
        this.itemPageModel = itemPageModel;
    }

    public Map<String, ThemeConfiguration> getThemeConfiguration() {
        return Collections.unmodifiableMap(themeConfiguration);
    }

    public void setThemeConfiguration(
        final Map<String, ThemeConfiguration> themeConfiguration) {

        this.themeConfiguration = new HashMap<>(themeConfiguration);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(indexPageModel);
        hash = 47 * hash + Objects.hashCode(itemPageModel);
        hash = 47 * hash + Objects.hashCode(themeConfiguration);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Page)) {
            return false;
        }
        final Page other = (Page) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(indexPageModel, other.getIndexPageModel())) {
            return false;
        }
        if (!Objects.equals(itemPageModel, other.getItemPageModel())) {
            return false;
        }
        return Objects.equals(themeConfiguration, other.getThemeConfiguration());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Page;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", indexPageModel = %s, "
                                                + "itemPageModel = %s, "
                                                + "themeConfiguration = %s%s",
                                            Objects.toString(indexPageModel),
                                            Objects.toString(itemPageModel),
                                            Objects.toString(themeConfiguration),
                                            data));
    }

}
