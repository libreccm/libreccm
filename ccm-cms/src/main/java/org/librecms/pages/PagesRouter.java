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
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.pagemodel.PageModelManager;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{page:.+}")
public class PagesRouter {

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategoryRepository categoryRepo;
    


    @Inject
    private PageModelManager pageModelManager;

    @Inject
    private PagesRepository siteRepo;

    @Path("/index.{lang}.html")
    @Produces("text/html")
    @Transactional(Transactional.TxType.REQUIRED)
    public String getCategoryIndexPage(
        @Context final UriInfo uriInfo,
        @PathParam("page") final String page,
        @PathParam("lang") final String language,
        @QueryParam("theme") @DefaultValue("--DEFAULT--") final String theme) {

        final String domain = uriInfo.getBaseUri().getHost();

        final Pages pages = siteRepo
            .findPagesForSite(domain)
            .orElseThrow(() -> new NotFoundException(String
            .format("No Pages for domain \"%s\" available.",
                    domain)));

        final Category category = categoryRepo
            .findByPath(pages.getCategoryDomain(), page)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No page for path \"%s\" in site \"%s\"",
            page,
            domain)));


        
        // ToDo Get PageModelBuilder
        // ToDo Build page
        // ToDo Get Theme Processor 
        // ToDo Pass page to theme processor
        // ToDo Return result of ThemeProcessor
        throw new UnsupportedOperationException();
    }

    @Path("/{name}.{lang}.html")
    public String getPage(
        @Context final UriInfo uriInfo,
        @PathParam("page") final String page,
        @PathParam("lang") final String language,
        @QueryParam("theme") @DefaultValue("--DEFAULT--") final String theme) {
        
        throw new UnsupportedOperationException();
    }

}
