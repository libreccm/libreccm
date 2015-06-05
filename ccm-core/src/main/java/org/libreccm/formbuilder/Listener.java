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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_listeners")
public class Listener extends CcmObject implements Serializable {

    private static final long serialVersionUID = 9030104813240364500L;

    @Column(name = "class_name")
    private String className;

    @Column(name = "attribute_string")
    private String attributeString;

    @ManyToOne
    private Widget widget;

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public String getAttributeString() {
        return attributeString;
    }

    public void setAttributeString(final String attributeString) {
        this.attributeString = attributeString;
    }

    public Widget getWidget() {
        return widget;
    }

    protected void setWidget(final Widget widget) {
        this.widget = widget;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + Objects.hashCode(className);
        hash = 59 * hash + Objects.hashCode(attributeString);
        hash = 59 * hash + Objects.hashCode(widget);
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
        
        if (!(obj instanceof Listener)) {
            return false;
        }
        final Listener other = (Listener) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(className, other.getClassName())) {
            return false;
        }
        if (!Objects.equals(attributeString, other.getAttributeString())) {
            return false;
        }
        return Objects.equals(widget, other.getWidget());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Listener;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", className = \"%s\", "
                                                + "attributeString = \"%s\"%s",
                                            className,
                                            attributeString,
                                            data));
    }

}
