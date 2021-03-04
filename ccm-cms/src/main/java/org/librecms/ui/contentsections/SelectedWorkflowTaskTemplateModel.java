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
@Named("SelectedWorkflowTaskTemplateModel")
public class SelectedWorkflowTaskTemplateModel {
    
    private long taskId;
    
    private String uuid;
    
    private Map<String, String> label;
    
    private Map<String, String> description;
    
    private List<WorkflowTaskTemplateListModel> blockedTasks;

    private List<WorkflowTaskTemplateListModel> blockingTasks;
    
      public long getTaskId() {
        return taskId;
    }

    public void setTaskId(final long taskId) {
        this.taskId = taskId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
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

    public List<WorkflowTaskTemplateListModel> getBlockedTasks() {
        return Collections.unmodifiableList(blockedTasks);
    }

    public void setBlockedTasks(
        final List<WorkflowTaskTemplateListModel> blockedTasks
    ) {
        this.blockedTasks = new ArrayList<>(blockedTasks);
    }

    public List< WorkflowTaskTemplateListModel> getBlockingTasks() {
        return Collections.unmodifiableList(blockingTasks);
    }

    public void setBlockingTasks(
        final List<WorkflowTaskTemplateListModel> blockingTasks
    ) {
        this.blockingTasks = new ArrayList<>(blockingTasks);
    }

    
}
