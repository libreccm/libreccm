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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Entity class for a file in the doc-repository. Instances will be persisted
 * into the database. Instance variables are inherited from {@link Resource}.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
@Entity
@Table(schema = "CCM_DOCREPO", name = "FILES")
public class File extends Resource {

    private static final long serialVersionUID = -504220783419811504L;

    /**
     * Content of the {@code Resource} as a {@link BlobObject}.
     */
    @OneToOne
    @JoinColumn(name = "CONTENT_ID")
    private BlobObject content;

    /**
     * Constructor calls the super-class-constructor of {@link Resource}.
     */
    public File() {
        super();
    }

    //> Begin GETTER & SETTER

    public BlobObject getContent() {
        return content;
    }

    public void setContent(BlobObject content) {
        this.content = content;
    }

    //< End GETTER & SETTER
}
