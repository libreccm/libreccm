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
package com.arsdigita.bebop;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * A variable whose value is local to each request. Objects that need to store
 * values that change in every request should declare them to be
 * <code>RequestLocal</code>. These variables hold their values only during a
 * duration of a request. They get reinitialized by a call to {@link
 * #initialValue(PageState)} for every new HTTP request.
 *
 * <p> For example, a class that wants to implement a request local property
 * <code>foo</code> would do the following:</p>
 *
 * <pre>
 * public class SomeClass {
 *     private RequestLocal m_foo;
 *     
 *     public SomeClass() {
 *       m_foo = new RequestLocal() {
 *             protected Object initialValue(PageState s) {
 *                 // Foo could be a much more complicated value
 *                 return s.getRequestURI();
 *             }
 *         };
 *     }
 *     
 *     public String getFoo(PageState s) {
 *         return (String) m_foo.get(s);
 *     }
 *     
 *     public void setFoo(PageState s, String v) {
 *         m_foo.set(s, v);
 *     }
 * }
 * </pre>
 *
 * @author David Lutterkort
 * @version $Id$
 */
public class RequestLocal {

    private static final String ATTRIBUTE_KEY =
        "com.arsdigita.bebop.RequestLocal";

    // Fetch the map used to store RequestLocals, possibly creating it along the
    // way
    private Map getMap(HttpServletRequest request) {
        // This lock is paranoid.  We can remove it if we know that only one
        // thread will be touching a request object at a time. (Seems likely,
        // but, like I said, I'm paranoid.)
        synchronized (request) {
            Map result = (Map)request.getAttribute(ATTRIBUTE_KEY);
            result = (Map)request.getAttribute(ATTRIBUTE_KEY);
            if (result == null) {
                result = new HashMap();
                request.setAttribute(ATTRIBUTE_KEY, result);
            }
            return result;
        }
    }

    /**
     * Returns the value to be used during the request represented by
     * <code>state</code>. This method is called at most once per request,
     * the first time the value of this <code>RequestLocal</code> is
     * requested with {@link #get get}. <code>RequestLocal</code> must be
     * subclassed, and this method must be overridden. Typically, an
     * anonymous inner class will be used.
     *
     *
     * @param state represents the current state of the request
     * @return the initial value for this request local variable.
     */
    protected Object initialValue(PageState state) {
        return null;
    }

    /**
     * Returns the request-specific value for this variable for the request
     * associated with <code>state</code>.
     *
     * @param state represents the current state of the request
     * @return the value for this request local variable.
     */
    public Object get(PageState state) {
        Map map = getMap(state.getRequest());
        Object result = map.get(this);

        if ( result == null && !map.containsKey(this) ) {
            result = initialValue(state);
            set(state, result);
        }
        return result;
    }

    /**
     * Sets a new value for the request local variable and associates it with
     * the request represented by <code>state</code>.
     *
     * @param state represents the current state of the request
     * @param value the new value for this request local variable
     */
    public void set(PageState state, Object value) {
        set(state.getRequest(), value);
    }

    /**
     * <p>Sets a new value for the request local variable and associates it with
     * the request represented by <code>request</code></p>
     *
     * <p>This method is intended for use when a Dispatcher needs to assign some
     * value to a RequestLocal for Bebop Page processing before Page processing
     * begins.</p>
     *
     * @param request represents the current request
     * @param value the new value for this request local variable
     */
    public void set(HttpServletRequest request, Object value) {
        getMap(request).put(this, value);
    }
}
