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
package org.libreccm.admin.ui;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PageModelsTableRow implements Serializable, 
                                           Comparable<PageModelsTableRow> {

    private static final long serialVersionUID = -6995542858134206603L;

    private long pageModelId;
    private String name;
    private String title;
    private String description;
    private boolean published;

    public long getPageModelId() {
        return pageModelId;
    }

    public void setPageModelId(final long pageModelId) {
        this.pageModelId = pageModelId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished(final boolean published) {
        this.published = published;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int) (pageModelId ^ (pageModelId >>> 32));
        hash = 17 * hash + Objects.hashCode(name);
        hash = 17 * hash + Objects.hashCode(title);
        hash = 17 * hash + Objects.hashCode(description);
        hash = 17 * hash + (published ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PageModelsTableRow)) {
            return false;
        }
        final PageModelsTableRow other = (PageModelsTableRow) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        if (pageModelId != other.getPageModelId()) {
            return false;
        }
        if (published != other.isPublished()) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        return Objects.equals(description, other.getDescription());
    }
    
    public boolean canEqual(final Object obj) {
        return obj instanceof PageModelsTableRow;
    }
    
    @Override
    public int compareTo(final PageModelsTableRow other) {
        
        int result = title.compareTo(other.getTitle());
        
        if (result == 0) {
            result = name.compareTo(other.getName());
        }
        
        if (result == 0) {
            result = Boolean.compare(published, other.isPublished());
        }
        
        return result;
    }
    
    @Override
    public final String toString() {
        return toString("");
    }
    
    public String toString(final String data) {
        
        return String.format("%s{ "
            + "pageModelId = %d, "
            + "name = \"%s\", "
            + "published = %b, "
            + "title = \"%s\", "
            + "description = \"%s\"%s"
            + " }",
                             super.toString(),
                             pageModelId,
                             name,
                             published,
                             title,
                             description,
                             data);
        
    }
}
