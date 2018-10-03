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
import org.libreccm.imexport.Exportable;
import org.libreccm.security.User;
import org.libreccm.web.CcmApplication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.List;

/**
 * Entity class of a repository for documents. Instances will be persisted into
 * the database. Instance variables are inherited from {@link CcmApplication}.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
@Entity
@Table(schema = "CCM_DOCREPO", name = "REPOSITORIES")
@NamedQueries({
        @NamedQuery(name = "DocRepo.findRepositoriesForOwner",
                    query = "SELECT r FROM Repository r WHERE r.owner = :owner")
})
public class Repository extends CcmApplication implements Exportable {

    private static final long serialVersionUID = 6673243021462798036L;

    /**
     * Name des {@code Repository}s.
     */
    @Column(name = "NAME", length = 512, unique = true, nullable = false)
    @NotBlank
    private String name;

    /**
     * The root of the {@code Repository}.
     */
    @OneToOne
    @JoinColumn(name = "ROOT_FOLDER_ID", unique = true, nullable = false)
    @NotBlank
    private Folder rootFolder;

    /**
     * The owner of the {@code Repository}.
     */
    @OneToOne
    @JoinColumn(name = "OWNER_ID", nullable = false)
    @NotBlank
    private User owner;

    /**
     * All {@link AbstractResource}s contained in this {@code Repository}.
     */
    @OneToMany(mappedBy = "repository")
    private List<AbstractResource> abstractResources;


    /**
     * Constructor calls the super-class-constructor of {@link CcmApplication}.
     */
    public Repository() {
        super();
    }

    //> Begin GETTER & SETTER

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Folder getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(Folder root_folder) {
        this.rootFolder = root_folder;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<AbstractResource> getAbstractResources() {
        return abstractResources;
    }

    public void setAbstractResources(List<AbstractResource> abstractResources) {
        this.abstractResources = abstractResources;
    }

    //< End GETTER & SETTER


}
