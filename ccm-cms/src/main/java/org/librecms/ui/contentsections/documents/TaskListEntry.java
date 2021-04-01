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

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskState;
import org.librecms.contentsection.ContentItem;

/**
 * An entry in the list of tasks of the workflow assigned to an
 * {@link ContentItem}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class TaskListEntry {

    /**
     * The UUID of the task.
     */
    private String taskUuid;

    /**
     * The label of the task. This value is determined from {@link Task#label}
     * using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String label;

    /**
     * The description of the task. This value is determined from
     * {@link Task#description} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String description;

    /**
     * The state of the task.
     */
    private TaskState taskState;

    /**
     * Is the task the current task?
     */
    private boolean currentTask;

    /**
     * Is the task assigned to the current user?
     */
    private boolean assignedToCurrentUser;

    /**
     * Is the task locked?
     */
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
