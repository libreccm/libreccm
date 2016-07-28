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
package com.arsdigita.dispatcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  Generic implementation
 * of "try this URL-resource mapping; if nothing found try ..."
 * pattern.  This is useful for packages whose URL-to-resource mapping
 * is a composition of many separate, reusable mappings. The goal is to
 * reduce multi-branched, hard-coded if-else blocks.
 *
 * <p>This class makes it easier to break up dispatchers into a series
 * of smaller, re-usable, but not totally self-contained classes whose
 * dispatch method tries to find a resource according to its mapping,
 * and serves it if it finds one. If it can't find a resource, it
 * returns a failure
 * status code (DISPATCHER_CONTINUE) and the DispatcherChain tries the
 * next dispatcher in the sequence.
 *
 * <p>The dispatcher chain tries each dispatcher in the dispatcher
 * chain successively in the order in which they were added to the chain.
 *
 * @version $Id$
 */
public class DispatcherChain implements Dispatcher {

    private LinkedList m_dispatcherChain = new LinkedList();

    /**
     * Dispatches to the dispatcher chain.  Tries each dispatcher
     * in the sequence, in which they were added, and breaks out of
     * the loop when either one dispatcher returns DISPATCH_BREAK or
     * a dispatcher throws an exception.
     *
     * @param req the current servlet request
     * @param resp the current servlet response object
     * @param ctx the current <code>RequestContext</code> object
     * @throws java.io.IOException re-thrown when a dispatcher in the
     * chain throws an IOException.
     * @throws javax.servlet.ServletException re-thrown when a dispatcher
     * in the chain throws a ServletException.
     */
    public void dispatch(HttpServletRequest req,
                         HttpServletResponse resp,
                         RequestContext ctx)
        throws ServletException, IOException {

        Iterator iter = null;
        synchronized(this) {
            iter = m_dispatcherChain.iterator();
        }
        // already have a new iterator instance, so don't need
        // to synchronize rest of proc.
        while (iter.hasNext()) {
            ChainedDispatcher disp = (ChainedDispatcher)iter.next();
            int status = disp.chainedDispatch(req, resp, ctx);
            if (status == ChainedDispatcher.DISPATCH_BREAK) {
                break;
            }
        }
    }

    /**
     * Adds a dispatcher to the dispatcher chain. Dispatchers are
     * executed in the order they are added to the chain, so this
     * dispatcher will be executed after all the dispatchers that
     * were previously added to the chain and before all the
     * dispatchers that haven't yet been added to the chain.
     * @param cd the dispatcher to add
     */
    public synchronized void addChainedDispatcher(ChainedDispatcher cd) {
        m_dispatcherChain.addLast(cd);
    }
}
