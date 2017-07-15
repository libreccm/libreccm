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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.libreccm.core.CcmObject;
import org.libreccm.core.Identifiable;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.portation.Portable;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * A workflow is a collection of tasks which are performed on an object. Tasks
 * can depend on each other.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "WORKFLOWS", schema = DB_SCHEMA)
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(
        name = "Workflow.findByUuid",
        query = "SELECT W FROM Workflow w WHERE w.uuid = :uuid")
    ,
    @NamedQuery(
        name = "Workflow.findForObject",
        query = "SELECT w FROM Workflow w "
                    + "WHERE W.object = :object")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = WorkflowIdResolver.class,
                  property = "uuid")
public class Workflow implements Identifiable, Serializable, Portable {

    private static final long serialVersionUID = 4322500264543325829L;

    /**
     * Database id of the workflow.
     */
    @Id
    @Column(name = "WORKFLOW_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long workflowId;

    /**
     * UUID of the workflow.
     */
    @Column(name = "UUID", unique = true, nullable = false)
    @NotNull
    private String uuid;

    /**
     * The template which was used to generate the workflow.
     */
    @ManyToOne
    @JoinColumn(name = "TEMPLATE_ID")
    private WorkflowTemplate template;

    /**
     * Human readable name of the workflow.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "WORKFLOW_NAMES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "WORKFLOW_ID")}))
    private LocalizedString name = new LocalizedString();

    /**
     * Description of the workflow.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "WORKFLOW_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "WORKFLOW_ID")
                               }))
    private LocalizedString description = new LocalizedString();

    /**
     * The current state of the workflow.
     */
    @Column(name = "WORKFLOW_STATE")
    @Enumerated(EnumType.STRING)
    private WorkflowState state;

    /**
     * Is the workflow active?
     */
    @Column(name = "ACTIVE")
    private boolean active;

    /**
     * The task state of the workflow. This field is a leftover from the old
     * implementation of workflow were workflow extended {@link Task}. This
     * field is here because we wanted to keep the basic logic.
     */
    @Column(name = "TASKS_STATE")
    @Enumerated(EnumType.STRING)
    private TaskState tasksState;

    /**
     * The object for which this workflow was generated.
     */
    @OneToOne
    @JoinColumn(name = "OBJECT_ID")
    private CcmObject object;

    /**
     * The tasks belonging to this workflow.
     */
    @OneToMany(mappedBy = "workflow")
    @JsonIgnore
    private List<Task> tasks;

    public Workflow() {
        super();

        name = new LocalizedString();
        description = new LocalizedString();
        tasks = new ArrayList<>();
    }

    public long getWorkflowId() {
        return workflowId;
    }

    protected void setWorkflowId(final long workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public WorkflowTemplate getTemplate() {
        return template;
    }

    protected void setTemplate(final WorkflowTemplate template) {
        this.template = template;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(final LocalizedString name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        Objects.requireNonNull(description);
        this.description = description;
    }

    public WorkflowState getState() {
        return state;
    }

    protected void setState(final WorkflowState state) {
        this.state = state;
    }

    public boolean isActive() {
        return active;
    }

    protected void setActive(final boolean active) {
        this.active = active;
    }

    public TaskState getTasksState() {
        return tasksState;
    }

    protected void setTasksState(final TaskState tasksState) {
        this.tasksState = tasksState;
    }

    public CcmObject getObject() {
        return object;
    }

    protected void setObject(final CcmObject object) {
        this.object = object;
    }

    public List<Task> getTasks() {
        if (tasks == null) {
            return null;
        } else {
            return Collections.unmodifiableList(tasks);
        }
    }

    protected void setTasks(final List<Task> tasks) {
        this.tasks = tasks;
    }

    protected void addTask(final Task task) {
        tasks.add(task);
    }

    protected void removeTask(final Task task) {
        tasks.remove(task);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (int) (workflowId ^ (workflowId >>> 32));
        hash = 79 * hash + Objects.hashCode(uuid);
        hash = 79 * hash + Objects.hashCode(name);
        hash = 79 * hash + Objects.hashCode(description);
        hash = 79 * hash + Objects.hashCode(state);
        hash = 79 * hash + (active ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(tasksState);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Workflow)) {
            return false;
        }
        final Workflow other = (Workflow) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (workflowId != other.getWorkflowId()) {
            return false;
        }

        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }

        if (!Objects.equals(name, other.getName())) {
            return false;
        }

        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }

        if (!Objects.equals(state, other.getState())) {
            return false;
        }

        if (active != other.isActive()) {
            return false;
        }

        return Objects.equals(tasksState, other.getTasksState());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Workflow;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "workflowId = %d, "
                                 + "uuid = \"%s\", "
                                 + "name = \"%s\", "
                                 + "description = \"%s\", "
                                 + "state = \"%s\", "
                                 + "active = %b%s"
                                 + " }",
                             super.toString(),
                             workflowId,
                             uuid,
                             Objects.toString(name),
                             Objects.toString(description),
                             Objects.toString(state),
                             active,
                             data);
    }

}
