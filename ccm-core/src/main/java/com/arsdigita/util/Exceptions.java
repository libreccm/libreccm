/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Exceptions {

    private static Logger s_log = Logger.getLogger(Exceptions.class);

    private static Map s_unwrappers = new HashMap();

    public static Throwable[] unwrap(Throwable t) {
        Assert.exists(t, Throwable.class);

        List exceptions = new ArrayList();
        
        exceptions.add(t);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Trying to unwrap " + t.getClass());
        }

        Throwable current = t;

        for (;;) {
            Throwable inner = null;
            ExceptionUnwrapper unwrapper = findUnwrapper(current.getClass());

            if (unwrapper != null) {
                inner = unwrapper.unwrap(current);
            }
            
            if (inner == null) {
                Assert.exists(current, Throwable.class);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Returning exception " + current.getClass());
                }
                return (Throwable[])exceptions.toArray(
                    new Throwable[exceptions.size()]);
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("Inner exception is " + inner.getClass());
            }

            exceptions.add(inner);

            current = inner;
        }

        // Unreachable
        //throw new RuntimeException("this cannot happen");
    }

    
    public static void registerUnwrapper(Class exception,
                                         ExceptionUnwrapper unwrapper) {
        s_unwrappers.put(exception, unwrapper);
    }

    public static void unregisterUnwrapper(Class exception) {
        s_unwrappers.remove(exception);
    }

    public static ExceptionUnwrapper getUnwrapper(Class exception) {
        return (ExceptionUnwrapper)s_unwrappers.get(exception);
    }

    public static ExceptionUnwrapper findUnwrapper(Class exception) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Finding unwrapper for " + exception.getName());
        }

        Class current = exception;
        ExceptionUnwrapper unwrapper = null;
        while (unwrapper == null && 
               current != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Trying class " + current.getName());
            }
            unwrapper = (ExceptionUnwrapper)s_unwrappers.get(current);
            current = current.getSuperclass();
        }
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Got unwrapper " + 
                        (unwrapper != null ? unwrapper.getClass() : null));
        }
        return unwrapper;
    }
}
