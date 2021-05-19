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
package org.librecms.ui.contentsections.assets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.librecms.assets.SideNote;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionsUi;

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
@Path(MvcAssetEditSteps.PATH_PREFIX + "sidenote-edit-resources")
public class SideNoteEditStepResources {

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    @GET
    @Path("/text/variants/wordcount/{locale}")
    @Produces(MediaType.TEXT_HTML)
    @Transactional(Transactional.TxType.REQUIRED)
    public String getWordCount(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPathParam,
        @PathParam("locale") final String localeParam
    ) {
        final ContentSection contentSection = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new NotFoundException()
            );

        final Asset asset = assetRepo
            .findByPath(contentSection, assetPathParam)
            .orElseThrow(() -> new NotFoundException());

        if (!(asset instanceof SideNote)) {
            throw new NotFoundException();
        }

        final SideNote sideNote = (SideNote) asset;
        if (assetPermissionsChecker.canEditAsset(asset)) {
            final String text = sideNote
                .getText()
                .getValue(new Locale(localeParam));
            final Document jsoupDoc = Jsoup.parseBodyFragment(text);
            final long result = new StringTokenizer(
                jsoupDoc.body().text()
            ).countTokens();
            return Long.toString(result);
        } else {
            throw new ForbiddenException();
        }
    }

    @GET
    @Path("/text/variants/{locale}")
    @Produces(MediaType.TEXT_HTML)
    @Transactional(Transactional.TxType.REQUIRED)
    public String viewTextValue(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPathParam,
        @PathParam("locale") final String localeParam
    ) {
        final ContentSection contentSection = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new NotFoundException()
            );

        final Asset asset = assetRepo
            .findByPath(contentSection, assetPathParam)
            .orElseThrow(() -> new NotFoundException());

        if (!(asset instanceof SideNote)) {
            throw new NotFoundException();
        }

        final SideNote sideNote = (SideNote) asset;
        if (assetPermissionsChecker.canEditAsset(asset)) {
            return sideNote.getText().getValue(new Locale(localeParam));
        } else {
            throw new ForbiddenException();
        }
    }
}