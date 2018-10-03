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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.validator.constraints.NotBlank;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.RecursivePermissions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.libreccm.categorization.CategorizationConstants.CAT_XML_NS;
import static org.libreccm.core.CoreConstants.DB_SCHEMA;

import org.libreccm.imexport.Exportable;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * The category entity represents a single category. Each category is part of a
 * {@link Domain}. A category can be assigned to multiple {@link CcmObject}s.
 *
 * In the old structure the properties of this class were split between the
 * {@code Category} entity from {@code ccm-core} and the {@code Term} entity
 * from {@code ccm-ldn-terms}. This class unifies the properties of these two
 * classes.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @apiviz.composedOf org.libreccm.categorization.Categorization
 */
@Entity
@Table(name = "CATEGORIES", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "Category.topLevelCategories",
        query = "SELECT c FROM Category c WHERE c.parentCategory IS NULL")
    ,
    @NamedQuery(
        name = "Category.findByName",
        query = "SELECT c FROM Category c WHERE c.name = :name")
    ,
    @NamedQuery(
        name = "Category.findByUuid",
        query = "SELECT c FROM Category c WHERE c.uuid = :uuid")
    ,
    @NamedQuery(
        name = "Category.findParentCategory",
        query = "SELECT c.parentCategory FROM Category c WHERE c = :category")
    ,
    @NamedQuery(
        name = "Category.countAssignedCategories",
        query = "SELECT COUNT(c) FROM Categorization c "
                    + "WHERE c.categorizedObject = :object"
    )
    ,
    @NamedQuery(
        name = "Category.isCategorized",
        query = "SELECT (CASE WHEN COUNT(c) > 0 THEN true ELSE false END) "
                    + "FROM Categorization c "
                    + "WHERE c.categorizedObject = :object")
    ,
    @NamedQuery(
        name = "Category.countObjects",
        query = "SELECT COUNT(c) FROM Categorization c "
                    + "WHERE c.category = :category")
    ,
    @NamedQuery(
        name = "Category.hasObjects",
        query = "SELECT (CASE WHEN COUNT(c) > 0 THEN true ELSE false END) "
                    + "FROM Categorization c "
                    + "WHERE c.category = :category")
    ,
    @NamedQuery(
        name = "Category.countSubCategories",
        query = "SELECT COUNT(c) FROM Category c "
                    + "WHERE c.parentCategory =:category")
    ,
    @NamedQuery(
        name = "Category.hasSubCategories",
        query = "SELECT (CASE WHEN COUNT(c) > 0 THEN true ELSE false END) "
                    + "FROM Category c "
                    + "WHERE c.parentCategory = :category")
    ,
    @NamedQuery(
        name = "Category.findByNameAndParent",
        query = "SELECT c FROM Category c "
                    + "WHERE c.name = :name AND c.parentCategory = :parent")
    ,
    @NamedQuery(
        name = "Category.hasSubCategoryWithName",
        query = "SELECT (CASE WHEN COUNT(c) > 0 THEN true ELSE False END) "
                    + "FROM Category c "
                    + "WHERE c.name = :name AND c.parentCategory = :parent"
    )
})
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "Category.withSubCategoriesAndObjects",
        attributeNodes = {
            @NamedAttributeNode(value = "subCategories"
            ),}
    )
})
@XmlRootElement(name = "category", namespace = CAT_XML_NS)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = CategoryIdResolver.class,
                  property = "uuid")
public class Category extends CcmObject implements Serializable, Exportable {

    private static final long serialVersionUID = -7250208963391878547L;

    /**
     * A unique ID for the category. This ID will be the same even the same
     * category system/domain is used in different installations.
     */
    @Column(name = "UNIQUE_ID")
    @XmlElement(name = "unique-id", namespace = CAT_XML_NS)
    private String uniqueId;

    /**
     * The name of the category. This is used as URL stub, therefore only the
     * characters a to z, A to Z and 0 to 9 are allowed.
     */
    @Column(name = "NAME", nullable = false)
    @NotBlank
//    @Pattern(regexp = "[\\w-.]*")
    @XmlElement(name = "name", namespace = CAT_XML_NS)
    private String name;

    /**
     * The human readable and localisable title of the category.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CATEGORY_TITLES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    @XmlElement(name = "title", namespace = CAT_XML_NS)
    private LocalizedString title;

    /**
     * A localisable description of the category.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CATEGORY_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    @XmlElement(name = "description", namespace = CAT_XML_NS)
    private LocalizedString description;

    /**
     * Defines if the category is enabled. If the category is <em>not</em>
     * enabled, the category can't be used in any way.
     */
    @Column(name = "ENABLED")
    @XmlElement(name = "enabled", namespace = CAT_XML_NS)
    private boolean enabled;

    /**
     * Defines if the category is visible. A category which is <em>not</em>
     * visible should be only visible in the backend but not in the frontend.
     */
    @Column(name = "VISIBLE")
    @XmlElement(name = "visible", namespace = CAT_XML_NS)
    private boolean visible;

    /**
     * Defines if the category is abstract. It is not possible to add objects to
     * an abstract category.
     */
    @Column(name = "ABSTRACT_CATEGORY")
    @XmlElement(name = "abstract", namespace = CAT_XML_NS)
    private boolean abstractCategory;

    /**
     * The objects assigned to this category.
     */
    @RecursivePermissions
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @OrderBy("objectOrder ASC")
    @XmlElementWrapper(name = "objects", namespace = CAT_XML_NS)
    @JsonIgnore
    private List<Categorization> objects;

    /**
     * The sub categories of this category.
     */
    @RecursivePermissions
    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
    @OrderBy("categoryOrder ASC")
    @XmlElementWrapper(name = "subcategories", namespace = CAT_XML_NS)
    @XmlElement(name = "category")
    @JsonIgnore
    private List<Category> subCategories;

    /**
     * The parent category category of this category. Despite the root category
     * of domain every category has a parent category.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CATEGORY_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private Category parentCategory;

    /**
     * Numeric value to define the order of the categories.
     */
    @Column(name = "CATEGORY_ORDER")
    @XmlElement(name = "order", namespace = CAT_XML_NS)
    private long categoryOrder;

    public Category() {
        super();
        title = new LocalizedString();
        description = new LocalizedString();
        objects = new ArrayList<>();
        subCategories = new ArrayList<>();
        enabled = true;
        visible = true;
        abstractCategory = false;
        categoryOrder = 0;
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

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        Objects.requireNonNull(title);
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        Objects.requireNonNull(description);
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

    /**
     * Retrieves an <strong>unmodifiable</strong> list of the objects assigned
     * to this category. To manage the assigned objects use the methods provided
     * by the {@link CategoryManager}.
     *
     * @return An unmodifiable list of objects assigned to this category.
     */
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

    /**
     * Retrieves an <strong>unmodifiable</strong> list of the sub categories of
     * this category. To manage the assigned objects use the methods provided by
     * the {@link CategoryManager}.
     *
     * @return An unmodifiable list of sub categories of this category.
     */
    public List<Category> getSubCategories() {
        return Collections.unmodifiableList(subCategories);
    }

    /**
     * <strong>Internal</strong> setter for the list of sub categories.
     *
     * @param subCategories The list of sub categories.
     */
    protected void setSubCategories(final List<Category> subCategories) {
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
        int hash = super.hashCode();
        hash = 23 * hash + Objects.hashCode(uniqueId);
        hash = 23 * hash + Objects.hashCode(name);
        hash = 23 * hash + Objects.hashCode(title);
        hash = 23 * hash + Objects.hashCode(description);
        hash = 23 * hash + (enabled ? 1 : 0);
        hash = 23 * hash + (visible ? 1 : 0);
        hash = 23 * hash + (abstractCategory ? 1 : 0);
//        hash = 23 * hash + Objects.hashCode(parentCategory);
        hash = 23 * hash + (int) (categoryOrder ^ (categoryOrder >>> 32));
        return hash;
    }

    @Override
    @SuppressWarnings({"PMD.NPathComplexity",
                       "PMD.CyclomaticComplexity",
                       "PMD.StdCyclomaticComplexity",
                       "PMD.ModifiedCyclomaticComplexity"})
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Category)) {
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
//        if (!Objects.equals(parentCategory, other.getParentCategory())) {
//            return false;
//        }
        return categoryOrder == other.getCategoryOrder();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Category;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", uniqueId = %s, "
                                                + "name = \"%s\", "
                                                + "title = %s, "
                                                + "enabled = %b, "
                                                + "visible = %b, "
                                                + "abstractCategory = %s, "
                                                + "categoryOrder = %d%s",
                                            uniqueId,
                                            name,
                                            Objects.toString(title),
                                            enabled,
                                            visible,
                                            abstractCategory,
                                            categoryOrder,
                                            data));
    }

}
