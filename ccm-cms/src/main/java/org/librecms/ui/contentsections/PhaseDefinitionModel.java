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
import org.librecms.lifecycle.PhaseDefinition;

/**
 * Model for displaying a {@link PhaseDefinition}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PhaseDefinitionModel {

    /**
     * The ID of the definition.
     */
    private long definitionId;

    /**
     * The label of the definition. This value is determined from
     * {@link PhaseDefinition#label} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String label;

    /**
     * The description of the definition. This value is determined from
     * {@link PhaseDefinition#description} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String description;

    /**
     * The default delay of the phase.
     */
    private Duration defaultDelay;

    /**
     * The default duration of the phase.
     */
    private Duration defaultDuration;

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(final long definitionId) {
        this.definitionId = definitionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Duration getDefaultDelay() {
        return defaultDelay;
    }

    public void setDefaultDelay(final Duration defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    public Duration getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(final Duration defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

}
