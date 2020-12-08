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
import java.util.concurrent.Future;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ImExportTaskStatus implements Comparable<ImExportTaskStatus> {

    private String name;

    private LocalDateTime started;

    private Future<?> status;

    public String getName() {
        return name;
    }

    protected void setName(final String name) {
        this.name = name;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public String getStartedAsIso() {
        return DateTimeFormatter.ISO_DATE_TIME.withZone(
            ZoneId.systemDefault()).format(started
        );
    }

    protected void setStarted(final LocalDateTime started) {
        this.started = started;
    }

    public Future<?> getStatus() {
        return status;
    }

    protected void setStatus(final Future<?> status) {
        this.status = status;
    }

    public boolean isDone() {
        return status.isDone();
    }

    protected void cancel() {
        status.cancel(true);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + Objects.hashCode(this.started);
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
        if (!(obj instanceof ImExportTaskStatus)) {
            return false;
        }
        final ImExportTaskStatus other = (ImExportTaskStatus) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        return Objects.equals(this.started, other.getStarted());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ImExportTaskStatus;
    }

    @Override
    public int compareTo(final ImExportTaskStatus other) {
        return Comparator
            .nullsFirst(Comparator
                .comparing(ImExportTaskStatus::getName)
                .thenComparing(ImExportTaskStatus::getStarted)
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
