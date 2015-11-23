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

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A {@code Group} is collection of {@link User}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CCM_GROUPS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "Group.findGroupByName",
                query = "SELECT g FROM Group g WHERE g.name = :groupName"),
    @NamedQuery(name = "Group.searchGroupByName",
                query = "SELECT g FROM Group g "
                            + "WHERE LOWER(g.name) LIKE '%:groupName%'")
})
@XmlRootElement(name = "user-group", namespace = CORE_XML_NS)
public class Group extends Subject implements Serializable {

    private static final long serialVersionUID = -5555063356689597270L;

    /**
     * The name of the {@code Group}. Must be unique.
     */
    @Column(name = "NAME", length = 512, unique = true, nullable = false)
    @NotBlank
    @XmlElement(name = "name", namespace = CORE_XML_NS)
    private String name;

    /**
     * The {@link Role}s assigned to the {@code Group}.
     */
    @OneToMany(mappedBy = "sourceGroup")
    @XmlElementWrapper(name = "roles", namespace = CORE_XML_NS)
    @XmlElement(name = "role", namespace = CORE_XML_NS)
    private List<Role> roles;

    /**
     * The members of the group. For adding or removing members the methods
     * provided by the {@link GroupManager} should be used.
     */
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

        return Objects.equals(this.name, other.getName());
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
