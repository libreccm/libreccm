/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryModel {

    private long categoryId;

    private String uuid;

    private String uniqueId;

    private String name;

    private String path;

    private String title;

    private Map<String, String> localizedTitles;

    private String description;

    private Map<String, String> localizedDescriptions;

    private boolean enabled;

    private boolean visible;

    private boolean abstractCategory;

    private List<CategoryModel> subCategories;

    private List<CategorizedObjectModel> objects;

    private long categoryOrder;

    private List<String> unusedTitleLocales;

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
