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

import org.libreccm.security.User;
import org.libreccm.web.CcmApplication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Entity class of a repository for documents. Instances will be persisted into
 * the database. Instance variables are inherited from {@link CcmApplication}.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
@Entity
@Table(schema = "CCM_DOCREPO", name = "REPOSITORIES")
public class Repository extends CcmApplication {

    private static final long serialVersionUID = 6673243021462798036L;

    /**
     * Name des {@code Repository}s.
     */
    @Column(name = "NAME")
    private String name;

    /**
     * The root of the {@code Repository}.
     */
    @OneToOne
    @JoinColumn(name = "ROOT_FOLDER")
    private Folder rootFolder;

    /**
     * The owner of the {@code Repository}.
     */
    @OneToOne
    @JoinColumn(name = "OWNER_ID")
    private User owner;

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

    //< End GETTER & SETTER


}
