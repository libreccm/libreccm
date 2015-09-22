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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity class of a folder in the doc-repository. Instances will be persisted
 * into the database. Instance variables are inherited from {@link ResourceImpl}.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
@Entity
@Table(schema = "CCM_DOCREPO", name = "FOLDERS")
public class Folder extends ResourceImpl {

    private static final long serialVersionUID = 1561466556458872622L;

    public Folder() {
        super();
    }
}
