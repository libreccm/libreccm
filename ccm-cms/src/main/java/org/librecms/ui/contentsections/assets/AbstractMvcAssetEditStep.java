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
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionModel;
import org.librecms.ui.contentsections.ContentSectionsUi;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
public abstract class AbstractMvcAssetEditStep implements MvcAssetEditStep {

    @Inject
    private AssetStepsDefaultMessagesBundle messageBundle;

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
    private MvcAssetEditStepModel mvcAssetEditStepModel;

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

//    private Map<String, String> titleValues;
//
//    private List<String> unusedTitleLocales;
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

        mvcAssetEditStepModel.setName(getName());
        mvcAssetEditStepModel.setCanEdit(getCanEdit());

        mvcAssetEditStepModel.setTitleValues(
            getAsset()
                .getTitle()
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

        final Set<Locale> titleLocales = getAsset()
            .getTitle()
            .getAvailableLocales();

        mvcAssetEditStepModel.setUnusedTitleLocales(
            globalizationHelper
                .getAvailableLocales()
                .stream()
                .filter(locale -> !titleLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );

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

    public String getName() {
        return getAsset().getDisplayName();
    }

    @POST
    @Path("/name")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateName(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @FormParam("name") @DefaultValue("") final String name
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            if (name.isEmpty() || name.matches("\\s*")) {
                models.put("nameMissing", true);

                return showStep(sectionIdentifier, assetPath);
            }

            getAsset().setDisplayName(name);
            assetRepo.save(getAsset());

            updateAssetPath();

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

//    public Map<String, String> getTitleValues() {
//        return Collections.unmodifiableMap(titleValues);
//    }
//
//    public List<String> getUnusedTitleLocales() {
//        return Collections.unmodifiableList(unusedTitleLocales);
//    }
    @POST
    @Path("/title/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addTitle(
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
            getAsset().getTitle().addValue(locale, value);
            assetRepo.save(getAsset());

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    @POST
    @Path("/title/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editTitle(
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
            getAsset().getTitle().addValue(locale, value);
            assetRepo.save(getAsset());

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    @POST
    @Path("/title/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeTitle(
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
            getAsset().getTitle().removeValue(locale);
            assetRepo.save(getAsset());

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

}
