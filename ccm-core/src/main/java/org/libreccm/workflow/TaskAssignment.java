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
import org.libreccm.portation.Portable;
import org.libreccm.security.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * Represents the assignment of a {@link AssignableTask} to a {@link Role}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "WORKFLOW_TASK_ASSIGNMENTS", schema = DB_SCHEMA)
@JsonIdentityInfo(generator = TaskAssignmentIdGenerator.class,
                  property = "customAssignId")
public class TaskAssignment implements Serializable, Portable {

    private static final long serialVersionUID = -4427537363301565707L;

    /**
     * Database ID of the entity.
     */
    @Id
    @Column(name = "TASK_ASSIGNMENT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long taskAssignmentId;

    /**
     * The task.
     */
    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private AssignableTask task;

    /**
     * The role to which the task is assigned.
     */
    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private Role role;

    public long getTaskAssignmentId() {
        return taskAssignmentId;
    }

    protected void setTaskAssignmentId(final long taskAssignmentId) {
        this.taskAssignmentId = taskAssignmentId;
    }

    public AssignableTask getTask() {
        return task;
    }

    protected void setTask(final AssignableTask task) {
        this.task = task;
    }

    public Role getRole() {
        return role;
    }

    protected void setRole(final Role role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash
                   + (int) (taskAssignmentId ^ (taskAssignmentId >>> 32));
        hash = 67 * hash + Objects.hashCode(task);
        hash = 67 * hash + Objects.hashCode(role);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof TaskAssignment)) {
            return false;
        }
        final TaskAssignment other = (TaskAssignment) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (taskAssignmentId != other.getTaskAssignmentId()) {
            return false;
        }
        if (!Objects.equals(task, other.getTask())) {
            return false;
        }
        return Objects.equals(role, other.getRole());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof TaskAssignment;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "taskAssignmentId = %d, "
                                 + "task = %s, "
                                 + "role = %s"
                                 + " }",
                             super.toString(),
                             taskAssignmentId,
                             Objects.toString(task),
                             Objects.toString(role));
    }

}
