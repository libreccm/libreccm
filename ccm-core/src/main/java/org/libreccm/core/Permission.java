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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "permissions")
//Can't reduce complexity yet
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity"})
@XmlRootElement(name = "permission", namespace = CORE_XML_NS)
public class Permission implements Serializable {

    private static final long serialVersionUID = -2368935232499907547L;

    @Id
    @Column(name = "permission_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "permission-id", namespace = CORE_XML_NS)
    private long permissionId;

    @ManyToOne
    @JoinColumn(name = "grantee_id")
    private Subject grantee;

    @OneToOne
    @JoinColumn(name = "granted_privilege_id")
    @XmlElement(name = "privilege", namespace = CORE_XML_NS)
    private Privilege grantedPrivilege;

    @ManyToOne
    @JoinColumn(name = "object_id")
    private CcmObject object;

    @ManyToOne
    @JoinColumn(name = "creation_user_id")
    @XmlElement(name = "creation-user", namespace = CORE_XML_NS)
    private User creationUser;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(name = "creation-date", namespace = CORE_XML_NS)
    private Date creationDate;

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
