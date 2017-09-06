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
import org.libreccm.portation.Portable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.CORE_XML_NS;
import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * Association class representing the association between a {@link Role} and a
 * {@code Party}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "ROLE_MEMBERSHIPS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "RoleMembership.findByRoleAndMember",
                query = "SELECT m FROM RoleMembership m "
                            + "WHERE m.member = :member AND m.role = :role")
})
@XmlRootElement(name = "role-membership", namespace = CORE_XML_NS)
@JsonIdentityInfo(generator = RoleMembershipIdGenerator.class,
                  property = "customMemId")
public class RoleMembership implements Serializable, Portable {

    private static final long serialVersionUID = -3049727720697964793L;

    @Id
    @Column(name = "MEMBERSHIP_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "membership-id", namespace = CORE_XML_NS)
    private long membershipId;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    @XmlTransient
    @JsonIdentityReference(alwaysAsId = true)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    @XmlTransient
    @JsonIdentityReference(alwaysAsId = true)
    private Party member;

    public long getMembershipId() {
        return membershipId;
    }

    protected void setMembershipId(final long membershipId) {
        this.membershipId = membershipId;
    }

    public Role getRole() {
        return role;
    }

    protected void setRole(final Role role) {
        this.role = role;
    }

    public Party getMember() {
        return member;
    }

    protected void setMember(final Party member) {
        this.member = member;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash
                   + (int) (membershipId ^ (membershipId >>> 32));
        hash = 37 * hash + Objects.hashCode(role);
        hash = 37 * hash + Objects.hashCode(member);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof RoleMembership)) {
            return false;
        }
        final RoleMembership other = (RoleMembership) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (membershipId != other.getMembershipId()) {
            return false;
        }
        if (!Objects.equals(role, other.getRole())) {
            return false;
        }
        return Objects.equals(member, other.getMember());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof RoleMembership;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "membershipId = %d, "
                                 + "user = %s, "
                                 + "role = %s "
                                 + " }",
                             super.toString(),
                             membershipId,
                             Objects.toString(member),
                             Objects.toString(role));
    }

}
