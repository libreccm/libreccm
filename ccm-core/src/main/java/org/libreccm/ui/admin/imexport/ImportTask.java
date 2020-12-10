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

import java.time.LocalDate;

/**
 * Data for an import task.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ImportTask {

    /**
     * Name of the import archive.
     */
    private final String name;

    /**
     * When was the import task started?
     */
    private final LocalDate started;

    /**
     * The status of the import task.
     */
    private final ImportTaskStatus status;

    public ImportTask(
        final String name,
        final LocalDate started,
        final ImportTaskStatus status
    ) {
        this.name = name;
        this.started = started;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStarted() {
        return started;
    }

    public ImportTaskStatus getStatus() {
        return status;
    }

}
