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
 * WebDAV displayname Property.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#PROPERTY_displayname">Chapter
 * 15.2 "displayname Property" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 *
 *
 */
@XmlJavaTypeAdapter(DisplayName.Adapter.class)
@XmlRootElement(name = "displayname")
public final class DisplayName {

    /**
     * Singleton empty instance for use as property name only, providing
     * improved performance and the ability to compare by <em>same</em>
     * instance.
     */
    public static final DisplayName DISPLAYNAME = new DisplayName();

    @XmlValue
    private final String name;

    private DisplayName() {
        this.name = "";
    }

    public DisplayName(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    public final String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(name);
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
        if (!(obj instanceof DisplayName)) {
            return false;
        }
        final DisplayName other = (DisplayName) obj;
        return Objects.equals(name, other.getName());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "name = \"%s\""
                                 + " }",
                             super.toString(),
                             name);
    }

    /**
     * Guarantees that any unmarshalled enum constants effectively are the
     * constant Java instances itself, so that {@code ==} can be used form
     * comparison.
     *
     */
    protected static final class Adapter extends ConstantsAdapter<DisplayName> {

        @Override
        protected final Collection<DisplayName> getConstants() {
            return Collections.singleton(DISPLAYNAME);
        }

    }

}
