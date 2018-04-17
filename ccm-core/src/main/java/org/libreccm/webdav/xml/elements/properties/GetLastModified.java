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
import org.libreccm.webdav.xml.elements.Rfc1123DateFormat;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * WebDAV getlastmodified Property.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#PROPERTY_getlastmodified">Chapter
 * 15.7 "getlastmodified Property" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlJavaTypeAdapter(GetLastModified.Adapter.class)
@XmlRootElement(name = "getlastmodified")
public final class GetLastModified {

    /**
     * Singleton empty instance for use as property name only, providing
     * improved performance and the ability to compare by <em>same</em>
     * instance.
     *
     */
    public static final GetLastModified GETLASTMODIFIED = new GetLastModified();

    private Date dateTime;

    private GetLastModified() {
        // For unmarshalling only.
    }

    public GetLastModified(final Date dateTime) {
        this.dateTime = Objects.requireNonNull(dateTime);
    }

    public final Date getDateTime() {

        if (dateTime == null) {
            return null;
        } else {
            return (Date) dateTime.clone();
        }
    }

    @XmlValue
    private String getXmlValue() {

        if (dateTime == null) {
            return null;
        } else {
            return new Rfc1123DateFormat().format(dateTime);
        }
    }

    @SuppressWarnings("unused")
    private void setXmlValue(final String xmlValue) throws ParseException {

        if (xmlValue == null || xmlValue.isEmpty()) {
            dateTime = null;
        } else {
            dateTime = new Rfc1123DateFormat().parse(xmlValue);
        }

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(dateTime);
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
        if (!(obj instanceof GetLastModified)) {
            return false;
        }
        final GetLastModified other = (GetLastModified) obj;
        return Objects.equals(dateTime, other.getDateTime());
    }

    @Override
    public String toString() {
        return String.format("%s{ dateTime = %s }",
                             super.toString(),
                             Objects.toString(dateTime));
    }

    /**
     * Guarantees that any unmarshalled enum constants effectively are the
     * constant Java instances itself, so that {@code ==} can be used form
     * comparison.
     *
     */
    protected static final class Adapter
        extends ConstantsAdapter<GetLastModified> {

        @Override
        protected final Collection<GetLastModified> getConstants() {
            return Collections.singleton(GETLASTMODIFIED);
        }

    }

}
