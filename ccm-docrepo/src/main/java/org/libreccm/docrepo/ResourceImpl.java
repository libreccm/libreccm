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

import org.hibernate.validator.constraints.NotBlank;
import org.libreccm.core.CcmObject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
@Entity
@Table(schema = "CCM_DOCREPO", name = "RESOURCE_IMPL")
public class ResourceImpl extends CcmObject {
    private static final long serialVersionUID = -910317798106611214L;

    @Column(name = "NAME")
    @NotBlank
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IS_FOLDER")
    @NotBlank
    private boolean isFolder;

    @Column(name = "PATH")
    @NotBlank
    private String path;

    @Column(name = "MIME_TYPE")
    private String mimeType;

    @Column(name = "SIZE")
    private long size;

    @Column(name = "CREATION_DATE")
    @NotBlank
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "CREATION_IP")
    private String creationIp;

    @Column(name = "LAST_MODIFIED_DATE")
    @NotBlank
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Column(name = "LAST_MODIFIED_IP")
    private String lastModifiedIp;

    public ResourceImpl() {
        super();
    }
}
