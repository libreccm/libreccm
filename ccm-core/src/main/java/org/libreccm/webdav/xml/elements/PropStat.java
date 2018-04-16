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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * WebDAV propstat XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_propstat">Chapter
 * 14.22 "propstat XML Element" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlType(propOrder = {"prop", "status", "error", "responseDescription"})
@XmlRootElement(name = "propstat")
public final class PropStat {

    @XmlElement
    private final Prop prop;

    @XmlElement
    private final Status status;

    @XmlElement
    private final Error error;

    @XmlElement(name = "responsedescription")
    private final ResponseDescription responseDescription;

    @SuppressWarnings("unused")
    private PropStat() {
        this.prop = null;
        this.status = null;
        this.error = null;
        this.responseDescription = null;
    }

    public PropStat(final Prop prop,
                    final Status status,
                    final Error error,
                    final ResponseDescription responseDescription) {

        this.prop = Objects.requireNonNull(prop);
        this.status = Objects.requireNonNull(status);
        this.error = error;
        this.responseDescription = responseDescription;
    }

    public PropStat(final Prop prop, final Status status) {
        this(prop, status, null, null);
    }

    public PropStat(final Prop prop, final Status status, final Error error) {
        this(prop, status, error, null);
    }

    public PropStat(final Prop prop,
                    final Status status,
                    final ResponseDescription responseDescription) {

        this(prop, status, null, responseDescription);
    }

    public final Prop getProp() {
        return this.prop;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(prop);
        hash = 29 * hash + Objects.hashCode(status);
        hash = 29 * hash + Objects.hashCode(error);
        hash = 29 * hash + Objects.hashCode(responseDescription);
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
        if (!(obj instanceof PropStat)) {
            return false;
        }
        final PropStat other = (PropStat) obj;
        if (!Objects.equals(prop, other.getProp())) {
            return false;
        }
        if (!Objects.equals(status, other.getStatus())) {
            return false;
        }
        if (!Objects.equals(error, other.getError())) {
            return false;
        }
        return Objects.equals(responseDescription,
                              other.getResponseDescription());
    }

    @Override
    public String toString() {
        return String.format("%s{ prop = %s,"
                                 + "status = %s, "
                                 + "error = %s, "
                                 + "responseDescription %s }",
                             super.toString(),
                             Objects.toString(prop),
                             Objects.toString(status),
                             Objects.toString(error),
                             Objects.toString(responseDescription));
    }

}
