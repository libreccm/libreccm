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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static java.util.Collections.*;
import static javax.xml.bind.annotation.XmlAccessType.*;

/**
 *
 * WebDAV response XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_response">Chapter
 * 14.24 "response XML Element" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlAccessorType(FIELD)
@XmlType(propOrder = {"hRefs", "status", "propStats", "error",
                      "responseDescription", "location"})
@XmlRootElement
public final class Response {

    @XmlElement(name = "href")
    private final List<HRef> hRefs;

    private final Status status;

    @XmlElement(name = "propstat")
    private final List<PropStat> propStats;

    private final Error error;

    @XmlElement(name = "responsedescription")
    private ResponseDescription responseDescription;

    private Location location;

    @SuppressWarnings("unused")
    private Response() {
        this(new LinkedList<HRef>(),
             null,
             new LinkedList<PropStat>(),
             null,
             null,
             null);
    }

    private Response(final List<HRef> hRefs,
                     final Status status,
                     final List<PropStat> propStats,
                     final Error error,
                     final ResponseDescription responseDescription,
                     final Location location) {
        this.hRefs = hRefs;
        this.status = status;
        this.propStats = propStats;
        this.error = error;
        this.responseDescription = responseDescription;
        this.location = location;
    }

    public Response(final HRef hRef,
                    final Error error,
                    final ResponseDescription responseDescription,
                    final Location location,
                    final PropStat propStat,
                    final PropStat... propStats) {

        this(Collections.singletonList(hRef),
             null,
             createList(propStat, propStats),
             error,
             responseDescription, location);
    }

    public Response(final Status status,
                    final Error error,
                    final ResponseDescription responseDescription,
                    final Location location,
                    final HRef hRef,
                    final HRef... hRefs) {

        this(createList(hRef, hRefs),
             Objects.requireNonNull(status),
             Collections.<PropStat>emptyList(),
             error,
             responseDescription,
             location);
    }
    
    private static <T> List<T> createList(T first, T[] other) {
        
        final List<T> list = new LinkedList<>();
        list.add(first);
        Collections.addAll(list, other);
        
        return list;
    }

    public final List<HRef> getHRefs() {
        return unmodifiableList(this.hRefs);
    }

    public final Status getStatus() {
        return this.status;
    }

    public final Error getError() {
        return this.error;
    }

    public final ResponseDescription getResponseDescription() {
        return this.responseDescription;
    }

    public final Location getLocation() {
        return this.location;
    }

    public final List<PropStat> getPropStats() {
        return unmodifiableList(this.propStats);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(hRefs);
        hash = 31 * hash + Objects.hashCode(status);
        hash = 31 * hash + Objects.hashCode(propStats);
        hash = 31 * hash + Objects.hashCode(error);
        hash = 31 * hash + Objects.hashCode(responseDescription);
        hash = 31 * hash + Objects.hashCode(location);
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
        if (!(obj instanceof Response)) {
            return false;
        }
        final Response other = (Response) obj;
        if (!Objects.equals(hRefs, other.getHRefs())) {
            return false;
        }
        if (!Objects.equals(status, other.getStatus())) {
            return false;
        }
        if (!Objects.equals(propStats, other.getPropStats())) {
            return false;
        }
        if (!Objects.equals(error, other.getError())) {
            return false;
        }
        if (!Objects.equals(responseDescription,
                            other.getResponseDescription())) {
            return false;
        }
        return Objects.equals(location, other.getLocation());
    }

    @Override
    public String toString() {
        return String.format("%s{ hRefs = %s, "
                                 + "status = %s, "
                                 + "propStats = %s,"
                                 + "error = %s,"
                                 + "responseDescription = %s }",
                             super.toString(),
                             Objects.toString(hRefs),
                             Objects.toString(status),
                             Objects.toString(propStats),
                             Objects.toString(error),
                             Objects.toString(responseDescription));
    }

}
