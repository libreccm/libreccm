/*
 * Copyright (C) 2021 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
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
