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

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.PhaseDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model for the details view of {@link LifecycleDefinition}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SelectedLifecycleDefinitionModel")
public class SelectedLifecycleDefinitionModel {

    /**
     * The UUID of the lifecycle definition.
     */
    private String uuid;

    /**
     * The display label of the lifecycle definition. This value determined from
     * {@link LifecycleDefinition#label} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String displayLabel;

    /**
     * The localized labels of the lifecycle definition.
     */
    private Map<String, String> label;

    /**
     * The locales for which no localized label has been defined yet.
     */
    private List<String> unusedLabelLocales;

    /**
     * The localized descriptions of the lifecycle definition.
     */
    private Map<String, String> description;

    /**
     * The locales for which no localized description has been defined yet.
     */
    private List<String> unusedDescriptionLocales;

    /**
     * The {@link PhaseDefinition}s of the {@link LifecycleDefinition}.
     */
    private List<PhaseDefinitionModel> phaseDefinitions;

    public Map<String, String> getLabel() {
        return Collections.unmodifiableMap(label);
    }

    public void setLabel(final Map<String, String> label) {
        this.label = new HashMap<>(label);
    }

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public void setDescription(final Map<String, String> description) {
        this.description = new HashMap<>(description);
    }

    public List<PhaseDefinitionModel> getPhaseDefinitions() {
        return Collections.unmodifiableList(phaseDefinitions);
    }

    public void setPhaseDefinitions(
        final List<PhaseDefinitionModel> phaseDefinitions
    ) {
        this.phaseDefinitions = new ArrayList<>(phaseDefinitions);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(final String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public List<String> getUnusedLabelLocales() {
        return Collections.unmodifiableList(unusedLabelLocales);
    }

    public void setUnusedLabelLocales(final List<String> unusedLabelLocales) {
        this.unusedLabelLocales = new ArrayList<>(unusedLabelLocales);
    }

    public boolean getHasUnusedLabelLocales() {
        return !unusedLabelLocales.isEmpty();
    }

    public List<String> getUnusedDescriptionLocales() {
        return Collections.unmodifiableList(unusedDescriptionLocales);
    }

    public void setUnusedDescriptionLocales(
        final List<String> unusedDescriptionLocales
    ) {
        this.unusedDescriptionLocales
            = new ArrayList<>(unusedDescriptionLocales);
    }

    public boolean getHasUnusedDescriptionLocales() {
        return !unusedDescriptionLocales.isEmpty();
    }

}
