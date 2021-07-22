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

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.ui.BaseUrl;
import org.librecms.assets.ExternalAudioAsset;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.AssetRepository;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAssetEditSteps.PATH_PREFIX + "external-audio-asset-edit")
@Controller
@MvcAssetEditStepDef(
    bundle = MvcAssetStepsConstants.BUNDLE,
    descriptionKey = "externalaudioasset.editstep.description",
    labelKey = "externalaudioasset.editstep.label",
    supportedAssetType = ExternalAudioAsset.class
)
public class ExternalAudioAssetEditStep extends BookmarkEditStep {

    @Inject
    private AssetStepsDefaultMessagesBundle messageBundle;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetUi assetUi;

    @Inject
    private BaseUrl baseUrl;

    @Inject
    private ExternalAudioAssetEditStepModel editStepModel;

    @Context
    private HttpServletRequest request;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Override
    public Class<? extends MvcAssetEditStep> getStepClass() {
        return ExternalAudioAssetEditStep.class;
    }

    @Override
    protected void init() throws ContentSectionNotFoundException,
                                 AssetNotFoundException {
        super.init();;

        if (getAsset() instanceof ExternalAudioAsset) {
            editStepModel.setLegalMetadata(
                getExternalAudioAsset().getLegalMetadata()
            );

            editStepModel.setBaseUrl(baseUrl.getBaseUrl(request));
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
            return "org/librecms/ui/contentsection/assets/external-audio-asset/edit-external-audio-asset.xhtml";
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    public ExternalAudioAsset getExternalAudioAsset() {
        return (ExternalAudioAsset) getBookmark();
    }

    @POST
    @Path("/legalmetadata")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String setLegalMetadata(
        @FormParam("legalMetadataIdentifier")
        final String legalMetadataIdentifier
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final Identifier identifier = identifierParser
            .parseIdentifier(legalMetadataIdentifier);
        final Optional<LegalMetadata> legalMetadataResult;
        switch (identifier.getType()) {
            case ID:
                legalMetadataResult = assetRepo.findById(
                    Long.parseLong(identifier.getIdentifier()),
                    LegalMetadata.class
                );
                break;
            case UUID:
                legalMetadataResult = assetRepo.findByUuidAndType(
                    identifier.getIdentifier(),
                    LegalMetadata.class
                );
                break;
            default:
                legalMetadataResult = assetRepo
                    .findByPath(identifier.getIdentifier())
                    .map(result -> (LegalMetadata) result);
                break;
        }
        if (!legalMetadataResult.isPresent()) {
            return showLegalMetadataNotFound(legalMetadataIdentifier);
        }

        final LegalMetadata legalMetadata = legalMetadataResult.get();

        getExternalAudioAsset().setLegalMetadata(legalMetadata);
        assetRepo.save(getExternalAudioAsset());

        return buildRedirectPathForStep();
    }

    @POST
    @Path("/legalmetadata/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeLegalMetadata() {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        getExternalAudioAsset().setLegalMetadata(null);
        assetRepo.save(getExternalAudioAsset());

        return buildRedirectPathForStep();
    }

    private String showLegalMetadataNotFound(
        final String legalMetadataIdentifer
    ) {
        models.put("legalMetadataIdentifier", legalMetadataIdentifer);
        return "org/librecms/ui/contentsection/assets/external-audio-asset/legal-metadata-not-found.xhtml";
    }

}
