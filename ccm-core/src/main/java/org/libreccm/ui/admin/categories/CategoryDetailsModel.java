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
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.ui.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    @Inject
    private DomainRepository domainRepository;
    
    @Inject
    private GlobalizationHelper globalizationHelper;

    private long categoryId;

    private String uuid;

    private String uniqueId;

    private String name;

    private String path;

    private Map<String, String> title;

    private List<String> unusedTitleLocales;

    private Map<String, String> description;

    private List<String> unusedDescriptionLocales;

    private boolean enabled;

    private boolean visible;

    private boolean abstractCategory;

    private List<CategoryNodeModel> subCategories;

    private CategoryNodeModel parentCategory;

    private CategoryPathModel categoryPath;

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

    public List<String> getUnusedTitleLocales() {
        return Collections.unmodifiableList(unusedTitleLocales);
    }

    public boolean hasUnusedTitleLocales() {
        return !unusedTitleLocales.isEmpty();
    }

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public List<String> getUnusedDescriptionLocales() {
        return Collections.unmodifiableList(unusedDescriptionLocales);
    }

    public boolean hasUnusedDescriptionLocales() {
        return !unusedDescriptionLocales.isEmpty();
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

    public CategoryPathModel getCategoryPath() {
        return categoryPath;
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

        final List<Locale> availableLocales = globalizationHelper
            .getAvailableLocales();
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
        final Set<Locale> titleLocales = category
            .getTitle()
            .getAvailableLocales();
        unusedTitleLocales = availableLocales
            .stream()
            .filter(locale -> !titleLocales.contains(locale))
            .map(Locale::toString)
            .sorted()
            .collect(Collectors.toList());

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
        final Set<Locale> descriptionLocales = category
            .getDescription()
            .getAvailableLocales();
        unusedDescriptionLocales = availableLocales
            .stream()
            .filter(locale -> !descriptionLocales.contains(locale))
            .map(Locale::toString)
            .sorted()
            .collect(Collectors.toList());

        enabled = category.isEnabled();
        visible = category.isVisible();
        abstractCategory = category.isAbstractCategory();
        subCategories = category
            .getSubCategories()
            .stream()
            .map(this::buildCategoryNodeModel)
            .sorted()
            .collect(Collectors.toList());
        if (category.getParentCategory() != null) {
            parentCategory
                = buildCategoryNodeModel(category.getParentCategory());
        }
        categoryPath = buildCategoryPathModel(category);
        categoryOrder = category.getCategoryOrder();
    }

    private DomainNodeModel buildDomainNodeModel(final Domain domain) {
        final DomainNodeModel model = new DomainNodeModel();
        model.setDomainId(domain.getObjectId());
        model.setUuid(domain.getUuid());
        model.setDomainKey(domain.getDomainKey());

        return model;
    }

    private CategoryNodeModel buildCategoryNodeModel(final Category category) {
        final CategoryNodeModel model = new CategoryNodeModel();
        model.setCategoryId(category.getObjectId());
        model.setUuid(category.getUuid());
        model.setUniqueId(category.getUniqueId());
        model.setName(category.getName());
        model.setPath(categoryManager.getCategoryPath(category));
        model.setCategoryOrder(category.getCategoryOrder());
        model.setEnabled(category.isEnabled());
        model.setVisible(category.isVisible());
        model.setAbstractCategory(category.isAbstractCategory());
        return model;
    }

    private CategoryPathModel buildCategoryPathModel(final Category category) {
        return buildCategoryPathModel(category, new CategoryPathModel());
    }

    private CategoryPathModel buildCategoryPathModel(
        final Category category,
        final CategoryPathModel categoryPathModel
    ) {
        categoryPathModel.addCategoryAtBegin(buildCategoryNodeModel(category));
        final Category parent = category.getParentCategory();
        if (parent == null) {
            final Optional<Domain> domain = domainRepository
                .findByRootCategory(category);
            if (domain.isPresent()) {
                categoryPathModel.setDomain(buildDomainNodeModel(domain.get()));
            }
            return categoryPathModel;
        } else {
            return buildCategoryPathModel(parent, categoryPathModel);
        }
    }

}
