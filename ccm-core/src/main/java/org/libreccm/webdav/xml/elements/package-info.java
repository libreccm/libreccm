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
 * This package contains WebDAV XML Elements.
 *
 * The classes in this package are based on the classes from the 
 * java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author <a href="mailto:mkarg@java.net">Markus Karg</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * 
 * @see <a href="http://www.webdav.org/specs/rfc4918.html#xml.element.definitions">Chapter 14 "XML Element Definitions" of RFC 4918 "HTTP Extensions for Web Distributed Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlSchema(namespace = "DAV:", 
           xmlns = @XmlNs(prefix = "D", namespaceURI = "DAV:"), 
           elementFormDefault = QUALIFIED)
package org.libreccm.webdav.xml.elements;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

import static javax.xml.bind.annotation.XmlNsForm.*;

