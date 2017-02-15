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

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.libreccm.core.CcmObject;
import org.libreccm.portation.Portable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

import org.libreccm.security.Relation;

/**
 * Association class describing the association between a category and an
 * object. Instances of these class should not created manually. The methods
 * provided by the {@link CategoryManager} take care of that.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @apiviz.has org.libreccm.core.CcmObject
 */
@Entity
@Table(name = "CATEGORIZATIONS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
            name = "Categorization.find",
            query = "SELECT c FROM Categorization c "
                            + "WHERE c.category = :category "
                            + "AND c.categorizedObject = :object")
    ,
    @NamedQuery(
            name = "Categorization.isAssignedTo",
            query = "SELECT (CASE WHEN COUNT(c) > 0 THEN true ELSE false END) "
                            + "FROM Categorization c "
                            + "WHERE c.category = :category "
                            + "AND c.categorizedObject = :object")
    ,
    @NamedQuery(
            name = "Categorization.isAssignedToWithType",
            query = "SELECT (CASE WHEN COUNT(c) > 0 THEN true ELSE false END) "
                            + "FROM Categorization c "
                            + "WHERE c.category = :category "
                            + "AND c.categorizedObject = :object "
                            + "AND c.type = :type")
    ,
    @NamedQuery(
            name = "Categorization.findIndexObject",
            query = "SELECT c.categorizedObject FROM Categorization c "
                            + "WHERE c.category = :category "
                            + "AND c.index = TRUE")
    ,
    @NamedQuery(
            name = "Categorization.findIndexObjectCategorization",
            query = "SELECT c FROM Categorization c "
                            + "WHERE c.category = :category "
                            + "AND c.index = TRUE"
    )
    ,
    @NamedQuery(
            name = "Categorization.hasIndexObject",
            query = "SELECT (CASE WHEN COUNT(c.categorizedObject) > 0 THEN true "
                    + "ELSE false END) "
                            + "FROM Categorization c "
                            + "WHERE c.category = :category "
                            + "AND c.index = TRUE")
})
public class Categorization implements Serializable, Relation, Portable {

    private static final long serialVersionUID = 201504301320L;

    /**
     * The ID of the categorisation object.
     */
    @Id
    @Column(name = "CATEGORIZATION_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long categorizationId;

    /**
     * The category to which this {@code Categorization} object belongs.
     */
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    @JsonBackReference(value = "category-categorization")
    private Category category;

    /**
     * The categorised object.
     */
    @ManyToOne
    @JoinColumn(name = "OBJECT_ID")
    @JsonBackReference(value = "object-categorization")
    private CcmObject categorizedObject;

    /**
     * If the categorised object is the index object of the category this
     * property is set to {@code true}.
     */
    @Column(name = "CATEGORY_INDEX")
    private boolean index;

    /**
     * Defines the order in which the categories assigned the the categorised
     * object are shown.
     */
    @Column(name = "CATEGORY_ORDER")
    private long categoryOrder;

    /**
     * Defines the order in which the objects assigned to the category are
     * shown.
     */
    @Column(name = "OBJECT_ORDER")
    private long objectOrder;

    /**
     * Used to distinguish between different kinds of categorisations. Used for
     * example by in the ccm-cms module to distinguish folders from categories.
     */
    @Column(name = "TYPE", length = 255, nullable = true)
    private String type;

    public Categorization() {
        index = false;
        categoryOrder = 0;
        objectOrder = 0;
    }

    public long getCategorizationId() {
        return categorizationId;
    }

    public void setCategorizationId(final long categorizationId) {
        this.categorizationId = categorizationId;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public CcmObject getOwner() {
        return getCategory();
    }

    protected void setCategory(final Category category) {
        this.category = category;
    }

    public CcmObject getCategorizedObject() {
        return categorizedObject;
    }

    @Override
    public CcmObject getRelatedObject() {
        return getCategorizedObject();
    }

    protected void setCategorizedObject(final CcmObject categorizedObject) {
        this.categorizedObject = categorizedObject;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(final boolean index) {
        this.index = index;
    }

    public long getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(final long categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    public long getObjectOrder() {
        return objectOrder;
    }

    public void setObjectOrder(final long objectOrder) {
        this.objectOrder = objectOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash
        = 89 * hash + (int) (categorizationId ^ (categorizationId >>> 32));
        hash = 89 * hash + Objects.hashCode(category);
        hash = 89 * hash + Objects.hashCode(categorizedObject);
        hash = 89 * hash + (index ? 1 : 0);
        hash = 89 * hash + (int) (categoryOrder ^ (categoryOrder >>> 32));
        hash = 89 * hash + (int) (objectOrder ^ (objectOrder >>> 32));
        hash = 89 * hash + Objects.hashCode(type);
        return hash;
    }

    @Override
    //No chance to make this method less complex, therefore suppress warning
    @SuppressWarnings("PMD.NPathComplexity")
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Categorization)) {
            return false;
        }
        final Categorization other = (Categorization) obj;
        if (categorizationId != other.getCategorizationId()) {
            return false;
        }
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(category, other.getCategory())) {
            return false;
        }
        if (!Objects.equals(categorizedObject, other.getCategorizedObject())) {
            return false;
        }

        if (index != other.isIndex()) {
            return false;
        }

        if (categoryOrder != other.getCategoryOrder()) {
            return false;
        }

        if (objectOrder != other.getObjectOrder()) {
            return false;
        }

        return Objects.equals(type, other.getType());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Categorization;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                     + "categorizationId = %d, "
                                     + "category = %s, "
                                     + "categorizedObject = %s, "
                                     + "index = %b,"
                                     + "categoryOrder = %d, "
                                     + "objectOrder = %d"
                                     + "type = %s"
                                     + "%s }",
                             super.toString(),
                             categorizationId,
                             Objects.toString(category),
                             Objects.toString(categorizedObject),
                             index,
                             categoryOrder,
                             objectOrder,
                             type,
                             data);
    }

}
