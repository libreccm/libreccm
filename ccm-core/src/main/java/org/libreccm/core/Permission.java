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

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a {@link Privilege} granted to a {@link Subject} on an object or
 * all objects.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "permissions")
@NamedQueries({
    @NamedQuery(name = "findPermissionsForSubject",
                query = "SELECT p FROM Permission p WHERE p.grantee = :subject"),
    @NamedQuery(name = "findPermissionsForUser",
                query = "SELECT p FROM Permission p "
                                + "WHERE p.grantee = :user "
                                + "   OR p.grantee IN (SELECT g "
                                + "                    FROM Group g JOIN g.members m"
                        + "                            WHERE m.user = :user)"),
    @NamedQuery(name = "findPermissionsForCcmObject",
                query = "SELECT p FROM Permission p WHERE p.object = :object"),
    @NamedQuery(name = "findPermissionsForUserPrivilegeAndObject",
                query = "SELECT p FROM Permission p "
                                + "WHERE (p.grantee = :user"
                                + "       OR p.grantee IN (SELECT g "
                                + "                        FROM Group g JOIN g.members m"
                        + "                                WHERE m.user = :user))"
                        + "                AND p.grantedPrivilege = :privilege"
                                + "        AND p.object = :object"),
    @NamedQuery(name = "findWildcardPermissionsForUserPrivilegeAndObject",
                query = "SELECT p FROM Permission p "
                                + "WHERE (p.grantee = :user"
                                + "       OR p.grantee IN (SELECT g "
                                + "                        FROM Group g JOIN g.members m"
                        + "                                WHERE m.user = :user))"
                        + "                AND p.grantedPrivilege = :privilege"
                                + "        AND p.object IS NULL"),
    @NamedQuery(name = "findPermissionsForSubjectPrivilegeAndObject",
                query = "SELECT p FROM Permission p "
                                + "WHERE p.grantee          = :subject"
                                + "  AND p.grantedPrivilege = :privilege"
                                + "  AND p.object           = :object"),
    @NamedQuery(name = "findWildcardPermissionsForSubjectPrivilegeAndObject",
                query = "SELECT p FROM Permission p "
                                + "WHERE p.grantee          = :subject"
                                + "  AND p.grantedPrivilege = :privilege"
                                + "  AND p.object IS NULL")

})
//Can't reduce complexity yet
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity"})
@XmlRootElement(name = "permission", namespace = CORE_XML_NS)
public class Permission implements Serializable {

    private static final long serialVersionUID = -2368935232499907547L;

    /**
     * The database id of the permission.
     */
    @Id
    @Column(name = "permission_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "permission-id", namespace = CORE_XML_NS)
    private long permissionId;

    /**
     * The {@link Subject} (a {@link User} or a {@link Group}) to which the
     * permission is granted. If the permission is granted to a {@link Group} a
     * {@link User}s in that have the permission.
     */
    @ManyToOne
    @JoinColumn(name = "grantee_id")
    private Subject grantee;

    /**
     * The {@link Privilege} granted by this {@code Permission}.
     */
    @OneToOne
    @JoinColumn(name = "granted_privilege_id")
    @XmlElement(name = "privilege", namespace = CORE_XML_NS)
    private Privilege grantedPrivilege;

    /**
     * The {@link CcmObject} on which the permission is granted. If the the
     * {@code object} is {@code null} the permission is granted for
     * <strong>all</strong> objects.
     */
    @ManyToOne
    @JoinColumn(name = "object_id")
    private CcmObject object;

    /**
     * The {@link User} which created this {@code Permission}. The property can
     * be {@code null} if this {@code Permission} was created by a system
     * process.
     */
    @ManyToOne
    @JoinColumn(name = "creation_user_id")
    @XmlElement(name = "creation-user", namespace = CORE_XML_NS)
    private User creationUser;

    /**
     * The date and time on which this {@code Permission} was created. This
     * property can be {@code null} if this {@code Permission} was created by a
     * system process.
     */
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(name = "creation-date", namespace = CORE_XML_NS)
    private Date creationDate;

    /**
     * The IP of the system from which this {@code Permission} was created. This
     * property can be {@code null} if this {@code Permission} was created by a
     * system process.
     */
    @Column(name = "creation_ip")
    @XmlElement(name = "creation-ip", namespace = CORE_XML_NS)
    private String creationIp;

    public long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(final long permissionId) {
        this.permissionId = permissionId;
    }

    public Subject getGrantee() {
        return grantee;
    }

    protected void setGrantee(final Subject grantee) {
        this.grantee = grantee;
    }

    public Privilege getGrantedPrivilege() {
        return grantedPrivilege;
    }

    protected void setGrantedPrivilege(final Privilege grantedPrivilege) {
        this.grantedPrivilege = grantedPrivilege;
    }

    public CcmObject getObject() {
        return object;
    }

    protected void setObject(final CcmObject object) {
        this.object = object;
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
        hash
        = 31 * hash + (int) (permissionId ^ (permissionId >>> 32));
        hash = 31 * hash + Objects.hashCode(grantee);
        hash = 31 * hash + Objects.hashCode(grantedPrivilege);
        hash = 31 * hash + Objects.hashCode(object);
        hash = 31 * hash + Objects.hashCode(creationUser);
        hash = 31 * hash + Objects.hashCode(creationDate);
        hash = 31 * hash + Objects.hashCode(creationIp);
        return hash;
    }

    @Override
    //Can't reduce complexity yet
    @SuppressWarnings({"PMD.CyclomaticComplexity",
                       "PMD.StdCyclomaticComplexity",
                       "PMD.ModifiedCyclomaticComplexity",
                       "PMD.NPathComplexity"})
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
        if (!Objects.equals(grantee, other.getGrantee())) {
            return false;
        }
        if (!Objects.equals(grantedPrivilege, other.getGrantedPrivilege())) {
            return false;
        }
        if (!Objects.equals(object, other.getObject())) {
            return false;
        }
        if (!Objects.equals(creationUser, other.getCreationUser())) {
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
                                     + "grantee = %s, "
                                     + "grantedPrivilege = %s, "
                                     + "object = %s, "
                                     + "creationUser = %s,"
                                     + "creationDate = %tF %<tT, "
                                     + "creationIp = %s"
                                     + " }",
                             super.toString(),
                             permissionId,
                             Objects.toString(grantee),
                             Objects.toString(grantedPrivilege),
                             Objects.toString(object),
                             Objects.toString(creationUser),
                             creationDate,
                             creationIp);
    }

}
