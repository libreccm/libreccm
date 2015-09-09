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
package org.libreccm.auditing;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import static org.libreccm.core.CoreConstants.*;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Revision entity for Hibernate Envers containing the editing user. We are not
 * using an association between this class and the user class because the user
 * may be deleted but the revisions will stay. Therefore we use the value of the
 * screenname property here.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CCM_REVISIONS", schema = DB_SCHEMA)
@RevisionEntity()
public class CcmRevision extends DefaultRevisionEntity {

    private static final long serialVersionUID = -3458682765535922544L;

    @Column(name = "USER_NAME")
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        
        hash = 17 * hash + Objects.hashCode(this.userName);
        
        return hash;
    }
    
    
    
    @Override
    public boolean equals(final Object object) {
        if(!super.equals(object)) {
            return false;
        }
        
        if (!(object instanceof CcmRevision)) {
            return false;
        }
        
        final CcmRevision other = (CcmRevision) object;
        return userName.equals(other.getUserName());
    }
    
    

}
