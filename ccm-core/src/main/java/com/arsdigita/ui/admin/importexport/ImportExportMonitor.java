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
package com.arsdigita.ui.admin.importexport;

import java.util.Date;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class ImportExportMonitor {

    /**
     * Indicates that an export process is running.
     */
    private boolean exportActive = false;

    /**
     * Indicates that an import process is running.
     */
    private boolean importActive = false;

    private boolean exportReportAvailable = false;

    private boolean importReportAvailable = false;

    /**
     * A UUID to identify to the import/export process.
     */
    private String importExportProcessUuid;

    /**
     * A {@link StringBuffer} for creating a report of the import/export
     * process. The report of the last import/export process will be stored
     * until another import/export process is started or the application is
     * restarted.
     */
    private StringBuffer report;

    public boolean isExportActive() {
        return exportActive;
    }

    public boolean isImportActive() {
        return importActive;
    }

    /**
     * Returns {@code true} if either an import process or an export process is
     * active.
     *
     * @return {@code true} if an import or export process is active.
     */
    public boolean isLocked() {
        return exportActive || importActive;
    }

    public boolean isExportReportAvailable() {
        return exportReportAvailable;
    }

    public boolean isImportReportAvailable() {
        return importReportAvailable;
    }

    public void startExport() {
        if (exportActive) {
            throw new IllegalStateException(
                "Can't start a new export process "
                    + "because there is already an export process running");
        }

        if (importActive) {
            throw new IllegalStateException(
                "Can't start a export process "
                    + "because there is already an import process running");
        }

        exportActive = true;
        importExportProcessUuid = UUID.randomUUID().toString();
        report = new StringBuffer();
        report.append(String
            .format("Lock for export process %s accquired at %tF %<tT%n",
                    importExportProcessUuid,
                    new Date()));
    }

    public void finishExport() {
        exportActive = false;
        report.append(String
            .format("Lock for export process %s released at %tF %<tT%n",
                    importExportProcessUuid,
                    new Date()));
        importExportProcessUuid = null;
        exportReportAvailable = true;
    }

    public void startImport() {
        if (exportActive) {
            throw new IllegalStateException(
                "Can't start a new import process "
                    + "because there is already an export process running");
        }

        if (importActive) {
            throw new IllegalStateException(
                "Can't start a new import process "
                    + "because there is already an import process running");
        }

        importActive = true;
        importExportProcessUuid = UUID.randomUUID().toString();
        report = new StringBuffer();
        report.append(String
            .format("Lock for import process %s released at %tF %<tT%n",
                    importExportProcessUuid,
                    new Date()));
    }

    public void finishImport() {
        importActive = false;
        report.append(String
            .format("Lock for import process %s released at %tF %<tT%n",
                    importExportProcessUuid,
                    new Date()));
        importExportProcessUuid = null;
        importReportAvailable = true;
    }

    public StringBuffer getReport() {
        return report;
    }

}
