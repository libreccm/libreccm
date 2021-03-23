/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorizationTreeNode {
    
    private long categoryId;
    
    private String categoryUuid;
    
    private String uniqueId;
    
    private String categoryName;
    
    private String title;
    
    private String description;
    
    private boolean assigned;
    
    private boolean subCategoryAssigned;
    
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

    public void setCategoryUuid(final String  categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String  uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(final String  categoryName) {
        this.categoryName = categoryName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String  title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String  description) {
        this.description = description;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(final boolean assigned) {
        this.assigned = assigned;
    }

    public boolean isSubCategoryAssigned() {
        return subCategoryAssigned;
    }

    public void setSubCategoryAssigned(final boolean subCategoryAssigned) {
        this.subCategoryAssigned = subCategoryAssigned;
    }

    public List<CategorizationTreeNode> getSubCategories() {
        return Collections.unmodifiableList(subCategories);
    }

    public void setSubCategories(
        final List<CategorizationTreeNode> subCategories
    ) {
        this.subCategories = new ArrayList<>(subCategories);
    }
    
    
    
}
