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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * WebDAV lockentry XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_lockentry">Chapter
 * 14.10 "lockentry XML Element" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlType(propOrder = {"lockScope", "lockType"})
@XmlRootElement(name = "lockentry")
public final class LockEntry {

    @XmlElement(name = "lockscope")
    private final LockScope lockScope;

    @XmlElement(name = "locktype")
    private final LockType lockType;

    @SuppressWarnings("unused")
    private LockEntry() {
        this.lockScope = null;
        this.lockType = null;
    }

    public LockEntry(final LockScope lockScope, final LockType lockType) {

        Objects.requireNonNull(lockScope);
        Objects.requireNonNull(lockType);

        this.lockScope = lockScope;
        this.lockType = lockType;
    }

    public final LockScope getLockScope() {
        return this.lockScope;
    }

    public final LockType getLockType() {
        return this.lockType;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(lockScope);
        hash = 59 * hash + Objects.hashCode(lockType);
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
        if (!(obj instanceof LockEntry)) {
            return false;
        }
        final LockEntry other = (LockEntry) obj;
        if (!Objects.equals(lockScope, other.getLockScope())) {
            return false;
        }
        return Objects.equals(lockType, other.getLockType());
    }

    @Override
    public String toString() {
        return String.format("%s{ lockScope = %s,"
                                 + "lockType = %s }",
                             super.toString(),
                             Objects.toString(lockScope),
                             Objects.toString(lockType));
    }

}
