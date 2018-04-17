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
package org.libreccm.webdav.xml.elements.properties;

import org.libreccm.webdav.ConstantsAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * WebDAV getcontenttype Property.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#PROPERTY_getcontenttype">Chapter
 * 15.5 "getcontenttype Property" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 *
 *
 */
@XmlJavaTypeAdapter(GetContentType.Adapter.class)
@XmlRootElement(name = "getcontenttype")
public final class GetContentType {

    /**
     * Singleton empty instance for use as property name only, providing
     * improved performance and the ability to compare by <em>same</em>
     * instance.
     *
     */
    public static final GetContentType GETCONTENTTYPE = new GetContentType();

    @XmlValue
    private final String mediaType;

    private GetContentType() {
        this.mediaType = "";
    }

    public GetContentType(final String mediaType) {
        this.mediaType = Objects.requireNonNull(mediaType);
    }

    public final String getMediaType() {
        return this.mediaType;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(mediaType);
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
        if (!(obj instanceof GetContentType)) {
            return false;
        }
        final GetContentType other = (GetContentType) obj;
        return Objects.equals(mediaType, other.getMediaType());
    }

    @Override
    public String toString() {
        return String.format("%s{ mediaType = \"%s\" }",
                             super.toString(),
                             mediaType);
    }

    protected static final class Adapter 
        extends ConstantsAdapter<GetContentType> {

        @Override
        protected final Collection<GetContentType> getConstants() {
            return Collections.singleton(GETCONTENTTYPE);
        }

    }

}
