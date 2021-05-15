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
import org.librecms.assets.PostalAddress;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.FolderManager;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
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
@Path(MvcAssetEditSteps.PATH_PREFIX + "postaladdress-edit")
@Controller
@Named("CmsPostalAddressEditStep")
@MvcAssetEditStepDef(
    bundle = MvcAssetStepsConstants.BUNDLE,
    descriptionKey = "postaladdress.editstep.description",
    labelKey = "postaladdress.editstep.label",
    supportedAssetType = PostalAddress.class
)
public class PostalAddressEditStep extends AbstractMvcAssetEditStep {

    @Inject
    private AssetStepsDefaultMessagesBundle messageBundle;

    @Inject
    private AssetManager assetManager;

    @Inject
    private AssetUi assetUi;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private FolderManager folderManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    @Inject
    private Models models;

    private Map<String, String> titleValues;

    private List<String> unusedTitleLocales;

    private Map<String, String> countries;

    @Override
    public Class<? extends MvcAssetEditStep> getStepClass() {
        return PostalAddressEditStep.class;
    }

    @Override
    protected void init() throws ContentSectionNotFoundException, 
                                 AssetNotFoundException {
        super.init();

        final Set<Locale> countryLocales = Arrays
            .stream(Locale.getISOCountries())
            .map(country -> new Locale("", country))
            .collect(Collectors.toCollection(LinkedHashSet::new));

        final Map<String, String> countriesMap = countryLocales
            .stream()
            .collect(
                Collectors.toMap(
                    Locale::getCountry,
                    locale -> locale.getDisplayCountry(
                        globalizationHelper.getNegotiatedLocale()
                    ),
                    (value1, value2) -> value1,
                    LinkedHashMap::new
                )
            );

        countries = new LinkedHashMap<>();
        countries.put("", "");
        countries.putAll(countriesMap);
    }

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
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
            titleValues = getAsset()
                .getTitle()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                );

            final Set<Locale> titleLocales = getAsset()
                .getTitle()
                .getAvailableLocales();

            unusedTitleLocales = globalizationHelper
                .getAvailableLocales()
                .stream()
                .filter(locale -> !titleLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList());

            return "org/librecms/ui/contentsection/assets/postaladdress/edit-postaladdress.xhtml";
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    public String getName() {
        return getAsset().getDisplayName();
    }

    public PostalAddress getPostalAddress() {
        return (PostalAddress) getAsset();
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

    public Map<String, String> getTitleValues() {
        return Collections.unmodifiableMap(titleValues);
    }

    public List<String> getUnusedTitleLocales() {
        return Collections.unmodifiableList(unusedTitleLocales);
    }

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
        @PathParam("value") final String value
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

    public String getAddress() {
        return getPostalAddress().getAddress();
    }

    public String getPostalCode() {
        return getPostalAddress().getPostalCode();
    }

    public String getCity() {
        return getPostalAddress().getCity();
    }

    public String getState() {
        return getPostalAddress().getState();
    }

    public String getIsoCountryCode() {
        return getPostalAddress().getIsoCountryCode();
    }

    public String getCountry() {
        return new Locale("", getPostalAddress().getIsoCountryCode())
            .getDisplayCountry(globalizationHelper.getNegotiatedLocale());
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
        @FormParam("address") final String address,
        @FormParam("postalCode") final String postalCode,
        @FormParam("city") final String city,
        @FormParam("state") final String state,
        @FormParam("isoCountryCode") final String isoCountryCode
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            final PostalAddress postalAddress = getPostalAddress();
            postalAddress.setAddress(address);
            postalAddress.setCity(city);
            postalAddress.setIsoCountryCode(isoCountryCode);
            postalAddress.setPostalCode(postalCode);
            postalAddress.setState(state);

            assetRepo.save(postalAddress);

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    public Map<String, String> getCountries() {
        return countries;
    }

}
