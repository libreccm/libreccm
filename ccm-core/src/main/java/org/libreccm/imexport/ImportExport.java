/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.imexport;


import org.libreccm.files.CcmFilesConfiguration;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Central service for importing and exporting entities.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ImportExport {

    @Inject
    @Any
    private Instance<EntityImporter<?>> importers;

    @Inject
    @Any
    private Instance<EntityExporter<?>> exporters;

    /**
     * Exports the provided entities. The export will be written to a to the
     * {@code exports} directory in the CCM files directory. If {@code split} is
     * {@code false} a file with the name provided by {@link exportName} will be
     * generated. Otherwise a directory with the provided name will be
     * generated. All files will be placed into that directory. For the main
     * file the provided name will be used.
     *
     *
     * @param entities The entities to export.
     * @param exportName The name file to which the export is written.
     * @param split Split the entities by package?
     *
     * @see CcmFilesConfiguration#dataPath
     */
    public void exportEntities(final List<Exportable> entities,
                               final String exportName,
                               final boolean split) {

        throw new UnsupportedOperationException();
    }
    
    /**
     * Imports all entities from the files in the {@link imports} directory inside
     * the CCM files data directory. The data to import can either be a file with
     * the provided name or a directory with the provided name. If it is a directory 
     * the entry file must also use the provided name.
     * 
     * If an entity which is part of the import already exists in the database
     * the values from the import are used to update the entity.
     * 
     * @param importName The name of the import.
     * 
     * @see CcmFilesConfiguration#dataPath
     */
    public void importEntities(final String importName) {
        
        throw new UnsupportedOperationException();
        
    }

}
