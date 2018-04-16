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
 * WebDAV lockinfo XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_lockinfo">Chapter
 * 14.11 "lockinfo XML Element" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlType(propOrder = {"lockScope", "lockType", "owner"})
@XmlRootElement(name = "lockinfo")
public final class LockInfo {

    @XmlElement(name = "lockscope")
    private final LockScope lockScope;

    @XmlElement(name = "locktype")
    private final LockType lockType;

    @XmlElement
    private final Owner owner;

    @SuppressWarnings("unused")
    private LockInfo() {
        this.lockScope = null;
        this.lockType = null;
        this.owner = null;
    }

    public LockInfo(final LockScope lockScope,
                    final LockType lockType,
                    final Owner owner) {

        this.lockScope = Objects.requireNonNull(lockScope);
        this.lockType = Objects.requireNonNull(lockType);
        this.owner = Objects.requireNonNull(owner);
    }

    public final LockScope getLockScope() {
        return this.lockScope;
    }

    public final LockType getLockType() {
        return this.lockType;
    }

    public final Owner getOwner() {
        return this.owner;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(lockScope);
        hash = 67 * hash + Objects.hashCode(lockType);
        hash = 67 * hash + Objects.hashCode(owner);
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
        if (!(obj instanceof LockInfo)) {
            return false;
        }
        final LockInfo other = (LockInfo) obj;
        if (!Objects.equals(lockScope, other.getLockScope())) {
            return false;
        }
        if (!Objects.equals(lockType, other.getLockType())) {
            return false;
        }
        return Objects.equals(owner, other.getOwner());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "lockScope = %s, "
                                 + "lockType = %s,"
                                 + "owner = %s"
                                 + " }",
                             super.toString(),
                             Objects.toString(lockScope),
                             Objects.toString(lockType),
                             Objects.toString(owner));
    }

}
