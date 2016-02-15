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

import org.hibernate.validator.constraints.NotBlank;
import org.libreccm.workflow.TaskAssignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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
})
@XmlRootElement(name = "role", namespace = CORE_XML_NS)
@SuppressWarnings({"PMD.ShortClassName", "PMD.TooManyMethods"})
public class Role implements Serializable {

    private static final long serialVersionUID = -7121296514181469687L;

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
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9\\-_]*")
    @XmlElement(name = "name", namespace = CORE_XML_NS)
    private String name;

    /**
     * All memberships of the roles.
     */
    @OneToMany(mappedBy = "role")
    @XmlElementWrapper(name = "role-memberships", namespace = CORE_XML_NS)
    @XmlElement(name = "role-membership", namespace = CORE_XML_NS)
    private Set<RoleMembership> memberships = new HashSet<>();
    
    /**
     * Permissions granted to the role.
     */
    @OneToMany(mappedBy = "grantee")
    @XmlElementWrapper(name = "permissions", namespace = CORE_XML_NS)
    @XmlElement(name = "permission", namespace = CORE_XML_NS)
    private List<Permission> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "role")
    private List<TaskAssignment> assignedTasks;
    
    protected Role() {
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
    
    protected void addAssignedTask(final TaskAssignment taskAssignment) {
        assignedTasks.add(taskAssignment);
    }
    
    protected void removeAssignedTask(final TaskAssignment taskAssignment) {
        assignedTasks.remove(taskAssignment);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (roleId ^ (roleId >>> 32));
        hash = 53 * hash + Objects.hashCode(name);
        hash = 53 * hash + Objects.hashCode(permissions);
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
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        return Objects.equals(permissions, other.getPermissions());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Role;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "roldId = %d, "
                                 + "name = \"%s\", "
                                 + "permissions = { %s }"
                                 + " }",
                             super.toString(),
                             roleId,
                             name,
                             Objects.toString(permissions));
    }

}
