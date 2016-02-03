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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Entity class for a file in the doc-repository. Instances will be persisted
 * into the database. Instance variables are inherited from {@link AbstractResource}.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
@Entity(name = "DocRepoFile")
@Table(schema = "CCM_DOCREPO", name = "FILES")
@NamedQueries({
        @NamedQuery(name = "DocRepo.findFileByName",
                query = "SELECT r FROM DocRepoFile r WHERE " +
                        "r.name = :name"),
        @NamedQuery(name = "DocRepo.findFileByPath",
                query = "SELECT r FROM DocRepoFile r WHERE " +
                        "r.path = :pathName"),
        @NamedQuery(name = "DocRepo.findCreatedFileFromUser",
                query = "SELECT r FROM DocRepoFile r WHERE " +
                        "r.creationUser = :user"),
        @NamedQuery(name = "DocRepo.findModifiedFileFromUser",
                query = "SELECT r FROM DocRepoFile r WHERE " +
                        "r.lastModifiedUser = :user")
})
public class File extends AbstractResource {

    private static final long serialVersionUID = -504220783419811504L;

    /**
     * Content of the {@code AbstractResource} as a {@link BlobObject}.
     */
    @OneToOne
    @JoinColumn(name = "CONTENT_ID")
    private BlobObject content;

    /**
     * Constructor calls the super-class-constructor of {@link AbstractResource}.
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

    /**
     * Returns the attribute names of this object class as a list of strings.
     *
     * @return List of strings with the attribute names of this class
     */
    public static String[] getAttributeNames() {
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
}
