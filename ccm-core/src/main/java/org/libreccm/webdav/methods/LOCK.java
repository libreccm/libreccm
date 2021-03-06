/*
 * #%L
 * WebDAV Support for JAX-RS
 * %%
 * Copyright (C) 2008 - 2018 The java.net WebDAV Project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.libreccm.webdav.methods;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ws.rs.HttpMethod;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Indicates that the annotated method responds to WebDAV LOCK requests.
 *
 * Adopted from
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/methods/LOCK.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/methods/LOCK.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 *
 * @see <a href="http://www.webdav.org/specs/rfc4918.html#METHOD_LOCK">Chapter
 * 9.10 "LOCK Method" of RFC 4918 "HTTP Extensions for Web Distributed Authoring
 * and Versioning (WebDAV)"</a>
 */
@Target(METHOD)
@Retention(RUNTIME)
@HttpMethod("LOCK")
public @interface LOCK {
    // Has no members.
}
