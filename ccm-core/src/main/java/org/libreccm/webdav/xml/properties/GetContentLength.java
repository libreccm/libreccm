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

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import static javax.xml.bind.annotation.XmlAccessType.*;

/**
 *
 * WebDAV getcontentlength Property.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#PROPERTY_getcontentlength">Chapter
 * 15.4 "getcontentlength Property" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 *
 *
 */
@XmlAccessorType(NONE)
@XmlJavaTypeAdapter(GetContentLength.Adapter.class)
@XmlRootElement(name = "getcontentlength")
public final class GetContentLength {

    /**
     * Singleton empty instance for use as property name only, providing
     * improved performance and the ability to compare by <em>same</em>
     * instance.
     *
     * @since 1.2
     */
    public static final GetContentLength GETCONTENTLENGTH
                                             = new GetContentLength();

    private Long contentLength;

    @SuppressWarnings("unused")
    private String getXmlValue() {

        if (contentLength == null) {
            return null;
        } else {
            return Long.toString(contentLength);
        }
    }

    @XmlValue
    private void setXmlValue(final String xmlValue) {

        if (xmlValue == null || xmlValue.isEmpty()) {
            contentLength = null;
        } else {
            contentLength = Long.parseLong(xmlValue);
        }

        this.contentLength = xmlValue == null || xmlValue.isEmpty() ? null
                                 : Long.parseLong(xmlValue);
    }

    private GetContentLength() {
        // For unmarshalling only
    }

    public GetContentLength(final long contentLength) {
        this.contentLength = contentLength;
    }

    public final long getContentLength() {
        if (contentLength == null) {
            return 0;
        } else {
            return contentLength;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(contentLength);
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
        if (!(obj instanceof GetContentLength)) {
            return false;
        }
        final GetContentLength other = (GetContentLength) obj;
        return Objects.equals(contentLength, other.getContentLength());
    }

    @Override
    public String toString() {
        return String.format("%s{ contentLength = %d }",
                             super.toString(),
                             contentLength);
    }

    /**
     * Guarantees that any unmarshalled enum constants effectively are the
     * constant Java instances itself, so that {@code ==} can be used form
     * comparison.
     *
     */
    protected static final class Adapter
        extends ConstantsAdapter<GetContentLength> {

        @Override
        protected final Collection<GetContentLength> getConstants() {
            return Collections.singleton(GETCONTENTLENGTH);
        }

    }

}
