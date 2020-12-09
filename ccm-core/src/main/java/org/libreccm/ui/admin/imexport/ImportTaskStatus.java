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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ImportTaskStatus implements Comparable<ImportTaskStatus> {

    private String name;

    private LocalDateTime started;

    private ImExportTaskStatus status;
    
    private Throwable exception;

    public String getName() {
        return name;
    }

   
    protected void setName(final String name) {
        this.name = name;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    protected void setStarted(final LocalDateTime started) {
        this.started = started;
    }

    public ImExportTaskStatus getStatus() {
        return status;
    }

    protected void setStatus(final ImExportTaskStatus status) {
        this.status = status;
    }
    
        public Throwable getException() {
        return exception;
    }

    protected void setException(final Throwable exception) {
        this.exception = exception;
    }

     @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(name);
        hash = 53 * hash + Objects.hashCode(started);
        hash = 53 * hash + Objects.hashCode(status);
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
        if (!(obj instanceof ExportTaskStatus)) {
            return false;
        }
        final ImportTaskStatus other = (ImportTaskStatus) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(started, other.getStarted())) {
            return false;
        }
        return status == other.getStatus();
    }

    
    public boolean canEqual(final Object obj) {
        return obj instanceof ImportTaskStatus;
    }

    @Override
    public int compareTo(final ImportTaskStatus other) {
        return Comparator
            .nullsFirst(Comparator
                .comparing(ImportTaskStatus::getName)
                .thenComparing(ImportTaskStatus::getStarted)
            )
            .compare(this, other);
    }

    @Override
    public String toString() {
        return String.format(
            "%s{ "
                + "name = %s, "
                + "started = %s, "
                + "status = %s"
                + " }",
            super.toString(),
            name,
            DateTimeFormatter.ISO_DATE_TIME.withZone(
                ZoneId.systemDefault()
            ).format(started),
            Objects.toString(status)
        );
    }

}
