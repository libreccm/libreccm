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
package org.libreccm.exchange.exporter.docrepo.exchange.exporter;

import org.libreccm.exchange.exporter.ObjectExporter;
import org.libreccm.exchange.exporter.docrepo.Resource;

import java.util.ArrayList;

/**
 * Exporter class for resources. Implements the abstract method of its super.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 13/01/2016
 */
public class ResourceExporter extends ObjectExporter<Resource> {
    @Override
    protected String[] getClassName() {
        return new String[] {Resource.class.getName()};
    }

    @Override
    protected String[] getAttributeNames() {
        return new String[] {
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
        };
    }

    @Override
    protected String[] reduceToStrings(Resource exportObject) {
        ArrayList<String> list = new ArrayList<>();
        list.add(exportObject.getName());
        list.add(exportObject.getDescription());
        list.add(String.valueOf(exportObject.isFolder()));
        list.add(exportObject.getPath());
        list.add(exportObject.getMimeType() != null ?
                 exportObject.getMimeType().toString() : "");
        list.add(String.valueOf(exportObject.getSize()));
        list.add(String.valueOf(exportObject.getContent().getBlobObjectId()));
        list.add(exportObject.getCreationDate() != null ?
                 exportObject.getCreationDate().toString() : "");
        list.add(exportObject.getLastModifiedDate() != null ?
                 exportObject.getLastModifiedDate().toString() : "");
        list.add(exportObject.getCreationIp());
        list.add(exportObject.getLastModifiedIp());
        list.add(exportObject.getCreationUser().getName());
        list.add(exportObject.getLastModifiedUser().getName());
        list.add(String.valueOf(exportObject.getParent().getObjectId()));
        list.add(String.valueOf(exportObject.getRepository().getObjectId()));

        return (String[]) list.toArray();
    }
}
