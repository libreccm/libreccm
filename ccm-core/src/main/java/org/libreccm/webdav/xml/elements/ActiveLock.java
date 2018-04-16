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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * WebDAV {@code activelock} XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_activelock">Chapter
 * 14.1 "activelock XML Element" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"lockScope",
                      "lockType",
                      "depth",
                      "owner",
                      "timeOut",
                      "lockToken",
                      "lockRoot"})
@XmlRootElement(name = "activelock")
public class ActiveLock {

    @XmlElement(name = "lockscope")
    private final LockScope lockScope;

    @XmlElement(name = "locktype")
    private final LockType lockType;

    private final Depth depth;

    private final Owner owner;

    @XmlElement(name = "timeout")
    private final TimeOut timeOut;

    @XmlElement(name = "locktoken")
    private final LockToken lockToken;

    @XmlElement(name = "lockroot")
    private final LockRoot lockRoot;

    private ActiveLock() {
        this.lockScope = null;
        this.lockType = null;
        this.depth = null;
        this.owner = null;
        this.timeOut = null;
        this.lockToken = null;
        this.lockRoot = null;
    }

    public ActiveLock(final LockScope lockScope,
                      final LockType lockType,
                      final Depth depth,
                      final Owner owner,
                      final TimeOut timeOut,
                      final LockToken lockToken,
                      final LockRoot lockRoot) {

        this.lockScope = Objects.requireNonNull(lockScope, "lockScope");
        this.lockType = Objects.requireNonNull(lockType, "lockType");
        this.depth = Objects.requireNonNull(depth, "depth");
        this.owner = owner;
        this.timeOut = timeOut;
        this.lockToken = lockToken;
        this.lockRoot = Objects.requireNonNull(lockRoot, "lockRoot");
    }

    public final LockScope getLockScope() {
        return this.lockScope;
    }

    public final LockType getLockType() {
        return this.lockType;
    }

    public final Depth getDepth() {
        return this.depth;
    }

    public final Owner getOwner() {
        return this.owner;
    }

    public final TimeOut getTimeOut() {
        return this.timeOut;
    }

    public final LockToken getLockToken() {
        return this.lockToken;
    }

    public final LockRoot getLockRoot() {
        return this.lockRoot;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(lockScope);
        hash = 71 * hash + Objects.hashCode(lockType);
        hash = 71 * hash + Objects.hashCode(depth);
        hash = 71 * hash + Objects.hashCode(owner);
        hash = 71 * hash + Objects.hashCode(timeOut);
        hash = 71 * hash + Objects.hashCode(lockToken);
        hash = 71 * hash + Objects.hashCode(lockRoot);
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
        if (!(obj instanceof ActiveLock)) {
            return false;
        }
        final ActiveLock other = (ActiveLock) obj;
        if (!Objects.equals(lockScope, other.getLockScope())) {
            return false;
        }
        if (!Objects.equals(lockType, other.getLockType())) {
            return false;
        }
        if (depth != other.getDepth()) {
            return false;
        }
        if (!Objects.equals(owner, other.getOwner())) {
            return false;
        }
        if (!Objects.equals(timeOut, other.getTimeOut())) {
            return false;
        }
        if (!Objects.equals(lockToken, other.getLockToken())) {
            return false;
        }
        return Objects.equals(lockRoot, other.getLockRoot());
    }
    
    @Override
    public String toString() {
        return String.format("%s{ lockScope = %s, "
            + "lockType = %s, "
            + "depth = %s, "
            + "owner = %s,"
            + "timeOut = %s, "
            + "lockToken = %s, "
            + "lockRoot = %s }",
                             super.toString(),
                             Objects.toString(lockScope),
                             Objects.toString(lockType),
                             Objects.toString(depth),
                             Objects.toString(owner),
                             Objects.toString(timeOut),
                             Objects.toString(lockToken),
                             Objects.toString(lockRoot));
    }
    

}
