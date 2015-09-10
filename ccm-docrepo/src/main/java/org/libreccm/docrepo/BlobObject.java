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


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
@Entity
@Table(schema = "ccm-docrepo", name = "blob_objects")
public class BlobObject implements Serializable {
    private static final long serialVersionUID = -7468014879548796218L;

    /**
     * The ID/primary key for the {@code BlobObject}. Please note that it is not
     * necessary to define an additional ID on classes which extend this class.
     */
    @Id
    @Column(name = "blob_object_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long blobObjectId;

    /**
     * The Content of the blob-object.
     */
    @Column(name = "content")
    @NotEmpty
    private Blob content;

    //> Begin GETTER & SETTER

    public long getBlobObjectId() {
        return blobObjectId;
    }

    public void setBlobObjectId(long blobObjectId) {
        this.blobObjectId = blobObjectId;
    }

    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }

    //< End GETTER & SETTER

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (int) (blobObjectId ^ (blobObjectId >>> 32));
        hash = 61 * hash + Objects.hashCode(content);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BlobObject)) {
            return false;
        }

        final BlobObject other = (BlobObject) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return blobObjectId == other.getBlobObjectId() &&
                Objects.equals(content, other.getContent());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof BlobObject;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                + "blobObjectId = %d, "
                                + "content = %s, "
                                + " }",
                            super.toString(),
                            blobObjectId,
                            content.toString());
    }
}
