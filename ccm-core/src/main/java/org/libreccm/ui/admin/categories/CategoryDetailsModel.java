/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.categories;

import org.libreccm.categorization.Category;
import org.libreccm.ui.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CategoryDetailsModel")
public class CategoryDetailsModel {

    private long categoryId;

    private String uuid;

    private String uniqueId;

    private String name;

    private Map<String, String> title;

    private Map<String, String> description;

    private boolean enabled;

    private boolean visible;

    private boolean abstractCategory;

    private List<CategoryNodeModel> subCategories;

    private CategoryNodeModel parentCategory;

    private long categoryOrder;

    private final List<Message> messages;

    public CategoryDetailsModel() {
        this.messages = new ArrayList<>();
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getTitle() {
        return Collections.unmodifiableMap(title);
    }

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isAbstractCategory() {
        return abstractCategory;
    }

    public List<CategoryNodeModel> getSubCategories() {
        return Collections.unmodifiableList(subCategories);
    }

    public CategoryNodeModel getParentCategory() {
        return parentCategory;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    /**
     * Only for testing components
     * @return 
     */
    public Map<String, String> getOptions() {
        final Map<String, String> options = new TreeMap<>();
        options.put("alpha", "Option Alpha");
        options.put("bravo", "Option Bravo");
        options.put("charlie", "Option Charlie");
        options.put("delta", "Option Delta");
        options.put("echo", "Option Echo");
        return options;
    }
    
    public Set<String> getSelectedOptions() {
        final Set<String> selectedOptions = new HashSet<>();
        selectedOptions.add("delta");
        return selectedOptions;
    }
    
    public Set<String> getMultipleSelectedOptions() {
        final Set<String> selectedOptions = new HashSet<>();
        selectedOptions.add("delta");
        selectedOptions.add("bravo");
        return selectedOptions;
    }
    
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(final Message message) {
        messages.add(message);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void setCategory(final Category category) {
        Objects.requireNonNull(category);

        categoryId = category.getObjectId();
        uuid = category.getUuid();
        uniqueId = category.getUniqueId();
        name = category.getName();
        title = category
            .getTitle()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
        description = category
            .getDescription()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
        enabled = category.isEnabled();
        visible = category.isVisible();
        abstractCategory = category.isAbstractCategory();
        subCategories = category
            .getSubCategories()
            .stream()
            .map(CategoryNodeModel::new)
            .sorted()
            .collect(Collectors.toList());
        parentCategory = new CategoryNodeModel(category.getParentCategory());
        categoryOrder = category.getCategoryOrder();
    }

}
