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
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionModel;
import org.librecms.ui.contentsections.ContentSectionsUi;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractMvcAssetEditStep implements MvcAssetEditStep {

    @Inject
    private AssetUi assetUi;

    @Inject
    private AssetManager assetManager;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ContentSectionModel sectionModel;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private HttpServletRequest request;

    @Inject
    private Models models;

    @Inject
    private SelectedAssetModel assetModel;

    @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
    private String sectionIdentifier;

    @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
    private String assetPathParam;

    private ContentSection contentSection;

    private Asset asset;

    private String assetPath;

    private String stepPath;

    protected void init() throws ContentSectionNotFoundException,
                                 AssetNotFoundException {
        contentSection = sectionsUi
            .findContentSection(sectionIdentifier)
            .orElseThrow(
                () -> new ContentSectionNotFoundException(
                    sectionsUi.showContentSectionNotFound(sectionIdentifier),
                    String.format(
                        "ContentSection %s not found.",
                        sectionIdentifier
                    )
                )
            );
        sectionModel.setSection(contentSection);

        asset = assetRepo
            .findByPath(contentSection, assetPathParam)
            .orElseThrow(
                () -> new AssetNotFoundException(
                    assetUi.showAssetNotFound(
                        contentSection, assetPathParam
                    ),
                    String.format(
                        "No asset for path %s found in section %s.",
                        assetPathParam,
                        contentSection.getLabel()
                    )
                )
            );
        assetModel.setAsset(asset);

        assetPath = assetManager.getAssetPath(asset);
        final Map<String, String> values = new HashMap<>();
        values.put(
            MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM,
            contentSection.getLabel()
        );
        values.put(
            MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME,
            assetPath
        );

        stepPath = Optional
            .ofNullable(getStepClass().getAnnotation(Path.class))
            .map(Path::value)
            .map(
                path -> UriBuilder
                    .fromPath(path)
                    .buildFromMap(values)
                    .toString()
            )
            .orElse("");

        models.put("activeAssetTab", "editTab");
        models.put("stepPath", stepPath);
    }

    @Override
    public ContentSection getContentSection() {
        return Optional
            .ofNullable(contentSection)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Edit step %s was not initialized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAssetEditStep.class.getName()
                    )
                )
            );
    }

    @Override
    public Asset getAsset() {
        return Optional
            .ofNullable(asset)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Edit step %s was not initialized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAssetEditStep.class.getName()
                    )
                )
            );
    }

    @Override
    public String getAssetPath() {
        return Optional
            .ofNullable(assetPath)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Edit step %s was not initialized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAssetEditStep.class.getName()
                    )
                )
            );
    }

    @Override
    public boolean getCanEdit() {
        return assetPermissionsChecker.canEditAsset(asset);
    }

    @Override
    public void updateAssetPath() {
        assetPath = assetManager.getAssetPath(asset).substring(1); // Without leading slash
    }

    @Override
    public String getStepPath() {
        return stepPath;
    }

    @Override
    public String buildRedirectPathForStep() {
        final ContentSection section = Optional
            .ofNullable(contentSection)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Edit Step %s was not initalized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAssetEditStep.class.getName()
                    )
                )
            );
        final String assetPathNonNull = Optional
            .ofNullable(assetPath)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Edit step %s was not initialized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAssetEditStep.class.getName()
                    )
                )
            );
        
        final Map<String, String> values = new HashMap<>();
        values.put(
            MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM,
            section.getLabel()
        );
        values.put(
            MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME, 
            assetPathNonNull
        );
        
        return Optional
            .ofNullable(getStepClass().getAnnotation(Path.class))
            .map(Path::value)
            .map(
                path -> UriBuilder
                .fromPath(path)
                .buildFromMap(values)
                .toString()
            )
            .map(path -> String.format("redirect:%s", path))
            .orElse("");
    }
    
    @Override
    public String buildRedirectPathForStep(final String subPath) {
        final ContentSection section = Optional
            .ofNullable(contentSection)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Edit Step %s was not initalized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAssetEditStep.class.getName()
                    )
                )
            );
        final String assetPathNonNull = Optional
            .ofNullable(assetPath)
            .orElseThrow(
                () -> new WebApplicationException(
                    String.format(
                        "Edit step %s was not initialized properly. "
                            + "Did you forget to call %s#init()?",
                        getStepClass().getName(),
                        AbstractMvcAssetEditStep.class.getName()
                    )
                )
            );
        
        final Map<String, String> values = new HashMap<>();
        values.put(
            MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM,
            section.getLabel()
        );
        values.put(
            MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME, 
            assetPathNonNull
        );
        
        return Optional
            .ofNullable(getStepClass().getAnnotation(Path.class))
            .map(Path::value)
            .map(
                path -> UriBuilder
                .fromPath(path)
                .path(subPath)
                .buildFromMap(values)
                .toString()
            )
            .map(path -> String.format("redirect:%s", path))
            .orElse("");
    }

   
    
}
