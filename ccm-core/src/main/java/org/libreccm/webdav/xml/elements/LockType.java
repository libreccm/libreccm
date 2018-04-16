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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * WebDAV locktype XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * <p>
 * This is a singleton. All instances are absolutely identical, hence can be
 * compared using {@code ==} and share one unique hash code. Use {@link #WRITE}
 * always.
 * </p>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_locktype">Chapter
 * 14.15 "locktype XML Element" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "locktype")
@XmlType(factoryMethod = "createSingleton")
public final class LockType {

    /**
     * Singleton instance, providing improved performance and the ability to
     * compare by <em>same</em> instance.
     */
    public static final LockType WRITE = new LockType(Write.WRITE);

    private static LockType createSingleton() {
        return WRITE;
    }

    @SuppressWarnings("unused")
    private final Write write;

    private LockType() {
        this.write = null;
    }

    // Enum
    private LockType(final Write write) {
        this.write = write;
    }
    
    protected Write getWrite() {
        return write;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(write);
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
        if (!(obj instanceof LockType)) {
            return false;
        }
        final LockType other = (LockType) obj;
        return Objects.equals(write, other.getWrite());
    }
    
    @Override
    public String toString() {
        return String.format("%s{  }",
                             super.toString(),
                             Objects.toString(write));
    }

}
