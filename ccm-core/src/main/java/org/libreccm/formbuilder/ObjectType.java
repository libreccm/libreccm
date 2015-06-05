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
package org.libreccm.formbuilder;

import org.libreccm.core.CcmObject;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_object_types")
public class ObjectType extends CcmObject implements Serializable {

    private static final long serialVersionUID = 5236718507025096569L;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "class_name")
    private String className;

    public String getAppName() {
        return appName;
    }

    public void setAppName(final String appName) {
        this.appName = appName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 61 * hash + Objects.hashCode(appName);
        hash = 61 * hash + Objects.hashCode(className);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        
        if (!(obj instanceof ObjectType)) {
            return false;
        }
        final ObjectType other = (ObjectType) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(appName, other.getAppName())) {
            return false;
        }
        return Objects.equals(className, other.getClassName());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ObjectType;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", appName = \"%s\", "
                                                + "className = \"%s\"%s",
                                            appName,
                                            className,
                                            data));
    }

}
