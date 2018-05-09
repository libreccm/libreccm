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
package org.libreccm.webdav.xml.properties;

import org.libreccm.webdav.ConstantsAdapter;
import org.libreccm.webdav.xml.elements.LockEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * WebDAV supportedlock Property.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#PROPERTY_supportedlock">Chapter
 * 15.10 "supportedlock Property" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlJavaTypeAdapter(SupportedLock.Adapter.class)
@XmlRootElement(name = "supportedlock")
public final class SupportedLock {

    /**
     * Singleton empty instance for use as property name only, providing
     * improved performance and the ability to compare by <em>same</em>
     * instance.
     *
     */
    public static final SupportedLock SUPPORTEDLOCK = new SupportedLock();

    @XmlElement(name = "lockentry")
    private final List<LockEntry> lockEntries;

    private SupportedLock() {
        this.lockEntries = new LinkedList<>();
    }

    public SupportedLock(final LockEntry... lockEntries) {

        this.lockEntries = Arrays.asList(Objects.requireNonNull(lockEntries));
    }

    public final List<LockEntry> getLockEntries() {
        return Collections.unmodifiableList(lockEntries);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(lockEntries);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SupportedLock)) {
            return false;
        }
        final SupportedLock other = (SupportedLock) obj;
        return Objects.equals(lockEntries, other.getLockEntries());
    }

    @Override
    public String toString() {
        return String.format("%s{ lockEntries = %s }",
                             super.toString(),
                             Objects.toString(lockEntries));
    }

    /**
     * Guarantees that any unmarshalled enum constants effectively are the
     * constant Java instances itself, so that {@code ==} can be used form
     * comparison.
     *
     */
    protected static final class Adapter
        extends ConstantsAdapter<SupportedLock> {

        @Override
        protected final Collection<SupportedLock> getConstants() {
            return Collections.singleton(SUPPORTEDLOCK);
        }

    }

}
