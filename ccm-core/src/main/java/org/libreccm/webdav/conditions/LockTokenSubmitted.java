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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static java.util.Collections.*;

/**
 *
 * WebDAV lock-token-submitted Precondition XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#precondition.postcondition.xml.elements">Chapter
 * 16 "Precondition/Postcondition XML Elements" of RFC 4918 "HTTP Extensions for
 * Web Distributed Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlRootElement(name = "lock-token-submitted")
public final class LockTokenSubmitted {

    @XmlElement(name = "href")
    private final List<HRef> hRefs;

    @SuppressWarnings("unused")
    private LockTokenSubmitted() {
        this.hRefs = new LinkedList<HRef>();
    }

    public LockTokenSubmitted(final HRef hRef, final HRef... hRefs) {
        this.hRefs = createList(Objects.requireNonNull(hRef), hRefs);
    }

    private static <T> List<T> createList(final T first, final T[] other) {

        final List<T> list = new LinkedList<>();
        list.add(first);
        Collections.addAll(list, other);

        return list;
    }

    public final List<HRef> getHRefs() {
        return unmodifiableList(hRefs);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(hRefs);
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
        if (!(obj instanceof LockTokenSubmitted)) {
            return false;
        }
        final LockTokenSubmitted other = (LockTokenSubmitted) obj;
        return Objects.equals(hRefs, other.getHRefs());
    }

    @Override
    public final String toString() {
        return String.format("%s{ hrefs = %s }",
                             super.toString(),
                             Objects.toString(hRefs));
    }

}
