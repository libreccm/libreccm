/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.imexport;

import org.libreccm.imexport.Exportable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

/**
 * Data for an export task.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ExportTask {

    /**
     * Name of the export archive.
     */
    private final String name;

    /**
     * When was the export task started?
     */
    private final LocalDate started;

    /**
     * The entities to export.
     */
    private final Collection<Exportable> entities;
    
    /**
     * The status of the export task.
     */
    private final ExportTaskStatus status;

    public ExportTask(
        final String name,
        final LocalDate started,
        final Collection<Exportable> entities,
        final ExportTaskStatus status
    ) {
        this.name = name;
        this.started = started;
        this.entities = entities;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStarted() {
        return started;
    }

    public Collection<Exportable> getEntities() {
        return Collections.unmodifiableCollection(entities);
    }

    public ExportTaskStatus getStatus() {
        return status;
    }
    
    

}
