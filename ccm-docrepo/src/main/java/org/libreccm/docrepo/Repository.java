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

import org.libreccm.web.Application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
@Entity
@Table(schema = "CCM_DOCREPO", name = "REPOSITORIES")
public class Repository extends Application {
    private static final long serialVersionUID = 6673243021462798036L;

    @Column(name = "ROOT_ID")
    private long rootId;

    @Column(name = "OWNER_ID")
    private long ownerId;

    public Repository() {
        super();
    }

    //> Begin GETTER & SETTER

    public long getRootId() {
        return rootId;
    }

    public void setRootId(long rootId) {
        this.rootId = rootId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    //< End GETTER & SETTER


}
