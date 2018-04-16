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

import org.libreccm.webdav.ConstantsAdapter;
import org.libreccm.webdav.Headers;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import static java.lang.Long.*;
import static java.util.Collections.*;

/**
 *
 * WebDAV timeout XML Element.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#ELEMENT_timeout">Chapter
 * 14.29 "timeout XML Element" of RFC 4918 "HTTP Extensions for Web Distributed
 * Authoring and Versioning (WebDAV)"</a>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "timeout")
@XmlJavaTypeAdapter(TimeOut.Adapter.class)
public final class TimeOut {

    private static final long INFINITE_VALUE = Long.MAX_VALUE;

    private static final String INFINITE_TOKEN = Headers.TIMEOUT_INFINITE;

    private static final String SECOND_TOKEN = Headers.TIMEOUT_SECOND + "%d";

    public static final TimeOut INFINITE = new TimeOut();

    /**
     * The number of seconds, or {@link #INFINITE_VALUE} for infinite timeout.
     */
    private long timeType;

    private TimeOut() {
        this(INFINITE_VALUE);
    }

    public TimeOut(final long seconds) {
        this.timeType = seconds;
    }

    @XmlValue
    private String getTimeType() {

        if (timeType == INFINITE_VALUE) {
            return INFINITE_TOKEN;
        } else {
            return String.format(SECOND_TOKEN, timeType);
        }
    }

    private void setTimeType(final String timeType) {

        if (isInfinite(timeType)) {
            this.timeType = INFINITE_VALUE;
        } else {
            this.timeType = parseSecond(timeType);
        }
    }

    private static boolean isInfinite(final String timeType) {
        return INFINITE_TOKEN.equals(timeType);
    }

    private static long parseSecond(final String timeType) {
        return parseLong(timeType.substring(timeType.lastIndexOf('-') + 1));
    }

    public final boolean isInfinite() {
        return this.timeType == INFINITE_VALUE;
    }

    /**
     * @return The duration of the timeout in seconds, or {@link Long#MAX_VALUE}
     *         for infinity. Note that future versions will return null for
     *         infinity.
     */
    public final long getSeconds() {
        return this.timeType;
    }

    /**
     * Factory method creating {@link TimeOut} instances from {@code String}
     * value representations (e. g. as used in HTTP {@link Headers#TIMEOUT}
     * header). Guarantees that {@link #INFINITE} singleton is returned for
     * {@code "Infinite"} string, hence allowing to compare for infinity using
     * {@code ==} comparison.
     * 
     * Example:
     * 
     * <pre>
     * TimeOut to = TimeOut.valueOf("Infinite");
     * if (to == Timeout.INFINITE) { ... }
     * </pre>
     *
     * @param timeType Either {@code Second-n} (where {@code n} is the length of
     *                 the timeout) or {@code Infinite}.
     *
     * @return An instance of {@link TimeOut} with the length taken from the
     *         {@code timeOutHeader} string. Instance is guaranteed to be
     *         {@link #INFINITE} in case {@code timeOutHeader} is
     *         {@code "Infinity"}.
     *
     * @since 1.2.1
     */
    public static final TimeOut valueOf(final String timeType) {
        
        if (isInfinite(timeType)) {
            return INFINITE;
        } else {
            return new TimeOut(parseSecond(timeType));
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (int) (timeType ^ (timeType >>> 32));
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
        if (!(obj instanceof TimeOut)) {
            return false;
        }
        final TimeOut other = (TimeOut) obj;
        return timeType == Long.parseLong(other.getTimeType());
    }
    
    @Override
    public String toString() {
        return String.format("%s{ %s }",
                             super.toString(),
                             Long.toString(timeType));
    }

    /**
     * Guarantees that any unmarshalled enum constants effectively are the
     * constant Java instances itself, so that {@code ==} can be used form
     * comparison.
     */
    protected static final class Adapter extends ConstantsAdapter<TimeOut> {

        @Override
        protected final Collection<TimeOut> getConstants() {
            return singleton(INFINITE);
        }
    }

}
