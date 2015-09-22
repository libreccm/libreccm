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
 * Abstract entity class of a resource. Instances will be persisted into the
 * database through the inheriting subclasses.
 *
 * The inheriting subclasses and therefore resources are:   {@link File},
 *                                                          {@link Folder}
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
@Entity
@Table(schema = "CCM_DOCREPO", name = "RESOURCE_IMPL")
public abstract class ResourceImpl extends CcmObject {

    private static final long serialVersionUID = -910317798106611214L;

    /**
     * Name of the resource.
     */
    @Column(name = "NAME")
    @NotBlank
    private String name;

    /**
     * Description of the resource.
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * Flag, wheather the resource is a folder or not.
     */
    @Column(name = "IS_FOLDER")
    @NotBlank
    private boolean isFolder;

    /**
     * Path to the resource
     */
    @Column(name = "PATH")
    @NotBlank
    private String path;

    /**
     * Mime-type of the resource
     */
    @Column(name = "MIME_TYPE")
    private String mimeType;

    /**
     * Size of the resource.
     */
    @Column(name = "SIZE")
    private long size;

    /**
     * Creation date of the resource.
     */
    @Column(name = "CREATION_DATE")
    @NotBlank
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /**
     * Creation ip of the resource.
     */
    @Column(name = "CREATION_IP")
    private String creationIp;

    /**
     * Date of the latest modification of the resource.
     */
    @Column(name = "LAST_MODIFIED_DATE")
    @NotBlank
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    /**
     * Ip of the latest modification of the resource.
     */
    @Column(name = "LAST_MODIFIED_IP")
    private String lastModifiedIp;

    public ResourceImpl() {
        super();
    }

    //> Begin GETTER & SETTER

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationIp() {
        return creationIp;
    }

    public void setCreationIp(String creationIp) {
        this.creationIp = creationIp;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedIp() {
        return lastModifiedIp;
    }

    public void setLastModifiedIp(String lastModifiedIp) {
        this.lastModifiedIp = lastModifiedIp;
    }

    //< End GETTER & SETTER
}
