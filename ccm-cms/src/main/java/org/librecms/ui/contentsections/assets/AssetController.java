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
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;
import org.librecms.contentsection.privileges.AssetPrivileges;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionModel;
import org.librecms.ui.contentsections.ContentSectionsUi;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{sectionIdentifier}/assets")
@Controller
public class AssetController {

    @Inject
    private AssetEditStepsValidator stepsValidator;

    @Inject
    private AssetManager assetManager;

    @Inject
    private ContentSectionModel sectionModel;

    /**
     * {@link ContentSectionsUi} instance providing for helper functions for
     * dealing with {@link ContentSection}s.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * {@link AssetUi} instance providing some common functions for managing
     * assets.
     */
    @Inject
    private AssetUi assetUi;

    /**
     * {@link FolderRepository} instance for retrieving folders.
     */
    @Inject
    private FolderRepository folderRepo;

    /**
     * {@link ContentItemRepository} instance for retrieving content items.
     */
    @Inject
    private AssetRepository assetRepo;

    @Inject
    @Any
    private Instance<MvcAssetCreateStep<?>> assetCreateSteps;

    @Inject
    private AssetStepsDefaultMessagesBundle defaultStepsMessageBundle;
    
    /**
     * {@link GlobalizationHelper} for working with localized texts etc.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    /**
     * Used to make avaiable in the views without a named bean.
     */
    @Inject
    private Models models;

    /**
     * Used to check permissions on content items.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Named bean providing access to the properties of the selected asset from
     * the view.
     */
    @Inject
    private SelectedAssetModel selectedAssetModel;

    /*
     * Redirect requests to the root path of this controller to the path for
     * displaying the content of the root asset folder. The root path of this
     * controller has no function. We assume that somebody who access the root
     * folders wants to browse all asset in the content section. Therefore we
     * redirect these requests to the root folder.
     *
     * @param sectionIdentifier The identififer of the current content section.
     *
     * @return A redirect to the root assets folder.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String redirectToAssetFolders(
        @PathParam("sectionIdentifier") final String sectionIdentifier
    ) {
        return String.format(
            "redirect:/%s/assetfolders/",
            sectionIdentifier
        );
    }

    /**
     * Delegates requests for the path {@code @create} to the create step
     * (subresource) of the asset type. The new asset will be created in the
     * root folder of the current content section.
     *
     * @param sectionIdentifier The identifier of the current content section.
     * @param assetType         The type of the asset to create.
     *
     * @return The template of the create step.
     */
    @GET
    @Path("/@create/{assetType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCreateStep(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("assetType") final String assetType
    ) {
        return showCreateStep(sectionIdentifier, "", assetType);
    }

    @POST
    @Path("/@create")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCreateStepPost(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @FormParam("assetType") final String assetType
    ) {
        return String.format(
            "redirect:/%s/assets/@create/%s",
            sectionIdentifier,
            assetType
        );
    }

    @GET
    @Path("/{folderPath:(.+)?}/@create/{assetType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCreateStep(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @PathParam("assetType") final String assetType
    ) {
        final CreateStepResult result = findCreateStep(
            sectionIdentifier,
            folderPath,
            assetType
        );

        if (result.isCreateStepAvailable()) {
            return result.getCreateStep().showCreateStep();
        } else {
            return result.getErrorTemplate();
        }
    }

    @POST
    @Path("/{folderPath:(.+)?}/@create")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCreateStepPost(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @FormParam("assetType") final String assetType
    ) {
        return String.format(
            "redirect:/%s/assets/%s/@create/%s",
            sectionIdentifier,
            folderPath,
            assetType
        );
    }

    @POST
    @Path("/@create/{assetType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String createAsset(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("assetType") final String assetType,
        @Context final HttpServletRequest request
    ) {
        return createAsset(
            sectionIdentifier,
            "",
            assetType,
            request
        );
    }

    @POST
    @Path("/{folderPath:(.+)?}/@create/{assetType}")
    @AuthorizationRequired
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createAsset(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("folderPath") final String folderPath,
        @PathParam("assetType") final String assetType,
        @Context final HttpServletRequest request
    ) {
        final CreateStepResult result = findCreateStep(
            sectionIdentifier,
            folderPath,
            assetType
        );

        if (result.isCreateStepAvailable()) {
            return result.getCreateStep().createAsset(
                request.getParameterMap()
            );
        } else {
            return result.getErrorTemplate();
        }
    }

    @GET
    @Path("/{assetPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editAsset(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("assetPath") final String assetPath
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifier);
        }
        final ContentSection section = sectionResult.get();

        final Optional<Asset> assetResult = assetRepo
            .findByPath(section, assetPath);
        if (!assetResult.isPresent()) {
            return assetUi.showAssetNotFound(section, assetPath);
        }
        final Asset asset = assetResult.get();
        if (!permissionChecker.isPermitted(AssetPrivileges.EDIT, asset)) {
            return assetUi.showAccessDenied(section, asset, assetPath);
        }

        return String.format("redirect:%s", findEditStep(asset, section));
    }

    /**
     * Helper method for finding the path fragment for the edit step of an
     * asset.
     *
     * @param asset The asset.
     *
     * @return The path of the edit step of the asset.
     *
     */
    private String findEditStep(
        final Asset asset, final ContentSection section
    ) {
        final MvcAssetEditKit editKit = asset
            .getClass()
            .getAnnotation(MvcAssetEditKit.class);

        final Class<?> step = editKit.editStep();
        final Path pathAnnotation = step.getAnnotation(Path.class);
        return pathAnnotation
            .value()
            .replace(
                String.format(
                    "{%s}",
                    MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM
                ),
                section.getLabel()
            )
            .replace(
                String.format(
                    "/{%s}",
                    MvcAssetEditSteps.ASSET_PATH_PATH_PARAM
                ),
                assetManager.getAssetPath(asset)
            );
    }

       /**
     * Helper method for showing the "asset folder not found" page if there
     * is no folder for the provided path.
     *
     * @param section    The content section.
     * @param folderPath The folder path.
     *
     * @return The template of the "asset folder not found" page.
     */
    private String showAssetFolderNotFound(
        final ContentSection section, final String folderPath
    ) {
        models.put("contentSection", section.getLabel());
        models.put("folderPath", folderPath);
        
        return "org/librecms/ui/contentsection/assetfolder/assetfolder-not-found.xhtml";
    }
      /**
     * Helper method for showing the "asset type not available" page if the
     * requested asset type is not available.
     *
     * @param section    The content section.
     * @param assetType The asset type.
     *
     * @return The template of the "asset type not found" page.
     */
    public String showAssetTypeNotFound(
        final ContentSection section, final String assetType
    ) {
        models.put("contentSection", section.getLabel());
        models.put("assetType", assetType);
        
        return "org/librecms/ui/contentsection/assets/asset-type-not-found.xhtml";
    }
    
    private String showCreateStepNotAvailable(
        final ContentSection section,
        final String folderPath,
        final String assetType
    ) {
         models.put("contentSection", section.getLabel());
        models.put("folderPath", folderPath);
          models.put("assetType", assetType);
        
        return "org/librecms/ui/contentsection/assets/create-step-not-available.xhtml";
    }
    
    
    private CreateStepResult findCreateStep(
        final String sectionIdentifier,
        final String folderPath,
        final String assetType
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifier);
        if (!sectionResult.isPresent()) {
            return new CreateStepResult(
                sectionsUi.showContentSectionNotFound(sectionIdentifier)
            );
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        
        final Folder folder;
        if (folderPath.isEmpty()) {
            folder = section.getRootAssetsFolder();
        } else {
            final Optional<Folder> folderResult = folderRepo
                .findByPath(section, folderPath, FolderType.ASSETS_FOLDER
                );
            if (!folderResult.isPresent()) {
                return new CreateStepResult(
                    showAssetFolderNotFound(section, folderPath)
                );
            }
            folder = folderResult.get();
        }
        
        if (!assetPermissionsChecker.canCreateAssets(folder)) {
            return new CreateStepResult(
                sectionsUi.showAccessDenied(
                    "sectionidentifier", sectionIdentifier,
                    "folderPath", folderPath,
                    "step", defaultStepsMessageBundle.getMessage("create_step")
                )
            );
        }
        
        final Class<?> clazz;
        try {
            clazz = Class.forName(assetType);
        } catch(ClassNotFoundException ex) {
            return  new CreateStepResult(
                showAssetTypeNotFound(section, assetType)
            );
        }
        @SuppressWarnings("unchecked")
        final Class<? extends Asset> assetClass = (Class<? extends Asset>) clazz;
        
        final Optional<MvcAssetEditKit> editKitResult = Optional.ofNullable(
            assetClass.getDeclaredAnnotation(MvcAssetEditKit.class)
        );
        if (!editKitResult.isPresent()) {
            return new CreateStepResult(
                showCreateStepNotAvailable(section, folderPath, assetType)
            );
        }
        final MvcAssetEditKit editKit = editKitResult.get();
        final Class<? extends MvcAssetCreateStep<?>> createStepClass 
            = editKit.createStep();
        
        final Instance<? extends MvcAssetCreateStep<?>> instance 
            = assetCreateSteps.select(createStepClass);
        if (instance.isUnsatisfied() || instance.isAmbiguous()) {
            return new CreateStepResult(
                showCreateStepNotAvailable(section, folderPath, assetType)
            );
        }
        final MvcAssetCreateStep<? extends Asset> createStep = instance.get();
        
        createStep.setContentSection(section);
        createStep.setFolder(folder);
        
        return new CreateStepResult(createStep);
    }
    
    private class CreateStepResult {

        private final MvcAssetCreateStep<? extends Asset> createStep;

        private final boolean createStepAvailable;

        private final String errorTemplate;

        public CreateStepResult(
            final MvcAssetCreateStep<? extends Asset> createStep
        ) {
            this.createStep = createStep;
            createStepAvailable = true;
            errorTemplate = null;
        }

        public CreateStepResult(final String errorTemplate) {
            this.createStep = null;
            createStepAvailable = false;
            this.errorTemplate = errorTemplate;
        }

        public MvcAssetCreateStep<? extends Asset> getCreateStep() {
            return createStep;
        }

        public boolean isCreateStepAvailable() {
            return createStepAvailable;
        }

        public String getErrorTemplate() {
            return errorTemplate;
        }

    }

}
