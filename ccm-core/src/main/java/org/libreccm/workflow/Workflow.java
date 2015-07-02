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

import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "workflows")
public class Workflow implements Serializable {

    private static final long serialVersionUID = 4322500264543325829L;

    @Id
    @Column(name = "workflow_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long workflowId;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "workflow_names",
                               joinColumns = {
                                   @JoinColumn(name = "workflow_id")}))
    private LocalizedString name;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "workflow_descriptions",
                               joinColumns = {
                                   @JoinColumn(name = "workflow_id")
                               }))
    private LocalizedString description;

    @OneToMany(mappedBy = "workflow")
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

    public void setWorkflowId(final long workflowId) {
        this.workflowId = workflowId;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(final LocalizedString name) {
        this.name = name;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
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
        hash = 79 * hash + (int) (this.workflowId ^ (this.workflowId >>> 32));
        hash = 79 * hash + Objects.hashCode(this.name);
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

        if (this.workflowId != other.getWorkflowId()) {
            return false;
        }
        return Objects.equals(this.name, other.getName());
        
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Workflow;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "workflowId = %d, "
                                 + "name = \"%s\""
                                 + " }",
                             super.toString(),
                             workflowId,
                             name);
    }

}
