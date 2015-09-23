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

import org.apache.log4j.Logger;

/**
 * <p>
 * A signal to <code>BaseServlet</code> requesting that the current transaction
 * be committed or aborted. As with all exceptions, throwing a
 * <code>TransactionSignal</code> stops the execution of currently running
 * code.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
class TransactionSignal extends Error {

    private static final long serialVersionUID = -6081887476661858043L;
    
    private static final Logger s_log = Logger
        .getLogger(TransactionSignal.class);
    

    private final boolean m_isCommitRequested;

    TransactionSignal(boolean isCommitRequested) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Constructing a transaction signal with "
                            + "isCommitRequested set " + isCommitRequested);
        }

        m_isCommitRequested = isCommitRequested;

    }

    public final boolean isCommitRequested() {
        return m_isCommitRequested;
    }

}
