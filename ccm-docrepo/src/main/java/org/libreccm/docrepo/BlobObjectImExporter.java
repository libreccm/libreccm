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
import org.libreccm.imexport.Processes;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Im/Exporter for importing and exporting {@code BlobObject}s from the
 * system into a specified file and the other way around.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * 
 */
@RequestScoped
@Processes(BlobObject.class)
public class BlobObjectImExporter extends AbstractEntityImExporter<BlobObject> {

    @Inject
    private BlobObjectRepository blobObjectRepository;

    @Override
    protected Class<BlobObject> getEntityClass() {
        return BlobObject.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final BlobObject entity) {
        
        blobObjectRepository.save(entity);
    }
}
