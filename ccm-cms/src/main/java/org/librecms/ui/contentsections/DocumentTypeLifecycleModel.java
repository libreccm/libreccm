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
public class DocumentTypeLifecycleModel {

    private long definitionId;

    private String uuid;

    private String label;

    private String description;

    private boolean defaultLifecycle;

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(final long definitionId) {
        this.definitionId = definitionId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
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

    public boolean isDefaultLifecycle() {
        return defaultLifecycle;
    }

    public void setDefaultLifecycle(final boolean defaultLifecycle) {
        this.defaultLifecycle = defaultLifecycle;
    }

}
