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
package com.arsdigita.cms.ui.workflow;

import org.libreccm.workflow.Task;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class TaskTableModelData {
    
    private final Iterator<Task> tasks;
    private final Map<Task, String> dependencies;
    
    protected TaskTableModelData(final Iterator<Task> tasks,
                                 final Map<Task, String> dependencies) {
        this.tasks = tasks;
        this.dependencies = dependencies;
    }

    public Iterator<Task> getTasks() {
        return tasks;
    }

    public Map<Task, String> getDependencies() {
        return dependencies;
    }
    
    
    
}
