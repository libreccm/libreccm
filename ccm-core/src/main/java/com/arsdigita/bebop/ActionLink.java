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

import com.arsdigita.globalization.GlobalizedMessage;



/**
 * A link that runs its action listeners when it is clicked. The target of the
 * link is the {@link Page} in which the action link is contained.
 *
 * <p> Typically, an action link is used in the following way:
 * <pre>
 *   ActionLink l = new ActionLink("Send email to everybody");
 *   l.addActionListener(new ActionListener() {
 *      public void actionPerformed(ActionEvent e) {
 *        System.out.println("Link was clicked.");
 *        ... figure out who everybody is and send them email ...
 *      }
 *   });
 * </pre>
 *
 * <p>See {@link BaseLink} for a description of all Bebop Link classes
 * and suggestions for using them.
 *
 * @author David Lutterkort 
 * @version $Id$ */
public class ActionLink extends ControlLink {

    /**
     * The value for the XML type attribute for an {@link ActionLink}.
     */
    protected static final String TYPE_ACTION = "action";

    /**
     * Constructs a new ActionLink. The link encapsulates
     * the child component (usually either a label or an image).
     *
     * @param child the component to be turned into a link
     */
    public ActionLink(Component child) {
        super(child);
        setTypeAttr(TYPE_ACTION);
    }

    /**
     * Constructs a new ActionLink with the given string label.
     *
     * @param label the string label for the link
     */
    public ActionLink(GlobalizedMessage label) {
        this(new Label(label));
    }

    /**
     * Constructs a new ActionLink with the given string label.
     *
     * @param label the string label for the link
     * @deprecated refactor to use @see ActionLink(GlobalizedMessage label)
     */
    public ActionLink(String label) {
        this(new Label(label));
    }

    /**
     * Sets the page state's control event. Should be overridden by child
     * classes. By default, the link does not receive any control events.
     *
     * @param s the current page state
     */
    @Override
    public void setControlEvent(PageState s) {
        s.setControlEvent(this);
    }

}
