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
import org.libreccm.cdi.utils.CdiUtil;

import java.util.Iterator;
import java.util.Objects;

/**
 * Implements the {@link com.arsdigita.bebop.tree.TreeModel} interface for
 * categories.
 *
 * @author Daniel Berrange
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter </a>
 */
public class CategoryTreeModelLite implements TreeModel {

    private static final Logger LOGGER = LogManager.getLogger(
        CategoryTreeModelLite.class);
//    String m_order = null;
    private final Category root;

    /**
     * Initialises with the passed in the root Category.
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
    public CategoryTreeModelLite(final Category root) {
//        super(root.getUniqueId(),
//              "com.arsdigita.categorization.getRootCategory",
//              "com.arsdigita.categorization.getSubCategories");
//        m_order = order;
        this.root = root;
    }

//    @Override
//    protected DataQueryTreeIterator getDataQueryTreeIterator(DataQueryTreeNode node,
//                                                             String getSubCategories) {
//        return new CategoryTreeIterator(node, getSubCategories, m_order);
//    }
    @Override
    public TreeNode getRoot(final PageState data) {
        return new CategoryTreeNode(root);
    }

    @Override
    public boolean hasChildren(final TreeNode node, final PageState state) {

        Objects.requireNonNull(node);
        if (node.getKey() == null
                || !(node.getKey() instanceof Long)) {
            throw new IllegalArgumentException(
                "The key of the provided TreeNode is null or not a Long.");
        }

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryTreeModelLiteController controller = cdiUtil
            .findBean(CategoryTreeModelLiteController.class);

        return controller.hasSubCategories((long) node.getKey());
    }

    @Override
    public Iterator<TreeNode> getChildren(
        final TreeNode node, final PageState state
    ) {
        Objects.requireNonNull(node);
        if (node.getKey() == null
                || !(node.getKey() instanceof Long)) {
            throw new IllegalArgumentException(
                "The key of the provided TreeNode is null or not a Long.");
        }

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryTreeModelLiteController controller = cdiUtil
            .findBean(CategoryTreeModelLiteController.class);

        return controller
            .findSubCategories((long) node.getKey())
            .stream()
            .map(this::buildTreeNode)
            .iterator();
    }

    private TreeNode buildTreeNode(final Category category) {
        return new CategoryTreeNode(category);
    }

    private class CategoryTreeNode implements TreeNode {

        private final Category category;

        public CategoryTreeNode(final Category category) {
            this.category = category;
        }

        @Override
        public Object getKey() {
            if (category == null) {
                return null;
            } else {
                return category.getObjectId();
            }
        }

        @Override
        public Object getElement() {

            if (category.getTitle().getValues().isEmpty()) {
                return category.getName();
            } else {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final CategoryTreeModelLiteController controller = cdiUtil
                .findBean(CategoryTreeModelLiteController.class);
                return controller.getTitle(category);
            }
        }

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
