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
package com.arsdigita.bebop.list;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Component;

/**
 * Produce a component to output one item in a
 * <code>List</code>. For example, to output the item either as a link
 * that, when clicked, will make the item selected or, if the item is
 * selected will display it as a bold label, you would write the following
 * code:
 *
 * <pre>
 * public class MyListCellRenderer implements ListCellRenderer {
 *
 *   public Component getComponent(List list, PageState state, Object value,
 *                                 String key, int index, boolean isSelected) {
 *     Label l = new Label(value.toString());
 *     if (isSelected) {
 *       l.setFontWeight(Label.BOLD);
 *       return l;
 *     }
 *     return new ControlLink(l);
 *   }
 * }
 * </pre>
 * This is actually exactly what the {@link DefaultListCellRenderer} does.
 *
 * @see List
 * @see DefaultListCellRenderer
 * @see ListModel
 * @author David Lutterkort
 * @version $Id$ */
public interface ListCellRenderer {

    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>generateXML</code> or <code>print</code>
     * method is then called to "render" the cell.
     *
     * @param list the <code>List</code> in which this item is being displayed.
     * @param state represents the state of the current request.
     * @param value the value returned by
     * <code>list.getModel(state).getElement()</code>
     * @param key the value returned by
     * <code>list.getModel(state).getKey()</code>
     * @param index the number of the item in the list
     * @param isSelected true is the item is selected
     * @return the component used to generate the output for the list item
     * @pre list != null
     * @pre state |= null
     * @pre value != null
     * @pre key != null
     * @post return != null
     */
    Component getComponent(List list, PageState state, Object value,
                           String key, int index, boolean isSelected);
}
