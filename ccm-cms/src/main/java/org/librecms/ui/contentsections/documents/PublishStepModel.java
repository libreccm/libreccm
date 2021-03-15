/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsDocumentPublishStepModel")
public class PublishStepModel {

    private List<LifecycleListEntry> availableListcycles;

    private List<PhaseListEntry> phases;

    public PublishStepModel() {
        availableListcycles = new ArrayList<>();
        phases = new ArrayList<>();
    }

    public List<LifecycleListEntry> getAvailableListcycles() {
        return Collections.unmodifiableList(availableListcycles);
    }

    public void setAvailableLifecycles(
        final List<LifecycleListEntry> availableListcycles
    ) {
        this.availableListcycles = new ArrayList<>(availableListcycles);
    }

    public List<PhaseListEntry> getPhases() {
        return Collections.unmodifiableList(phases);
    }

    public void setPhases(final List<PhaseListEntry> phases) {
        this.phases = new ArrayList<>(phases);
    }

}
