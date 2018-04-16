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

import org.libreccm.webdav.ConstantsAdapter;

import java.util.Collection;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import static java.util.Arrays.*;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * WebDAV lockscope XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_lockscope">Chapter
 * 14.13 "lockscope XML Element" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlAccessorType(FIELD)
@XmlType(propOrder = {"exclusive", "shared"})
@XmlJavaTypeAdapter(LockScope.Adapter.class)
@XmlRootElement(name = "lockscope")
public final class LockScope {

    public static final LockScope SHARED = new LockScope(Shared.SHARED, null);

    public static final LockScope EXCLUSIVE = new LockScope(null,
                                                            Exclusive.EXCLUSIVE);

    private final Shared shared;

    private final Exclusive exclusive;

    private LockScope() {
        this.shared = null;
        this.exclusive = null;
    }

    private LockScope(final Shared shared, final Exclusive exclusive) {
        this.shared = shared;
        this.exclusive = exclusive;
    }

    protected Shared getShared() {
        return shared;
    }

    protected Exclusive getExclusive() {
        return exclusive;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(shared);
        hash = 97 * hash + Objects.hashCode(exclusive);
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
        if (!(obj instanceof LockScope)) {
            return false;
        }
        final LockScope other = (LockScope) obj;
        if (!Objects.equals(shared, other.getShared())) {
            return false;
        }
        return Objects.equals(exclusive, other.getExclusive());
    }

    /**
     * Guarantees that any unmarshalled enum constants effectively are the
     * constant Java instances itself, so that {@code ==} can be used form
     * comparison.
     *
     */
    protected static final class Adapter extends ConstantsAdapter<LockScope> {

        @Override
        protected final Collection<LockScope> getConstants() {
            return asList(SHARED, EXCLUSIVE);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s{ shared = %s, exclusive = %s }",
                             super.toString(),
                             Objects.toString(shared),
                             Objects.toString(exclusive));
    }

}
