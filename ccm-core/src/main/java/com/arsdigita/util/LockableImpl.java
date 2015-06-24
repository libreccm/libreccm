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
package com.arsdigita.util;

/**
 * A generic implementation of the Lockable interface.
 *
 * @see Lockable
 *
 * @author Michael Bryzek
 * @version $Id$
 *
 */
public class LockableImpl implements Lockable {

    private boolean m_locked = false;

    /**
     * Lock an object. Locked objetcs are to be considered immutable. Any
     * attempt to modify them, e.g., through a <code>setXXX</code> method should
     * lead to an exception.
     *
     * @see Lockable#lock()
     *
     */
    // must not be final so cms.ui.Grid.GridModelBuilder can override it.
    @Override
    public void lock() {
        m_locked = true;
    }

    /**
     * Return whether an object is locked and thus immutable, or can still be
     * modified.
     *
     * @return
     *
     * @see Lockable#isLocked()
     *
     */
    // must not be final so cms.ui.PropertySheet.PSTMBAdapter can override it.
    @Override
    public boolean isLocked() {
        return m_locked;
    }

}
