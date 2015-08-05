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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Privileges are used the create {@link Permission}s for specific operations.
 * Modules can create {@code Privilege}s by using the
 * {@link PrivilegeRepository}.
 *
 * This class is an JPA implementation of the PDL object type
 * {@code com.arsdigita.kernel.permissions.Privilege} which has been implemented
 * as an JPA entity. In future releases this class will may refactored to an
 * {@code enum}. After the class has been refactored to an {@code enum} it is
 * not longer necessary to store the available privileges in the database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "ccm_privileges")
@NamedQueries({
    @NamedQuery(name = "findPrivilegeByName",
                query = "SELECT p FROM Privilege p "
                            + "WHERE p.label = :label"),
    @NamedQuery(name = "isPrivilegeInUse",
                query = "SELECT COUNT(p) FROM Permission p "
                + "      JOIN p.grantedPrivilege g "
                + "      WHERE g.label = :label")
})
@XmlRootElement(name = "privilege", namespace = CORE_XML_NS)
public class Privilege implements Serializable {

    private static final long serialVersionUID = -3986038536996049440L;
    
    //Constant for the admin privilege.
    public static final String ADMIN = "admin";

    @Id
    @Column(name = "privilege_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "privilege-id", namespace = CORE_XML_NS)
    private long privilegeId;

    @Column(name = "label", length = 255, nullable = false)
    //Field is named like this in the old PDL class, don't want to change it now
    @XmlElement(name = "label", namespace = CORE_XML_NS)
    private String label;

    public long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(final long privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (privilegeId ^ (privilegeId >>> 32));
        hash = 43 * hash + Objects.hashCode(label);
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
        return Objects.equals(label, other.getLabel());
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
                             label);
    }

}
