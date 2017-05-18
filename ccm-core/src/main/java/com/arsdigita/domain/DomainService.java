/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.domain;

import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * This class should be extended by domain object services that need privileged
 * access to the DataObject encapsulated by a DomainObject or the DataCollection
 * encapsulated by a DomainCollection.
 *
 * Note: This class was tightly coupled with the old PDL based persistence
 * system. The class has been adapted to use standard Bean/Reflection methods
 * instead, but some the results may differ.
 *
 * @author Oumi Mehrotra
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class DomainService {

    /**
     *
     * Get a property of the specified domain object.
     *
     * @param domainObject
     * @param attr
     *
     * @return
     *
     */
    protected static Object get(final Object domainObject,
                                final String attr) {

        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(domainObject.getClass());
        } catch (IntrospectionException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final Optional<PropertyDescriptor> property = Arrays
            .stream(beanInfo.getPropertyDescriptors())
            .filter(current -> attr.equals(current.getName()))
            .findAny();

        if (property.isPresent()) {

            final Method readMethod = property.get().getReadMethod();

            final Object result;
            try {
                result = readMethod.invoke(domainObject);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new UnexpectedErrorException(ex);
            }
            return result;
        } else {
            return null;
        }
    }

    // These methods modify domain objects and should be used cautiously.
    /**
     *
     * Set a property of the DomainObjects DataObject.
     *
     *
     * @param domainObject
     * @param attr
     * @param value
     */
    protected static void set(final Object domainObject,
                              final String attr,
                              final Object value) {

        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(domainObject.getClass());
        } catch (IntrospectionException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final Optional<PropertyDescriptor> property = Arrays
            .stream(beanInfo.getPropertyDescriptors())
            .filter(current -> attr.equals(current.getName()))
            .findAny();

        if (property.isPresent()) {

            final Method writeMethod = property.get().getWriteMethod();

            try {
                writeMethod.invoke(domainObject, value);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new UnexpectedErrorException(ex);
            }

        }
    }

    /**
     *
     * Set an association DomainObjects DataObject.
     *
     * @see DomainObject#set(String, Object)
     *
     */
//    protected static void setAssociation(DomainObject domainObject,
//                                         String attr, DomainObject dobj) {
//        domainObject.set(attr, dobj);
//    }

    /**
     * Adds data object specified by <i>value</i> to the specified role (with
     * multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#add(String, DataObject)
     */
//    protected static DataObject add(DomainObject domainObject,
//                                    String roleName, DataObject value) {
//        return domainObject.add(roleName, value);
//    }

    /**
     * Adds domain object specified by <i>value</i> to the specified role (with
     * multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#add(String, DomainObject)
     */
//    protected static DataObject add(DomainObject domainObject,
//                                    String roleName, DomainObject value) {
//        return domainObject.add(roleName, value);
//    }

    /**
     *
     * Removes data object specified by <i>value</i> from the specified role
     * (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#remove(String, DataObject)
     */
//    protected static void remove(DomainObject domainObject,
//                                 String roleName, DataObject value) {
//        domainObject.remove(roleName, value);
//    }

    /**
     *
     * Removes domain object specified by <i>value</i> from the specified role
     * (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#remove(String, DomainObject)
     */
//    protected static void remove(DomainObject domainObject,
//                                 String roleName, DomainObject value) {
//        domainObject.remove(roleName, value);
//    }

    /**
     *
     * Clears specified role (with multiplicity > 1) of specified domain object.
     *
     * @see DomainObject#clear(String)
     */
//    protected static void clear(DomainObject domainObject, String roleName) {
//        domainObject.clear(roleName);
//    }

}
