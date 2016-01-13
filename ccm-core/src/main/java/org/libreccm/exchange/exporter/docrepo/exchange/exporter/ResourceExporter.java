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
package org.libreccm.exchange.exporter.docrepo.exchange;

import org.libreccm.exchange.exporter.ObjectExporter;
import org.libreccm.exchange.exporter.docrepo.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exporter class for resources. Implements the abstract method of its super.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 13/01/2016
 */
public class ResourceExporter extends ObjectExporter<Resource> {
    @Override
    public List<String[]> asList(List<Resource> exportObjects) {
        List<String[]> exportList = new ArrayList<>();

        exportList.add(new String[]{Resource.class.getName()});
        exportList.add(new String[]{
                "name",
                "description",
                "isFolder",
                "path",
                "mimeType",
                "size",
                "blobObject_ID",
                "creationDate",
                "lastModifiedDate",
                "creationIP",
                "lastModifiedIp",
                "creator_ID",
                "modifier_ID",
                "parent_ID",
                "repo_ID"
        });

        return exportList.addAll(exportObjects.stream().map(
                this::reduceToString).collect(Collectors.toList()))
                ? exportList : exportList;
    }

    @Override
    protected String[] reduceToString(Resource exportObject) {
        return new String[] {
                // name
                exportObject.getName(),
                // description
                exportObject.getDescription(),
                // isFolder
                String.valueOf(exportObject.isFolder()),
                // path
                exportObject.getPath(),
                // mimeType
                exportObject.getMimeType().toString(),
                // size
                String.valueOf(exportObject.getSize()),
                // blobObject_ID
                String.valueOf(exportObject.getContent().getBlobObjectId()),
                // creationDate
                exportObject.getCreationDate().toString(),
                // lastModifiedDate
                exportObject.getLastModifiedDate().toString(),
                // creationIp
                exportObject.getCreationIp(),
                // lastModifiedIp
                exportObject.getLastModifiedIp(),
                // creator_ID
                exportObject.getCreationUser().getName(),
                // modifier_ID
                exportObject.getLastModifiedUser().getName(),
                // parent_ID
                String.valueOf(exportObject.getParent().getObjectId()),
                // repo_ID
                String.valueOf(exportObject.getRepository().getObjectId()),
        };
    }
}
