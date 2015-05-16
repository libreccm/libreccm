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

import org.libreccm.core.User;
import org.libreccm.core.UserGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "workflow_user_tasks")
public class UserTask extends Task implements Serializable {

    private static final long serialVersionUID = 4188064584389893019L;

    @Column(name = "locked")
    private boolean locked;

    @OneToOne
    @JoinColumn(name = "locking_user_id")
    private User lockingUser;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(name = "duration_minutes")
    private long durationMinutes;

    @OneToOne
    @JoinColumn(name = "notification_sender")
    private User notificationSender;

    @OneToMany
    @JoinTable(name = "workflow_user_task_assigned_users",
               joinColumns = {
                   @JoinColumn(name = "user_task_id")},
               inverseJoinColumns = {
                   @JoinColumn(name = "assigned_user_id")})
    private List<User> assignedUsers;

    @OneToMany
    @JoinTable(name = "workflow_user_task_assigned_groups",
               joinColumns = {
                   @JoinColumn(name = "user_task_id")},
               inverseJoinColumns = {
                   @JoinColumn(name = "assigned_group_id")})
    private List<UserGroup> assignedGroups;

    public UserTask() {
        super();
        assignedUsers = new ArrayList<>();
        assignedGroups = new ArrayList<>();
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
        return new Date(startDate.getTime());
    }

    public void setStartDate(final Date startDate) {
        this.startDate = new Date(startDate.getTime());
    }

    public Date getDueDate() {
        return new Date(dueDate.getTime());
    }

    public void setDueDate(final Date dueDate) {
        this.dueDate = new Date(dueDate.getTime());
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

    public void setNotificationSender(final User notificationSender) {
        this.notificationSender = notificationSender;
    }

    public List<User> getAssignedUsers() {
        return Collections.unmodifiableList(assignedUsers);
    }

    protected void setAssignedUsers(final List<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    protected void addAssignedUser(final User user) {
        assignedUsers.add(user);
    }

    protected void removeAssignedUser(final User user) {
        assignedUsers.remove(user);
    }

    public List<UserGroup> getAssignedGroups() {
        return Collections.unmodifiableList(assignedGroups);
    }

    protected void setAssignedGroups(final List<UserGroup> assignedGroups) {
        this.assignedGroups = assignedGroups;
    }

    protected void addAssignedGroup(final UserGroup group) {
        assignedGroups.add(group);
    }

    protected void removeAssignedGroup(final UserGroup group) {
        assignedGroups.remove(group);
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
        hash = 37 * hash + Objects.hashCode(assignedUsers);
        hash = 37 * hash + Objects.hashCode(assignedGroups);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserTask other = (UserTask) obj;
        if (!other.canEqual(obj)) {
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
        if (!Objects.equals(notificationSender, other.getNotificationSender())) {
            return false;
        }
        if (!Objects.equals(assignedUsers, other.getAssignedUsers())) {
            return false;
        }
        return Objects.equals(assignedGroups, other.getAssignedGroups());
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
