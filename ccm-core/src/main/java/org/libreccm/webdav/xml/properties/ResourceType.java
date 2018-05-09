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
import org.libreccm.webdav.xml.elements.Collection;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * WebDAV resourcetype Property.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#PROPERTY_resourcetype">Chapter
 * 15.9 "resourcetype Property" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 *
 *
 */
@XmlJavaTypeAdapter(ResourceType.Adapter.class)
@XmlRootElement(name = "resourcetype")
public final class ResourceType {

    /**
     * Singleton empty instance for use as property name only, providing
     * improved performance and the ability to compare by <em>same</em>
     * instance.
     *
     * @since 1.2
     */
    public static final ResourceType RESOURCETYPE = new ResourceType();

    @XmlAnyElement(lax = true)
    private final List<Object> resourceTypes;

    public static final ResourceType COLLECTION = new ResourceType(
        Collection.COLLECTION);

    private ResourceType() {
        this.resourceTypes = new LinkedList<>();
    }

    public ResourceType(final Object... resourceTypes) {

        this.resourceTypes = Arrays.asList(
            Objects.requireNonNull(resourceTypes));

    }

    public final List<Object> getResourceTypes() {
        return Collections.unmodifiableList(this.resourceTypes);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(resourceTypes);
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
        if (!(obj instanceof ResourceType)) {
            return false;
        }
        final ResourceType other = (ResourceType) obj;
        return Objects.equals(resourceTypes, other.getResourceTypes());
    }

    @Override
    public String toString() {
        return String.format("%s{ resourceTypes = %s }",
                             super.toString(),
                             Objects.toString(resourceTypes));
    }

    /**
     * Guarantees that any unmarshalled enum constants effectively are the
     * constant Java instances itself, so that {@code ==} can be used form
     * comparison.
     *
     */
    protected static final class Adapter
        extends ConstantsAdapter<ResourceType> {

        @Override
        protected final java.util.Collection<ResourceType> getConstants() {
            return Arrays.asList(RESOURCETYPE, COLLECTION);
        }

    }

}
