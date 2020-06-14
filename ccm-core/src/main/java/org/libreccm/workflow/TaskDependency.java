/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.libreccm.core.CoreConstants;
import org.libreccm.imexport.Exportable;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "WORKFLOW_TASK_DEPENDENCIES", schema = CoreConstants.DB_SCHEMA)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  property = "uuid")
public class TaskDependency implements Serializable, Exportable {

    private static final long serialVersionUID = -4383255770131633943L;

    @Id
    @Column(name = "TASK_DEPENDENCY_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long taskDependencyId;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "BLOCKED_TASK_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private Task blockedTask;

    @ManyToOne
    @JoinColumn(name = "BLOCKING_TASK_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private Task blockingTask;

    public long getTaskDependencyId() {
        return taskDependencyId;
    }

    protected void setTaskDependencyId(final long taskDependencyId) {
        this.taskDependencyId = taskDependencyId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public Task getBlockedTask() {
        return blockedTask;
    }

    protected void setBlockedTask(final Task blockedTask) {
        this.blockedTask = blockedTask;
    }

    public Task getBlockingTask() {
        return blockingTask;
    }

    protected void setBlockingTask(final Task blockingTask) {
        this.blockingTask = blockingTask;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash
            = 89 * hash + (int) (taskDependencyId ^ (taskDependencyId >>> 32));
        hash = 89 * hash + Objects.hashCode(blockedTask);
        hash = 89 * hash + Objects.hashCode(blockingTask);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TaskDependency)) {
            return false;
        }
        final TaskDependency other = (TaskDependency) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (taskDependencyId != other.getTaskDependencyId()) {
            return false;
        }
        if (!Objects.equals(blockedTask, other.getBlockedTask())) {
            return false;
        }
        return Objects.equals(blockingTask, other.getBlockingTask());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof TaskDependency;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "taskDependencyId = %d, "
                                 + "blockedTask = %s, "
                                 + "blockingTask = %s%s"
                                 + " }",
                             super.toString(),
                             taskDependencyId,
                             Objects.toString(blockedTask),
                             Objects.toString(blockingTask),
                             data);
    }

}
