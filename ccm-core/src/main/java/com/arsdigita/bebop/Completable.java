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

import com.arsdigita.util.Assert;

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
// Stacktraces is a support tool to use in a specifically difficult development
// situation. It is abundant in production and for normal development work and
// it provved to have funny side effects in a production environment. So it is
// commented out here but kept for further references.
// import com.arsdigita.developersupport.StackTraces;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Completable.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 **/

public abstract class Completable implements Component {

    private ArrayList m_completionListeners = new ArrayList();

    public Completable() {
     // See note above!
     // if ( s_log.isDebugEnabled() ) {
     //     StackTraces.captureStackTrace(this);
     // }
    }

    public void addCompletionListener(ActionListener listener) {
        Assert.isUnlocked(this);
        m_completionListeners.add(listener);
    }

    protected void fireCompletionEvent(PageState ps) {
        ActionEvent evt = new ActionEvent(this, ps);
        for (Iterator it = m_completionListeners.iterator(); it.hasNext(); ) {
            ActionListener listener = (ActionListener) it.next();
            listener.actionPerformed(evt);
        }
    }
}
