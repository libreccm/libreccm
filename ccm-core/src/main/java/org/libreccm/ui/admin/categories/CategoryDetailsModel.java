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
import org.libreccm.categorization.CategoryManager;
import org.libreccm.ui.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CategoryDetailsModel")
public class CategoryDetailsModel {

    @Inject
    private CategoryManager categoryManager;

    private long categoryId;

    private String uuid;

    private String uniqueId;

    private String name;

    private String path;

    private Map<String, String> title;

    private Map<String, String> description;

    private boolean enabled;

    private boolean visible;

    private boolean abstractCategory;

    private List<CategoryNodeModel> subCategories;

    private CategoryNodeModel parentCategory;

    private long categoryOrder;

    private final List<Message> messages;

    private Set<String> invalidFields;

    public CategoryDetailsModel() {
        this.messages = new ArrayList<>();
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getIdentifier() {
        return String.format("ID-%d", categoryId);
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

    public String getPath() {
        return path;
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

    protected void setParentCategory(final Category parent) {
        parentCategory = buildCategoryNodeModel(parent);
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    public boolean isNew() {
        return categoryId == 0;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(final Message message) {
        messages.add(message);
    }

    public Set<String> getInvalidFields() {
        return Collections.unmodifiableSet(invalidFields);
    }

    protected void addInvalidField(final String invalidField) {
        invalidFields.add(invalidField);
    }

    protected void setInvalidFields(final Set<String> invalidFields) {
        this.invalidFields = new HashSet<>(invalidFields);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void setCategory(final Category category) {
        Objects.requireNonNull(category);

        categoryId = category.getObjectId();
        uuid = category.getUuid();
        uniqueId = category.getUniqueId();
        name = category.getName();
        path = categoryManager.getCategoryPath(category);
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
            .map(this::buildCategoryNodeModel)
            .sorted()
            .collect(Collectors.toList());
        parentCategory = buildCategoryNodeModel(category.getParentCategory());
        categoryOrder = category.getCategoryOrder();
    }

    private CategoryNodeModel buildCategoryNodeModel(final Category category) {
        final CategoryNodeModel model = new CategoryNodeModel();
        model.setCategoryId(category.getObjectId());
        model.setUuid(category.getUuid());
        model.setUniqueId(category.getUniqueId());
        model.setName(category.getName());
        model.setPath(categoryManager.getCategoryPath(category));
        model.setCategoryOrder(category.getCategoryOrder());
        return model;
    }

}
