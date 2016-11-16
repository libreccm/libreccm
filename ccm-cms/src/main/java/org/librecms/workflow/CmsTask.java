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
package org.librecms.workflow;

import org.libreccm.workflow.AssignableTask;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "WORKFLOW_TASKS", schema = DB_SCHEMA)
public class CmsTask extends AssignableTask implements Serializable {

    private static final long serialVersionUID = -3988352366529930659L;
    
    @Column(name = "TASK_TYPE")
    @Enumerated(EnumType.STRING)
    private CmsTaskType taskType;

    public CmsTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(final CmsTaskType taskType) {
        this.taskType = taskType;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + Objects.hashCode(taskType);
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
        if (!super.equals(obj)) {
            return false;
        }
        
        if (obj instanceof CmsTask) {
            return false;
        }
        final CmsTask other = (CmsTask) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        return Objects.equals(taskType, other.getTaskType());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof CmsTask;
    }
    
    @Override
    public String toString(final String data) {
        return super.toString(String.format(", taskType = %s%s",
                                            Objects.toString(taskType),
                                            data));
    }
}
