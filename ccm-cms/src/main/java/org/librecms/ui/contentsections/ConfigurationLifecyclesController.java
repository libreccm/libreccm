/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/lifecycles")
public class ConfigurationLifecyclesController {

    @Inject
    private AdminPermissionsChecker adminPermissionsChecker;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private ContentSectionsUi sectionsUi;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private LifecycleManager lifecycleManager;

    @Inject
    private LifecycleDefinitionRepository definitionRepo;

    @Inject
    private Models models;

    @Inject
    private PhaseDefinititionRepository phaseDefinititionRepo;

    @Inject
    private SelectedLifecycleDefinitionModel selectedLifecycleDefModel;

    @Inject
    private SelectedPhaseDefinitionModel selectedPhaseDefModel;

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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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

    @POST
    @Path("/@add")
    @AuthorizationRequired
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final LifecycleDefinition definition = new LifecycleDefinition();
        definition
            .getLabel()
            .addValue(globalizationHelper.getNegotiatedLocale(), label);
        sectionManager.addLifecycleDefinitionToContentSection(
            definition, section
        );

        return String.format(
            "redirect:/%s/configuration/lifecycles", sectionIdentifierParam
        );
    }

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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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
        lifecycleManager.addPhaseDefinition(definition, phaseDefinition);

        return String.format(
            "redirect:/%s/configuration/lifecycles/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam
        );
    }

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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
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
            = findPhaseDefinition(definition, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, sectionIdentifierParam);
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
            "redirect:/%s/configuration/lifecycles/%s/phases/%s",
            sectionIdentifierParam,
            lifecycleIdentiferParam,
            phaseIdentifierParam
        );
    }

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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, sectionIdentifierParam);
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

    @POST
    @Path(
        "/{lifecycleIdentifier}/phases/{phaseIdentifier}/label/@remove/{locale}")
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, sectionIdentifierParam);
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, sectionIdentifierParam);
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

    @POST
    @Path(
        "/{lifecycleIdentifier}/phases/{phaseIdentifier}/description/@edit/{locale}")
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, sectionIdentifierParam);
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

    @POST
    @Path(
        "/{lifecycleIdentifier}/phases/{phaseIdentifier}/description/@remove/{locale}")
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
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<LifecycleDefinition> definitionResult
            = findLifecycleDefinition(section, sectionIdentifierParam);
        if (!definitionResult.isPresent()) {
            return showLifecycleDefinitionNotFound(
                section, sectionIdentifierParam
            );
        }
        final LifecycleDefinition definition = definitionResult.get();
        final Optional<PhaseDefinition> phaseDefinitionResult
            = findPhaseDefinition(definition, sectionIdentifierParam);
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

    private String showLifecycleDefinitionNotFound(
        final ContentSection section,
        final String definitionIdentifier
    ) {
        models.put("sectionIdentifier", section.getLabel());
        models.put("definitionIdentifier", definitionIdentifier);
        return "org/librecms/ui/contentsection/configuration/lifecycle-not-found.xhtml";
    }

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

    private Optional<PhaseDefinition> findPhaseDefinition(
        final LifecycleDefinition lifecycleDefinition,
        final String phaseDefinitionIdentifierParam
    ) {
        return lifecycleDefinition
            .getPhaseDefinitions()
            .stream()
            .filter(
                definition -> definition.getDefinitionId() == Long
                .parseLong(phaseDefinitionIdentifierParam)
            ).findAny();
    }

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
