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
package com.arsdigita.bebop.tree;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;

/**
 *  The interface
 * describes how a tree node (component) can be rendered.
 *
 * @author David Lutterkort
 * @author Tri Tran
 *
 * @see TreeModel
 * @see TreeNode
 * @see Tree
 * @version $Id$ */
public class DefaultTreeCellRenderer implements TreeCellRenderer {

    /**
     * Returns node component to be displayed.  The component's
     * <code>generateXML</code> or <code>print</code> is called
     * to render the node.
     *
     * @param tree the <code>Tree</code> in which this node is being displayed
     * @param state represents the state of the current request
     * @param value the object returned by the TreeModel for that node,
     *              such as pretty name
     * @param isSelected true if the node is selected
     * @param isExpanded true if the node is expanded (not collapsed)
     * @param isLeaf true if the node is a leaf node (no children)
     * @param key the object uniquely identify that node (primary key)
     * @return the component used to generate the output for the node item
     */
    public Component getComponent (Tree tree, PageState state, Object value,
                                   boolean isSelected, boolean isExpanded,
                                   boolean isLeaf, Object key) {
        Label l = new Label(value.toString());
        // Bold if selected
        if (isSelected) {
            l.setFontWeight(Label.BOLD);
            return l;
        }
        // Currently, we are not doing anything special here for
        // collapsed/expanded node, or leaf node... Also not doing anything
        // fancy with node's key.  We are leaving this to Tree.java for now
        // to set the appropriate attributes...
        return new ControlLink(l);
    }
}
