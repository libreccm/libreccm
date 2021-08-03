/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.documents;

import org.libreccm.categorization.Category;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A node of a {@link CategorizationTree}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorizationTreeNode {

    /**
     * The ID of the {@link Category} represented by the node.
     */
    private long categoryId;

    /**
     * The UUID of the {@link Category} represented by the node.
     */
    private String categoryUuid;

    /**
     * The unique ID of the {@link Category} represented by the node.
     */
    private String uniqueId;

    /**
     * The name of the {@link Category} represented by the node.
     */
    private String categoryName;

    /**
     * The title of the {@link Category} represented by the node. This value is
     * determined from {@link Category#title} using
     * {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)}.
     */
    private String title;

    /**
     * The description of the {@link Category} represented by the node. This
     * value is determined from {@link Category#description} using
     * {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)}.
     */
    private String description;

    /**
     * Is the category assigned to the current content item?
     */
    private boolean assigned;

//    /**
//     * Is any subcategory of the category represented by this node assigned to
//     * the current content item?
//     */
//    private boolean subCategoryAssigned;

    /**
     * Nodes for the subcategories of the category represented by this node.
     */
    private List<CategorizationTreeNode> subCategories;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(final String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(final boolean assigned) {
        this.assigned = assigned;
    }

    public boolean isSubCategoryAssigned() {
//        return subCategoryAssigned;
        return subCategories
            .stream()
            .map(CategorizationTreeNode::isAssigned)
            .reduce(false, (value1, value2) -> value1 || value2);
    }

//    public void setSubCategoryAssigned(final boolean subCategoryAssigned) {
//        this.subCategoryAssigned = subCategoryAssigned;
//    }

    public List<CategorizationTreeNode> getSubCategories() {
        return Collections.unmodifiableList(subCategories);
    }

    public void setSubCategories(
        final List<CategorizationTreeNode> subCategories
    ) {
        this.subCategories = new ArrayList<>(subCategories);
    }

}
