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
package org.libreccm.categorization;


import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

/**
 * Implements the {@link com.arsdigita.bebop.tree.TreeModel} interface for
 * categories.
 *
 * @author Daniel Berrange
 * @version $Revision: #17 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class CategoryTreeModelLite implements TreeModel {

    private static final Logger LOGGER = LogManager.getLogger(
            CategoryTreeModelLite.class);
//    String m_order = null;
    final Category category;

    /**
     * Initializes with the passed in the root Category.
     *
     * @param root the root category for this TreeModel
     */
//    public CategoryTreeModelLite(Category root) {
//        this(root, null);
//    }

    /**
     * Initializes with the passed in the root Category.
     *
     * @param root the root category for this TreeModel
     */
    public CategoryTreeModelLite(Category root) {
//        super(root.getUniqueId(),
//              "com.arsdigita.categorization.getRootCategory",
//              "com.arsdigita.categorization.getSubCategories");
//        m_order = order;
        category = root;
    }

//    @Override
//    protected DataQueryTreeIterator getDataQueryTreeIterator(DataQueryTreeNode node,
//                                                             String getSubCategories) {
//        return new CategoryTreeIterator(node, getSubCategories, m_order);
//    }

    @Override
    public TreeNode getRoot(PageState data) {
        return null;
    }

    @Override
    public boolean hasChildren(TreeNode n, PageState data) {
        return false;
    }

    @Override
    public Iterator<TreeNode> getChildren(TreeNode n, PageState data) {
        return null;
    }

//    private static class CategoryTreeIterator extends DataQueryTreeIterator {
//
//        public CategoryTreeIterator(DataQueryTreeNode node, String getSubCategories, String order) {
//            super(node, getSubCategories);
//            if (order != null) {
//                addOrder(order);
//            }
//        }
//
//        @Override
//        public Object next() {
//            DataQueryTreeNode node = (DataQueryTreeNode) super.next();
//
//            //                    m_nodes.getLink
//            node.setValue(Category.IS_ABSTRACT,
//                          (Boolean) m_nodes.get(Category.IS_ABSTRACT));
//            return node;
//        }
//
//    }
}
