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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * WebDAV getetag Property.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#PROPERTY_getetag">Chapter
 * 15.6 "getetag Property" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 *
 *
 */
@XmlJavaTypeAdapter(GetETag.Adapter.class)
@XmlRootElement(name = "getetag")
public final class GetETag {

    /**
     * Singleton empty instance for use as property name only, providing
     * improved performance and the ability to compare by <em>same</em>
     * instance.
     *
     */
    public static final GetETag GETETAG = new GetETag();

    @XmlValue
    private final String entityTag;

    private GetETag() {
        this.entityTag = "";
    }

    public GetETag(final String entityTag) {
        this.entityTag = Objects.requireNonNull(entityTag);
    }

    public final String getEntityTag() {
        return entityTag;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(entityTag);
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
        if (!(obj instanceof GetETag)) {
            return false;
        }
        final GetETag other = (GetETag) obj;
        return Objects.equals(entityTag, other.getEntityTag());
    }

    @Override
    public String toString() {
        return String.format("%s{ entityTag = \"%s\" }",
                             super.toString(),
                             entityTag);
    }

    /**
     * Guarantees that any unmarshalled enum constants effectively are the
     * constant Java instances itself, so that {@code ==} can be used form
     * comparison.
     *
     * @since 1.2
     */
    protected static final class Adapter extends ConstantsAdapter<GetETag> {

        @Override
        protected final Collection<GetETag> getConstants() {
            return Collections.singleton(GETETAG);
        }

    }

}
