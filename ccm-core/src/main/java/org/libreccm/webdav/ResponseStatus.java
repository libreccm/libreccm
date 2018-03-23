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

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.Family.*;

/**
 * Commonly used status codes defined by WebDAV.
 *
 * The enum is based on an enum from the java.net WebDAV project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/ResponseStatus.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/ResponseStatus.java</a>
 *
 * @author <a href="mailto:mkarg@java.net">Markus KARG</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#status.code.extensions.to.http11">Chapter
 * 11 "Status Code Extensions to HTTP/1.1" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 */
public enum ResponseStatus implements Response.StatusType {

    /**
     * 207 Multi-Status
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#STATUS_207">Chapter
     * 11.1 "207 Multi-Status" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    MULTI_STATUS(207, "Multi-Status"),
    /**
     * 422 Unprocessable Entity
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#STATUS_422">Chapter
     * 11.2 "422 Unprocessable Entity" of RFC 4918 "HTTP Extensions for Web
     * Distributed Authoring and Versioning (WebDAV)"</a>
     */
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    /**
     * 423 Locked
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#STATUS_423">Chapter
     * 11.3 "423 Locked" of RFC 4918 "HTTP Extensions for Web Distributed
     * Authoring and Versioning (WebDAV)"</a>
     */
    LOCKED(423, "Locked"),
    /**
     * 424 Failed Dependency
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#STATUS_424">Chapter
     * 11.4 "424 Failed Dependency" of RFC 4918 "HTTP Extensions for Web
     * Distributed Authoring and Versioning (WebDAV)"</a>
     */
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    /**
     * 507 Insufficient Storage
     *
     * @see
     * <a href="http://www.webdav.org/specs/rfc4918.html#STATUS_507">Chapter
     * 11.5 "507 Insufficient Storage" of RFC 4918 "HTTP Extensions for Web
     * Distributed Authoring and Versioning (WebDAV)"</a>
     */
    INSUFFICIENT_STORAGE(507, "Insufficient Storage");

    private final int statusCode;

    private final String reasonPhrase;

    private ResponseStatus(final int statusCode, final String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public int getStatusCode() {
        return statusCode;

    }

    @Override
    public Response.Status.Family getFamily() {
        switch (this.statusCode / 100) {
            case 1:
                return INFORMATIONAL;
            case 2:
                return SUCCESSFUL;
            case 3:
                return REDIRECTION;
            case 4:
                return CLIENT_ERROR;
            case 5:
                return SERVER_ERROR;
            default:
                return OTHER;
        }
    }

    @Override
    public String getReasonPhrase() {
        return reasonPhrase;
    }
    
    @Override
    public String toString() {
        return reasonPhrase;
    }

}
