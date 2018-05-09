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

import org.libreccm.webdav.conditions.CannotModifyProtectedProperty;
import org.libreccm.webdav.conditions.LockTokenMatchesRequestUri;
import org.libreccm.webdav.conditions.LockTokenSubmitted;
import org.libreccm.webdav.conditions.NoConflictingLock;
import org.libreccm.webdav.conditions.NoExternalEntities;
import org.libreccm.webdav.conditions.PreservedLiveProperties;
import org.libreccm.webdav.conditions.PropFindFiniteDepth;
import org.libreccm.webdav.xml.elements.ActiveLock;
import org.libreccm.webdav.xml.elements.AllProp;
import org.libreccm.webdav.xml.elements.Collection;
import org.libreccm.webdav.xml.elements.Depth;
import org.libreccm.webdav.xml.elements.Exclusive;
import org.libreccm.webdav.xml.elements.HRef;
import org.libreccm.webdav.xml.elements.Include;
import org.libreccm.webdav.xml.elements.Location;
import org.libreccm.webdav.xml.elements.LockEntry;
import org.libreccm.webdav.xml.elements.LockInfo;
import org.libreccm.webdav.xml.elements.LockRoot;
import org.libreccm.webdav.xml.elements.LockScope;
import org.libreccm.webdav.xml.elements.LockToken;
import org.libreccm.webdav.xml.elements.LockType;
import org.libreccm.webdav.xml.elements.MultiStatus;
import org.libreccm.webdav.xml.elements.Owner;
import org.libreccm.webdav.xml.elements.Prop;
import org.libreccm.webdav.xml.elements.PropFind;
import org.libreccm.webdav.xml.elements.PropName;
import org.libreccm.webdav.xml.elements.PropStat;
import org.libreccm.webdav.xml.elements.PropertyUpdate;
import org.libreccm.webdav.xml.elements.Remove;
import org.libreccm.webdav.xml.elements.ResponseDescription;
import org.libreccm.webdav.xml.elements.Shared;
import org.libreccm.webdav.xml.elements.Status;
import org.libreccm.webdav.xml.elements.TimeOut;
import org.libreccm.webdav.xml.elements.Write;
import org.libreccm.webdav.xml.properties.CreationDate;
import org.libreccm.webdav.xml.properties.DisplayName;
import org.libreccm.webdav.xml.properties.GetContentLanguage;
import org.libreccm.webdav.xml.properties.GetContentLength;
import org.libreccm.webdav.xml.properties.GetContentType;
import org.libreccm.webdav.xml.properties.GetETag;
import org.libreccm.webdav.xml.properties.GetLastModified;
import org.libreccm.webdav.xml.properties.LockDiscovery;
import org.libreccm.webdav.xml.properties.ResourceType;
import org.libreccm.webdav.xml.properties.SupportedLock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.text.Utilities;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Provides support for custom extensions to WebDAV, like custom Properties and
 * XML Elements.<br>
 *
 * WebDAV allows custom extensions for XML Elements and Properties. To enable
 * JAX-RS to deal with these, each of them must be implemented as a JAXB class
 * and registered by passing it to the constructor of this factory.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#xml-extensibility">Chapter
 * 17 "XML Extensibility in DAV" of RFC 2616 "Hypertext Transfer Protocol --
 * HTTP/1.1"</a>
 */
final class WebDavJAXBContextBuilder {

    /**
     * Builds a JAXB context for WebDAV.
     *
     * @param auxiliaryClasses Optional set of custom XML elements which shall
     *                         get part of the context.
     *
     * @throws JAXBException If JAXB cannot create the context.
     */
    public static final JAXBContext build(final Class<?>... auxiliaryClasses) 
        throws JAXBException {
        
        final Class<?>[] webDavClasses = new Class<?>[]{
            ActiveLock.class,
            AllProp.class,
            CannotModifyProtectedProperty.class,
            Collection.class,
            CreationDate.class,
            Depth.class,
            DisplayName.class,
            Error.class,
            Exclusive.class,
            GetContentLanguage.class,
            GetContentLength.class,
            GetContentType.class,
            GetETag.class,
            GetLastModified.class,
            HRef.class,
            Include.class,
            Location.class,
            LockDiscovery.class,
            LockEntry.class,
            LockInfo.class,
            LockRoot.class,
            LockScope.class,
            LockToken.class,
            LockTokenMatchesRequestUri.class,
            LockTokenSubmitted.class,
            LockType.class,
            MultiStatus.class,
            NoConflictingLock.class,
            NoExternalEntities.class,
            Owner.class,
            PreservedLiveProperties.class,
            Prop.class,
            PropertyUpdate.class,
            PropFind.class,
            PropFindFiniteDepth.class,
            PropName.class,
            PropStat.class,
            Remove.class,
            ResourceType.class,
            Response.class,
            ResponseDescription.class,
            Set.class, Shared.class,
            Status.class,
            SupportedLock.class,
            TimeOut.class,
            Write.class};
        
        final List<Class<?>> allClasses = new ArrayList<>();
        allClasses.addAll(Arrays.asList(webDavClasses));
        allClasses.addAll(Arrays.asList(auxiliaryClasses));
        
        
        return JAXBContext.newInstance(allClasses.toArray(new Class<?>[]{}));
    }

}
