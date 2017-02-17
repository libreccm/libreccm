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

import com.arsdigita.bebop.PageState;
import java.util.Iterator;

/**
 *  The interface
 * describes a model for a tree structure.
 *
 * @author David Lutterkort 
 * @author Stanislav Freidin 
 * @author Tri Tran 
 *
 * @version $Id$ */
public interface TreeModel {


    /**
     * Obtain the root node of the tree, passing
     * in PageState for permissioning purposes
     */
    TreeNode getRoot(PageState data);

    /**
     * Check whether the node has children
     */
    boolean hasChildren(TreeNode n, PageState data);

    /**
     * Check whether a given node has children, passing
     * in PageState for permissioning purposes
     */
    Iterator<TreeNode> getChildren(TreeNode n, PageState data);
}
