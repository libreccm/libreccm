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
@Named("SelectedWorkflowTemplateModel")
public class SelectedWorkflowTemplateModel {
    
    private long workflowId;
    
    private String uuid;
    
    private Map<String, String> name;
    
    private Map<String, String> description;
    
    private List<WorkflowTaskTemplateListModel> tasks;

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

    public Map<String, String> getName() {
        return Collections.unmodifiableMap(name);
    }

    public void setName(Map<String, String> name) {
        this.name = new HashMap<>(name);
    }

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public void setDescription(final Map<String, String> description) {
        this.description = new HashMap<>(description);
    }

    public List<WorkflowTaskTemplateListModel> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void setTasks(final List<WorkflowTaskTemplateListModel> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }
    
    
    
}
