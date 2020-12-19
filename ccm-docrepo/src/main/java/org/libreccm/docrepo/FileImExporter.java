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
package org.libreccm.docrepo;

import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.Processes;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.RequestScoped;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Im/Exporter for importing and exporting {@code File}s from the system into a
 * specified file and the other way around.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(File.class)
public class FileImExporter extends AbstractEntityImExporter<File> {

    @Inject
    private FileRepository fileRepository;

    @Override
    public Class<File> getEntityClass() {
        return File.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final File portableObject) {
        fileRepository.save(portableObject);
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        return Collections.emptySet();
    }

    @Override
    protected File reloadEntity(final File entity) {
        return fileRepository
            .findById(Objects.requireNonNull(entity).getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "File entity %s not found in database",
                        Objects.toString(entity)
                    )
                )
            );
    }

}
