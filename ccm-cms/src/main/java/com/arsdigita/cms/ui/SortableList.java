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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.cms.CMS;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.io.IOException;

/**
 * This list offers the option for the code to provide the developer with links
 * to sort the given categories.
 *
 * NOTE: This UI currently does not scale well with large numbers of items since
 * it just lists all of them. It would probably be nice to integrate a paginator
 * as well to as to allow the user to move an item in large distances and to
 * insert an item in the middle. Right now, when you add an item it is just
 * placed at the end. However, if you want the item to appear in the middle then
 * you must hit the "up" arrow n/2 times where n is the number of items in the
 * list. This clearly is not a good setup.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Id: SortableList.java 1618 2007-09-13 12:14:51Z chrisg23 $
 */
public abstract class SortableList extends List {

    private static final org.apache.log4j.Logger s_log
            = org.apache.log4j.Logger.getLogger(SortableList.class);

    // It would be really nice if this used the save variable as is
    // used by List but because List has it as private, we cannot do that.
    private static final String SELECT_EVENT = "s";
    protected static final String PREV_EVENT = "prev";
    protected static final String NEXT_EVENT = "next";
    public boolean m_sortItems;

    /**
     * This just makes a standard {@link SortableList}
     */
    public SortableList(ParameterSingleSelectionModel model) {
        this(model, false);
    }

    public SortableList(ParameterSingleSelectionModel model, boolean suppressSort) {
        super(model);
        m_sortItems = !suppressSort;
    }
    
    /**
     * This geneates the XML as specified by the arguments pass in to the
     * constructor.
     */
    public void generateXML(PageState state, Element parent) {
        if (!isVisible(state)) {
            return;
        }

        // They want the special sort items
        ListModel m = getModel(state);

        if (!m.next()) {
            super.generateXML(state, parent);
            return;
        }

        // because m.next() returned true, we know there are items
        // in the list
        Element list = parent.newChildElement("cms:sortableList", CMS.CMS_XML_NS);
        exportAttributes(list);

        Component c;
        Object selKey = getSelectedKey(state);
        int i = 0;
        boolean hasNext;
        do {
            Element item = list.newChildElement(BebopConstants.BEBOP_CELL, BEBOP_XML_NS);
            if (m_sortItems) {

                item.addAttribute("configure", "true");
            }
            String key = m.getKey();
            Assert.exists(key);

            // Converting both keys to String for comparison
            // since ListModel.getKey returns a String
            boolean selected = (selKey != null)
                    && key.equals(selKey.toString());

            if (selected) {
                item.addAttribute("selected", "selected");
            }

            generateLabelXML(state, item,
                    new Label(new GlobalizedMessage(m.getElement().toString())), key, m.getElement());

            hasNext = m.next();

            // Add attributes containing URLs that fire control events
            // for various portlet actions
            try {
                // Maybe add attribute containing URL for "move up" link
                if (i > 0) {
                    state.setControlEvent(this, PREV_EVENT, key);
                    item.addAttribute("prevURL", state.stateAsURL());
                }

                // Maybe add attribute containing URL for "move down" link
                if (hasNext) {
                    state.setControlEvent(this, NEXT_EVENT, key);
                    item.addAttribute("nextURL", state.stateAsURL());
                }

            } catch (IOException ex) {
                throw new IllegalStateException("Caught IOException: "
                        + ex.getMessage());
            }
            i++;
        } while (hasNext);

        state.clearControlEvent();
    }

    protected void generateLabelXML(PageState state, Element parent,
            Label label, String key, Object element) {
        state.setControlEvent(this, SELECT_EVENT, key);
        Component c = new ControlLink(label);
        c.generateXML(state, parent);
    }
}
