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
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "roles")
@SuppressWarnings("PMD.ShortClassName") //Role is perfectly fine name.
public class Role implements Serializable {

    private static final long serialVersionUID = 3314358449751376350L;

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long roleId;

    @Column(name = "name", length = 512)
    @NotBlank
    private String name;

    @ManyToOne
    private UserGroup sourceGroup;

    @OneToOne
    private UserGroup implicitGroup;

    @Column(name = "description")
    private String description;

    public Role() {
        //Nothing
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(final long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public UserGroup getSourceGroup() {
        return sourceGroup;
    }

    protected void setSourceGroup(final UserGroup sourceGroup) {
        this.sourceGroup = sourceGroup;
    }

    public UserGroup getImplicitGroup() {
        return implicitGroup;
    }

    protected void setImplicitGroup(final UserGroup implicitGroup) {
        this.implicitGroup = implicitGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.roleId ^ (this.roleId >>> 32));
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.sourceGroup);
        hash = 59 * hash + Objects.hashCode(this.implicitGroup);
        hash = 59 * hash + Objects.hashCode(this.description);
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

        if (this.roleId != other.getRoleId()) {
            return false;
        }
        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        if (!Objects.equals(this.sourceGroup, other.getSourceGroup())) {
            return false;
        }
        if (!Objects.equals(this.implicitGroup, other.getImplicitGroup())) {
            return false;
        }
        return Objects.equals(this.description, other.getDescription());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Role;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "roleId = %d, "
                                 + "name = \"%s\", "
                                 + "sourceGroup = %s, "
                                 + "implicitGroup = %s, "
                                 + " }",
                             super.toString(),
                             roleId,
                             name,
                             Objects.toString(sourceGroup),
                             Objects.toString(implicitGroup));
    }

}
