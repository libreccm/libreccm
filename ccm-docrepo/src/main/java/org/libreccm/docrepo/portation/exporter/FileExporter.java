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
package org.libreccm.docrepo.portation.exporter;

import org.libreccm.docrepo.File;
import org.libreccm.portation.exporter.ObjectExporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Exporter class for {@link File}s. Implements the abstract method of its
 * super.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 13/01/2016
 */
public class FileExporter extends ObjectExporter<File> {
    @Override
    protected String[] getClassName() {
        return new String[] {File.class.getName()};
    }

    @Override
    protected String[] getAttributeNames() {
        return new String[] {
                "name",
                "description",
                "path",
                "mimeType",
                "size",
                "blobObject_ID",
                "creationDate",
                "lastModifiedDate",
                "creationIp",
                "lastModifiedIp",
                "creator_ID",
                "modifier_ID",
                "parent_ID",
                "repo_ID"
        };
    }

    // Todo: change ID to UUID
    @Override
    protected String[] reduceToStrings(File exportObject) {
        List<String> list = new ArrayList<>();

        list.add(exportObject.getName());
        list.add(exportObject.getDescription());
        list.add(exportObject.getPath());
        list.add(exportObject.getMimeType() != null ?
                 exportObject.getMimeType().toString() : "");
        list.add(String.valueOf(exportObject.getSize()));
        list.add(exportObject.getContent() != null ? String.valueOf(
                 exportObject.getContent().getBlobObjectId()) : "");
        list.add(exportObject.getCreationDate() != null ?
                 exportObject.getCreationDate().toString() : "");
        list.add(exportObject.getLastModifiedDate() != null ?
                 exportObject.getLastModifiedDate().toString() : "");
        list.add(exportObject.getCreationIp());
        list.add(exportObject.getLastModifiedIp());
        list.add(exportObject.getCreationUser() != null ? String.valueOf(
                 exportObject.getCreationUser().getPartyId()) : "");
        list.add(exportObject.getLastModifiedUser() != null ? String.valueOf(
                 exportObject.getLastModifiedUser().getPartyId()) : "");
        list.add(exportObject.getParent() != null ? String.valueOf(
                 exportObject.getParent().getObjectId()) : "");
        list.add(exportObject.getRepository() != null ? String.valueOf(
                 exportObject.getRepository().getObjectId()) : "");

        String[] array = new String[list.size()];
        return list.toArray(array);
    }
}
