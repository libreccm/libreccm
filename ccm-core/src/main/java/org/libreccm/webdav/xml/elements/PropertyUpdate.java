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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * WebDAV propertyupdate XML Element.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_propertyupdate">Chapter
 * 14.19 "propertyupdate XML Element" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 *
 *
 */
@XmlRootElement(name = "propertyupdate")
public final class PropertyUpdate {

    @XmlElements({
        @XmlElement(name = "remove", type = Remove.class)
        , @XmlElement(name = "set", type = Set.class)})
    private final List<RemoveOrSet> removesOrSets;

    @SuppressWarnings("unused")
    private PropertyUpdate() {
        removesOrSets = new LinkedList<>();
    }

    public PropertyUpdate(final RemoveOrSet removeOrSet,
                          final RemoveOrSet... removesOrSets) {

        Objects.requireNonNull(removeOrSet);
        this.removesOrSets = createList(removeOrSet, removesOrSets);
    }

    private static <T> List<T> createList(T first, T[] other) {

        final List<T> list = new LinkedList<>();
        list.add(first);
        Collections.addAll(list, other);

        return list;
    }

    public List<RemoveOrSet> list() {
        return Collections.unmodifiableList(removesOrSets);
    }
    
    public List<RemoveOrSet> getRemoveOrSets() {
        return list();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(removesOrSets);
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
        if (!(obj instanceof PropertyUpdate)) {
            return false;
        }
        final PropertyUpdate other = (PropertyUpdate) obj;
        return Objects.equals(removesOrSets, other.getRemoveOrSets());
    }
    
    @Override
    public String toString() {
        return String.format("%s{ removeOrSets = %s }",
                             super.toString(),
                             Objects.toString(removesOrSets));
    }

}
