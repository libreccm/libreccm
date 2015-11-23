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

import org.apache.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Manager class for complex operations on {@code Resource}-objects.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
@RequestScoped
public class ResourceManager {
    private static final Logger log = Logger.getLogger(ResourceManager.class);

    @Inject
    private ResourceRepository resourceRepository;

    public void copyToFolder(Resource original, Folder folder) {
        Resource copy = original.isFolder() ? new Folder() : new File();
        copy.setName(original.getName());
        copy.setDescription(original.getDescription());
        copy.setIsFolder(original.isFolder());
        copy.setPath(String.format("%s/%s", folder.getPath(), copy.getName()));
        copy.setMimeType(original.getMimeType());
        copy.setSize(original.getSize());
        copy.setContent(original.getContent());

        copy.setParent(folder);
        copy.setImmediateChildren(original.getImmediateChildren());

        resourceRepository.save(copy);
    }



}
