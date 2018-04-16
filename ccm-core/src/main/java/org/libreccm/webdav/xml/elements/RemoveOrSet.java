/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.webdav.xml.elements;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * Internal superclass of set and remove WebDAV elements.
 *
 * This class shall not be used directly, but instead <code>Set</code> and
 * <code>Remove</code> classes should be used.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @see Set
 * @see Remove
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public abstract class RemoveOrSet {

    @XmlElement
    private final Prop prop;

    public final Prop getProp() {
        return this.prop;
    }

    protected RemoveOrSet() {
        this.prop = null;
    }

    public RemoveOrSet(final Prop prop) {
        this.prop = Objects.requireNonNull(prop);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(prop);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RemoveOrSet)) {
            return false;
        }
        final RemoveOrSet other = (RemoveOrSet) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        return Objects.equals(prop, other.getProp());
    }
    
    public boolean canEqual(final Object obj) {
        return obj instanceof RemoveOrSet;
    }
    
    public String toString(final String data) {
        return String.format("%s{ prop = %s%s }",
                             super.toString(),
                             Objects.toString(prop),
                             data);
    }
    
    @Override
    public final String toString() {
        return toString("");
    }

}
