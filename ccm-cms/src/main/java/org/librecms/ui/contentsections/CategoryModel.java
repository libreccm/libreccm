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
package org.librecms.ui.contentsections;

import org.libreccm.categorization.Category;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model for the details view of a category.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryModel {

    /**
     * The ID of the category.
     */
    private long categoryId;

    /**
     * The UUID of the category.
     */
    private String uuid;

    /**
     * The unique ID of the category.
     */
    private String uniqueId;

    /**
     * The name of the category.
     */
    private String name;

    /**
     * The path of the category.
     */
    private String path;

    /**
     * The title of the category to display. This value is determined from
     * {@link Category#title} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String title;

    /**
     * The localized titles of the category.
     */
    private Map<String, String> localizedTitles;

    /**
     * The description of the category to display. This value is determined from
     * {@link Category#description} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String description;

    /**
     * The localized descriptions of the category.
     */
    private Map<String, String> localizedDescriptions;

    /**
     * Is the category enabled?
     */
    private boolean enabled;

     /**
     * Is the category visible?
     */
    private boolean visible;

     /**
     * Is the category an abstract category?
     */
    private boolean abstractCategory;

    /**
     * The sub categories of the category.
     */
    private List<CategoryModel> subCategories;

    /**
     * The objects assigned to which the category is assigned.
     */
    private List<CategorizedObjectModel> objects;

    /**
     * The order of the category.
     */
    private long categoryOrder;

    /**
     * Locales for which no localized title has been defined yet.
     */
    private List<String> unusedTitleLocales;

    /**
     * Locales for which no localized description has been defined yet.
     */
    private List<String> unusedDescriptionLocales;

    public CategoryModel() {
        subCategories = new ArrayList<>();
        objects = new ArrayList<>();
        localizedTitles = new HashMap<>();
        localizedDescriptions = new HashMap<>();
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public boolean isAbstractCategory() {
        return abstractCategory;
    }

    public void setAbstractCategory(final boolean abstractCategory) {
        this.abstractCategory = abstractCategory;
    }

    public List<CategoryModel> getSubCategories() {
        return Collections.unmodifiableList(subCategories);
    }

    public void setSubCategories(final List<CategoryModel> subCategories) {
        this.subCategories = new ArrayList<>(subCategories);
    }

    public List<CategorizedObjectModel> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public void setObjects(final List<CategorizedObjectModel> objects) {
        this.objects = new ArrayList<>();
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(final long categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    public boolean isHasUnusedTitleLocales() {
        return !unusedTitleLocales.isEmpty();
    }

    public Map<String, String> getLocalizedTitles() {
        return Collections.unmodifiableMap(localizedTitles);
    }

    public void setLocalizedTitles(final Map<String, String> localizedTitles) {
        this.localizedTitles = new HashMap<>(localizedTitles);
    }

    public Map<String, String> getLocalizedDescriptions() {
        return Collections.unmodifiableMap(localizedDescriptions);
    }

    public void setLocalizedDescriptions(
        final Map<String, String> localizedDescriptions
    ) {
        this.localizedDescriptions = new HashMap<>(localizedDescriptions);
    }

    public boolean isHasUnusedDescriptionLocales() {
        return !unusedDescriptionLocales.isEmpty();
    }

    public List<String> getUnusedTitleLocales() {
        return Collections.unmodifiableList(unusedTitleLocales);
    }

    public void setUnusedTitleLocales(final List<String> unusedTitleLocales) {
        this.unusedTitleLocales = new ArrayList<>(unusedTitleLocales);
    }

    public List<String> getUnusedDescriptionLocales() {
        return Collections.unmodifiableList(unusedDescriptionLocales);
    }

    public void setUnusedDescriptionLocales(
        final List<String> unusedDescriptionLocales
    ) {
        this.unusedDescriptionLocales = new ArrayList<>(
            unusedDescriptionLocales
        );
    }

}
