/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.workflow;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.libreccm.core.CcmObject;
import org.libreccm.core.Identifiable;
import org.libreccm.l10n.LocalizedString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * A task is part of a workflow and represents a specific step in the creation
 * process of an {@link CcmObject}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "WORKFLOW_TASKS", schema = DB_SCHEMA)
@Inheritance(strategy = InheritanceType.JOINED)
//Can't reduce complexity yet, Task is a fine name
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity",
                   "PMD.ShortClassName",
                   "PMD.TooManyMethods",
                   "PMD.AvoidDuplicateLiterals"})
@NamedQueries({
    @NamedQuery(
        name = "Task.findByUuid",
        query = "SELECT t FROM Task t WHERE t.uuid = :uuid")
    ,
    @NamedQuery(
        name = "Task.countUnfinishedAndActiveTasksForWorkflow",
        query = "SELECT COUNT(t) FROM Task t "
                    + "WHERE t.taskState != org.libreccm.workflow.TaskState.FINISHED "
                + "AND t.active = true "
                    + "AND t.workflow = :workflow")
    ,
    @NamedQuery(
        name = "Task.countUnfinishedTasksForWorkflow",
        query = "SELECT COUNT(t) FROM Task t "
                    + "WHERE t.taskState != org.libreccm.workflow.TaskState.FINISHED "
                + "AND t.workflow = :workflow"
    )
    ,
    @NamedQuery(
        name = "Task.findEnabledTasks",
        query = "SELECT t FROM Task t "
                    + "WHERE t.workflow = :workflow "
                    + "AND t.taskState = org.libreccm.workflow.TaskState.ENABLED "
                + "AND t.active = true"
    )
    ,
    @NamedQuery(
        name = "Task.findFinishedTasks",
        query = "SELECT t FROM Task t "
                    + "WHERE t.workflow = :workflow "
                    + "AND t.taskState = org.libreccm.workflow.TaskState.FINISHED")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = TaskIdResolver.class,
                  property = "uuid")
public class Task implements Identifiable, Serializable {

    private static final long serialVersionUID = 8161343036908150426L;

    /**
     * Database ID of the task.
     */
    @Id
    @Column(name = "TASK_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long taskId;

    /**
     * The UUID of the task.
     */
    @Column(name = "UUID", unique = true, nullable = false)
    @NotNull
    private String uuid;

    /**
     * A human readable, localisable label for the task.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "WORKFLOW_TASK_LABELS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "TASK_ID")}))
    private LocalizedString label;

    /**
     * A description of the task.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "WORKFLOW_TASK_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "TASK_ID")}))
    private LocalizedString description;

    /**
     * Is the task active?
     */
    @Column(name = "ACTIVE")
    private boolean active;

    /**
     * The state of the task.
     */
    @Column(name = "TASK_STATE", length = 512)
    @Enumerated(EnumType.STRING)
    private TaskState taskState;

    /**
     * The workflow to which the task belongs.
     */
    @ManyToOne
    @JoinColumn(name = "WORKFLOW_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private Workflow workflow;

    /**
     * Tasks which depend on this task.
     */
    @ManyToMany(mappedBy = "dependsOn", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Task> dependentTasks;

    /**
     * The dependencies of this task.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "WORKFLOW_TASK_DEPENDENCIES",
               schema = DB_SCHEMA,
               joinColumns = {
                   @JoinColumn(name = "DEPENDS_ON_TASK_ID")},
               inverseJoinColumns = {
                   @JoinColumn(name = "DEPENDENT_TASK_ID")})
    @JsonIdentityReference(alwaysAsId = true)
    private List<Task> dependsOn;

    /**
     * Comments for the task.
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "TASK_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private List<TaskComment> comments;

    public Task() {
        super();

        label = new LocalizedString();
        description = new LocalizedString();
        dependentTasks = new ArrayList<>();
        dependsOn = new ArrayList<>();
        comments = new ArrayList<>();
        active = false;
    }

    public long getTaskId() {
        return taskId;
    }

    protected void setTaskId(final long taskId) {
        this.taskId = taskId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {
        Objects.requireNonNull(label);
        this.label = label;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        Objects.requireNonNull(description);
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    protected void setTaskState(final TaskState taskState) {
        this.taskState = taskState;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    protected void setWorkflow(final Workflow workflow) {
        this.workflow = workflow;
    }

    public List<Task> getDependentTasks() {
        if (dependentTasks == null) {
            return null;
        } else {
            return Collections.unmodifiableList(dependentTasks);
        }
    }

    protected void setDependentTasks(final List<Task> dependentTasks) {
        this.dependentTasks = dependentTasks;
    }

    protected void addDependentTask(final Task task) {
        dependentTasks.add(task);
    }

    protected void removeDependentTask(final Task task) {
        dependentTasks.remove(task);
    }

    public List<Task> getDependsOn() {
        if (dependsOn == null) {
            return null;
        } else {
            return Collections.unmodifiableList(dependsOn);
        }
    }

    protected void setDependsOn(final List<Task> dependsOn) {
        this.dependsOn = dependsOn;
    }

    protected void addDependsOn(final Task task) {
        dependsOn.add(task);
    }

    protected void removeDependsOn(final Task task) {
        dependsOn.remove(task);
    }

    public List<TaskComment> getComments() {
        if (comments == null) {
            return null;
        } else {
            return Collections.unmodifiableList(comments);
        }
    }

    protected void setComments(final List<TaskComment> comments) {
        this.comments = comments;
    }

    public void addComment(final TaskComment comment) {
        comments.add(comment);
    }

    public void removeComment(final TaskComment comment) {
        comments.remove(comment);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (taskId ^ (taskId >>> 32));
        hash = 79 * hash + Objects.hashCode(uuid);
        hash = 79 * hash + Objects.hashCode(label);
        hash = 79 * hash + Objects.hashCode(description);
        hash = 79 * hash + (active ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(taskState);
        hash = 79 * hash + Objects.hashCode(workflow);
        return hash;
    }

    @Override
    //Can't reduce complexity yet
    @SuppressWarnings({"PMD.CyclomaticComplexity",
                       "PMD.StdCyclomaticComplexity",
                       "PMD.ModifiedCyclomaticComplexity",
                       "PMD.NPathComplexity"})
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Task)) {
            return false;
        }
        final Task other = (Task) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (taskId != other.getTaskId()) {
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }
        if (!Objects.equals(label, other.getLabel())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (active != other.isActive()) {
            return false;
        }
        if (!Objects.equals(taskState, other.getTaskState())) {
            return false;
        }
        return Objects.equals(workflow, other.getWorkflow());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Task;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "taskId = %d, "
                                 + "uuid = \"%s\", "
                                 + "label = %s, "
                                 + "active = %b, "
                                 + "taskState = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             taskId,
                             uuid,
                             Objects.toString(label),
                             active,
                             taskState,
                             data);
    }

}
