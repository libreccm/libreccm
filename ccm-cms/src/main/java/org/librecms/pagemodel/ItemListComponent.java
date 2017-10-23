/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.pagemodel;

import org.libreccm.pagemodel.ComponentModel;
import org.librecms.contentsection.ContentItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * A component for displaying the list of {@link ContentItem}s assigned to a
 * Category.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "ITEM_LIST_COMPONENTS", schema = DB_SCHEMA)
public class ItemListComponent extends ComponentModel {

    private static final long serialVersionUID = -8058080493341203684L;

    /**
     * Should the list show also items assigned to sub categories?
     */
    @Column(name = "DESCINDING")
    private boolean descending;

    /**
     * Include only items of the specified type into the list. This must be a
     * subtype of {@link ContentItem}.
     */
    @Column(name = "LIMIT_TO_TYPE")
    private String limitToType;

    /**
     * Maximum number of items shown on one page.
     */
    @Column(name = "PAGE_SIZE")
    private int pageSize;

    /**
     * Order the list by this properties. Warning: All items must have the
     * properties listed here, otherwise an error will occur!
     */
    @ElementCollection
    @CollectionTable(name = "ITEM_LIST_ORDER",
                     schema = DB_SCHEMA,
                     joinColumns = {
                         @JoinColumn(name = "ITEM_LIST_ID")
                     })
    @Column(name = "LIST_ORDER")
    private List<String> listOrder;

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(final boolean descending) {
        this.descending = descending;
    }

    public String getLimitToType() {
        return limitToType;
    }

    public void setLimitToTypes(final String limitToType) {
        this.limitToType = limitToType;
    }

    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }
    
    public List<String> getListOrder() {
        return Collections.unmodifiableList(listOrder);
    }

    public void addListOrder(final String order) {
        listOrder.add(order);
    }

    public void removeListOrder(final String order) {
        listOrder.remove(order);
    }

    public void setListOrder(final List<String> listOrder) {
        this.listOrder = new ArrayList<>(listOrder);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 41 * hash + (descending ? 1 : 0);
        hash = 41 * hash + Objects.hashCode(limitToType);
        hash = 41 * hash + Objects.hashCode(listOrder);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ItemListComponent)) {
            return false;
        }
        final ItemListComponent other = (ItemListComponent) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (descending != other.isDescending()) {
            return false;
        }
        if (!Objects.equals(limitToType, other.getLimitToType())) {
            return false;
        }
        return Objects.equals(listOrder, other.getListOrder());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ItemListComponent;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", descending = %b, "
                                                + "limitToTypes = %s, "
                                                + "listOrder = %s%s",
                                            descending,
                                            limitToType,
                                            Objects.toString(listOrder),
                                            data));
    }

}
