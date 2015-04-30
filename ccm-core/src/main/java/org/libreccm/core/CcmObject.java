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

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.DomainOwnership;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Root class of all entities in LibreCCM which need categorisation and 
 * permission services. 
 * 
 * This class defines several basic properties including associations
 * to {@link Category} (via the {@link Categorization} class and permissions.
 * 
 * In the old hierarchy the equivalent of this class was the {@code ACSObject}
 * entity.
 * 
 * We are using the {@code JOINED} inheritance strategy for the inheritance
 * hierarchy of this class to achieve modularity and to minimise duplicate data 
 * in the database.
 * 
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "ccm_objects")
@Inheritance(strategy = InheritanceType.JOINED)
public class CcmObject implements Serializable {

    private static final long serialVersionUID = 201504261329L;

    /**
     * The ID/primary key for the {@code CcmObject}. Please note that it is
     * not necessary to define an additional ID on classes which extend this
     * class.
     */
    @Id
    @Column(name = "object_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long objectId;

    /**
     * A human readable name identifying this {@code CcmObject}
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * Category Domains owned by this {@code CcmObject}.
     */
    @OneToMany(mappedBy = "owner")
    private List<DomainOwnership> domains;
    
    /**
     * Categories which have been assigned to this {@code CcmObject}.
     */
    @OneToMany(mappedBy = "categorizedObject")
    private List<Categorization> categories;
    
    
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

    public CcmObject() {
        domains = new ArrayList<>();
        categories = new ArrayList<>();
    }
    
    /**
     * Getter for the list of owned domains.
     * 
     * @return An unmodifiable list of the domain ownerships of this 
     * {@code CcmObject}. Might be {@code null} or empty.
     */
    public List<DomainOwnership> getDomains() {
        return Collections.unmodifiableList(domains);
    }

    /**
     * Setter for the list of domain ownerships, only for use by JPA. 
     * 
     * @param domains A list of domain ownerships. 
     */
    protected void setDomains(final List<DomainOwnership> domains) {
        this.domains = domains;
    }
    
    /**
     * <strong>Internal</strong> method for  adding a domain ownership. 
     * User should use the appropriate methods of the {@code CategoryManager} 
     * class.
     * 
     * @param domain The domain ownership to add.
     */
    protected void addDomain(final DomainOwnership domain) {
        domains.add(domain);
    }
    
    /**
     * <strong>Internal</strong> method for removing a domain ownership. 
     * User should use the appropriate methods of the {@code CategoryManager} 
     * 
     * @param domain 
     */
    protected void removeDomain(final DomainOwnership domain) {
        domains.remove(domain);
    }

    public List<Categorization> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    protected void setCategories(final List<Categorization> categories) {
        this.categories = categories;
    }
    
    protected void addCategory(final Categorization category) {
        categories.add(category);
    }
    
    protected void removeCategory(final Categorization category) {
        categories.remove(category);
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
