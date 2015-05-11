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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "user_groups")
public class UserGroup extends Party implements Serializable {

    private static final long serialVersionUID = -5555063356689597270L;

    @Column(name = "name", length = 512, nullable = false)
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "sourceGroup")
    private List<Role> roles;
    
    @OneToMany(mappedBy = "group")
    private List<GroupMembership> groupMemberships;

    public UserGroup() {
        super();

        roles = new ArrayList<>();
        groupMemberships = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Role> getRoles() {
        return Collections.unmodifiableList(roles);
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
    
    public List<GroupMembership> getGroupMembership() {
        return Collections.unmodifiableList(groupMemberships);
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserGroup other = (UserGroup) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.roles, other.roles);
    }

    public boolean canEquals(final Object obj) {
        return obj instanceof UserGroup;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", name = \"%s\"%s",
                                            name,
                                            data));
    }

}
