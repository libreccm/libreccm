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
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.cdi.utils.CdiUtil;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoriesTreeModel implements TreeModel {

    private final Domain domain;

    public CategoriesTreeModel(final Domain domain) {
        this.domain = domain;
    }

    @Override
    public TreeNode getRoot(final PageState data) {
        final CategoryRepository categoryRepository = CdiUtil.createCdiUtil()
            .findBean(CategoryRepository.class);
        final Category category = categoryRepository.findById(domain.getRoot()
            .getObjectId()).get();
        return new CategoryTreeNode(category);
    }

    @Override
    public boolean hasChildren(final TreeNode node,
                               final PageState state) {
        final CategoryRepository categoryRepo = CdiUtil.createCdiUtil()
            .findBean(CategoryRepository.class);
        final Category category = categoryRepo.findById(
            ((CategoryTreeNode) node).getCategory().getObjectId()).get();

        return (category.getSubCategories() != null
                && !category.getSubCategories().isEmpty());
    }

    @Override
    public Iterator getChildren(final TreeNode node,
                                final PageState state) {
        final CategoryRepository categoryRepo = CdiUtil.createCdiUtil()
            .findBean(CategoryRepository.class);
        final Category category = categoryRepo.findById(
            ((CategoryTreeNode) node).getCategory().getObjectId()).get();

        return new SubCategoryNodesIterator(category.getSubCategories());
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
