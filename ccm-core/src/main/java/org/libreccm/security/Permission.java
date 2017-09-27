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
package org.libreccm.security;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.libreccm.core.CcmObject;
import org.libreccm.portation.Portable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.CORE_XML_NS;
import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * A permission grants a privilege on an object or system wide to {@link Role}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PERMISSIONS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "Permission.findByCustomPermId",
                query = "SELECT p FROM Permission p "
                            + "WHERE p.grantedPrivilege = :privilege "
                            + "AND p.grantee = :grantee "
                            + "AND p.object = :object")
    ,
    @NamedQuery(name = "Permission.existsForPrivilegeRoleObject",
                query = "SELECT COUNT(p) FROM Permission p "
                            + "WHERE p.grantedPrivilege = :privilege "
                            + "AND p.grantee = :grantee "
                            + "AND p.object = :object")
    ,
    @NamedQuery(name = "Permission.existsDirectForPrivilegeRoleObject",
                query = "SELECT COUNT(p) FROM Permission p "
                            + "WHERE p.grantedPrivilege = :privilege "
                            + "AND p.grantee = :grantee "
                            + "AND p.object = :object "
                            + "AND p.inherited = false")
    ,
    @NamedQuery(name = "Permission.existsInheritedForPrivilegeRoleObject",
                query = "SELECT COUNT(p) FROM Permission p "
                            + "WHERE p.grantedPrivilege = :privilege "
                            + "AND p.grantee = :grantee "
                            + "AND p.object = :object "
                            + "AND p.inherited = true")
    ,
    @NamedQuery(name = "Permission.existsForPrivilegeAndRole",
                query = "SELECT COUNT(p) FROM Permission p "
                            + "WHERE p.grantedPrivilege = :privilege "
                            + "AND p.grantee = :grantee "
                            + "AND p.object IS NULL")
    ,
    @NamedQuery(name = "Permission.findPermissionsForRole",
                query = "SELECT p FROM Permission p "
                            + "WHERE p.grantee = :grantee")
    ,
    @NamedQuery(name = "Permission.findPermissionsForCcmObject",
                query = "SELECT p FROM Permission p "
                            + "WHERE p.object = :object")
    ,
    @NamedQuery(name = "Permission.findPermissionsForRoleAndObject",
                query = "SELECT p FROM Permission p "
                            + "WHERE p.object = :object and p.grantee = :grantee")

})
@XmlRootElement(name = "permission", namespace = CORE_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIdentityInfo(generator = PermissionIdGenerator.class,
                  resolver = PermissionIdResolver.class,
                  property = "customPermId")
public class Permission implements Serializable, Portable {

    private static final long serialVersionUID = -5178045844045517958L;

    /**
     * The database id of the permission.
     */
    @Id
    @Column(name = "PERMISSION_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "permission-id", namespace = CORE_XML_NS)
    private long permissionId;

    /**
     * The granted privilege.
     */
    @Column(name = "GRANTED_PRIVILEGE")
    @Field
    @XmlElement(name = "privilege", namespace = CORE_XML_NS)
    private String grantedPrivilege;

    /**
     * The object on which the privilege is granted. My be {@code null}.
     */
    @ManyToOne
    @JoinColumn(name = "OBJECT_ID")
    @ContainedIn
    @JsonIdentityReference(alwaysAsId = true)
    private CcmObject object;

    /**
     * The role to which the permission is granted.
     */
    @ManyToOne
    @IndexedEmbedded
    @JoinColumn(name = "GRANTEE_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private Role grantee;

    /**
     * The {@link User} which created this {@code Permission}. The property can
     * be {@code null} if this {@code Permission} was created by a system
     * process.
     */
    @ManyToOne
    @JoinColumn(name = "CREATION_USER_ID")
    @XmlElement(name = "creation-user", namespace = CORE_XML_NS)
    @JsonIdentityReference(alwaysAsId = true)
    private User creationUser;

    /**
     * The date and time on which this {@code Permission} was created. This
     * property can be {@code null} if this {@code Permission} was created by a
     * system process.
     */
    @Column(name = "CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(name = "creation-date", namespace = CORE_XML_NS)
    private Date creationDate;

    /**
     * The IP of the system from which this {@code Permission} was created. This
     * property can be {@code null} if this {@code Permission} was created by a
     * system process.
     */
    @Column(name = "CREATION_IP")
    @XmlElement(name = "creation-ip", namespace = CORE_XML_NS)
    private String creationIp;

    /**
     * If the permission is inherited from another object this field is set to
     * {@code true}.
     */
    @Column(name = "INHERITED")
    private boolean inherited;

    /**
     * If the permission is inherited from another object this field points to
     * the object from the which the permission was inherited.
     */
    @OneToOne
    @JoinColumn(name = "INHERITED_FROM_ID")
    private CcmObject inheritedFrom;

    protected Permission() {
        //Nothing
    }

    public long getPermissionId() {
        return permissionId;
    }

    protected void setPermissionId(final long permissionId) {
        this.permissionId = permissionId;
    }

    public String getGrantedPrivilege() {
        return grantedPrivilege;
    }

    public void setGrantedPrivilege(final String grantedPrivilege) {
        this.grantedPrivilege = grantedPrivilege;
    }

    public CcmObject getObject() {
        return object;
    }

    public void setObject(final CcmObject object) {
        this.object = object;
    }

    public Role getGrantee() {
        return grantee;
    }

    public void setGrantee(final Role grantee) {
        this.grantee = grantee;
    }

    public User getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(final User creationUser) {
        this.creationUser = creationUser;
    }

    public Date getCreationDate() {
        if (creationDate == null) {
            return null;
        } else {
            return new Date(creationDate.getTime());
        }
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = new Date(creationDate.getTime());
    }

    public String getCreationIp() {
        return creationIp;
    }

    public void setCreationIp(final String creationIp) {
        this.creationIp = creationIp;
    }

    public boolean isInherited() {
        return inherited;
    }

    protected void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public CcmObject getInheritedFrom() {
        return inheritedFrom;
    }

    protected void setInheritedFrom(CcmObject inheritedFrom) {
        this.inheritedFrom = inheritedFrom;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (permissionId ^ (permissionId >>> 32));
        hash = 97 * hash + Objects.hashCode(grantedPrivilege);
        hash = 97 * hash + Objects.hashCode(creationDate);
        hash = 97 * hash + Objects.hashCode(creationIp);
        hash = 97 * hash + Boolean.hashCode(inherited);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Permission)) {
            return false;
        }
        final Permission other = (Permission) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (permissionId != other.getPermissionId()) {
            return false;
        }
        if (!Objects.equals(grantedPrivilege, other.getGrantedPrivilege())) {
            return false;
        }
        if (!Objects.equals(creationDate, other.getCreationDate())) {
            return false;
        }

        if (!Objects.equals(creationIp, other.getCreationIp())) {
            return false;
        }

        return inherited == other.isInherited();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Permission;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "permissionId = %d, "
                                 + "grantedPrivilege = { %s }, "
                                 + "creationDate = %tF %<tT, "
                                 + "creationIp = %s }",
                             super.toString(),
                             permissionId,
                             Objects.toString(grantedPrivilege),
                             creationDate,
                             creationIp);
    }

}
