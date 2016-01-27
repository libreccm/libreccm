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
 * You should have received list copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.libreccm.docrepo.portation.importer;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.docrepo.BlobObject;
import org.libreccm.docrepo.BlobObjectRepository;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.Folder;
import org.libreccm.docrepo.Repository;
import org.libreccm.docrepo.RepositoryRepository;
import org.libreccm.docrepo.ResourceRepository;
import org.libreccm.portation.importer.AbstractImporter;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.enterprise.context.RequestScoped;
import java.util.Date;

/**
 * Importer class for {@link File}s. Implements the abstract method
 * of its super.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 20/01/2016
 */
@RequestScoped
public class FileImporter extends AbstractImporter<File> {

    @Override
    protected boolean checkAttributeNames(String[] attributeNames) {
        return attributeNames.equals(File.getAttributeNames());
    }

    @Override
    protected File expandFromStrings(String[] importStrings) {
        CdiUtil cdiUtil = new CdiUtil();
        File file = new File();

        file.setName(importStrings[0]);
        file.setDescription(importStrings[1]);
        file.setPath(importStrings[2]);

        MimeType mimeType = new MimeType();
        try {
            mimeType.setPrimaryType(importStrings[3]);
        } catch (MimeTypeParseException ignored) {
        }
        file.setMimeType(mimeType);

        file.setSize(Long.valueOf(importStrings[4]));

        BlobObjectRepository blobObjectRepository = cdiUtil.findBean
                (BlobObjectRepository.class);
        BlobObject blobObject = blobObjectRepository.findById(Long.valueOf
                (importStrings[5]));
        if (blobObject != null) {
            file.setContent(blobObject);
        }

        Date date = new Date(Long.valueOf(importStrings[6]));
        if (date != null) {
            file.setCreationDate(date);
        }
        date = new Date(Long.valueOf(importStrings[7]));
        if (date != null) {
            file.setLastModifiedDate(date);
        }

        file.setCreationIp(importStrings[8]);
        file.setLastModifiedIp(importStrings[9]);

        UserRepository userRepository = cdiUtil.findBean(UserRepository.class);
        User user = userRepository.findById(Long.valueOf(importStrings[10]));
        if (user != null) {
            file.setCreationUser(user);
        }
        user = userRepository.findById(Long.valueOf(importStrings[11]));
        if (user != null) {
            file.setLastModifiedUser(user);
        }

        ResourceRepository resourceRepository = cdiUtil.findBean
                (ResourceRepository.class);
        Folder folder = (Folder) resourceRepository.findById(Long.valueOf
                (importStrings[12]));
        if (folder != null) {
            file.setParent(folder);
        }

        RepositoryRepository repositoryRepository = cdiUtil.findBean
                (RepositoryRepository.class);
        Repository repository = repositoryRepository.findById(Long.valueOf
                (importStrings[13]));
        if (repository != null) {
            file.setRepository(repository);
        }

        resourceRepository.save(file);
        return file;
    }
}