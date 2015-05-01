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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.libreccm.core.CcmObject;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "categorizations")
public class Categorization implements Serializable {

    private static final long serialVersionUID = 201504301320L;

    @Id
    @Column(name = "categorization_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long categorizationId;

    @ManyToOne
    private Category category;

    @ManyToOne
    private CcmObject categorizedObject;

    @Column(name = "category_order")
    private long categoryOrder;

    @Column(name = "object_order")
    private long objectOrder;

    public long getCategorizationId() {
        return categorizationId;
    }

    public void setCategorizationId(final long categorizationId) {
        this.categorizationId = categorizationId;
    }

    public Category getCategory() {
        return category;
    }

    protected void setCategory(final Category category) {
        this.category = category;
    }

    public CcmObject getCategorizedObject() {
        return categorizedObject;
    }

    protected void setCategorizedObject(final CcmObject categorizedObject) {
        this.categorizedObject = categorizedObject;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash
            = 89 * hash + (int) (categorizationId ^ (categorizationId >>> 32));
        hash = 89 * hash + Objects.hashCode(category);
        hash = 89 * hash + Objects.hashCode(categorizedObject);
        hash = 89 * hash + (int) (categoryOrder ^ (categoryOrder >>> 32));
        hash = 89 * hash + (int) (objectOrder ^ (objectOrder >>> 32));
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
        if (categoryOrder != other.getCategoryOrder()) {
            return false;
        }
        return objectOrder == other.getObjectOrder();
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
                                 + "categoryOrder = %d, "
                                 + "objectOrder = %d"
                                 + "%s }",
                             super.toString(),
                             categorizationId,
                             Objects.toString(category),
                             Objects.toString(categorizedObject),
                             categoryOrder,
                             objectOrder);
    }

}
