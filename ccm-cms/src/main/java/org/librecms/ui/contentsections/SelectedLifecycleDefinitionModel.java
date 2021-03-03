/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SelectedLifecycleModel")
public class SelectedLifecycleDefinitionModel {

    private String uuid;
    
    private Map<String, String> label;

    private Map<String, String> description;

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

}
