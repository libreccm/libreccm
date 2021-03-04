/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("SelectedPhaseDefinitionModel")
public class SelectedPhaseDefinitionModel {

    private long definitionId;

    private Map<String, String> label;

    private Map<String, String> description;

    private long defaultDelay;

    private long defaultDuration;

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(final long definitionId) {
        this.definitionId = definitionId;
    }

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

    public long getDefaultDelay() {
        return defaultDelay;
    }

    public void setDefaultDelay(final long defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    public long getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(final long defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

}
