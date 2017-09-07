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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FORMBUILDER_METAOBJECTS", schema = DB_SCHEMA)
public class MetaObject extends CcmObject implements Serializable {

    private static final long serialVersionUID = -3770682858640493776L;

    @Column(name = "PRETTY_NAME")
    private String prettyName;

    @Column(name = "PRETTY_PLURAL")
    private String prettyPlural;

    @Column(name = "CLASS_NAME")
    private String className;

    @Column(name = "PROPERTIES_FORM")
    private String propertiesForm;

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(final String prettyName) {
        this.prettyName = prettyName;
    }

    public String getPrettyPlural() {
        return prettyPlural;
    }

    public void setPrettyPlural(final String prettyPlural) {
        this.prettyPlural = prettyPlural;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public String getPropertiesForm() {
        return propertiesForm;
    }

    public void setPropertiesForm(final String propertiesForm) {
        this.propertiesForm = propertiesForm;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + Objects.hashCode(prettyName);
        hash = 37 * hash + Objects.hashCode(prettyPlural);
        hash = 37 * hash + Objects.hashCode(className);
        hash = 37 * hash + Objects.hashCode(propertiesForm);
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
        
        if (!(obj instanceof MetaObject)) {
            return false;
        }
        final MetaObject other = (MetaObject) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(prettyName, other.getPrettyName())) {
            return false;
        }
        if (!Objects.equals(prettyPlural, other.getPrettyPlural())) {
            return false;
        }
        if (!Objects.equals(className, other.getClassName())) {
            return false;
        }
        return Objects.equals(propertiesForm, other.getPropertiesForm());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof MetaObject;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", prettyName = \"%s\", "
                                                + "prettyPlural = \"%s\", "
                                                + "className = \"%s\", "
                                                + "propertiesForm = \"%s\"%s",
                                            prettyName,
                                            prettyPlural,
                                            className,
                                            propertiesForm,
                                            data));
    }

}
