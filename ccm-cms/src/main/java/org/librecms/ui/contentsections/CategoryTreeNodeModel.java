/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryTreeNodeModel {

    private String path;

    private String title;

    private List<CategoryTreeNodeModel> subCategories;

    public CategoryTreeNodeModel() {
        subCategories = new ArrayList<>();
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public List<CategoryTreeNodeModel> getSubCategories() {
        return Collections.unmodifiableList(subCategories);
    }

    public void setSubCategories(
        final List<CategoryTreeNodeModel> subCategories
    ) {
        this.subCategories = new ArrayList<>(subCategories);
    }

}
