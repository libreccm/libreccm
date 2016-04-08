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

import org.libreccm.core.DefaultEntityGraph;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A group is basically a collection of users.
 *
 * Group extends the {@link Party} class. Therefore {@link Role}s can be
 * assigned to a group. When a {@link Role} is assigned to a group each member
 * of the group gets the role and the permissions associated with that role.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "GROUPS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "Group.findByName",
                query = "SELECT g FROM Group g WHERE g.name = :name"),
    @NamedQuery(name = "Group.searchByName",
                query = "SELECT g FROM Group g "
                            + "WHERE LOWER(g.name) LIKE '%:name%'")
})
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "Group.withMembersAndRoleMemberships",
        attributeNodes = {
            @NamedAttributeNode(
                value = "memberships"),
            @NamedAttributeNode(
                value = "roleMemberships",
                subgraph = "role")},
        subgraphs = {
            @NamedSubgraph(
                name = "role",
                attributeNodes = {
                    @NamedAttributeNode(value = "role",
                                        subgraph = "permissions")
                }),
            @NamedSubgraph(
                name = "permissions",
                attributeNodes = {
                    @NamedAttributeNode(value = "permissions")})
        })
})
@DefaultEntityGraph("Group.withMembersAndRoleMemberships")
@XmlRootElement(name = "user-group", namespace = CORE_XML_NS)
public class Group extends Party implements Serializable {

    private static final long serialVersionUID = -4800759206452780739L;

    /**
     * The memberships of the group. For adding or removing memberships the
     * methods provided by the {@link GroupManager} should be used.
     */
    @OneToMany(mappedBy = "group")
    @XmlElementWrapper(name = "group-memberships", namespace = CORE_XML_NS)
    @XmlElement(name = "group-membership", namespace = CORE_XML_NS)
    private Set<GroupMembership> memberships = new HashSet<>();

    protected Group() {
        super();
    }

    public Set<GroupMembership> getMemberships() {
        if (memberships == null) {
            return null;
        } else {
            return Collections.unmodifiableSet(memberships);
        }
    }

    protected void setMemberships(final Set<GroupMembership> memberships) {
        this.memberships = memberships;
    }

    protected void addMembership(final GroupMembership member) {
        memberships.add(member);
    }

    protected void removeMembership(final GroupMembership member) {
        memberships.remove(member);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Group)) {
            return false;
        }
        final Group other = (Group) obj;
        return other.canEqual(this);
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Group;
    }

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", members = { %s }%s",
                                            Objects.toString(memberships),
                                            data));
    }

}
