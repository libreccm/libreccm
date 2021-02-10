/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CategorySystemModel")
public class CategorySystemModel {

    private List<DomainListEntryModel> categorySystems;

    private DomainListEntryModel selectedCategorySystem;

    private CategoryTreeNodeModel categoryTree;

    private CategoryModel selectedCategory;
    
    public CategorySystemModel() {
        categorySystems = new ArrayList<>();
    }

    public List<DomainListEntryModel> getCategorySystems() {
        return Collections.unmodifiableList(categorySystems);
    }

    public void setCategorySystems(
        final List<DomainListEntryModel> categorySystems
    ) {
        this.categorySystems = new ArrayList<>(categorySystems);
    }

    public DomainListEntryModel getSelectedCategorySystem() {
        return selectedCategorySystem;
    }

    public void setSelectedCategorySystem(
        final DomainListEntryModel selectedCategorySystem
    ) {
        this.selectedCategorySystem = selectedCategorySystem;
    }

    public CategoryTreeNodeModel getCategoryTree() {
        return categoryTree;
    }

    public void setCategoryTree(final CategoryTreeNodeModel categoryTree) {
        this.categoryTree = categoryTree;
    }

    public CategoryModel getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(final CategoryModel selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

}
