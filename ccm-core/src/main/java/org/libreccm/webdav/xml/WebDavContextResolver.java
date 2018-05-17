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
package org.libreccm.webdav.xml;

import org.libreccm.core.UnexpectedErrorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;

/**
 * Provides support for custom extensions to WebDAV, like custom Properties and
 * XML Elements.<br>
 *
 * WebDAV allows custom extensions for XML Elements and Properties. To enable
 * JAX-RS to deal with these, each of them must be implemented as a JAXB class
 * and registered by passing it to the constructor of this resolver.
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
 * <a href="http://www.webdav.org/specs/rfc4918.html#xml-extensibility">Chapter
 * 17 "XML Extensibility in DAV" of RFC 2616 "Hypertext Transfer Protocol --
 * HTTP/1.1"</a>
 */
@Provider
@Produces(MediaType.APPLICATION_XML)
public class WebDavContextResolver implements ContextResolver<JAXBContext> {

    private final JAXBContext context;

    private final JAXBIntrospector introspector;

    public WebDavContextResolver() throws JAXBException {
        
        this.context = WebDavJAXBContextBuilder.build();
        this.introspector = this.context.createJAXBIntrospector();
    }
    
    /**
     * Creates an instance of this resolver, registering the provided custom XML
     * Elements and Properties.
     *
     * @param additionalClasses The custom extensions (JAXB classes) to be
     *                          registered (can be left blank).
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       JAXBContext, such as (but not limited to): No JAXB
     *                       implementation was discovered, Classes use JAXB
     *                       annotations incorrectly, Classes have colliding
     *                       annotations (i.e., two classes with the same type
     *                       name), The JAXB implementation was unable to locate
     *                       provider-specific out-of-band information (such as
     *                       additional files generated at the development
     *                       time.)
     */
    public WebDavContextResolver(final Class<?>... additionalClasses)
        throws JAXBException {

        this.context = WebDavJAXBContextBuilder.build(additionalClasses);
        this.introspector = this.context.createJAXBIntrospector();
    }

    /**
     * @return A single, shared context for both, WebDAV XML Elements and
     *         Properties and custom extensions.
     */
    @Override
    public JAXBContext getContext(final Class<?> type) {

        if (introspector.isElement(buildInstanceOf(type))) {
            return context;
        } else {
            return null;
        }
    }

    private <T> T buildInstanceOf(final Class<T> type) {

        if (type == null) {

            return null;

        } else if (type.isEnum()) {

            final T[] enumConstants = type.getEnumConstants();
            if (enumConstants.length > 0) {
                return enumConstants[0];
            } else {
                return null;
            }

        } else {

            try {
                final Constructor<T> constructor = type
                    .getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (NoSuchMethodException
                     | InstantiationException
                     | IllegalAccessException
                     | InvocationTargetException ex) {

                throw new UnexpectedErrorException(ex);
            }
        }
    }

}
