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

import static org.libreccm.core.CoreConstants.*;

import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "workflow_tasks", schema = DB_SCHEMA)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//Can't reduce complexity yet, Task is a fine name
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity",
                   "PMD.ShortClassName",
                   "PMD.TooManyMethods",
                   "PMD.AvoidDuplicateLiterals"})
public class Task implements Serializable {

    private static final long serialVersionUID = 8161343036908150426L;

    @Id
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long taskId;

    @Embedded
    @AssociationOverride(
            name = "values",
            joinTable = @JoinTable(name = "workflow_task_labels",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "task_id")}))
    private LocalizedString label;

    @Embedded
    @AssociationOverride(
            name = "values",
            joinTable = @JoinTable(name = "workflow_tasks_descriptions",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "task_id")}))
    private LocalizedString description;

    @Column(name = "active")
    private boolean active;

    @Column(name = "task_state", length = 512)
    private String taskState;

    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @ManyToMany(mappedBy = "dependsOn")
    private List<Task> dependentTasks;

    @ManyToMany
    @JoinTable(name = "workflow_task_dependencies",
               schema = DB_SCHEMA,
               joinColumns = {
                   @JoinColumn(name = "depends_on_task_id")},
               inverseJoinColumns = {
                   @JoinColumn(name = "dependent_task_id")})
    private List<Task> dependsOn;

    @ElementCollection
    @JoinTable(name = "workflow_task_comments",
               schema = DB_SCHEMA,
               joinColumns = {
                   @JoinColumn(name = "task_id")})
    @Column(name = "comment")
    @Lob
    private List<String> comments;

    public Task() {
        super();

        label = new LocalizedString();
        description = new LocalizedString();
        dependentTasks = new ArrayList<>();
        dependsOn = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(final long taskId) {
        this.taskId = taskId;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {
        this.label = label;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(final String taskState) {
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

    public List<String> getComments() {
        if (comments == null) {
            return null;
        } else {
            return Collections.unmodifiableList(comments);
        }
    }

    protected void setComments(final List<String> comments) {
        this.comments = comments;
    }

    public void addComment(final String comment) {
        comments.add(comment);
    }

    public void removeComment(final String comment) {
        comments.remove(comment);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (taskId ^ (taskId >>> 32));
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
                                     + "label = %s, "
                                     + "active = %b, "
                                     + "taskState = \"%s\", "
                                     + "workflow = %s, "
                                     + "dependentTasks = %s, "
                                     + "dependsOn = %s%s"
                                     + " }",
                             super.toString(),
                             taskId,
                             Objects.toString(label),
                             active,
                             taskState,
                             Objects.toString(workflow),
                             Objects.toString(dependentTasks),
                             Objects.toString(dependsOn),
                             data);
    }

}
