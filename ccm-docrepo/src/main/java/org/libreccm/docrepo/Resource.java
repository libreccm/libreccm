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
import org.libreccm.security.User;

import javax.activation.MimeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Entity(name = "DocRepoResource")
@Table(schema = "CCM_DOCREPO", name = "RESOURCES")
@NamedQueries({
        @NamedQuery(name = "DocRepo.findResourceByPath",
                    query = "SELECT r FROM DocRepoResource r WHERE " +
                            "r.path = :pathName"),
        @NamedQuery(name = "DocRepo.findResourcesByName",
                    query = "SELECT r FROM DocRepoResource r WHERE " +
                            "r.name = :name"),
        @NamedQuery(name = "DocRepo.findCreatedResourcesFromUser",
                    query = "SELECT r FROM DocRepoResource r WHERE " +
                            "r.creationUser = :user"),
        @NamedQuery(name = "DocRepo.findModifiedResourcesFromUser",
                    query = "SELECT r FROM DocRepoResource r WHERE " +
                            "r.lastModifiedUser = :user")})
public abstract class Resource extends CcmObject {

    private static final long serialVersionUID = -910317798106611214L;

    /**
     * Name of the {@code Resource}.
     */
    @Column(name = "NAME", length = 512, unique = true, nullable = false)
    @NotBlank
    private String name;

    /**
     * Description of the {@code Resource}.
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * Path to the {@code Resource}.
     */
    @Column(name = "PATH", unique = true)
    @NotBlank
    private String path;

    /**
     * Mime-type of the {@code Resource}.
     */
    @Column(name = "MIME_TYPE")
    private MimeType mimeType;

    /**
     * Size of the {@code Resource}.
     */
    @Column(name = "SIZE")
    private long size;

    /**
     * Creation date of the {@code Resource}.
     */
    @Column(name = "CREATION_DATE")
    @NotBlank
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    /**
     * Date of the latest modification of the {@code Resource}.
     */
    @Column(name = "LAST_MODIFIED_DATE")
    @NotBlank
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    /**
     * Creation ip of the {@code Resource}.
     */
    @Column(name = "CREATION_IP")
    private String creationIp;

    /**
     * Ip of the latest modification of the {@code Resource}.
     */
    @Column(name = "LAST_MODIFIED_IP")
    private String lastModifiedIp;

    /**
     * The {@link User}, who created the {@code Resource}.
     */
    @ManyToOne
    @JoinColumn(name = "CREATION_USER_ID")
    private User creationUser;

    /**
     * The {@link User}, who last modified the {@code Resource}.
     */
    @ManyToOne
    @JoinColumn(name = "LAST_MODIFIED_USER_ID")
    private User lastModifiedUser;

    /**
     * The parent-{@code Resource} of the {@code Resource}.
     */
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Folder parent;

    /**
     * The {@link Repository} containing this {@code Resource}.
     */
    @ManyToOne
    @JoinColumn(name = "REPOSITORY_ID")
    private Repository repository;

    /**
     * Constructor calls the super-class-constructor of {@link CcmObject}.
     */
    public Resource() {
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
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
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

    public Resource getParent() {
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
