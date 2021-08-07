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
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model providing some data for the views of the {@link PublishStep}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsDocumentPublishStepModel")
public class PublishStepModel {

    private String assignedLifecycleLabel;

    private String assignedLifecycleDescription;

    private boolean live;

    /**
     * The phases of the lifecycle assigned to the current content item.
     */
    private List<PhaseListEntry> phases;

    /**
     * A list of the available lifecycles.
     */
    private List<LifecycleListEntry> availableLivecycles;

    /**
     * Get the label of the lifecycle assigned to the current content item. The
     * value is determined from the label of the definition of the lifecycle
     * using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     *
     * @return The label of the lifecycle assigned to the current content item,
     *         or an empty string if no lifecycle is assigned to the item.
     */
    public String getAssignedLifecycleLabel() {
        return assignedLifecycleLabel;
    }

    protected void setAssignedLifecycleLabel(
        final String assignedLifecycleLabel
    ) {
        this.assignedLifecycleLabel = assignedLifecycleLabel;
    }

    public boolean getLive() {
        return live;
    }

    public void setLive(final boolean live) {
        this.live = live;
    }

    /**
     * Get the description of the lifecycle assigned to the current content
     * item. The value is determined from the description of the definition of
     * the lifecycle using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     *
     * @return The description of the lifecycle assigned to the current content
     *         item, or an empty string if no lifecycle is assigned to the item.
     */
    public String getAssignedLifecycleDecription() {
        return assignedLifecycleDescription;
    }

    protected void setAssignedLifecycleDescription(
        final String assignedLifecycleDescription
    ) {
        this.assignedLifecycleDescription = assignedLifecycleDescription;
    }

    public PublishStepModel() {
        availableLivecycles = new ArrayList<>();
        phases = new ArrayList<>();
    }

    public List<LifecycleListEntry> getAvailableLifecycles() {
        return Collections.unmodifiableList(availableLivecycles);
    }

    public void setAvailableLifecycles(
        final List<LifecycleListEntry> availableListcycles
    ) {
        this.availableLivecycles = new ArrayList<>(availableListcycles);
    }

    public List<PhaseListEntry> getPhases() {
        return Collections.unmodifiableList(phases);
    }

    public void setPhases(final List<PhaseListEntry> phases) {
        this.phases = new ArrayList<>(phases);
    }

    public String getToday() {
        return LocalDateTime
            .now()
            .format(DateTimeFormatter.ISO_DATE.withZone(ZoneId.systemDefault()));
    }
    
    public String getNow() {
        return LocalTime
            .now()
            .format(
                DateTimeFormatter
                    .ofPattern("HH:mm")
                    .withZone(ZoneId.systemDefault())
            );
    }

}
