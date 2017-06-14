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

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.files.CcmFiles;
import org.libreccm.files.FileAccessException;
import org.libreccm.files.FileDoesNotExistException;
import org.libreccm.files.InsufficientPermissionsException;

import java.util.Date;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Stateless
public class ImportExportController {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ImportExportMonitor monitor;

    @Inject
    private CcmFiles ccmFiles;

    /**
     * Return the fully qualified class names for all available exporters.
     *
     * @return
     */
    public List<String> getAvailableExporters() {
        // Note: Return value may needs to be adjusted. Possibly we need a
        // data structure which contains some more information like a 
        // localised label for the exporters etc.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Create an export into the directory configured by
     * {@link KernelConfig#exportPath} using the provided exporters.
     *
     * @param exporters The exporters to use.
     */
    @Asynchronous
    public void export(final List<String> exporters) {
        monitor.startExport();

        final long start = System.currentTimeMillis();

        while (System.currentTimeMillis() < start + 60 * 1000) {
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException ex) {
                throw new UnexpectedErrorException(ex);
            }
            monitor.getReport().append(String.format("...%tF %<tT%n",
                                                     new Date()));
        }

        monitor.finishExport();
    }

    public List<String> getAvailableImportFiles() {

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        final String importPath = kernelConfig.getImportPath();

        try {
            return ccmFiles.listFiles(importPath);
        } catch (FileAccessException
                 | FileDoesNotExistException
                 | InsufficientPermissionsException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }
    
    public void importFiles(final List<String> filesToImport) {
        monitor.startImport();
        
        // ToDol Import code here
        
        monitor.finishImport();
    }
 
}
