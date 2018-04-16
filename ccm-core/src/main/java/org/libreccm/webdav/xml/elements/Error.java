package org.libreccm.webdav.xml.elements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import static java.util.Collections.*;

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
/**
 *
 * WebDAV error XML Element.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_error">Chapter
 * 14.5 "error XML Element" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 */
@XmlRootElement
public final class Error {

    @XmlAnyElement(lax = true)
    public final List<Object> errors;

    @SuppressWarnings("unused")
    private Error() {
        errors = new LinkedList<>();
    }

    public Error(final Object error, final Object... errors) {

        Objects.requireNonNull(error);

        this.errors = new LinkedList<>();
        this.errors.add(error);
        this.errors.addAll(Arrays.asList(errors));
    }

    public final List<Object> getErrors() {
        return unmodifiableList(this.errors);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(errors);
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
        if (!(obj instanceof Error)) {
            return false;
        }
        final Error other = (Error) obj;
        return Objects.equals(errors, other.errors);
    }

    @Override
    public String toString() {
        return String.format("%s{ errors = %s }",
                             super.toString(),
                             Objects.toString(errors));
    }

}
