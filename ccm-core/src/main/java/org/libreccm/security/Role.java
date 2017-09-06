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
import org.hibernate.search.annotations.Field;
import org.hibernate.validator.constraints.NotBlank;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.portation.Portable;
import org.libreccm.workflow.TaskAssignment;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

import static org.libreccm.core.CoreConstants.CORE_XML_NS;
import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * A role is basically a collection a {@link Permission}s and {@code Task}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CCM_ROLES", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "Role.findByName",
                query = "SELECT r FROM Role r "
                            + "WHERE r.name = :name")
    ,
    @NamedQuery(
        name = "Role.count",
        query = "SELECT COUNT(r) FROM Role r")
    ,
    @NamedQuery(
        name = "Role.findAllOrderedByRoleName",
        query = "SELECT r FROM Role r ORDER BY r.name")
    ,
    @NamedQuery(
        name = "Role.findAllOrderedByRoleNameLimit",
        query = "SELECT r FROM Role r ORDER BY r.name ")
    ,
    @NamedQuery(
        name = "Role.findAllOrderedByRoleNameDesc",
        query = "SELECT r FROM Role r ORDER BY r.name DESC")
    ,
    @NamedQuery(
        name = "Role.searchByName",
        query = "SELECT r FROM Role r "
                    + "WHERE LOWER(r.name) LIKE CONCAT(LOWER(:name), '%') "
                    + "ORDER BY r.name ")
    ,
    @NamedQuery(
        name = "Role.searchByNameCount",
        query = "SELECT COUNT(r.name) FROM Role r "
                    + "WHERE LOWER(r.name) LIKE CONCAT(LOWER(:name), '%') "
                    + "GROUP BY r.name "
                    + "ORDER BY r.name ")
    ,
    @NamedQuery(
        name = "Role.findByPrivilege",
        query = "SELECT r FROM Role r JOIN r.permissions p "
                    + "WHERE p.grantedPrivilege = :privilege "
                    + "ORDER BY r.name")
    ,
    @NamedQuery(
        name = "Role.findByPrivilegeAndObject",
        query = "SELECT r FROM Role r JOIN r.permissions p "
                    + "WHERE p.grantedPrivilege = :privilege "
                    + "AND p.object = :object "
                    + "ORDER BY r.name")
    ,
    @NamedQuery(
        name = "Role.findRolesOfUser",
        query = "SELECT r.role FROM RoleMembership r "
                    + "WHERE r.member = :user")
    ,
    @NamedQuery(
        name = "Role.findByParty",
        query = "SELECT r FROM Role r "
                    + "JOIN r.memberships m "
                    + "WHERE m.member = :member"
    )
})
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = Role.ENTITY_GRPAH_WITH_MEMBERS,
        attributeNodes = {
            @NamedAttributeNode(value = "memberships"),})
    ,
    @NamedEntityGraph(
        name = Role.ENTITY_GRPAH_WITH_PERMISSIONS,
        attributeNodes = {
            @NamedAttributeNode(value = "permissions")
        })
})
@XmlRootElement(name = "role", namespace = CORE_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings({"PMD.ShortClassName", "PMD.TooManyMethods"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = RoleIdResolver.class,
                  property = "name")
public class Role implements Serializable, Portable {

    private static final long serialVersionUID = -7121296514181469687L;

    public static final String ENTITY_GRPAH_WITH_MEMBERS
                                   = "Role.withMembers";
    public static final String ENTITY_GRPAH_WITH_PERMISSIONS
                                   = "Role.withPermissions";

    @Id
    @Column(name = "ROLE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "role-id", namespace = CORE_XML_NS)
    private long roleId;

    /**
     * The name of the role. May only contain the letters a to z, A to Z, the
     * numbers 0 to 9, the {@code -} (dash) and the {@code _} (underscore).
     */
    @Column(name = "NAME", length = 512, nullable = false)
    @Field
    @NotBlank
//    @Pattern(regexp = "[a-zA-Z0-9\\-_]*")
    @XmlElement(name = "name", namespace = CORE_XML_NS)
    private String name;

    /**
     * An optional description for a role.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "ROLE_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "ROLE_ID")
                               }))
    @XmlElement(name = "description", namespace = CORE_XML_NS)
    private LocalizedString description = new LocalizedString();

    /**
     * All memberships of the roles.
     */
    @OneToMany(mappedBy = "role")
    @XmlElementWrapper(name = "role-memberships", namespace = CORE_XML_NS)
    @XmlElement(name = "role-membership", namespace = CORE_XML_NS)
    @JsonIgnore
    private Set<RoleMembership> memberships = new HashSet<>();

    /**
     * Permissions granted to the role.
     */
    @OneToMany(mappedBy = "grantee")
    @XmlElementWrapper(name = "permissions", namespace = CORE_XML_NS)
    @XmlElement(name = "permission", namespace = CORE_XML_NS)
    @JsonIgnore
    private List<Permission> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<TaskAssignment> assignedTasks = new ArrayList<>();

    public Role() {
        super();
    }

    public long getRoleId() {
        return roleId;
    }

    protected void setRoleId(final long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<RoleMembership> getMemberships() {
        if (memberships == null) {
            return null;
        } else {
            return Collections.unmodifiableSet(memberships);
        }
    }

    protected void setMemberships(final Set<RoleMembership> memberships) {
        this.memberships = memberships;
    }

    protected void addMembership(final RoleMembership membership) {
        memberships.add(membership);
    }

    protected void removeMembership(final RoleMembership membership) {
        memberships.remove(membership);
    }

    public List<Permission> getPermissions() {
        if (permissions == null) {
            return null;
        } else {
            return Collections.unmodifiableList(permissions);
        }
    }

    protected void setPermissions(final List<Permission> permissions) {
        this.permissions = permissions;
    }

    protected void addPermission(final Permission permission) {
        permissions.add(permission);
    }

    protected void removePermission(final Permission permission) {
        permissions.remove(permission);
    }

    public List<TaskAssignment> getAssignedTasks() {
        if (assignedTasks == null) {
            return null;
        } else {
            return Collections.unmodifiableList(assignedTasks);
        }
    }

    protected void setAssignedTasks(final List<TaskAssignment> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public void addAssignedTask(final TaskAssignment taskAssignment) {
        assignedTasks.add(taskAssignment);
    }

    public void removeAssignedTask(final TaskAssignment taskAssignment) {
        assignedTasks.remove(taskAssignment);
    }

    public LocalizedString getDescription() {
        return this.description;
    }

    public void setDescription(final LocalizedString description) {
        Objects.requireNonNull(description);
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (roleId ^ (roleId >>> 32));
        hash = 53 * hash + Objects.hashCode(name);
//        hash = 53 * hash + Objects.hashCode(permissions);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Role)) {
            return false;
        }
        final Role other = (Role) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (roleId != other.getRoleId()) {
            return false;
        }

        return Objects.equals(name, other.getName());
//        if (!Objects.equals(name, other.getName())) {
//            return false;
//        }
//        return Objects.equals(permissions, other.getPermissions());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Role;
    }

    @Override
    public String toString() {
//        return String.format("%s{ "
//                                 + "roldId = %d, "
//                                 + "name = \"%s\", "
//                                 + "permissions = { %s }"
//                                 + " }",
//                             super.toString(),
//                             roleId,
//                             name,
//                             Objects.toString(permissions));
        return String.format("%s{ "
                                 + "roldId = %d, "
                                 + "name = \"%s\", "
                                 + " }",
                             super.toString(),
                             roleId,
                             name);
    }

}
