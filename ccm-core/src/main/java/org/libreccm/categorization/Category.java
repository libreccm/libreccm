/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.categorization;

import org.libreccm.core.CcmObject;

import java.io.Serializable;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.libreccm.l10n.LocalizedString;

import java.util.Collections;
import java.util.Objects;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "categories")
public class Category extends CcmObject implements Serializable {

    private static final long serialVersionUID = -7250208963391878547L;

    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "category_titles",
                               joinColumns = {
                                   @JoinColumn(name = "object_id")}
        ))
    private LocalizedString title;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "category_descriptions",
                               joinColumns = {
                                   @JoinColumn(name = "object_id")}
        ))
    private LocalizedString description;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "visible")
    private boolean visible;

    @Column(name = "abstract_category")
    private boolean abstractCategory;

    @OneToMany(mappedBy = "category")
    private List<Categorization> objects;

    @OneToMany(mappedBy = "parent_category")
    private List<Category> subCategories;

    @ManyToOne
    private Category parentCategory;

    @Column(name = "category_order")
    private long categoryOrder;

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

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
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

    public List<Categorization> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    protected void setObjects(final List<Categorization> objects) {
        this.objects = objects;
    }

    protected void addObject(final Categorization object) {
        objects.add(object);
    }

    protected void removeObject(final Categorization object) {
        objects.remove(object);
    }

    public List<Category> getSubCategories() {
        return Collections.unmodifiableList(subCategories);
    }

    public void setSubCategories(final List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    protected void addSubCategory(final Category category) {
        subCategories.add(category);
    }

    protected void removeSubCategory(final Category category) {
        subCategories.remove(category);
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(final Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(final long categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(uniqueId);
        hash = 23 * hash + Objects.hashCode(name);
        hash = 23 * hash + Objects.hashCode(title);
        hash = 23 * hash + Objects.hashCode(description);
        hash = 23 * hash + (enabled ? 1 : 0);
        hash = 23 * hash + (visible ? 1 : 0);
        hash = 23 * hash + (abstractCategory ? 1 : 0);
        hash = 23 * hash + Objects.hashCode(parentCategory);
        hash = 23 * hash + (int) (categoryOrder ^ (categoryOrder >>> 32));
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Category other = (Category) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(uniqueId, other.getUniqueId())) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (enabled != other.isEnabled()) {
            return false;
        }
        if (visible != other.isVisible()) {
            return false;
        }
        if (abstractCategory != other.isAbstractCategory()) {
            return false;
        }
        if (!Objects.equals(parentCategory, other.getParentCategory())) {
            return false;
        }
        return categoryOrder == other.getCategoryOrder();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Category;
    }

    @Override
    public String toString(final String data) {
        return String.format(", uniqueId = %s, "
                                 + "name = \"%s\", "
                                 + "title = %s, "
                                 + "enabled = %b, "
                                 + "visible = %b, "
                                 + "abstractCategory = %b, "
                                 + "parentCategory = %s, "
                                 + "categoryOrder = %d%s",
                             uniqueId,
                             name,
                             title.toString(),
                             enabled,
                             visible,
                             abstractCategory,
                             parentCategory,
                             categoryOrder,
                             data);
    }

}
