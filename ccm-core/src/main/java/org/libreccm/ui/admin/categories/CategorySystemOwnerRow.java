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

/**
 * Data for a row in the table of owner applications of a category system.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorySystemOwnerRow 
    implements Comparable<CategorySystemOwnerRow>{
    
    private long ownershipId;
    
    private String uuid;
    
    private String ownerAppName;
    
    private String context;
    
    private long ownerOrder;

    public long getOwnershipId() {
        return ownershipId;
    }

    void setOwnershipId(final long ownershipId) {
        this.ownershipId = ownershipId;
    }

    public String getUuid() {
        return uuid;
    }

    void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getOwnerAppName() {
        return ownerAppName;
    }

    void setOwnerAppName(final String ownerAppName) {
        this.ownerAppName = ownerAppName;
    }

    public String getContext() {
        return context;
    }

    void setContext(final String context) {
        this.context = context;
    }

    public long getOwnerOrder() {
        return ownerOrder;
    }

    void setOwnerOrder(final long ownerOrder) {
        this.ownerOrder = ownerOrder;
    }
    
    @Override
    public int compareTo(final CategorySystemOwnerRow other) {
        return Long.compare(ownerOrder, other.getOwnerOrder());
    }
    
}
