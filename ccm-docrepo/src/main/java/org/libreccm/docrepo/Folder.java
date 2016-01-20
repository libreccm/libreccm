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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * Entity class of a folder in the doc-repository. Instances will be persisted
 * into the database. Instance variables are inherited from {@link Resource}.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
@Entity
@Table(schema = "CCM_DOCREPO", name = "FOLDERS")
public class Folder extends Resource {

    private static final long serialVersionUID = 1561466556458872622L;

    /**
     * The child-{@code Resource}s of the {@code Resource}.
     */
    @OneToMany(mappedBy = "parent")
    private List<Resource> immediateChildren;

    /**
     * The {@link Repository} this {@code Folder} is assigned to as root.
     */
    @OneToOne(mappedBy = "rootFolder")
    private Repository rootAssignedRepository;

    /**
     * Constructor calls the super-class-constructor of {@link Resource}.
     */
    public Folder() {
        super();
    }

    //> Begin GETTER & SETTER

    public List<Resource> getImmediateChildren() {
        return immediateChildren;
    }

    public void setImmediateChildren(List<Resource> immediateChildren) {
        this.immediateChildren = immediateChildren;
    }

    public Repository getRootAssignedRepository() {
        return rootAssignedRepository;
    }

    public void setRootAssignedRepository(Repository rootAssignedRepository) {
        this.rootAssignedRepository = rootAssignedRepository;
    }


    //< End GETTER & SETTER
}
