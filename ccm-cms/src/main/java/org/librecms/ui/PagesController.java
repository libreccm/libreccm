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
package org.librecms.ui;

import com.vaadin.cdi.ViewScoped;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;
import org.librecms.pages.Page;
import org.librecms.pages.PageManager;
import org.librecms.pages.PageRepository;
import org.librecms.pages.Pages;
import org.librecms.pages.PagesManager;
import org.librecms.pages.PagesRepository;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class PagesController {

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private DomainRepository domainRepo;

    @Inject
    private PageModelSelectDataProvider pageModelSelectDataProvider;

    @Inject
    private PageManager pageManager;

    @Inject
    private PageRepository pageRepo;

    @Inject
    private PagesCategoryTreeDataProvider pagesCategoryTreeDataProvider;

    @Inject
    private PagesEditorDomainSelectDataProvider pagesEditorDomainSelectDataProvider;

    @Inject
    private PagesEditorSiteSelectDataProvider pagesEditorSiteSelectDataProvider;

    @Inject
    private PagesGridDataProvider pagesGridDataProvider;

    @Inject
    private PagesManager pagesManager;

    @Inject
    private PagesRepository pagesRepo;

    @Inject
    private SiteRepository siteRepo;

    protected PageModelSelectDataProvider getPageModelSelectDataProvider() {
        return pageModelSelectDataProvider;
    }

    protected PageManager getPageManager() {
        return pageManager;
    }

    protected PageRepository getPageRepo() {
        return pageRepo;
    }

    protected PagesCategoryTreeDataProvider getPagesCategoryTreeDataProvider() {
        return pagesCategoryTreeDataProvider;
    }

    protected PagesEditorDomainSelectDataProvider getPagesEditorDomainSelectDataProvider() {
        return pagesEditorDomainSelectDataProvider;
    }

    protected PagesEditorSiteSelectDataProvider getPagesEditorSiteSelectDataProvider() {
        return pagesEditorSiteSelectDataProvider;
    }

    protected PagesGridDataProvider getPagesGridDataProvider() {
        return pagesGridDataProvider;
    }

    protected PagesManager getPagesManager() {
        return pagesManager;
    }

    protected PagesRepository getPagesRepo() {
        return pagesRepo;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected Optional<Page> findPage(final Category category) {

        Objects.requireNonNull(category);

        final Category theCategory = categoryRepo
            .findById(category.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the database.",
                    category.getObjectId())));

        return pageRepo.findPageForCategory(theCategory);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected Pages createPages(final String name,
                                final Site site,
                                final Domain domain) {

        Objects.requireNonNull(name);
        Objects.requireNonNull(site);
        Objects.requireNonNull(domain);

        if (name.isEmpty()
                || name.matches("\\s*")) {

            throw new IllegalArgumentException(
                "The name of a Pages instance can't be empty.");
        }

        final Site forSite = siteRepo
            .findById(site.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Site with ID %d in the database.",
                    site.getObjectId())));

        final Domain withDomain = domainRepo
            .findById(domain.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Domain with ID %d in the database.",
                    domain.getObjectId())));

        return pagesManager.createPages(name, forSite, withDomain);
    }

}
