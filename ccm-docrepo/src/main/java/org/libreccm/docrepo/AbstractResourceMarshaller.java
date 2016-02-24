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

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.libreccm.portation.AbstractMarshaller;
import org.libreccm.portation.Marshals;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 2/10/16
 */
@Marshals(AbstractResource.class)
public class AbstractResourceMarshaller extends AbstractMarshaller<AbstractResource> {

    @Override
    protected Class getClassT() {
        return AbstractResource.class;
    }

    @Override
    protected CsvSchema getCsvSchema() {
        return CsvSchema.builder()
                .addColumn("object_ID", CsvSchema.ColumnType.NUMBER)
                .addColumn("displayName")
                .addColumn("permissions", CsvSchema.ColumnType.ARRAY)
                .addColumn("categories", CsvSchema.ColumnType.ARRAY)
                .addColumn("name")
                .addColumn("description")
                .addColumn("path")
                .addColumn("mimeType")
                .addColumn("size", CsvSchema.ColumnType.NUMBER)
                .addColumn("creationDate", CsvSchema.ColumnType.NUMBER)
                .addColumn("lastModifiedDate", CsvSchema.ColumnType.NUMBER)
                .addColumn("creationIp")
                .addColumn("lastModifiedIp")
                .addColumn("creationUser")
                .addColumn("lastModifiedUser")
                .addColumn("parent")
                .addColumn("repository")
                .build();
    }
}
