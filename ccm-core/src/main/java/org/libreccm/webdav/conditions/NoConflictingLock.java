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
package org.libreccm.webdav.conditions;

import org.libreccm.webdav.xml.elements.HRef;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * WebDAV no-conflicting-lock Precondition XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#precondition.postcondition.xml.elements">Chapter
 * 16 "Precondition/Postcondition XML Elements" of RFC 4918 "HTTP Extensions for
 * Web Distributed Authoring and Versioning (WebDAV)"</a>
 *
 *
 */
@XmlRootElement(name = "no-conflicting-lock")
public final class NoConflictingLock {

    @XmlElement(name = "href")
    private final List<HRef> hRefs;

    public NoConflictingLock() {
        this.hRefs = new LinkedList<HRef>();
    }

    public NoConflictingLock(final HRef... hRefs) {
        this.hRefs = Arrays.asList(hRefs);
    }

    public final List<HRef> getHRefs() {
        return Collections.unmodifiableList(this.hRefs);

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.hRefs);
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
        if (!(obj instanceof NoConflictingLock)) {
            return false;
        }
        final NoConflictingLock other = (NoConflictingLock) obj;
        return Objects.equals(this.hRefs, other.getHRefs());
    }

    @Override
    public final String toString() {
        return String.format("%s{ %s }",
                             super.toString(),
                             Objects.toString(hRefs));
    }

}
