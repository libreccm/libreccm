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

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import javax.ws.rs.core.Response;

import static javax.xml.bind.annotation.XmlAccessType.*;

/**
 *
 * WebDAV status XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_status">Chapter
 * 14.28 "status XML Element" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 */
@XmlAccessorType(NONE)
@XmlRootElement
public final class Status {

    @XmlValue
    private String statusLine;

    @SuppressWarnings("unused")
    private Status() {
        // For unmarshalling only.
    }

    private Status(final int statusCode, final String reasonPhrase) {
        this.statusLine = String.format("HTTP/1.1 %d %s",
                                        statusCode,
                                        reasonPhrase);
    }

    public Status(final Response.StatusType responseStatus) {
        this(responseStatus.getStatusCode(), responseStatus.getReasonPhrase());
    }

    public final String getStatus() {
        return statusLine;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(statusLine);
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
        if (!(obj instanceof Status)) {
            return false;
        }
        final Status other = (Status) obj;
        return Objects.equals(statusLine, other.getStatus());
    }

    @Override
    public String toString() {
        return String.format("%s{ statusLine = \"%s\" }",
                             super.toString(),
                             statusLine);
    }

}
