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
import org.libreccm.portation.Marshals;


import javax.inject.Inject;

/**
 * Marshaller class for importing and exporting {@code File}s from the
 * system into a specified file and the other way around.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version created the 3/16/16
 */
@RequestScoped
@Marshals(File.class)
public class FileMarshaller extends AbstractResourceMarshaller<File> {

    @Inject
    private FileRepository fileRepository;

    @Override
    protected Class<File> getObjectClass() {
        return File.class;
    }

    @Override
    protected void insertIntoDb(File portableObject) {
        fileRepository.save(portableObject);
    }
}
