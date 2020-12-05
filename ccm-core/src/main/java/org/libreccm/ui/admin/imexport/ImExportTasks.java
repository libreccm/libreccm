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
import org.libreccm.imexport.ImportExport;

import java.util.Collection;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Stateless
public class ImExportTasks {

    @Inject
    private ImportExport importExport;

    @Asynchronous
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<?> startExport(
        final Collection<Exportable> entities,
        final String exportName
    ) {
        importExport.exportEntities(entities, exportName);
        return new AsyncResult<>(null);
    }

    @Asynchronous
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<?> startImport(final String importName) {
        importExport.importEntities(importName);
        return new AsyncResult<>(null);
    }

}
