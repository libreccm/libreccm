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
package org.librecms.ui.contentsections;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;
import org.librecms.lifecycle.LifecycleManager;
import org.librecms.lifecycle.PhaseDefinition;
import org.librecms.lifecycle.PhaseDefinititionRepository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Controller for managing the {@link LifecycleDefinition}s of a
 * {@link ContentSection}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/lifecycles")
public class ConfigurationLifecyclesController {

    /**
     * Checks admin permissions for the current content section.
     */
    @Inject
    private AdminPermissionsChecker adminPermissionsChecker;

    /**
     * Used for some operations on {@link ContentSection}s.
     */
    @Inject
    private ContentSectionManager sectionManager;

    /**
     * Model for the current content section.
     */
    @Inject
    private ContentSectionModel sectionModel;

    /**
     * Provides common functions for controllers working with content sections.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * Provides functions for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to parse identifiers.
     */
    @Inject
    private IdentifierParser identifierParser;

    /**
     * Used to manage lifecycles.
     */
    @Inject
    private LifecycleManager lifecycleManager;

    /**
     * Used to retrieve and save {@link LifecycleDefinition}s.
     */
    @Inject
    private LifecycleDefinitionRepository definitionRepo;

    /**
     * Used to provide data for the views without a named bean.
     */
    @Inject
    private Models models;

    /**
     * Used to retrive and save {@link PhaseDefinition}s.
     */
    @Inject
    private PhaseDefinititionRepository phaseDefinititionRepo;

    /**
     * Model for the current {@link LifecycleDefinition}.
     */
    @Inject
    private SelectedLifecycleDefinitionModel selectedLifecycleDefModel;

    /**
     * Model for the selected {@link PhaseDefinition}.
     */
    @Inject
    private SelectedPhaseDefinitionModel selectedPhaseDefModel;

    /**
     * List all {@link LifecycleDefinition}s of the current content section.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     *
     * @return The template for the list of {@link LifecycleDefinition}s.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listLifecycleDefinitions(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        models.put(
            "lifecycleDefinitions",
            section
                .getLifecycleDefinitions()
                .stream()
                .map(this::buildListModel)
                .collect(Collectors.toList())
        );
        return "org/librecms/ui/contentsection/configuration/lifecycles.xhtml";
    }

    /**
     * Show the details view for a {@link LifecycleDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the lifecycle
     *                                definition.
     *
     * @return The template for the details view of
     *         {@link LifecycleDefinition}s.
     */
    @GET
    @Path("/{lifecycleIdentifier}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showLifecycleDefinition(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, lifecycleIdentiferParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        selectedLifecycleDefModel.setDisplayLabel(
            globalizationHelper.getValueFromLocalizedString(
                definition.getLabel()
            )
        );
        selectedLifecycleDefModel.setUuid(definition.getUuid());

        final List<Locale> availableLocales = globalizationHelper
            .getAvailableLocales();

        selectedLifecycleDefModel.setLabel(
            definition
                .getLabel()
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
        final Set<Locale> labelLocales = definition
            .getLabel()
            .getAvailableLocales();
        selectedLifecycleDefModel.setUnusedLabelLocales(
            availableLocales
                .stream()
                .filter(locale -> !labelLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );
        selectedLifecycleDefModel.setDescription(
            definition
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
        final Set<Locale> descriptionLocales = definition
            .getDescription()
            .getAvailableLocales();
        selectedLifecycleDefModel.setUnusedDescriptionLocales(
            availableLocales
                .stream()
                .filter(locale -> !descriptionLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );
        selectedLifecycleDefModel.setPhaseDefinitions(
            definition
                .getPhaseDefinitions()
                .stream()
                .map(this::buildPhaseDefinitionModel)
                .collect(Collectors.toList())
        );

        return "org/librecms/ui/contentsection/configuration/lifecycle.xhtml";
    }

    /**
     * Add a {@link LifecycleDefinition} to the current {@link ContentSection}
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param label                  The label of the new
     *                               {@link LifecycleDefinition}.
     *
     * @return A redirect to the list of lifecycle definitions of the content
     *         section.
     */
    @POST
    @Path("/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addLifecycleDefinition(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("label") final String label
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final LifecycleDefinition definition = new LifecycleDefinition();
        definition
            .getLabel()
            .addValue(globalizationHelper.getNegotiatedLocale(), label);
        definitionRepo.save(definition);
        sectionManager.addLifecycleDefinitionToContentSection(
            definition, section
        );

        return String.format(
            "redirect:/%s/configuration/lifecycles", sectionIdentifierParam
        );
    }

    /**
     * Deletes a {@link LifecycleDefinition} of the current
     * {@link ContentSection}
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the lifecycle to delete.
     *
     * @return A redirect to the list of lifecycle definitions of the content
     *         section.
     */
    @POST
    @Path("/{lifecycleIdentifier}/@delete")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteLifecycleDefinition(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        sectionManager.removeLifecycleDefinitionFromContentSection(
            definition, section
        );
        definitionRepo.delete(definition);

        return String.format(
            "redirect:/%s/configuration/lifecycles", sectionIdentifierParam
        );
    }

    /**
     * Adds a localized label to a {@link LifecycleDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the lifecycle
     *                                definition.
     * @param localeParam             The locale of the value to add.
     * @param value                   The value to add.
     *
     * @return A redirect to the details view of the lifecycle.
     */
    @POST
    @Path("/{lifecycleIdentifier}/label/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addLifecycleDefinitionLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        definition.getLabel().addValue(new Locale(localeParam), value);
        definitionRepo.save(definition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam
        );
    }

    /**
     * Updates a localized label of a {@link LifecycleDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the lifecycle
     *                                definition.
     * @param localeParam             The locale of the value to update.
     * @param value                   The updated value.
     *
     * @return A redirect to the details view of the lifecycle.
     */
    @POST
    @Path("/{lifecycleIdentifier}/label/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editLifecycleDefinitionLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        definition.getLabel().addValue(new Locale(localeParam), value);
        definitionRepo.save(definition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam
        );
    }

    /**
     * Removes a localized label from a {@link LifecycleDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the lifecycle
     *                                definition.
     * @param localeParam             The locale of the value to remove.
     *
     * @return A redirect to the details view of the lifecycle.
     */
    @POST
    @Path("/{lifecycleIdentifier}/label/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeLifecycleDefinitionLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        definition.getLabel().removeValue(new Locale(localeParam));
        definitionRepo.save(definition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam
        );
    }

    /**
     * Adds a localized description to a {@link LifecycleDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the lifecycle
     *                                definition.
     * @param localeParam             The locale of the value to add.
     * @param value                   The value to add.
     *
     * @return A redirect to the details view of the lifecycle.
     */
    @POST
    @Path("/{lifecycleIdentifier}/description/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addLifecycleDefinitionDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        definition.getDescription().addValue(new Locale(localeParam), value);
        definitionRepo.save(definition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam
        );
    }

    /**
     * Updates a localized description of a {@link LifecycleDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the lifecycle
     *                                definition.
     * @param localeParam             The locale of the value to update.
     * @param value                   The updated value.
     *
     * @return A redirect to the details view of the lifecycle.
     */
    @POST
    @Path("/{lifecycleIdentifier}/description/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editLifecycleDefinitionDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        definition.getDescription().addValue(new Locale(localeParam), value);
        definitionRepo.save(definition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam
        );
    }

    /**
     * Removes a localized description from a {@link LifecycleDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the lifecycle
     *                                definition.
     * @param localeParam             The locale of the value to remove
     *
     * @return A redirect to the details view of the lifecycle.
     */
    @POST
    @Path("/{lifecycleIdentifier}/description/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeLifecycleDefinitionDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        definition.getDescription().removeValue(new Locale(localeParam));
        definitionRepo.save(definition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam
        );
    }

    /**
     * Adds a {@link PhaseDefinition} to a {@link LifecycleDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle.
     * @param label                   The label of the new phase definition.
     * @param defaultDelayDays        The day part of the default deplay.
     * @param defaultDelayHours       The hours part of the default delay.
     * @param defaultDelayMinutes     The minutes part of the default delay.
     * @param defaultDurationDays     The day part of the default duration.
     * @param defaultDurationHours    The hours part of the default duration.
     * @param defaultDurationMinutes  The minutes part of the default duration.
     *
     * @return A redirect to the details view of the
     *         {@link LifecycleDefinition}.
     */
    @POST
    @Path("/{lifecycleIdentifier}/phases/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addPhase(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @FormParam("label") final String label,
        @FormParam("defaultDelayDays") final long defaultDelayDays,
        @FormParam("defaultDelayHours") final long defaultDelayHours,
        @FormParam("defaultDelayMinutes") final long defaultDelayMinutes,
        @FormParam("defaultDurationDays") final long defaultDurationDays,
        @FormParam("defaultDurationHours") final long defaultDurationHours,
        @FormParam("defaultDurationMinutes") final long defaultDurationMinutes
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();

        final PhaseDefinition phaseDefinition = new PhaseDefinition();

        final Duration defaultDelay = new Duration();
        defaultDelay.setDays(defaultDelayDays);
        defaultDelay.setHours(defaultDelayHours);
        defaultDelay.setMinutes(defaultDelayMinutes);
        phaseDefinition.setDefaultDelay(defaultDelay.toMinutes());

        final Duration defaultDuration = new Duration();
        defaultDuration.setDays(defaultDurationDays);
        defaultDuration.setHours(defaultDurationHours);
        defaultDuration.setMinutes(defaultDurationMinutes);
        phaseDefinition.setDefaultDuration(defaultDuration.toMinutes());

        phaseDefinition
            .getLabel()
            .addValue(globalizationHelper.getNegotiatedLocale(), label);

        phaseDefinititionRepo.save(phaseDefinition);
        lifecycleManager.addPhaseDefinition(definition, phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam
        );
    }

    /**
     * Show the details view of a phase definition.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle
     *                                definition.
     * @param phaseIdentifierParam    the identifier of the lifecycle
     *                                definition.
     *
     * @return The template for the details view of the phase definition.
     */
    @GET
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showPhase(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        selectedLifecycleDefModel.setDisplayLabel(
            globalizationHelper.getValueFromLocalizedString(
                definition.getLabel()
            )
        );
        selectedLifecycleDefModel.setUuid(definition.getUuid());

        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, phaseIdentifierParam);
        if (!phaseDefinitionResult.isPresent()) {
            return showPhaseDefinitionNotFound(
                section,
                sectionIdentifierParam,
                phaseIdentifierParam
            );
        }

        final PhaseDefinition phaseDefinition = phaseDefinitionResult.get();
        selectedPhaseDefModel.setDefaultDelay(
            Duration.fromMinutes(
                phaseDefinition.getDefaultDelay()
            )
        );
        selectedPhaseDefModel.setDefaultDuration(
            Duration.fromMinutes(
                phaseDefinition.getDefaultDuration()
            )
        );
        selectedPhaseDefModel.setDefinitionId(phaseDefinition.getDefinitionId());
        final List<Locale> availableLocales
            = globalizationHelper.getAvailableLocales();
        final Set<Locale> descriptionLocales = phaseDefinition
            .getDescription()
            .getAvailableLocales();
        selectedPhaseDefModel.setDescription(
            phaseDefinition
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
        selectedPhaseDefModel.setUnusedDescriptionLocales(
            availableLocales
                .stream()
                .filter(locale -> !descriptionLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );
        final Set<Locale> labelLocales = phaseDefinition
            .getLabel()
            .getAvailableLocales();
        selectedPhaseDefModel.setLabel(
            phaseDefinition
                .getLabel()
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
        selectedPhaseDefModel.setUnusedLabelLocales(
            availableLocales
                .stream()
                .filter(locale -> !labelLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );
        selectedPhaseDefModel.setDisplayLabel(
            globalizationHelper.getValueFromLocalizedString(
                phaseDefinition.getLabel()
            )
        );

        return "org/librecms/ui/contentsection/configuration/lifecycle-phase.xhtml";
    }

    /**
     * Updates the parameters of a phase definition.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle.
     * @param phaseIdentifier         The identifier of the phase to update.
     * @param defaultDelayDays        The day part of the default deplay.
     * @param defaultDelayHours       The hours part of the default delay.
     * @param defaultDelayMinutes     The minutes part of the default delay.
     * @param defaultDurationDays     The day part of the default duration.
     * @param defaultDurationHours    The hours part of the default duration.
     * @param defaultDurationMinutes  The minutes part of the default duration.
     *
     * @return A redirect to the details view of the
     *         {@link LifecycleDefinition}.
     */
    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/@edit")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editPhase(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @FormParam("defaultDelayDays") final long defaultDelayDays,
        @FormParam("defaultDelayHours") final long defaultDelayHours,
        @FormParam("defaultDelayMinutes") final long defaultDelayMinutes,
        @FormParam("defaultDurationDays") final long defaultDurationDays,
        @FormParam("defaultDurationHours") final long defaultDurationHours,
        @FormParam("defaultDurationMinutes") final long defaultDurationMinutes
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, phaseIdentifierParam);
        if (!phaseDefinitionResult.isPresent()) {
            return showPhaseDefinitionNotFound(
                section,
                sectionIdentifierParam,
                phaseIdentifierParam
            );
        }
        final PhaseDefinition phaseDefinition = phaseDefinitionResult.get();

        final Duration defaultDelay = new Duration();
        defaultDelay.setDays(defaultDelayDays);
        defaultDelay.setHours(defaultDelayHours);
        defaultDelay.setMinutes(defaultDelayMinutes);
        phaseDefinition.setDefaultDelay(defaultDelay.toMinutes());

        final Duration defaultDuration = new Duration();
        defaultDuration.setDays(defaultDurationDays);
        defaultDuration.setHours(defaultDurationHours);
        defaultDuration.setMinutes(defaultDurationMinutes);
        phaseDefinition.setDefaultDuration(defaultDuration.toMinutes());

        phaseDefinititionRepo.save(phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s/phases/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam,
            phaseIdentifierParam
        );
    }

    /**
     * Removes a phase definition from the a lifecycle definition.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle
     *                                definition.
     * @param phaseIdentifierParam    The identifier of the phase definition to
     *                                remove.
     *
     * @return A redirect to the details view of the lifecycle definition.
     */
    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removePhase(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, phaseIdentifierParam);
        if (!phaseDefinitionResult.isPresent()) {
            return showPhaseDefinitionNotFound(
                section,
                sectionIdentifierParam,
                phaseIdentifierParam
            );
        }
        final PhaseDefinition phaseDefinition = phaseDefinitionResult.get();
        lifecycleManager.removePhaseDefinition(definition, phaseDefinition);
        phaseDefinititionRepo.delete(phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam
        );
    }

    /**
     * Adds a localized label to a {@link PhaseDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle
     *                                definition.
     * @param phaseIdentifierParam    Identifier of the {@link PhaseDefinition}.
     * @param localeParam             The locale of the value to add.
     * @param value                   The value to add.
     *
     * @return A redirect to the details view of the {@link PhaseDefinition}.
     */
    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/label/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addPhaseLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, phaseIdentifierParam);
        if (!phaseDefinitionResult.isPresent()) {
            return showPhaseDefinitionNotFound(
                section,
                sectionIdentifierParam,
                phaseIdentifierParam
            );
        }
        final PhaseDefinition phaseDefinition = phaseDefinitionResult.get();
        phaseDefinition.getLabel().addValue(new Locale(localeParam), value);
        phaseDefinititionRepo.save(phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s/phases/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam,
            phaseIdentifierParam
        );
    }

    /**
     * Updates the localized label of a {@link PhaseDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle
     *                                definition.
     * @param phaseIdentifierParam    Identifier of the {@link PhaseDefinition}.
     * @param localeParam             The locale of the value to update.
     * @param value                   The updated value.
     *
     * @return A redirect to the details view of the {@link PhaseDefinition}.
     */
    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/label/@edit/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editPhaseLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, phaseIdentifierParam);
        if (!phaseDefinitionResult.isPresent()) {
            return showPhaseDefinitionNotFound(
                section,
                sectionIdentifierParam,
                phaseIdentifierParam
            );
        }
        final PhaseDefinition phaseDefinition = phaseDefinitionResult.get();
        phaseDefinition.getLabel().addValue(new Locale(localeParam), value);
        phaseDefinititionRepo.save(phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s/phases/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam,
            phaseIdentifierParam
        );
    }

    /**
     * Removes a localized label of a {@link PhaseDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle
     *                                definition.
     * @param phaseIdentifierParam    Identifier of the {@link PhaseDefinition}.
     * @param localeParam             The locale of the value to remove.
     *
     * @return A redirect to the details view of the {@link PhaseDefinition}.
     */
    @POST
    @Path(
        "/{lifecycleIdentifier}/phases/{phaseIdentifier}/label/@remove/{locale}"
    )
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removePhaseLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, phaseIdentifierParam);
        if (!phaseDefinitionResult.isPresent()) {
            return showPhaseDefinitionNotFound(
                section,
                sectionIdentifierParam,
                phaseIdentifierParam
            );
        }
        final PhaseDefinition phaseDefinition = phaseDefinitionResult.get();
        phaseDefinition.getLabel().removeValue(new Locale(localeParam));
        phaseDefinititionRepo.save(phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s/phases/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam,
            phaseIdentifierParam
        );
    }

    /**
     * Adds a localized description to a {@link PhaseDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle
     *                                definition.
     * @param phaseIdentifierParam    Identifier of the {@link PhaseDefinition}.
     * @param localeParam             The locale of the value to add.
     * @param value                   The value to add.
     *
     * @return A redirect to the details view of the {@link PhaseDefinition}.
     */
    @POST
    @Path("/{lifecycleIdentifier}/phases/{phaseIdentifier}/description/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addPhaseDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, phaseIdentifierParam);
        if (!phaseDefinitionResult.isPresent()) {
            return showPhaseDefinitionNotFound(
                section,
                sectionIdentifierParam,
                phaseIdentifierParam
            );
        }
        final PhaseDefinition phaseDefinition = phaseDefinitionResult.get();
        phaseDefinition.getDescription().addValue(
            new Locale(localeParam), value
        );
        phaseDefinititionRepo.save(phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s/phases/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam,
            phaseIdentifierParam
        );
    }

    /**
     * Updates the localized description of a {@link PhaseDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle
     *                                definition.
     * @param phaseIdentifierParam    Identifier of the {@link PhaseDefinition}.
     * @param localeParam             The locale of the value to update.
     * @param value                   The updated value.
     *
     * @return A redirect to the details view of the {@link PhaseDefinition}.
     */
    @POST
    @Path(
        "/{lifecycleIdentifier}/phases/{phaseIdentifier}/description/@edit/{locale}"
    )
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editPhaseDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, phaseIdentifierParam);
        if (!phaseDefinitionResult.isPresent()) {
            return showPhaseDefinitionNotFound(
                section,
                sectionIdentifierParam,
                phaseIdentifierParam
            );
        }
        final PhaseDefinition phaseDefinition = phaseDefinitionResult.get();
        phaseDefinition.getDescription().addValue(
            new Locale(localeParam), value
        );
        phaseDefinititionRepo.save(phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s/phases/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam,
            phaseIdentifierParam
        );
    }

    /**
     * Removes a localized description of a {@link PhaseDefinition}.
     *
     * @param sectionIdentifierParam  The identifier of the current content
     *                                section.
     * @param lifecycleIdentiferParam The identifier of the current lifecycle
     *                                definition.
     * @param phaseIdentifierParam    Identifier of the {@link PhaseDefinition}.
     * @param localeParam             The locale of the value to remove.
     *
     * @return A redirect to the details view of the {@link PhaseDefinition}.
     */
    @POST
    @Path(
        "/{lifecycleIdentifier}/phases/{phaseIdentifier}/description/@remove/{locale}"
    )
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removePhaseDescription(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("lifecycleIdentifier") final String lifecycleIdentiferParam,
        @PathParam("phaseIdentifier") final String phaseIdentifierParam,
        @PathParam("locale") final String localeParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, lifecycleIdentiferParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, phaseIdentifierParam);
        if (!phaseDefinitionResult.isPresent()) {
            return showPhaseDefinitionNotFound(
                section,
                sectionIdentifierParam,
                phaseIdentifierParam
            );
        }
        final PhaseDefinition phaseDefinition = phaseDefinitionResult.get();
        phaseDefinition.getDescription().removeValue(new Locale(localeParam));
        phaseDefinititionRepo.save(phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s/phases/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam,
            phaseIdentifierParam
        );
    }

    /**
     * Builds a {@link LifecycleDefinitionListModel} for a
     * {@link LifecycleDefinition}.
     *
     * @param definition The lifecycle definition.
     *
     * @return A {@link LifecycleDefinitionListModel} for the
     *         {@code definition}.
     */
    private LifecycleDefinitionListModel buildListModel(
        final LifecycleDefinition definition
    ) {
        final LifecycleDefinitionListModel model
            = new LifecycleDefinitionListModel();
        model.setDefinitionId(definition.getDefinitionId());
        model.setUuid(definition.getUuid());
        model.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                definition.getLabel()
            )
        );
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                definition.getDescription()
            )
        );
        return model;
    }

    /**
     * Finds a lifecycle definition.
     *
     * @param section                   The current content section.
     * @param definitionIdentifierParam The identifier of the definition.
     *
     * @return An {@link Optional} with the {@link LifecycleDefinition} or an
     *         empty {@link Optional} if the current content section has no
     *         matching {@link LifecycleDefinition}.
     */
    private Optional<LifecycleDefinition> findLifecycleDefinition(
        final ContentSection section, final String definitionIdentifierParam
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            definitionIdentifierParam
        );
        switch (identifier.getType()) {
            case ID:
                return section
                    .getLifecycleDefinitions()
                    .stream()
                    .filter(
                        definition -> definition.getDefinitionId() == Long
                        .parseLong(identifier.getIdentifier())
                    ).findAny();
            default:
                return section
                    .getLifecycleDefinitions()
                    .stream()
                    .filter(
                        definition -> definition.getUuid().equals(identifier
                            .getIdentifier())
                    ).findAny();
        }
    }

    /**
     * Shows the "lifecycle definition not found" error page.
     *
     * @param section              The current content section.
     * @param definitionIdentifier The identifier of the lifecycle definition.
     *
     * @return The template for the "lifecycle definition not found" error page.
     */
    private String showLifecycleDefinitionNotFound(
        final ContentSection section,
        final String definitionIdentifier
    ) {
        models.put("sectionIdentifier", section.getLabel());
        models.put("definitionIdentifier", definitionIdentifier);
        return "org/librecms/ui/contentsection/configuration/lifecycle-not-found.xhtml";
    }

    /**
     * Build the {@link PhaseDefinitionModel} for a {@link PhaseDefinition}.
     *
     * @param definition The phase definition.
     *
     * @return A {@link PhaseDefinitionModel} for the {@code definition}.
     */
    private PhaseDefinitionModel buildPhaseDefinitionModel(
        final PhaseDefinition definition
    ) {
        final PhaseDefinitionModel model = new PhaseDefinitionModel();
        model.setDefaultDelay(
            Duration.fromMinutes(definition.getDefaultDelay())
        );
        model.setDefaultDuration(
            Duration.fromMinutes(definition.getDefaultDuration())
        );
        model.setDefinitionId(definition.getDefinitionId());
        model.setDescription(
            globalizationHelper
                .getValueFromLocalizedString(definition.getDescription())
        );
        model.setLabel(
            globalizationHelper
                .getValueFromLocalizedString(definition.getLabel())
        );
        return model;
    }

    /**
     * Finds a {@link PhaseDefinition}.
     *
     * @param lifecycleDefinition            The lifecycle definition.
     * @param phaseDefinitionIdentifierParam The identifier of the phase
     *                                       definition.
     *
     * @return An {@link Optional} with the {@link PhaseDefinition} or an empty
     *         {@link Optional} if the {@link LifecycleDefinition} has the
     *         {@link PhaseDefinition} with the provided identifier.
     */
    private Optional<PhaseDefinition> findPhaseDefinition(
        final LifecycleDefinition lifecycleDefinition,
        final String phaseDefinitionIdentifierParam
    ) {
        return lifecycleDefinition
            .getPhaseDefinitions()
            .stream()
            .filter(
                definition -> definition.getDefinitionId() == Long
                .parseLong(phaseDefinitionIdentifierParam.substring(3))
            ).findAny();
    }

    /**
     * Shows the "phase definition not found" error page.
     *
     * @param section                   The current content section.
     * @param definitionIdentifier      The identifier of the lifecycle
     *                                  definition.
     * @param phaseDefinitionIdentifier The idenfifier of the phase definition.
     *
     * @return The template for the "phase definition not found" error page.
     */
    private String showPhaseDefinitionNotFound(
        final ContentSection section,
        final String definitionIdentifier,
        final String phaseDefinitionIdentifier
    ) {
        models.put("sectionIdentifier", section.getLabel());
        models.put("lifecycleDefinitionIdentifier", definitionIdentifier);
        models.put("phaseDefinitionIdentifier", phaseDefinitionIdentifier);
        return "org/librecms/ui/contentsection/configuration/lifecyclephase-not-found.xhtml";
    }

}
