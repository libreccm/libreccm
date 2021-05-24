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

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.assets.Bookmark;
import org.librecms.contentsection.AssetRepository;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAssetEditSteps.PATH_PREFIX + "bookmark-edit")
@Controller
@MvcAssetEditStepDef(
    bundle = MvcAssetStepsConstants.BUNDLE,
    descriptionKey = "bookmark.editstep.description",
    labelKey = "bookmark.editstep.label",
    supportedAssetType = Bookmark.class
)
public class BookmarkEditStep extends AbstractMvcAssetEditStep {

    @Inject
    private AssetStepsDefaultMessagesBundle messageBundle;

    @Inject
    private AssetUi assetUi;

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    @Inject
    private BookmarkEditStepModel bookmarkEditStepModel;

    @Override
    public Class<? extends MvcAssetEditStep> getStepClass() {
        return BookmarkEditStep.class;
    }

    @Override
    protected void init() throws ContentSectionNotFoundException,
                                 AssetNotFoundException {
        super.init();

        if (getAsset() instanceof Bookmark) {
            bookmarkEditStepModel.setUrl(getBookmark().getUrl());

            bookmarkEditStepModel.setDescriptionValues(
                getBookmark()
                    .getDescription()
                    .getValues()
                    .entrySet()
                    .stream()
                    .collect(
                        Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            entry -> entry.getValue()
                        )
                    )
            );

            final Set<Locale> descriptionLocales = getBookmark()
                .getDescription()
                .getAvailableLocales();
            bookmarkEditStepModel.setUnusedDescriptionLocales(
                globalizationHelper
                    .getAvailableLocales()
                    .stream()
                    .filter(locale -> !descriptionLocales.contains(locale))
                    .map(Locale::toString)
                    .collect(Collectors.toList())
            );
        } else {
            throw new AssetNotFoundException(
                assetUi.showAssetNotFound(
                    getContentSection(), getAssetPath()
                ),
                String.format(
                    "No asset for path %s found in section %s.",
                    getAssetPath(),
                    getContentSection().getLabel()
                )
            );
        }
    }

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String showStep(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            return "org/librecms/ui/contentsection/bookmark/bookmark/edit-bookmark.xhtml";
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    public Bookmark getBookmark() {
        return (Bookmark) getAsset();
    }

    @POST
    @Path("/properties")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateProperties(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @FormParam("url") final String url
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            final Bookmark bookmark = getBookmark();
            bookmark.setUrl(url);

            assetRepository.save(bookmark);

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    @POST
    @Path("/description/add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addDescription(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            final Locale locale = new Locale(localeParam);
            final Bookmark bookmark = getBookmark();
            bookmark.getDescription().addValue(locale, value);

            assetRepository.save(bookmark);

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    @POST
    @Path("/description/edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editDescription(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            final Locale locale = new Locale(localeParam);
            final Bookmark bookmark = getBookmark();
            bookmark.getDescription().addValue(locale, value);

            assetRepository.save(bookmark);

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    @POST
    @Path("/description/remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @PathParam("locale") final String localeParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            final Locale locale = new Locale(localeParam);
            final Bookmark bookmark = getBookmark();
            bookmark.getDescription().removeValue(locale);

            assetRepository.save(bookmark);

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

}
