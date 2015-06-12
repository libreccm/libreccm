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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "ccm_groups")
@XmlRootElement(name = "user-group", namespace = CORE_XML_NS)
public class Group extends Party implements Serializable {

    private static final long serialVersionUID = -5555063356689597270L;

    @Column(name = "name", length = 512, nullable = false)
    @NotBlank
    @XmlElement(name = "name", namespace = CORE_XML_NS)
    private String name;

    @OneToMany(mappedBy = "sourceGroup")
    @XmlElementWrapper(name = "roles", namespace = CORE_XML_NS)
    @XmlElement(name ="role", namespace = CORE_XML_NS)
    private List<Role> roles;

    @OneToMany(mappedBy = "group")
    @XmlElementWrapper(name = "group-memberships", namespace = CORE_XML_NS)
    @XmlElement(name = "group-membership", namespace = CORE_XML_NS)
    private List<GroupMembership> members;

    public Group() {
        super();

        roles = new ArrayList<>();
        members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Role> getRoles() {
        if (roles == null) {
            return null;
        } else {
            return Collections.unmodifiableList(roles);
        }
    }

    protected void setRoles(final List<Role> roles) {
        this.roles = roles;
    }

    protected void addRole(final Role role) {
        roles.add(role);
    }

    protected void removeRole(final Role role) {
        roles.remove(role);
    }

    public List<GroupMembership> getMembers() {
        if (members == null) {
            return null;
        } else {
            return Collections.unmodifiableList(members);
        }
    }

    protected void setMembers(final List<GroupMembership> members) {
        this.members = members;
    }

    protected void addMember(final GroupMembership member) {
        members.add(member);
    }

    protected void removeMember(final GroupMembership member) {
        members.remove(member);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.roles);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        final Group other = (Group) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        return Objects.equals(this.roles, other.getRoles());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Group;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", name = \"%s\"%s",
                                            name,
                                            data));
    }

}
