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
import java.util.Set;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(Repository.class)
public class RepositoryImExporter extends AbstractEntityImExporter<Repository> {

    @Inject
    private RepositoryRepository repositoryRepository;

    @Override
    protected Class<Repository> getEntityClass() {
        return Repository.class;
    }

    @Override
    protected void saveImportedEntity(final Repository portableObject) {
        repositoryRepository.save(portableObject);
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        return Collections.emptySet();
    }
    
    
}
    