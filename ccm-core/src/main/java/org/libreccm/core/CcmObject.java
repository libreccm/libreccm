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
package org.libreccm.core;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 *
 * @author Jens Pelzetter jens@jp-digital.de
 */
@Entity
@Table(name = "ccm_objects")
@Inheritance(strategy = InheritanceType.JOINED)
public class CcmObject implements Serializable {

    private static final long serialVersionUID = 201504261329L;

    @Id
    @Column(name = "object_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long objectId;

    private String displayName;

    //private Map<String, Category> ownedCategories;
    //private Map<String, Category> assignedCategories;
    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(final long objectId) {
        this.objectId = objectId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (int) (objectId ^ (objectId >>> 32));
        hash = 61 * hash + Objects.hashCode(displayName);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CcmObject other = (CcmObject) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (objectId != other.getObjectId()) {
            return false;
        }
        return Objects.equals(displayName, other.getDisplayName());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof CcmObject;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format(
            "%s{ "
                + "objectId = %d; displayName = \"%s\""
                + "%s"
                + " }",
            super.toString(), 
            objectId,
            displayName,
            data);
    }

}
