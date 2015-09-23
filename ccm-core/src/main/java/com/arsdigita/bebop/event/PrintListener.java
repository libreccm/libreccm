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
package com.arsdigita.bebop.event;

import java.util.EventListener;

/**
 * Listeners of this class are called just before a {@link com.arsdigita.bebop.Component} is
 * about to be output, either in the form of an XML element, or by printing
 * its HTML representation. The {@link #prepare prepare method} of the
 * listener can make modifications to the {@link PrintEvent#getTarget
 * target} of the event. The target will then be used to produce output
 * instead of the source.
 * <p>
 * {@link PrintEvent PrintEvents} are <i>unicast</i> events, which means
 * that components should only permit the registration of one
 * <code>PrintListener</code>. Since the <code>PrintListener</code> is
 * expected to modify the target, allowing multiple listeners to modify the
 * target of one event would make it impossible to predict the resulting
 * target component, since an individual listener can not know which
 * listeners have run before it and which ones will run after it.
 * <p>
 * As an example consider the following code:
 * <pre>
 *   Label l = new Label("Default text");
 *   l.addPrintListener( new PrintListener {
 *     private static final BigDecimal ONE = new BigDecimal(1);
 *     private BigDecimal count = new BigDecimal(0);
 *     public void prepare(PrintEvent e) {
 *       Label t = e.getTarget();
 *       synchronized (count) {
 *         count.add(ONE);
 *       }
 *       t.setLabel("Call no." + count + " since last server restart");
 *     }
 *   });</pre>
 * Adding the label <code>l</code> to a page will lead to a label that
 * changes in every request and print how many times the containing label
 * has been called.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author David Lutterkort
 * @version $Id$
 */

public interface PrintListener extends EventListener {

    /**
     * Prepare the target component returned by {@link PrintEvent#getTarget
     * e.getTarget()} for output. The target component is an unlocked clone
     * of the source of the event and can be freely modified within this
     * method.
     *
     * @param e Event containing the page state, the source and the target of
     * the event
     *
     * @see PrintEvent
     */

    void prepare(PrintEvent e);

}
