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

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.libreccm.core.CcmObject;
import org.libreccm.portation.Portable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @NamedQuery(name = "Permission.existsForPrivilegeRoleObject",
                query = "SELECT COUNT(p) FROM Permission p "
                            + "WHERE p.grantedPrivilege = :privilege "
                            + "AND p.grantee = :grantee "
                            + "AND p.object = :object"),
    @NamedQuery(name = "Permission.existsForPrivilegeAndRole",
                query = "SELECT count(p) FROM Permission p "
                            + "WHERE p.grantedPrivilege = :privilege "
                            + "AND p.grantee = :grantee "
                            + "AND p.object IS NULL"),
    @NamedQuery(name = "Permission.findPermissionsForRole",
                query = "SELECT p FROM Permission p "
                            + "WHERE p.grantee = :grantee"),
    @NamedQuery(name = "Permission.findPermissionsForCcmObject",
                query = "SELECT p FROM Permission p "
                            + "WHERE p.object = :object")
})
@XmlRootElement(name = "permission", namespace = CORE_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
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
    @Column(name = "granted_privilege")
    @XmlElement(name = "privilege", namespace = CORE_XML_NS)
    private String grantedPrivilege;

    /**
     * The object on which the privilege is granted. My be {@code null}.
     */
    @ManyToOne
    @JoinColumn(name = "OBJECT_ID")
    @JsonBackReference
    private CcmObject object;

    /**
     * The role to which the permission is granted.
     */
    @ManyToOne
    @JoinColumn(name = "GRANTEE_ID")
    @JsonBackReference
    private Role grantee;

    /**
     * The {@link User} which created this {@code Permission}. The property can
     * be {@code null} if this {@code Permission} was created by a system
     * process.
     */
    @ManyToOne
    @JoinColumn(name = "CREATION_USER_ID")
    @XmlElement(name = "creation-user", namespace = CORE_XML_NS)
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (permissionId ^ (permissionId >>> 32));
        hash = 97 * hash + Objects.hashCode(grantedPrivilege);
        hash = 97 * hash + Objects.hashCode(creationDate);
        hash = 97 * hash + Objects.hashCode(creationIp);
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
        
        return Objects.equals(creationIp, other.getCreationIp());
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
