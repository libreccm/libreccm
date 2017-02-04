/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

/**
 * A class that provides request-framed control over a thread-local
 * value.  With such control, it is possible to safely reuse
 * thread-local data across requests.  For example, the following
 * <code>InternalRequestLocal</code> reuses a <code>HashMap</code>.
 *
 * <pre><blockquote>
 * class HashMapRequestLocal extends InternalRequestLocal {
 *     protected Object initialValue() {
 *         return new HashMap();
 *     }
 *
 *     // Does not override prepareValue(HttpServletRequest).
 *     // InternelRequestLocal's default implementation calls
 *     // clearValue() to prepare the value for the request.
 *
 *     protected void clearValue() {
 *         ((HashMap) get()).clear();
 *     }
 * }
 * </blockquote></pre>
 *
 * <p><code>initialValue()</code> is called just once, when the value
 * is first accessed.  <code>prepareValue(HttpServletRequest)</code>
 * is called at the start of every request serviced by {@link
 * com.arsdigita.web.BaseServlet}.  <code>clearValue()</code> is
 * called at the end of every request handled by said servlet.</p>
 *
 * <p>The default implementation of <code>clearValue()</code> sets the
 * value to <code>null</code>, and the default implementation of
 * <code>prepareValue(HttpServletRequest)</code> calls
 * <code>clearValue()</code>.  As a result, an
 * <code>InternalRequestLocal</code> as used in the following example
 * is similar to using a request attribute.</p>
 *
 * <pre><blockquote>
 * // This value is s_servletContext.set(null) at the start and end of
 * // each request.
 * static ThreadLocal s_servletContext = new InternalRequestLocal();
 * </blockquote></pre>
 *
 * <p>Be advised that errors in using this class can easily result in
 * excess trash left on threads and, worse, big memory leaks.  Please
 * use caution.</p>
 *
 * @see java.lang.ThreadLocal
 * @see com.arsdigita.web.BaseServlet
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id$
 */
class InternalRequestLocal extends ThreadLocal {

    private static final Logger LOGGER = 
                         LogManager.getLogger(InternalRequestLocal.class);

    private static final ArrayList s_locals = new ArrayList();

    /**
     * <p>Constructs a new InternalRequestLocal and registers it to be
     * initialized and cleared on each request.</p>
     */
    public InternalRequestLocal() {
        super();

        s_locals.add(this);
    }

    /**
     * 
     * @param sreq 
     */
    static void prepareAll(final HttpServletRequest sreq) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initializing all request-local objects; there are " +
                        s_locals.size());
        }

        final Iterator iter = s_locals.iterator();

        while (iter.hasNext()) {
            final InternalRequestLocal local = (InternalRequestLocal) iter.next();

            local.prepareValue(sreq);
        }
    }

    /**
     * 
     */
    static void clearAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Clearing all request-local objects; there are " +
                        s_locals.size());
        }

        final Iterator iter = s_locals.iterator();

        while (iter.hasNext()) {
            final InternalRequestLocal local = (InternalRequestLocal) iter.next();

            local.clearValue();
        }
    }

    /**
     * <p>Called at the start of each request, this method returns the
     * request-initialized value of the thread-local variable.</p>
     *
     * <p>By default this method calls <code>clearValue()</code>.</p>
     *
     * @param sreq the current servlet request
     * @return the request-initialized value
     */
    protected void prepareValue(HttpServletRequest sreq) {
        clearValue();
    }

    /**
     * <p>Called at the end of each request, this method clears the
     * thread-local value.</p>
     *
     * <p>By default this method calls <code>set(null)</code>.  Users
     * of this class may override this method to better reuse the
     * thread-local value.</p>
     */
    protected void clearValue() {
        set(null);
    }
}
