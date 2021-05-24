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
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.assets.FileAsset;
import org.librecms.contentsection.AssetRepository;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(MvcAssetEditSteps.PATH_PREFIX + "fileasset-edit")
@Controller
@MvcAssetEditStepDef(
    bundle = MvcAssetStepsConstants.BUNDLE,
    descriptionKey = "fileasset.editstep.description",
    labelKey = "fileasset.editstep.lable",
    supportedAssetType = FileAsset.class
)
public class FileAssetEditStep extends AbstractMvcAssetEditStep {

    private static final Logger LOGGER = LogManager.getLogger(
        FileAssetEditStep.class
    );

    @Inject
    private AssetStepsDefaultMessagesBundle messageBundle;

    @Inject
    private AssetUi assetUi;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    @Inject
    private Models models;

    @Inject
    private FileAssetEditStepModel editStepModel;

    @Override
    public Class<? extends MvcAssetEditStep> getStepClass() {
        return FileAssetEditStep.class;
    }

    @Override
    protected void init() throws ContentSectionNotFoundException,
                                 AssetNotFoundException {
        super.init();

        if (getAsset() instanceof FileAsset) {
            editStepModel.setDescriptionValues(
                getFileAsset()
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

            final Set<Locale> descriptionLocales = getFileAsset()
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

            editStepModel.setFileName(getFileAsset().getFileName());
            editStepModel.setMimeType(getFileAsset().getMimeType().toString());
            editStepModel.setSize(getFileAsset().getSize());
            
            final long size = getFileAsset().getSize();
            if (size < 2048) {
                editStepModel.setSizeLabel(String.format("%d Bytes", size));
            } else if(size < 1024 * 1024) {
                editStepModel.setSizeLabel(
                    String.format("%d kB", size / 1024)
                );
            } else if (size < 1024 * 1024 * 1024){
                editStepModel.setSizeLabel(
                    String.format("%d MB", size / (1024 * 1024))
                );
            } else {
                editStepModel.setSizeLabel(
                    String.format("%d GB", size / (1024 * 1024 * 1024))
                );
            }
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
            return "org/librecms/ui/contentsection/assets/fileasset/edit-fileasset.xhtml";
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    public FileAsset getFileAsset() {
        return (FileAsset) getAsset();
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
            final FileAsset bookmark = getFileAsset();
            bookmark.getDescription().addValue(locale, value);

            assetRepo.save(bookmark);

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
            final FileAsset bookmark = getFileAsset();
            bookmark.getDescription().addValue(locale, value);

            assetRepo.save(bookmark);

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
            final FileAsset bookmark = getFileAsset();
            bookmark.getDescription().removeValue(locale);

            assetRepo.save(bookmark);

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDescription(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @Context final HttpServletRequest request
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            final FileAsset fileAsset = getFileAsset();

            final Part part;
            final String fileName;
            try {
                part = request.getPart("file");
                final String contentDisposition = part.getHeader(
                    "content-disposition"
                );
                fileName = Arrays
                    .stream(contentDisposition.split(";"))
                    .filter(field -> field.startsWith("filename"))
                    .findAny()
                    .map(
                        field -> field
                            .substring(field.indexOf('=') + 1)
                            .trim().replace("\"", "")
                    ).orElse("");
            } catch (IOException | ServletException ex) {
                LOGGER.error(
                    "Failed to upload file for FileAsset {}:", assetPath
                );
                LOGGER.error(ex);
                models.put("uploadFailed", true);
                return buildRedirectPathForStep();
            }

            final byte[] bytes = new byte[1024];
            try (InputStream fileInputStream = part.getInputStream();
                 ByteArrayOutputStream fileDataOutputStream
                 = new ByteArrayOutputStream()) {
                while (fileInputStream.read(bytes) != -1) {
                    fileDataOutputStream.writeBytes(bytes);
                }

                fileAsset.setData(fileDataOutputStream.toByteArray());
            } catch (IOException ex) {
                LOGGER.error(
                    "Failed to upload file for FileAsset {}:", assetPath
                );
                LOGGER.error(ex);

                models.put("uploadFailed", true);
                return buildRedirectPathForStep();
            }

            fileAsset.setFileName(fileName);
            fileAsset.setSize(fileAsset.getData().length);
            try (BufferedInputStream stream = new BufferedInputStream(
                new ByteArrayInputStream(fileAsset.getData()))) {
                fileAsset.setMimeType(
                    new MimeType(URLConnection
                        .guessContentTypeFromStream(stream))
                );
            } catch (IOException | MimeTypeParseException ex) {
                LOGGER.error("Failed to get file type.", ex);
                models.put("failedToGetType", true);
                return buildRedirectPathForStep();
            }

            assetRepo.save(fileAsset);

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

}
