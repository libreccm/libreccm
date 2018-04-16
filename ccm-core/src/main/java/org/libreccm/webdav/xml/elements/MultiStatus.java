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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * WebDAV multistatus XML Element.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_multistatus">Chapter
 * 14.16 "multistatus XML Element" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlType(propOrder = {"responses", "responseDescription"})
@XmlRootElement(name = "multistatus")
public final class MultiStatus {

    @XmlElement(name = "response")
    private final List<Response> responses;

    @XmlElement(name = "responsedescription")
    private final ResponseDescription responseDescription;

    public MultiStatus() {
        this.responses = new LinkedList<>();
        this.responseDescription = null;
    }

    public MultiStatus(final ResponseDescription responseDescription,
                       final Response... responses) {

        if (responses == null || responses.length == 0) {
            this.responses = Collections.emptyList();
        } else {
            this.responses = Arrays.asList(responses);
        }

        this.responseDescription = responseDescription;
    }

    public MultiStatus(final Response... responses) {
        this(null, responses);
    }

    public MultiStatus(final ResponseDescription responseDescription) {
        this(responseDescription, (Response[]) null);
    }

    public final List<Response> getResponses() {
        return Collections.unmodifiableList(responses);
    }

    public final ResponseDescription getResponseDescription() {
        return this.responseDescription;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(responses);
        hash = 47 * hash + Objects.hashCode(responseDescription);
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
        if (!(obj instanceof MultiStatus)) {
            return false;
        }
        final MultiStatus other = (MultiStatus) obj;
        if (!Objects.equals(responses, other.getResponses())) {
            return false;
        }
        return Objects.equals(responseDescription,
                              other.getResponseDescription());
    }

    @Override
    public String toString() {
        return String.format("%s{ responses = %s, "
                                 + "responseDescription = %s }",
                             super.toString(),
                             Objects.toString(responses),
                             Objects.toString(responseDescription));
    }

}
