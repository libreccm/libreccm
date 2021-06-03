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
import org.librecms.assets.Person;
import org.librecms.assets.PersonManager;
import org.librecms.assets.PersonName;
import org.librecms.contentsection.AssetRepository;
import org.librecms.ui.contentsections.AssetPermissionsChecker;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
@Path(MvcAssetEditSteps.PATH_PREFIX + "person-edit")
@Controller
@MvcAssetEditStepDef(
    bundle = MvcAssetStepsConstants.BUNDLE,
    descriptionKey = "person.editstep.description",
    labelKey = "person.editstep.lable",
    supportedAssetType = Person.class
)
public class PersonEditStep extends AbstractContactableEntityEditStep {

    @Inject
    private AssetStepsDefaultMessagesBundle messageBundle;

    @Inject
    private AssetPermissionsChecker assetPermissionsChecker;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetUi assetUi;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PersonEditStepModel editStepModel;

    @Inject
    private PersonManager personManager;

    @Override
    public Class<? extends MvcAssetEditStep> getStepClass() {
        return PersonEditStep.class;
    }

    protected Person getPerson() {
        return (Person) getAsset();
    }

    @Override
    protected void init() throws ContentSectionNotFoundException,
                                 AssetNotFoundException {
        super.init();

        if (getAsset() instanceof Person) {
            editStepModel.setBirthdate(
                Optional
                    .ofNullable(getPerson().getBirthdate())
                    .map(
                        birthdate -> birthdate.format(
                            DateTimeFormatter
                                .ofLocalizedDate(FormatStyle.SHORT)
                                .withLocale(
                                    globalizationHelper.getNegotiatedLocale()
                                )
                                .withZone(ZoneId.systemDefault())))
                    .orElse("")
            );
            final List<PersonNameRow> personNames = new ArrayList<>();
            for (int i = 0; i < getPerson().getPersonNames().size(); i++) {
                personNames.add(
                    buildPersonNameRow(i, getPerson().getPersonNames().get(i))
                );
            }
            Collections.reverse(personNames);
            editStepModel.setPersonNames(personNames);
        } else {
            throw new AssetNotFoundException(
                assetUi.showAssetNotFound(
                    getContentSection(), getAssetPath()
                ),
                String.format(
                    "No Person for path %s found in section %s.",
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
            return "org/librecms/ui/contentsection/assets/person/edit-person.xhtml";
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    @POST
    @Path("/personnames/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addPersonName(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @FormParam("surname") final String surname,
        @FormParam("givenName") final String givenName,
        @FormParam("prefix") final String prefix,
        @FormParam("suffix") final String suffix
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            final PersonName personName = new PersonName();
            personName.setGivenName(givenName);
            personName.setPrefix(prefix);
            personName.setSuffix(suffix);
            personName.setSurname(surname);
            personManager.addPersonName(getPerson(), personName);

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    @POST
    @Path("/personnames/{index}/@edit")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updatePersonName(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @PathParam("index") final int index,
        @FormParam("surname") final String surname,
        @FormParam("givenName") final String givenName,
        @FormParam("prefix") final String prefix,
        @FormParam("suffix") final String suffix
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            if (index < getPerson().getPersonNames().size()) {
                final PersonName personName = getPerson()
                    .getPersonNames()
                    .get(index);

                personName.setGivenName(givenName);
                personName.setPrefix(prefix);
                personName.setSuffix(suffix);
                personName.setSurname(surname);

                assetRepo.save(getPerson());
            }

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    @POST
    @Path("/personnames/{index}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removePersonName(
        @PathParam(MvcAssetEditSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAssetEditSteps.ASSET_PATH_PATH_PARAM_NAME)
        final String assetPath,
        @PathParam("index") final int index
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (assetPermissionsChecker.canEditAsset(getAsset())) {
            if (index < getPerson().getPersonNames().size()) {
                final PersonName personName = getPerson()
                    .getPersonNames()
                    .get(index);
                personManager.removePersonName(getPerson(), personName);
            }

            return buildRedirectPathForStep();
        } else {
            return assetUi.showAccessDenied(
                getContentSection(),
                getAsset(),
                messageBundle.get("asset.edit.denied"));
        }
    }

    private PersonNameRow buildPersonNameRow(
        final int index, final PersonName personName
    ) {
        final PersonNameRow row = new PersonNameRow();
        row.setIndex(index);
        row.setSurname(personName.getSurname());
        row.setPrefix(personName.getPrefix());
        row.setSuffix(personName.getSuffix());
        row.setGivenName(personName.getGivenName());
        
        return row;
    }

}
