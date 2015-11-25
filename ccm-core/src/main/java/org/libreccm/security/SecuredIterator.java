/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.security;

import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;

import java.util.Iterator;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <E>
 */
public class SecuredIterator<E extends CcmObject> implements Iterator<E> {
    
    private static final Logger LOGGER = LogManager.getLogger(SecuredIterator.class);
    
    private final Iterator<E> iterator;
    
    private final Class<E> clazz;
    
    private final String requiredPrivilege;
    
    public SecuredIterator(final Iterator<E> iterator,
                           final Class<E> clazz,
                           final String requiredPrivilege) {
        this.iterator = iterator;
        this.clazz = clazz;
        this.requiredPrivilege = requiredPrivilege;
    }
    
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    @Override
    public E next() {
        final CdiUtil cdiUtil = new CdiUtil();
        final PermissionChecker permissionChecker ;
        try {
            permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
        } catch (CdiLookupException ex) {
            throw new UncheckedWrapperException(ex);
        }
        
        final E object = iterator.next();
        if (permissionChecker.isPermitted(requiredPrivilege, object)) {
            return object;
        } else {
            try {
                final E placeholder = clazz.newInstance();
                placeholder.setDisplayName("Access denied");
                
                return placeholder;
            } catch (InstantiationException | IllegalAccessException ex) {
                LOGGER.error("Failed to create placeholder object. Returing null.", ex);
                return null;
            } 
        }
    }
    
}
