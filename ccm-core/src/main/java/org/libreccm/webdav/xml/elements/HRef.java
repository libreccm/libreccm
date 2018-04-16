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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * WebDAV href XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_href">Chapter
 * 14.7 "href XML Element" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlRootElement(name = "href")
public final class HRef {

    @XmlValue
    private final String value;

    private HRef() {
        this.value = null;
    }

    public HRef(final URI uri) {

        Objects.requireNonNull(uri);
        value = uri.toString();
    }

    public HRef(final String uri) {

        Objects.requireNonNull(uri);
        value = uri;
    }

    /**
     * @return Value as a <code>URI</code> instance, if the value is a valid
     *         URI; <code>null</code> otherwise.
     *
     * @throws java.net.URISyntaxException
     */
    public final URI getURI() throws URISyntaxException {
        return new URI(this.value);
    }

    public final String getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(value);
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HRef other = (HRef) obj;
        return Objects.equals(value, other.getValue());
    }
    
    @Override
    public String toString() {
        return String.format("%s{ %s }", 
                             super.toString(),
                             value);
    }

}
