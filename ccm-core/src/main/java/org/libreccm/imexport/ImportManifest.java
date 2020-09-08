/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.imexport;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Java class containg the properties of an parsed import manifest. 
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ImportManifest {

    private final Date created;
    private final String onServer;
    private final List<String> types;

    public ImportManifest(final Date created,
                          final String onServer,
                          final List<String> types) {

        this.created = created;
        this.onServer = onServer;
        this.types = types;
    }

    public Date getCreated() {
        return new Date(created.getTime());
    }

    public String getOnServer() {
        return onServer;
    }

    public List<String> getTypes() {
        return Collections.unmodifiableList(types);
    }

}
