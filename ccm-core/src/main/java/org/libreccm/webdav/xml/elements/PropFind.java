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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static javax.xml.bind.annotation.XmlAccessType.*;

/**
 * WebDAV propfind XML Element.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_propfind">Chapter
 * 14.20 "propfind XML Element" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlAccessorType(FIELD)
@XmlType(propOrder = {"propName", "allProp", "include", "prop"})
@XmlRootElement(name = "propfind")
public final class PropFind {

    @XmlElement(name = "propname")
    private final PropName propName;

    @XmlElement(name = "allprop")
    private final AllProp allProp;

    private final Include include;

    private final Prop prop;

    @SuppressWarnings("unused")
    private PropFind() {
        this(null, null, null, null);
    }

    private PropFind(final PropName propName, final AllProp allProp,
                     final Include include, final Prop prop) {
        this.propName = propName;
        this.allProp = allProp;
        this.include = include;
        this.prop = prop;

    }

    public PropFind(final PropName propName) {
        this(Objects.requireNonNull(propName), null, null, null);
    }

    public PropFind(final AllProp allProp, final Include include) {
        this(null, Objects.requireNonNull(allProp), include, null);
    }

    public PropFind(final AllProp allProp) {
        this(null, Objects.requireNonNull(allProp), null, null);
    }

    public PropFind(final Prop prop) {
        this(null, null, null, Objects.requireNonNull(prop));
    }

    public final PropName getPropName() {
        return this.propName;
    }

    public final AllProp getAllProp() {
        return this.allProp;
    }

    public final Include getInclude() {
        return this.include;
    }

    public final Prop getProp() {
        return this.prop;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(propName);
        hash = 31 * hash + Objects.hashCode(allProp);
        hash = 31 * hash + Objects.hashCode(include);
        hash = 31 * hash + Objects.hashCode(prop);
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
        if (!(obj instanceof PropFind)) {
            return false;
        }
        final PropFind other = (PropFind) obj;
        if (!Objects.equals(propName, other.getPropName())) {
            return false;
        }
        if (!Objects.equals(allProp, other.getAllProp())) {
            return false;
        }
        if (!Objects.equals(include, other.getInclude())) {
            return false;
        }
        return Objects.equals(prop, other.getProp());
    }

    @Override
    public String toString() {
        return String.format("%s{ propName = %s,"
                                 + "allProp = %s, "
                                 + "include = %s, "
                                 + "prop = %s }",
                             super.toString(),
                             Objects.toString(propName),
                             Objects.toString(allProp),
                             Objects.toString(include),
                             Objects.toString(prop));
    }

}
