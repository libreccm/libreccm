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

import java.io.IOException;
import java.util.ArrayList;

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 * A link back to the page in which it is contained. The control link captures
 * and preserves the current state of the page, and possibly any control events
 * that have been set. It is most useful inside a {@link
 * com.arsdigita.bebop.list.ListCellRenderer} or a {@link
 * com.arsdigita.bebop.table.TableCellRenderer}, where the list or table has
 * already set up the events 'tight' for the control link to do the right thing.
 *
 * <p>
 * <b>Warning:</b> Even though a control link lets you add action listeners,
 * they are not run unless you override {@link #setControlEvent
 * setControlEvent}. If you need this behavior, you should use an {@link
 * ActionLink}. A control link is hardly ever useful unless it is contained in
 * an event-generating component like {@link List} or {@link Table}.
 *
 * <p>
 * <b>Example:</b> A control link is mainly useful to send events to other
 * components. For example, the following control link will cause a control
 * event <tt>delete</tt> with associated value <tt>42</tt> to be sent to the
 * component <tt>fooComponent</tt> when the user clicks on it:
 *
 * <pre>
 *    ControlLink l = new ControlLink("click here") {
 *      public void setControlEvent(PageState s) {
 *        s.setControlEvent(fooComponent, "delete", 42);
 *      }
 *    };
 * </pre>
 *
 * <p>
 * This requires that <tt>fooComponent</tt> is part of the page hierarchy. The
 * control link <tt>l</tt> does not have to be part of the page hierarchy, and
 * may be generated on the fly. (See {@link PageState} for details on control
 * events.)
 *
 * <p>
 * See {@link BaseLink} for a description of all Bebop Link classes.
 *
 * @author Stanislav Freidin
 * @author David Lutterkort
 * @version $Id: ControlLink.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ControlLink extends BaseLink {

    /**
     * The XML type attribute for a {@link ControlLink}.
     */
    protected final String TYPE_CONTROL = "control";

    /**
     * A list of all action listeners. The list is instantiated lazily, and will
     * therefore be null in most applications.
     */
    private ArrayList m_actionListeners;

    /**
     * Constructs a new ControlLink. The link will encapsulates the child
     * component (which should be a label or an image).
     *
     * @param child the component that will be turned into a link
     */
    public ControlLink(Component child) {
        super(child, "");
        setTypeAttr(TYPE_CONTROL);
    }

    /**
     * Constructs a new ControlLink with the given string label.
     *
     * @param label the string label for the link
     */
    public ControlLink(String label) {
        this(new Label(label));
    }

    /**
     * Adds an <code>ActionListener</code>, which will be run when
     * {@link#respond respond} is called.
     *
     * @param listener The listener to add.
     *
     * @see #respond respond
     */
    public void addActionListener(ActionListener listener) {
        Assert.isUnlocked(this);
        if (m_actionListeners == null) {
            m_actionListeners = new ArrayList();
        }
        m_actionListeners.add(listener);
    }

    /**
     * Removes a previously added <code>ActionListener</code>.
     *
     * @param listener The listener to remove.
     *
     * @see #addActionListener addActionListener
     */
    public void removeActionListener(ActionListener listener) {
        Assert.isUnlocked(this);
        if (m_actionListeners == null) {
            return;
        }
        m_actionListeners.remove(listener);
    }

    /**
     * Fires an <code>ActionEvent</code>, which causes all registered
     * <code>ActionListener</code>s to be run. The source of the event is the
     * <code>TabbedPane</code>.
     *
     * @param state the current page state
     *
     * @see #respond respond
     */
    protected void fireActionEvent(PageState state) {
        ActionEvent e = null;
        if (m_actionListeners == null) {
            return;
        }
        for (int i = 0; i < m_actionListeners.size(); i++) {
            if (e == null) {
                e = new ActionEvent(this, state);
            }
            ((ActionListener) m_actionListeners.get(i)).actionPerformed(e);
        }
    }

    /**
     * Responds to the incoming request. Fires the <code>ActionEvent</code>.
     *
     * @param state the current page state
     */
    @Override
    public void respond(PageState state) {
        fireActionEvent(state);
    }

    /**
     * Generates the URL for a link and sets it as the "href" attribute of the
     * parent.
     *
     * @param state  the current page state
     * @param parent the parent element
     */
    @Override
    protected void generateURL(PageState state, Element parent) {
        setControlEvent(state);
        try {
            parent.addAttribute("href", state.stateAsURL());
        } catch (IOException e) {
            parent.addAttribute("href", "");
        }
        exportAttributes(parent);
        state.clearControlEvent();
    }

    /**
     * Sets the page state's control event. Should be overridden by child
     * classes. By default, the link receives no control events whatsoever.
     *
     * @param ps the current page state
     */
    // FIXME: Why is this not protected ?
    public void setControlEvent(PageState ps) {
        return;
    }

}
