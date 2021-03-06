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
package org.libreccm.webdav;

/**
 * WebDAV Headers.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 *
 * @author <a href="mailto:karg@java.net">Markus KARG</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#http.headers.for.distributed.authoring">
 * Chapter 10 "HTTP Headers for Distributed Authoring" of RFC 4918 "HTTP
 * Extensions for Web Distributed Authoring and Versioning (WebDAV)"
 * </a>
 */
public final class Headers {

    /**
     * WebDAV DAV Header
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_DAV">Chapter
     * 10.1 "DAV Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String DAV = "DAV";

    /**
     * WebDAV Depth Header
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Depth">Chapter
     * 10.2 "Depth Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String DEPTH = "Depth";

    /**
     * WebDAV Depth Header Value "0"
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Depth">Chapter
     * 10.2 "Depth Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String DEPTH_0 = "0";

    /**
     * WebDAV Depth Header Value "1"
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Depth">Chapter
     * 10.2 "Depth Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String DEPTH_1 = "1";

    /**
     * WebDAV Depth Header Value "infinity"
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Depth">Chapter
     * 10.2 "Depth Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String DEPTH_INFINITY = "infinity";

    /**
     * WebDAV Destination Header
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Destination">Chapter
     * 10.3 "Destination Header" of RFC 4918 "HTTP Extensions for Web
     * Distributed Authoring and Versioning (WebDAV)"</a>
     */
    public static final String DESTINATION = "Destination";

    /**
     * WebDAV If Header
     *
     * @see <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_If">Chapter
     * 10.4 "If Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String IF = "If";

    /**
     * WebDAV Lock-Token Header
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Lock-Token">Chapter
     * 10.5 "Lock-Token Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String LOCK_TOKEN = "Lock-Token";

    /**
     * WebDAV Overwrite Header
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Overwrite">Chapter
     * 10.6 "Overwrite Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String OVERWRITE = "Overwrite";

    /**
     * WebDAV Overwrite Header Value "T"
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Overwrite">Chapter
     * 10.6 "Overwrite Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String OVERWRITE_TRUE = "T";

    /**
     * WebDAV Overwrite Header Value "F"
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Overwrite">Chapter
     * 10.6 "Overwrite Header" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    public static final String OVERWRITE_FALSE = "F";

    /**
     * WebDAV Timeout Header
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Timeout">Chapter
     * 10.7 "Timeout Request Header" of RFC 4918 "HTTP Extensions for Web
     * Distributed Authoring and Versioning (WebDAV)"</a>
     */
    public static final String TIMEOUT = "Timeout";

    /**
     * WebDAV Timeout Header Value "Second-"
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Timeout">Chapter
     * 10.7 "Timeout Request Header" of RFC 4918 "HTTP Extensions for Web
     * Distributed Authoring and Versioning (WebDAV)"</a>
     */
    public static final String TIMEOUT_SECOND = "Second-";

    /**
     * WebDAV Timeout Header Value "Infinite"
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#HEADER_Timeout">Chapter
     * 10.7 "Timeout Request Header" of RFC 4918 "HTTP Extensions for Web
     * Distributed Authoring and Versioning (WebDAV)"</a>
     */
    public static final String TIMEOUT_INFINITE = "Infinite";

}
