/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.toolbox.ui.Property;

import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;
import org.librecms.lifecycle.PhaseDefinition;
import org.librecms.lifecycle.PhaseDefinititionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class LifecycleAdminPaneController {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private LifecycleDefinitionRepository lifecycleDefRepo;

    @Inject
    private PhaseDefinititionRepository phaseDefRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<LifecycleDefinition> getLifecyclesForContentSection(
        final ContentSection section) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getLifecycleDefinitions());
    }

    @Transactional
    public List<Map<String, String>> listLifecyclesForContentSection(
        final ContentSection section
    ) {
        return getLifecyclesForContentSection(section)
            .stream()
            .map(this::buildLifecycleListItem)
            .collect(Collectors.toList());
    }

    private Map<String, String> buildLifecycleListItem(
        final LifecycleDefinition lifecycleDefinition) {
        final Map<String, String> item = new HashMap<>();
        item.put(
            LifecycleListModelBuilder.LIFECYCLE_DEF_ID,
            Long.toString(lifecycleDefinition.getDefinitionId())
        );
        item.put(
            LifecycleListModelBuilder.LIFECYCLE_DEF_LABEL,
            lifecycleDefinition
            .getLabel()
            .getValue(KernelConfig.getConfig().getDefaultLocale())
        );
        return item;
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Property> getLifecycleProperties(
        final LifecycleDefinition ofLifecycleDefinition
    ) {
        final LifecycleDefinition definition = lifecycleDefRepo
        .findById(ofLifecycleDefinition.getDefinitionId())
        .orElseThrow(
            () -> new IllegalArgumentException(
                String.format(
                    "No LifecycleDefinition with ID %d found.",
                    ofLifecycleDefinition.getDefinitionId()
                )
            )
        );
        
        final KernelConfig kernelConfig = confManager
        .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();
        
        final List<Property> properties = new ArrayList<>();
        properties.add(
            new Property(
                new GlobalizedMessage(
                    "cms.ui.lifecycle.name", 
                    CmsConstants.CMS_BUNDLE
                ),
                definition.getLabel().getValue(defaultLocale)
            )
        );
       properties.add(
            new Property(
                new GlobalizedMessage(
                    "cms.ui.lifecycle.description", 
                    CmsConstants.CMS_BUNDLE
                ),
                definition.getDescription().getValue(defaultLocale)
            )
        );
       return properties;
    }

    /**
     * Create a new lifecycle definition
     *
     * @param section     The content section which the
     *                    {@link LifecycleDefinition} is created.
     * @param name        The name of the new lifecycle definition.
     * @param description The description of the new lifecycle definition.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public LifecycleDefinition createLifecycleDefinition(
        final ContentSection section,
        final String name,
        final String description) {

        Objects.requireNonNull(section, "ContentSection can't be null.");
        Objects.requireNonNull(name, "Name can't be null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name can't be empty.");
        }

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        final LifecycleDefinition definition = new LifecycleDefinition();

        definition.getLabel().addValue(defaultLocale, name);
        definition.getDescription().addValue(defaultLocale, description);
        lifecycleDefRepo.save(definition);

        sectionManager.addLifecycleDefinitionToContentSection(definition,
                                                              contentSection);

        return definition;
    }

    /**
     * Update the name and/or description of a {@link LifecycleDefinition}.
     *
     * @param definition  The definition to update.
     * @param name        The new name.
     * @param description The new description.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void updateLifecycleDefinition(final LifecycleDefinition definition,
                                          final String name,
                                          final String description) {

        Objects.requireNonNull(definition, "definition can't be null.");
        Objects.requireNonNull(name, "name can't be null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name can't be empty.");
        }

        final LifecycleDefinition lifecycleDefinition = lifecycleDefRepo
            .findById(definition.getDefinitionId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No LifecycleDefinition with ID %d in the database. "
                + "Where did that ID come from?",
            definition.getDefinitionId())));

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        if (!lifecycleDefinition.getLabel().getValue(defaultLocale).equals(name)) {
            lifecycleDefinition.getLabel().addValue(defaultLocale, name);
        }

        if (!lifecycleDefinition.getDescription().getValue(defaultLocale)
            .equals(description)) {
            lifecycleDefinition.getDescription().addValue(defaultLocale,
                                                          description);
        }

        lifecycleDefRepo.save(lifecycleDefinition);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteLifecycleDefinition(
        final LifecycleDefinition definition,
        final ContentSection section) {

        Objects.requireNonNull(definition, "definition can't be null.");

        final LifecycleDefinition lifecycleDefinition = lifecycleDefRepo
            .findById(definition.getDefinitionId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No LifecycleDefinition with ID %d in the database. "
                + "Where did that ID come from?",
            definition.getDefinitionId())));
        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        sectionManager.removeLifecycleDefinitionFromContentSection(
            lifecycleDefinition,
            contentSection);
        lifecycleDefRepo.delete(lifecycleDefinition);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<PhaseDefinition> getPhaseDefinitions(
        final LifecycleDefinition definition) {

        Objects.requireNonNull(definition);

        final LifecycleDefinition lifecycleDefinition = lifecycleDefRepo
            .findById(definition.getDefinitionId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No LifecycleDefinition with ID %d in the database. "
                + "Where did that ID come from?",
            definition.getDefinitionId())));

        return new ArrayList<>(lifecycleDefinition.getPhaseDefinitions());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void addPhaseDefinition(final LifecycleDefinition definition,
                                   final String label,
                                   final String description,
                                   final int delayDays,
                                   final int delayHours,
                                   final int delayMinutes,
                                   final int durationDays,
                                   final int durationHours,
                                   final int durationMinutes) {

        Objects.requireNonNull(definition, "definition can't be null");
        Objects.requireNonNull(label, "label can't be null");
        if (label.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "label can't be empty."));
        }

        final LifecycleDefinition lifecycleDefinition = lifecycleDefRepo
            .findById(definition.getDefinitionId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No LifecycleDefinition with ID %d in the database. "
                + "Where did that ID come from?",
            definition.getDefinitionId())));

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final PhaseDefinition phaseDefinition = new PhaseDefinition();
        lifecycleDefinition.addPhaseDefinition(phaseDefinition);

        phaseDefinition.getLabel().addValue(defaultLocale, label);
        phaseDefinition.getDescription().addValue(defaultLocale, description);
        int delay = delayDays * 24 * 60 * 60;
        delay += delayHours * 60 * 60;
        delay += delayMinutes * 60;
        phaseDefinition.setDefaultDelay(delay);
        int duration = durationDays * 24 * 60 * 60;
        duration += durationHours * 60 * 60;
        duration += durationMinutes * 60;
        phaseDefinition.setDefaultDuration(duration);

        phaseDefRepo.save(phaseDefinition);
        lifecycleDefRepo.save(lifecycleDefinition);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void updatePhaseDefinition(final PhaseDefinition definition,
                                      final String label,
                                      final String description,
                                      final int delayDays,
                                      final int delayHours,
                                      final int delayMinutes,
                                      final int durationDays,
                                      final int durationHours,
                                      final int durationMinutes) {

        Objects.requireNonNull(definition, "definition can't be null");
        Objects.requireNonNull(label, "label can't be null");
        if (label.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "label can't be empty."));
        }

        final PhaseDefinition phaseDefinition = phaseDefRepo
            .findById(definition.getDefinitionId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No PhaseDefinition with ID %d in the database. "
                + "Where did that ID come from?",
            definition.getDefinitionId())));

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        phaseDefinition.getLabel().addValue(defaultLocale, label);
        phaseDefinition.getDescription().addValue(defaultLocale, description);
        int delay = delayDays * 24 * 60 * 60;
        delay += delayHours * 60 * 60;
        delay += delayMinutes * 60;
        phaseDefinition.setDefaultDelay(delay);
        int duration = durationDays * 24 * 60 * 60;
        duration += durationHours * 60 * 60;
        duration += durationMinutes * 60;
        phaseDefinition.setDefaultDuration(duration);

        phaseDefRepo.save(phaseDefinition);

    }

}
