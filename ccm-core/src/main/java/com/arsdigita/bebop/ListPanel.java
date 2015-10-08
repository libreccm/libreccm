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

import static com.arsdigita.bebop.Component.*;

import java.util.Iterator;

import com.arsdigita.xml.Element;

/**
 * A container that outputs its components in a &lt;list&gt;. Each child is
 * printed in its own list item.  The components are put into the list
 * in the order in which they were added to the
 * <code>ListPanel</code>, progressing from top to bottom.
 *
 * <p> ListPanels can be ordered or unordered.</p>
 *
 * @author Christian Brechb&uuml;hler (christian@arsdigita.com)
 *
 * @version $Id$
 * */
public class ListPanel extends SimpleContainer  {

    public static final boolean ORDERED   = true ;
    public static final boolean UNORDERED = false;
    private boolean m_ordered;

    /**
     * Creates a simple list.
     * @param ordered <code>true</code> is an ordered (numbered) list;
     * <code>false</code> is an unordered (bulleted) list
     *
     */
    public ListPanel(boolean ordered) {
        m_ordered = ordered;
    }

    /**
     * Adds child components as a subtree under list-item nodes.
     * <p>Generates a DOM fragment:
     * <p><pre>
     * &lt;bebop:listPanel>
     *   &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *   &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *   ...
     * &lt;/bebop:list></pre></p>
     * @param state the state of the current request
     * @param parent the node under which this subtree will be added
     */
    public void generateXML(PageState state, Element parent) {

        if ( ! isVisible(state) ) {
            return;
        }

        Element list = parent.newChildElement("bebop:listPanel", BEBOP_XML_NS);
        list.addAttribute("ordered", String.valueOf(m_ordered));
        exportAttributes(list);

        // generate XML for children
        for (Iterator i = children(); i.hasNext(); ) {
            Component c = (Component) i.next();

            Element item  = list.newChildElement("bebop:cell", BEBOP_XML_NS);
            c.generateXML(state, item);
        }
    }
}
