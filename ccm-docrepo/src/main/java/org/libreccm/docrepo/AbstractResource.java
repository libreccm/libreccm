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

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;
import org.libreccm.core.CcmObject;
import org.libreccm.security.User;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Abstract entity class of a resource. Instances will be persisted into the
 * database through the inheriting subclasses.
 *
 * The inheriting subclasses and therefore resources are:   {@link File},
 *                                                          {@link Folder}
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
@Entity
@Table(schema = "CCM_DOCREPO", name = "RESOURCES")
public abstract class AbstractResource extends CcmObject {
    private static final Logger log = Logger.getLogger(AbstractResource.class);

    private static final long serialVersionUID = -910317798106611214L;

    /**
     * Name of the {@code AbstractResource}.
     */
    @Column(name = "NAME", length = 512, unique = true, nullable = false)
    @NotBlank
    private String name;

    /**
     * Description of the {@code AbstractResource}.
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * Path to the {@code AbstractResource}.
     */
    @Column(name = "PATH", unique = true)
    @NotBlank
    private String path;

    /**
     * Mime-type of the {@code AbstractResource}.
     */
    @Column(name = "MIME_TYPE")
    private String mimeType;

    /**
     * Size of the {@code AbstractResource}.
     */
    @Column(name = "SIZE")
    private long size;

    /**
     * Creation date of the {@code AbstractResource}.
     */
    @Column(name = "CREATION_DATE", nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /**
     * Date of the latest modification of the {@code AbstractResource}.
     */
    @Column(name = "LAST_MODIFIED_DATE", nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    /**
     * Creation ip of the {@code AbstractResource}.
     */
    @Column(name = "CREATION_IP")
    private String creationIp;

    /**
     * Ip of the latest modification of the {@code AbstractResource}.
     */
    @Column(name = "LAST_MODIFIED_IP")
    private String lastModifiedIp;

    /**
     * The {@link User}, who created the {@code AbstractResource}.
     */
    @ManyToOne
    @JoinColumn(name = "CREATION_USER_ID")
    private User creationUser;

    /**
     * The {@link User}, who last modified the {@code AbstractResource}.
     */
    @ManyToOne
    @JoinColumn(name = "LAST_MODIFIED_USER_ID")
    private User lastModifiedUser;

    /**
     * The parent-{@code AbstractResource} of the {@code AbstractResource}.
     */
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Folder parent;

    /**
     * The {@link Repository} containing this {@code AbstractResource}.
     */
    @ManyToOne
    @JoinColumn(name = "REPOSITORY_ID")
    private Repository repository;

    /**
     * Constructor calls the super-class-constructor of {@link CcmObject}.
     */
    public AbstractResource() {
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MimeType getMimeType() {
        MimeType mimeType = null;
        try {
            mimeType = new MimeType(this.mimeType);
        } catch (MimeTypeParseException e) {
            log.error("Error on parsing the db-string for mimeType to actual" +
                    "MimeType", e);
        }
        return mimeType != null ? mimeType : null;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType.toString();
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

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreationIp() {
        return creationIp;
    }

    public void setCreationIp(String creationIp) {
        this.creationIp = creationIp;
    }

    public String getLastModifiedIp() {
        return lastModifiedIp;
    }

    public void setLastModifiedIp(String lastModifiedIp) {
        this.lastModifiedIp = lastModifiedIp;
    }

    public User getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(User creationUser) {
        this.creationUser = creationUser;
    }

    public User getLastModifiedUser() {
        return lastModifiedUser;
    }

    public void setLastModifiedUser(User lastModifiedUser) {
        this.lastModifiedUser = lastModifiedUser;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    //< End GETTER & SETTER
}
