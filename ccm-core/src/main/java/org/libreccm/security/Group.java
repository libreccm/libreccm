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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.libreccm.portation.Portable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.libreccm.core.CoreConstants.CORE_XML_NS;
import static org.libreccm.core.CoreConstants.DB_SCHEMA;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
    @NamedQuery(
        name = "Group.findByName",
        query = "SELECT g FROM Group g WHERE g.name = :name "
                    + "ORDER BY g.name")
    ,
    @NamedQuery(
        name = "Group.searchByName",
        query = "SELECT g FROM Group g "
                    + "WHERE LOWER(g.name) LIKE CONCAT(LOWER(:name), '%') "
                    + "ORDER BY g.name")
    ,
    @NamedQuery(
        name = "Group.findAllOrderedByGroupName",
        query = "SELECT g FROM Group g ORDER BY g.name")
    ,
    @NamedQuery(
        name = "Group.findByMember",
        query = "SELECT g FROM Group g "
                    + "JOIN g.memberships m "
                    + "WHERE m.member = :member"
    )
})
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "Group.withMembersAndRoleMemberships",
        attributeNodes = {
            @NamedAttributeNode(value = "memberships")
            ,
            @NamedAttributeNode(value = "roleMemberships",
                                subgraph = "role")},
        subgraphs = {
            @NamedSubgraph(
                name = "role",
                attributeNodes = {
                    @NamedAttributeNode(value = "role",
                                        subgraph = "permissions")
                })
            ,
            @NamedSubgraph(
                name = "permissions",
                attributeNodes = {
                    @NamedAttributeNode(value = "permissions")})
        })
})
@XmlRootElement(name = "user-group", namespace = CORE_XML_NS)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = GroupIdResolver.class,
                  property = "name")
public class Group extends Party implements Serializable, Portable {

    private static final long serialVersionUID = -4800759206452780739L;

    /**
     * The memberships of the group. For adding or removing memberships the
     * methods provided by the {@link GroupManager} should be used.
     */
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @XmlElementWrapper(name = "group-memberships", namespace = CORE_XML_NS)
    @XmlElement(name = "group-membership", namespace = CORE_XML_NS)
    @JsonIgnore
    private Set<GroupMembership> memberships = new HashSet<>();

    public Group() {
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

}