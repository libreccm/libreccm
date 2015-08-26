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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * An association class for the association between a group and it members.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "group_memberships", schema = "ccm_core")
@XmlRootElement(name = "group-membership", 
                namespace = CORE_XML_NS)
public class GroupMembership implements Serializable {

    private static final long serialVersionUID = 1897274442468035089L;

    @Id
    @Column(name = "membership_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "membership-id", namespace = CORE_XML_NS)
    private long membershipId;

    @ManyToOne
    @XmlTransient
    private Group group;

    @ManyToOne
    @XmlTransient
    private User user;

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

    public User getUser() {
        return user;
    }

    protected void setUser(final User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash
                   + (int) (this.membershipId ^ (this.membershipId >>> 32));
        hash = 37 * hash + Objects.hashCode(this.group);
        hash = 37 * hash + Objects.hashCode(this.user);
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
        return Objects.equals(this.user, other.getUser());
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
                             Objects.toString(user),
                             Objects.toString(group));
    }

}
