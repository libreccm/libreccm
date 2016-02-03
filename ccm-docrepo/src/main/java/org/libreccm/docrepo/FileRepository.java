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

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

/**
 * Repository class for retrieving, storing and deleting {@code File}s.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 27.01.2016
 */
@RequestScoped
public class FileRepository extends AbstractResourceRepository<File> {

    public FileRepository() {
        classOfT = File.class;
    }

    @Override
    public TypedQuery<File> getFindByNameQuery() {
        return entityManager.createNamedQuery(
                "DocRepo.findFileByName", File.class);
    }

    @Override
    public TypedQuery<File> getFindByPathNameQuery() {
        return entityManager.createNamedQuery(
                "DocRepo.findFileByPath", File.class);
    }

    @Override
    public TypedQuery<File> getFindForCreatorQuery() {
        return entityManager.createNamedQuery(
                "DocRepo.findCreatedFileFromUser", File.class);
    }

    @Override
    public TypedQuery<File> getFindForModifierQuery() {
        return entityManager.createNamedQuery(
                "DocRepo.findModifiedFileFromUser", File.class);
    }
}
