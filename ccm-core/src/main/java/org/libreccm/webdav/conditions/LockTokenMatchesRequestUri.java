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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * WebDAV lock-token-matches-request-uri Precondition XML Element.
 *
 * This is a singleton. All instances are absolutely identical, hence can be
 * compared using {@code ==} and share one unique hash code. Use
 * {@link #LOCK_TOKEN_MATCHES_REQUEST_URI} always.
 *
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
@XmlRootElement(name = "lock-token-matches-request-uri")
@XmlType(factoryMethod = "createSingleton")
public final class LockTokenMatchesRequestUri {

    /**
     * Singleton instance, providing improved performance and the ability to
     * compare by <em>same</em> instance.
     *
     */
    public static final LockTokenMatchesRequestUri LOCK_TOKEN_MATCHES_REQUEST_URI
                                                   = new LockTokenMatchesRequestUri();

    /**
     * Singleton factory to be used solely by JAXB.
     */
    @SuppressWarnings("unused")
    private static final LockTokenMatchesRequestUri createSingleton() {
        return LOCK_TOKEN_MATCHES_REQUEST_URI;
    }

    private LockTokenMatchesRequestUri() {
        // For unmarshalling only.
    }

    @Override
    public final boolean equals(final Object object) {
        return object instanceof LockTokenMatchesRequestUri;
    }

    @Override
    public final int hashCode() {
        return 1;
    }

    @Override
    public final String toString() {
        return getClass().getName();
    }

}
