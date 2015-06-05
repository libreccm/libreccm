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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "ccm_privileges")
@XmlRootElement(name = "privilege", namespace = XML_NS)
public class Privilege implements Serializable {

    private static final long serialVersionUID = -3986038536996049440L;

    @Id
    @Column(name = "privilege_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "privilege-id", namespace = XML_NS)
    private long privilegeId;

    @Column(name = "privilege", length = 255, nullable = false)
    //Field is named like this in the old PDL class, don't want to change it now
    @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
    @XmlElement(name = "privilege", namespace = XML_NS)
    private String privilege;

    public long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(final long privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(final String privilege) {
        this.privilege = privilege;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (privilegeId ^ (privilegeId >>> 32));
        hash = 43 * hash + Objects.hashCode(privilege);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Privilege)) {
            return false;
        }
        final Privilege other = (Privilege) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (privilegeId != other.getPrivilegeId()) {
            return false;
        }
        return Objects.equals(privilege, other.getPrivilege());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Privilege;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "privilegeId = %d, "
                                 + "privilege = \"%s\""
                                 + " }",
                             super.toString(),
                             privilegeId,
                             privilege);
    }

}
