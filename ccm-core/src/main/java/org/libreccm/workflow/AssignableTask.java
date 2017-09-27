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
import org.libreccm.portation.Portable;
import org.libreccm.security.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * A task which can be assigned to a user. Also a {@code AssignableTask} can be
 * locked by a user to indicate that the user is currently working on the object
 * to which the workflow to which the task belongs is assigned.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "WORKFLOW_ASSIGNABLE_TASKS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "AssignableTask.findByUuid",
        query = "SELECT t FROM AssignableTask t WHERE t.uuid = :uuid")
    ,
    @NamedQuery(
        name = "AssignableTask.findLockedBy",
        query = "SELECT t FROM AssignableTask t WHERE t.lockingUser = :user")
    ,
    @NamedQuery(
        name = "AssignableTask.findEnabledTasksForWorkflow",
        query = "SELECT t FROM AssignableTask t "
                    + "WHERE t.lockingUser = :user "
                    + "AND t.workflow = :workflow"
    )
    ,
    @NamedQuery(
        name = "AssignableTask.findAssignedTasks",
        query = "SELECT DISTINCT t FROM AssignableTask t "
                    + "JOIN t.assignments a "
                    + "WHERE a.role IN :roles "
                    + "AND t.workflow = :workflow "
                    + "AND t.active = true")
    ,
    @NamedQuery(
        name = "AssignableTask.isAssignedTo",
        query = "SELECT (CASE WHEN COUNT(t) > 0 THEN true ELSE false END) "
                    + "FROM AssignableTask t "
                    + "JOIN t.assignments a "
                    + "WHERE a.role IN :roles "
                    + "AND t = :task"
    )
    ,
    @NamedQuery(
        name = "AssignableTask.findOverdueTasks",
        query = "SELECT t FROM AssignableTask t "
                    + "WHERE t.workflow = :workflow "
                    + "AND t.dueDate < :now")
})
//Can't reduce complexity yet
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = AssignableTaskIdResolver.class,
                  property = "uuid")
public class AssignableTask extends Task implements Serializable, Portable {

    private static final long serialVersionUID = 4188064584389893019L;

    /**
     * Is the task locked?
     */
    @Column(name = "LOCKED")
    private boolean locked;

    /**
     * The user which has locked the task (if the task is locked).
     */
    @OneToOne
    @JoinColumn(name = "LOCKING_USER_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private User lockingUser;

    /**
     * The date on which the task was started.
     */
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    /**
     * The date on which the task should be finished.
     */
    @Column(name = "DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    /**
     * How long did it take to complete the task?
     */
    @Column(name = "DURATION_MINUTES")
    private long durationMinutes;

    /**
     * Which user should be used as sender of notification emails for the task?
     */
    @OneToOne
    @JoinColumn(name = "NOTIFICATION_SENDER")
    @SuppressWarnings("PMD.LongVariable") //Shorter name would not be descriptive
    @JsonIdentityReference(alwaysAsId = true)
    private User notificationSender;

    /**
     * The roles to which task is assigned.
     */
    @OneToMany(mappedBy = "task")
    @JsonIgnore
    private List<TaskAssignment> assignments;

    public AssignableTask() {
        super();
        assignments = new ArrayList<>();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    public User getLockingUser() {
        return lockingUser;
    }

    public void setLockingUser(final User lockingUser) {
        this.lockingUser = lockingUser;
    }

    public Date getStartDate() {
        if (startDate == null) {
            return null;
        } else {
            return new Date(startDate.getTime());
        }
    }

    public void setStartDate(final Date startDate) {
        if (startDate == null) {
            this.startDate = null;
        } else {
            this.startDate = new Date(startDate.getTime());
        }
    }

    public Date getDueDate() {
        if (dueDate == null) {
            return null;
        } else {
            return new Date(dueDate.getTime());
        }
    }

    public void setDueDate(final Date dueDate) {
        if (dueDate == null) {
            this.dueDate = null;
        } else {
            this.dueDate = new Date(dueDate.getTime());
        }
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(final long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public User getNotificationSender() {
        return notificationSender;
    }

    @SuppressWarnings("PMD.LongVariable")
    public void setNotificationSender(final User notificationSender) {
        this.notificationSender = notificationSender;
    }

    public List<TaskAssignment> getAssignments() {
        if (assignments == null) {
            return null;
        } else {
            return Collections.unmodifiableList(assignments);
        }
    }

    protected void setAssignments(final List<TaskAssignment> assignments) {
        this.assignments = assignments;
    }

    protected void addAssignment(final TaskAssignment assignment) {
        assignments.add(assignment);
    }

    protected void removeAssignment(final TaskAssignment assignment) {
        assignments.remove(assignment);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + (locked ? 1 : 0);
        hash = 37 * hash + Objects.hashCode(lockingUser);
        hash = 37 * hash + Objects.hashCode(startDate);
        hash = 37 * hash + Objects.hashCode(dueDate);
        hash
            = 37 * hash + (int) (durationMinutes ^ (durationMinutes >>> 32));
        hash = 37 * hash + Objects.hashCode(notificationSender);
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

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof AssignableTask)) {
            return false;
        }
        final AssignableTask other = (AssignableTask) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (locked != other.isLocked()) {
            return false;
        }
        if (!Objects.equals(lockingUser, other.getLockingUser())) {
            return false;
        }
        if (!Objects.equals(startDate, other.getStartDate())) {
            return false;
        }
        if (!Objects.equals(dueDate, other.getDueDate())) {
            return false;
        }
        if (durationMinutes != other.getDurationMinutes()) {
            return false;
        }
        return Objects.equals(notificationSender, other.getNotificationSender());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof AssignableTask;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", locked = %b, "
                                                + "lockingUser = %s, "
                                                + "startDate = %tF %<tT,"
                                                + "dueDate = %tF %<tT, "
                                                + "durationMinutes = %d, "
                                                + "notificationSender = %s%s",
                                            locked,
                                            Objects.toString(lockingUser),
                                            startDate,
                                            dueDate,
                                            durationMinutes,
                                            Objects.toString(notificationSender),
                                            data));
    }

}
