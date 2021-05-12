/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contenttypes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contenttypes.Article;
import org.librecms.ui.contentsections.ContentSectionsUi;
import org.librecms.ui.contentsections.ItemPermissionChecker;
import org.librecms.ui.contentsections.documents.MvcAuthoringSteps;

import java.util.Locale;
import java.util.StringTokenizer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAuthoringSteps.PATH_PREFIX + "article-text-resources")
public class MvcArticleTextBodyStepResources {

    /**
     * Used for retrieving and saving the article.
     */
    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private ItemPermissionChecker itemPermissionChecker;

    @GET
    @Path("/variants/{locale}/wordcount")
    @Produces(MediaType.TEXT_HTML)
    @Transactional(Transactional.TxType.REQUIRED)
    public long getWordCount(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPathParam,
        @PathParam("locale") final String localeParam
    ) {
         final ContentSection contentSection = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new NotFoundException()
            );

        final ContentItem document = itemRepo
            .findByPath(contentSection, documentPathParam)
            .orElseThrow(
                () -> new NotFoundException()
            );

        if (!(document instanceof Article)) {
            throw new NotFoundException();
        }

        final Article article = (Article) document;
        if (itemPermissionChecker.canEditItem(article)) {
            final String text = article
                .getText()
                .getValue(new Locale(localeParam));
            final Document jsoupDoc = Jsoup.parseBodyFragment(text);
            return new StringTokenizer(jsoupDoc.body().text()).countTokens();
        } else {
            throw new ForbiddenException();
        }
    }
    
    @GET
    @Path("/variants/{locale}")
    @Produces(MediaType.TEXT_HTML)
    @Transactional(Transactional.TxType.REQUIRED)
    public String viewTextValue(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPathParam,
        @PathParam("locale") final String localeParam
    ) {
        final ContentSection contentSection = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new NotFoundException()
            );

        final ContentItem document = itemRepo
            .findByPath(contentSection, documentPathParam)
            .orElseThrow(
                () -> new NotFoundException()
            );

        if (!(document instanceof Article)) {
            throw new NotFoundException();
        }

        final Article article = (Article) document;
        if (itemPermissionChecker.canEditItem(article)) {
            return article.getText().getValue(new Locale(localeParam));
        } else {
            throw new ForbiddenException();
        }
    }

}
