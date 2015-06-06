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

import static org.libreccm.core.CoreConstants.*;

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainOwnership;
import org.omg.CORBA.DomainManager;

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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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
 * 
 */
@Entity
@Table(name = "ccm_objects")
@Inheritance(strategy = InheritanceType.JOINED)
@XmlRootElement(name = "ccm-object", namespace = CORE_XML_NS)
//False warning (?). Because this class has been migrated from the old PDL style
//persistence system we can't yet refactor it to make PMD happy. Also I think
//this is a false warning.
@SuppressWarnings("PMD.TooManyMethods")
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
    @XmlElement(name = "object-id", namespace = CORE_XML_NS)
    private long objectId;

    /**
     * A human readable name identifying this {@code CcmObject}
     */
    @Column(name = "display_name")
    @XmlElement(name = "display-name", namespace = CORE_XML_NS)
    private String displayName;

    @OneToMany(mappedBy = "object")
    @XmlElementWrapper(name = "permissions", namespace = CORE_XML_NS)
    @XmlElement(name = "permission", namespace = CORE_XML_NS)
    private List<Permission> permissions;
    
    /**
     * Category Domains owned by this {@code CcmObject}.
     */
    @OneToMany(mappedBy = "owner")
    @XmlElementWrapper(name = "domains", namespace = CORE_XML_NS)
    @XmlElement(name = "domain", namespace = CORE_XML_NS)
    private List<DomainOwnership> domains;
    
    /**
     * Categories which have been assigned to this {@code CcmObject}.
     */
    @OneToMany(mappedBy = "categorizedObject")
    @XmlElementWrapper(name = "categories", namespace = CORE_XML_NS)
    @XmlElement(name = "category", namespace = CORE_XML_NS)
    private List<Categorization> categories;
    
    public CcmObject() {
        super();
        
        permissions = new ArrayList<>();
        domains = new ArrayList<>();
        categories = new ArrayList<>();
    }
    
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

    public List<Permission> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }

    protected void setPermissions(final List<Permission> permissions) {
        this.permissions = permissions;
    }

    protected void addPermission(final Permission permission) {
        permissions.add(permission);
    }
    
    protected void removePermission(final Permission permission) {
        permissions.remove(permission);
    }
    
    /**
     * Gets an <strong>unmodifiable</strong> list of the domains which are owned
     * by the {@code CcmObject}.
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
     * Users should use the appropriate methods of the {@link DomainManager} 
     * class to
     * manage the {@link Domain}s assigned to {@code CcmObject}.
     * 
     * @param domain The domain ownership to add.
     */
    protected void addDomain(final DomainOwnership domain) {
        domains.add(domain);
    }
    
    /**
     * <strong>Internal</strong> method for removing a domain ownership. 
     * Users should use the appropriate methods of the {@link DomainManager} to
     * manage the {@link Domain}s assigned to {@code CcmObject}.
     * 
     * @param domain The domain to remove.
     */
    protected void removeDomain(final DomainOwnership domain) {
        domains.remove(domain);
    }

    /**
     * Returns a <strong>unmodifiable</strong> list of the categories this 
     * object is assigned to. To manage the categories of a {@code CcmObject}
     * use the methods provided by the {@link CategoryManager} class.
     * 
     * @return An <strong>unmodifiable</strong> list of the categories of this
     * {@code CcmObject}. Might be {@code null} or empty.
     */
    public List<Categorization> getCategories() {
        return Collections.unmodifiableList(categories);
    }

     /**
     * Setter for the list of categories assigned to this {@code CcmObject}, 
     * only for use by JPA. 
     * 
     * @param categories  A list of domain ownerships. 
     */
    protected void setCategories(final List<Categorization> categories) {
        this.categories = categories;
    }
    
    /**
     * <strong>Internal</strong> method for  adding a category. 
     * Users should use the appropriate methods of the {@link CategoryManager} 
     * class to manage the categories assigned to a {@code CcmObject}.
     * 
     * @param category  The domain ownership to add.
     */
    protected void addCategory(final Categorization category) {
        categories.add(category);
    }
    
    /**
     * <strong>Internal</strong> method for removing a assigned category. 
     * Users should use the appropriate methods of the {@link CategoryManager} 
     * to manage the categories assigned to a {@code CcmObject}.
     * 
     * @param category The assigned category to remove.
     */
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

    /**
     * Implementation of the {@code equals(Object)} method for {@code CcmObject}. We are
     * using the <em>canEqual</em> approach described at 
     * http://www.jqno.nl/equalsverifier/2011/01/20/subclass-object-is-not-equal-to-an-instance-of-a-trivial-subclass-with-equal-fields/
     * here. Therefore, in addition to overwrite this method subclasses should
     * also overwrite the {@link #canEqual(java.lang.Object)} method.
     * 
     * A good pattern for implementing {@code equals(Object)} is the following
     * (this is similar to {@code equals(Object)} implemenation created by 
     * Netbeans):
     * 
     * <pre>
     * public boolean equals(final Object obj) {
     *     //Check if obj is null
     *     if (obj == null) {
     *         return false;
     *     }
     *     
     *     //Check if obj is an instance of the class implementing equals
     *     if (!(obj instanceof YourClass)) {
     *         return false;
     *     }
     * 
     *     //Cast obj to the specific class
     *     final YourClass other = (YourClass) obj;
     *     //Check if other can equal YourClass
     *     if (!other.canEqual(this) {
     *         return false;
     *     }
     * 
     *     if(!super.equals(obj) {
     *         return false;
     *     }
     * 
     *     //Check properties of YourClass
     *     ...
     * }
     * </pre>
     * 
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CcmObject)) {
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

    /**
     * The {@code canEqual(Object} method is a helper method for 
     * {@link #equals(java.lang.Object)}. Subclasses which overwrite 
     * {@code #equals(Object)} must override this method also. 
     * 
     * Usually the implementation of this method will be a onliner:
     * <pre>
     * public boolean canEqual(final Object obj) {
     *     return obj instanceof YourClass;
     * }
     * </pre>
     * 
     * @param obj The object to check.
     * @return {@code true} if {@link obj} can equal this, false otherwise.
     */
    public boolean canEqual(final Object obj) {
        return obj instanceof CcmObject;
    }

    /**
     * Implementation of the {@code toString()} method for {@code CcmObject}. To
     * make extension easy {@code toString()} simply calls 
     * {@link #toString(java.lang.String)} with an empty string as parameter. 
     * Subclasses should not overwrite {@code toString}, instead they should use
     * {@link #toString(java.lang.String)}.
     * 
     * @return {@inheritDoc}
     */
    @Override
    public final String toString() {
        return toString("");
    }

    /**
     * Creates a string representation of this object. Example:
     * 
     * <pre>
     *   org.libreccm.core.CcmObject@1adafefe4222{ objectId = 42, 
     *   displayName = "example" }
     * </pre>
     * 
     * Subclasses can simply add their properties by creating a containing the 
     * additional properties and call {@code super.toString(String)} with this
     * data. The string should start with a leading space and comma because the
     * additional properties are inserted after the {@code displayName} in the
     * string. Also an overwriting method should insert its own data parameter
     * and the end of the string it creates.
     * 
     * Collections should not be included in this string. Associated objects
     * might be included. In that case their representation should like this:
     * <pre>
     * someObject = { ... }
     * </pre>
     * 
     * The content of the curly braces should be the string representation of
     * the object. If the object is very complex it might be sufficent to 
     * include only a subset of the objects properties.
     * 
     * Likewise, strings would be enclosed by quotes. The value of date 
     * properties should be shown in ISO format.
     * 
     * @param data data from a subclass
     * @return A string representation of this object.
     */
    public String toString(final String data) {
        return String.format(
            "%s{ "
                + "objectId = %d, "
                + "displayName = \"%s\""
                + "%s"
                + " }",
            super.toString(), 
            objectId,
            displayName,
            data);
    }

}
