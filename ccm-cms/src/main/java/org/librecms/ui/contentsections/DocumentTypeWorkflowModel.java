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
public class DocumentTypeWorkflowModel {

    private long workflowId;

    private String uuid;

    private String name;

    private String description;

    private boolean defaultWorkflow;

    public long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(final long workflowId) {
        this.workflowId = workflowId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isDefaultWorkflow() {
        return defaultWorkflow;
    }

    public void setDefaultWorkflow(final boolean defaultWorkflow) {
        this.defaultWorkflow = defaultWorkflow;
    }

}
