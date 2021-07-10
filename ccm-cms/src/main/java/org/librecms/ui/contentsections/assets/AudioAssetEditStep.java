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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.assets.BinaryAssetDataService;
import org.librecms.assets.FileAsset;
import org.librecms.assets.AudioAsset;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.AssetRepository;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.activation.MimeType;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAssetEditSteps.PATH_PREFIX + "audioasset-edit")
@Controller
@MvcAssetEditStepDef(
    bundle = MvcAssetStepsConstants.BUNDLE,
    descriptionKey = "audioasset.editstep.description",
    labelKey = "audioasset.editstep.label",
    supportedAssetType = FileAsset.class
)
public class AudioAssetEditStep extends AbstractMvcAssetEditStep {

    private static final Logger LOGGER = LogManager.getLogger(
        AudioAssetEditStep.class
    );

    @Context
    private HttpServletRequest request;

    @Inject
    private AssetStepsDefaultMessagesBundle messageBundle;

    @Inject
    private AssetUi assetUi;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private BinaryAssetDataService dataService;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    @Inject
    private Models models;

    @Inject
    private AudioAssetEditStepModel editStepModel;

    @Override
    public Class<? extends MvcAssetEditStep> getStepClass() {
        return AudioAssetEditStep.class;
    }

    @Override
    protected void init() throws ContentSectionNotFoundException,
                                 AssetNotFoundException {
        super.init();

        if (getAsset() instanceof AudioAsset) {
            editStepModel.setDescriptionValues(
                getAudioAsset()
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

            final Set<Locale> descriptionLocales = getAudioAsset()
                .getDescription()
                .getAvailableLocales();
            editStepModel.setUnusedDescriptionLocales(
                globalizationHelper
                    .getAvailableLocales()
                    .stream()
                    .filter(locale -> !descriptionLocales.contains(locale))
                    .map(Locale::toString)
                    .collect(Collectors.toList())
            );

            editStepModel.setFileName(getAudioAsset().getFileName());
            editStepModel.setMimeType(
                Optional
                    .ofNullable(getAudioAsset().getMimeType())
                    .map(MimeType::toString)
                    .orElse("")
            );
            editStepModel.setSize(getAudioAsset().getSize());

            final long size = getAudioAsset().getSize();
            if (size < 2048) {
                editStepModel.setSizeLabel(String.format("%d Bytes", size));
            } else if (size < 1024 * 1024) {
                editStepModel.setSizeLabel(
                    String.format("%d kB", size / 1024)
                );
            } else if (size < 1024 * 1024 * 1024) {
                editStepModel.setSizeLabel(
                    String.format("%d MB", size / (1024 * 1024))
                );
            } else {
                editStepModel.setSizeLabel(
                    String.format("%d GB", size / (1024 * 1024 * 1024))
                );
            }

            editStepModel.setLegalMetadata(getAudioAsset().getLegalMetadata());

            final StringBuilder baseUrlBuilder = new StringBuilder();
            editStepModel.setBaseUrl(
                baseUrlBuilder
                    .append(request.getScheme())
                    .append("://")
                    .append(request.getServerName())
                    .append(addServerPortToBaseUrl())
                    .append(addContextPathToBaseUrl())
                    .toString()
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
            return "org/librecms/ui/contentsection/assets/audioasset/edit-audioasset.xhtml";
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
            final AudioAsset asset = getAudioAsset();
            asset.getDescription().addValue(locale, value);

            assetRepo.save(asset);

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
            final AudioAsset asset = getAudioAsset();
            asset.getDescription().addValue(locale, value);

            assetRepo.save(asset);

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
            final AudioAsset asset = getAudioAsset();
            asset.getDescription().removeValue(locale);

            assetRepo.save(asset);

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
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

        getAudioAsset().setLegalMetadata(legalMetadata);
        assetRepo.save(getAudioAsset());

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

        getAudioAsset().setLegalMetadata(null);
        assetRepo.save(getAudioAsset());

        return buildRedirectPathForStep();
    }

    private String showLegalMetadataNotFound(
        final String legalMetadataIdentifer
    ) {
        models.put("legalMetadataIdentifier", legalMetadataIdentifer);
        return "org/librecms/ui/contentsection/assets/external-video-asset/legal-metadata-not-found.xhtml";
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String uploadFile(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        final MultipartFormDataInput input
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            final AudioAsset asset = getAudioAsset();

            final Map<String, List<InputPart>> uploadForm = input
                .getFormDataMap();
            final List<InputPart> inputParts = uploadForm.get("fileData");

            String fileName = "";
            String contentType = "";
            for (final InputPart inputPart : inputParts) {
                try {
                    final MultivaluedMap<String, String> headers = inputPart
                        .getHeaders();

                    fileName = getFileName(headers);
                    contentType = getContentType(headers);

                    dataService.saveData(
                        asset,
                        inputPart.getBody(InputStream.class, null),
                        fileName,
                        contentType
                    );
                } catch (IOException | UnexpectedErrorException ex) {
                    LOGGER.error(
                        "Failed to upload file for FileAsset {}:", assetPath
                    );
                    LOGGER.error(ex);

                    models.put("uploadFailed", true);
                    return buildRedirectPathForStep();
                }
            }

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    public AudioAsset getAudioAsset() {
        return (AudioAsset) getAsset();
    }

    private String getFileName(final MultivaluedMap<String, String> headers) {
        final String[] contentDisposition = headers
            .getFirst("Content-Disposition")
            .split(";");

        for (final String fileName : contentDisposition) {
            if (fileName.trim().startsWith("filename")) {
                final String[] name = fileName.split("=");

                return name[1].trim().replaceAll("\"", "");
            }
        }

        return "";
    }

    private String getContentType(
        final MultivaluedMap<String, String> headers
    ) {
        return headers.getFirst("Content-Type");
    }

    private String addServerPortToBaseUrl() {
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
            return "";
        } else {
            return String.format(":%d", request.getServerPort());
        }
    }

    private String addContextPathToBaseUrl() {
        if (request.getServletContext().getContextPath() == null
                || request.getServletContext().getContextPath().isEmpty()) {
            return "/";
        } else {
            return request.getServletContext().getContextPath();
        }
    }

}
