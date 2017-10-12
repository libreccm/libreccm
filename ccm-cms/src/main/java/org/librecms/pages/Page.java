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

import org.libreccm.categorization.Category;
import org.libreccm.pagemodel.PageModel;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
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
@NamedQueries(
    @NamedQuery(
        name = "Page.findForCategory",
        query = "SELECT p FROM Page p WHERE p.category = :category"
    )
)
public class Page implements Serializable {

    private static final long serialVersionUID = 5108486858438122008L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PAGE_ID")
    private long pageId;

    @OneToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "INDEX_PAGE_MODEL_ID")
    private PageModel indexPageModel;

    @ManyToOne
    @JoinColumn(name = "ITEM_PAGE_MODEL_ID")
    private PageModel itemPageModel;

    @Embedded
    @JoinTable(name = "PAGE_THEME_CONFIGURATIONS",
               schema = DB_SCHEMA,
               joinColumns = {
                   @JoinColumn(name = "PAGE_ID")
               })
    private Map<String, ThemeConfiguration> themeConfiguration;

    public long getPageId() {
        return pageId;
    }

    protected void setPageId(final long pageId) {
        this.pageId = pageId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

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

}
