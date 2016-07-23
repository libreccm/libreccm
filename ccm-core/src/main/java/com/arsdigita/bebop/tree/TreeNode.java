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

/**
 *  The interface
 * describes a node for a tree.
 *
 * @author David Lutterkort 
 * @author Stanislav Freidin 
 * @author Tri Tran 
 *
 * @version $Id$ */
public interface TreeNode {


    /**
     * Obtain a unique ID representing the node
     * @return 
     */
    Object getKey();

    /**
     * Get the element of the tree node.  The concrete type
     * of the object returned is specific to each implementation
     * of the <code>TreeModel</code> and should be documented
     * there.
     *
     * @return the element for the tree node
     */
    Object getElement();
}
