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

import static org.libreccm.core.CoreConstants.*;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A association class representing the assoication between a {@link User} and
 * a {@code Group}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "GROUP_MEMBERSHIPS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "GroupMembership.findByGroupAndUser",
                query = "SELECT m FROM GroupMembership m "
                            + "WHERE m.member = :member AND m.group = :group")})
@XmlRootElement(name = "group-membership", namespace = CORE_XML_NS)
public class GroupMembership implements Serializable {

    private static final long serialVersionUID = 83192968306850665L;

    @Id
    @Column(name = "MEMBERSHIP_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "membership-id", namespace = CORE_XML_NS)
    private long membershipId;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    @XmlTransient
    private Group group;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    @XmlTransient
    private User member;

    public long getMembershipId() {
        return membershipId;
    }

    protected void setMembershipId(final long membershipId) {
        this.membershipId = membershipId;
    }

    public Group getGroup() {
        return group;
    }

    protected void setGroup(final Group group) {
        this.group = group;
    }

    public User getMember() {
        return member;
    }

    protected void setMember(final User member) {
        this.member = member;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash
                   + (int) (this.membershipId ^ (this.membershipId >>> 32));
        hash = 37 * hash + Objects.hashCode(this.group);
        hash = 37 * hash + Objects.hashCode(this.member);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GroupMembership)) {
            return false;
        }
        final GroupMembership other = (GroupMembership) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (this.membershipId != other.getMembershipId()) {
            return false;
        }
        if (!Objects.equals(this.group, other.getGroup())) {
            return false;
        }
        return Objects.equals(this.member, other.getMember());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof GroupMembership;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "membershipId = %d, "
                                 + "user = %s, "
                                 + "group = %s, "
                                 + " },",
                             super.toString(),
                             membershipId,
                             Objects.toString(member),
                             Objects.toString(group));
    }

}
