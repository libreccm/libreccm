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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryMoverModel implements TreeModel {

    private final Domain domain;
    private final Category selectedCategory;

    public CategoryMoverModel(final Domain domain,
                              final Category selectedCategory) {
        this.domain = domain;
        this.selectedCategory = selectedCategory;
    }

    @Override
    public TreeNode getRoot(final PageState data) {
        final CategoryRepository categoryRepository = CdiUtil.createCdiUtil()
            .findBean(CategoryRepository.class);
        final Category category = categoryRepository.findById(domain.getRoot()
            .getObjectId()).get();
        return new CategoryMoverNode(category);
    }

    /**
     * Creates a list of the sub categories of the provided node
     * <em>without</em>
     * the selected category. This prevents the selected category and its sub
     * categories from be added to tree. Therefore the user can't move a
     * category beneath itself.
     *
     * @param node The current node.
     *
     * @return A list of the subcategories of the category managed by the
     *         provided node.
     */
    private List<Category> getSubCategories(final TreeNode node) {
        final CategoryRepository categoryRepo = CdiUtil.createCdiUtil()
            .findBean(CategoryRepository.class);
        final Category category = categoryRepo.findById(
            ((CategoryMoverNode) node).getCategory().getObjectId()).get();

        final List<Category> subCategories = new ArrayList<>();
        category.getSubCategories().forEach(c -> {
            if (!c.equals(selectedCategory)) {
                subCategories.add(c);
            }
        });

        return subCategories;
    }

    @Override
    public boolean hasChildren(final TreeNode node, final PageState state) {
        final List<Category> subCategories = getSubCategories(node);
        return (subCategories != null && !subCategories.isEmpty()) ;
    }

    @Override
    public Iterator getChildren(final TreeNode node, final PageState state) {
        return new SubCategoryNodesIterator(getSubCategories(node));
    }

    private class CategoryMoverNode implements TreeNode {

        private final Category category;

        public CategoryMoverNode(final Category category) {
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
        implements Iterator<CategoryMoverNode> {

        private final Iterator<Category> subCategoriesIterator;

        public SubCategoryNodesIterator(final List<Category> subCategories) {
            subCategoriesIterator = subCategories.iterator();
        }

        @Override
        public boolean hasNext() {
            return subCategoriesIterator.hasNext();
        }

        @Override
        public CategoryMoverNode next() {
            return new CategoryMoverNode(subCategoriesIterator.next());
        }

    }

}
