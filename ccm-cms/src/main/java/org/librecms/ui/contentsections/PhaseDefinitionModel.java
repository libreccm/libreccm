/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PhaseDefinitionModel {

    private long definitionId;
    
    private String label;

    private String description;

    private Duration defaultDelay;

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
