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
package org.libreccm.core.modules;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "installed_modules", schema = DB_SCHEMA)
public class InstalledModule implements Serializable {

    private static final long serialVersionUID = 6240025652113643164L;

    /**
     * ID of the installed module. By convention the hash code of the module's
     * class name is used (@code{getModuleClassName().hashCode()}). We would use
     * the {@code moduleClassName} directly but not all databases (MySQL in
     * particular) accept long varchar fields as primary keys.
     */
    @Id
    @Column(name = "module_id")
    private int moduleId;

    @Column(name = "module_class_name", length = 2048, unique = true)
    private String moduleClassName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ModuleStatus status;

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(final int moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleClassName() {
        return moduleClassName;
    }

    public void setModuleClassName(final String moduleClassName) {
        this.moduleClassName = moduleClassName;
    }

    public ModuleStatus getStatus() {
        return status;
    }

    public void setStatus(final ModuleStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(moduleClassName);
        hash = 79 * hash + Objects.hashCode(status);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final InstalledModule other = (InstalledModule) obj;
        if (!Objects.equals(moduleClassName, other.getModuleClassName())) {
            return false;
        }
        return status == other.getStatus();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof InstalledModule;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format(
            "%s{ "
                + "moduleClassName = \"%s\"; "
                + "status = %s"
                + "%s"
                + " }",
            super.toString(),
            moduleClassName,
            status.toString(),
            data);
    }

}
