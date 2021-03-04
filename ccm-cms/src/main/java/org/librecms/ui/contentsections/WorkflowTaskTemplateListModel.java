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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class WorkflowTaskTemplateListModel {

    private long taskId;

    private String uuid;

    private String label;

    private String description;

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

}
