/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.workflow.TaskState;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class TaskListEntry {

    private String taskUuid;

    private String label;

    private String description;

    private TaskState taskState;

    private boolean currentTask;

    private boolean assignedToCurrentUser;

    private boolean locked;

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(final String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public boolean isCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(final boolean currentTask) {
        this.currentTask = currentTask;
    }

    public boolean isAssignedToCurrentUser() {
        return assignedToCurrentUser;
    }

    public void setAssignedToCurrentUser(final boolean assignedToCurrentUser) {
        this.assignedToCurrentUser = assignedToCurrentUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

}
