/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.ui.admin.categories;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.cdi.utils.CdiUtil;

import java.util.Iterator;
import java.util.List;

/**
 * A reusable implementation of {@link TreeModel} for creating category trees.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoriesTreeModel implements TreeModel {

    private final Tree tree;
    private final Domain domain;

    public CategoriesTreeModel(final Tree tree, final Domain domain) {
        this.tree = tree;
        this.domain = domain;
    }

    @Override
    public TreeNode getRoot(final PageState state) {
        final CategoryRepository categoryRepository = CdiUtil.createCdiUtil()
            .findBean(CategoryRepository.class);
        final Category category = categoryRepository.findById(domain.getRoot()
            .getObjectId()).get();
        
        tree.expand(Long.toString(category.getObjectId()), state);
        
        return new CategoryTreeNode(category);
    }

    @Override
    public boolean hasChildren(final TreeNode node,
                               final PageState state) {

        final CategoriesController controller = CdiUtil
            .createCdiUtil()
            .findBean(CategoriesController.class);
        return controller.hasChildren(((CategoryTreeNode) node).getCategory());
    }

    @Override
    public Iterator getChildren(final TreeNode node,
                                final PageState state) {

        if (node.getKey().equals(getRoot(state).getKey())
            && tree.isCollapsed(node.getKey().toString(), state)) {
            
            tree.expand(node.getKey().toString(), state);
        }
        
        final CategoriesController controller = CdiUtil
            .createCdiUtil()
            .findBean(CategoriesController.class);
        final List<Category> subCategories = controller
            .getSubCategories(((CategoryTreeNode) node).getCategory());

        return new SubCategoryNodesIterator(subCategories);
    }

    private class CategoryTreeNode implements TreeNode {

        private final Category category;

        public CategoryTreeNode(final Category category) {
            this.category = category;
        }

        public Category getCategory() {
            return category;
        }

        @Override
        public Object getKey() {
            return Long.toString(category.getObjectId());
        }

        @Override
        public Object getElement() {
            return category.getName();
        }

    }

    private class SubCategoryNodesIterator
        implements Iterator<CategoryTreeNode> {

        private final Iterator<Category> subCategoriesIterator;

        public SubCategoryNodesIterator(final List<Category> subCategories) {
            subCategoriesIterator = subCategories.iterator();
        }

        @Override
        public boolean hasNext() {
            return subCategoriesIterator.hasNext();
        }

        @Override
        public CategoryTreeNode next() {
            return new CategoryTreeNode(subCategoriesIterator.next());
        }

    }

}
