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
import org.libreccm.pagemodel.PageModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * Basic {@link PageModel} component for displaying a content item. This class
 * is not indented for direct use. The subclasses should be used instead.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CONTENT_ITEM_COMPONENTS", schema = DB_SCHEMA)
public class ContentItemComponent extends ComponentModel {

    private static final long serialVersionUID = 4904530823926147281L;

    @ElementCollection
    @CollectionTable(name = "GREETING_ITEM_INCLUDED_PATHS",
                     schema = DB_SCHEMA,
                     joinColumns = {
                         @JoinColumn(name = "GREETING_ITEM_ID")
                     })
    private Set<String> includedPropertyPaths;

    @ElementCollection
    @CollectionTable(name = "GREETING_ITEM_EXCLUDED_PATHS",
                     schema = DB_SCHEMA,
                     joinColumns = {
                         @JoinColumn(name = "GREETING_ITEM_ID")
                     })
    private Set<String> excludedProperties;

    public ContentItemComponent() {
        includedPropertyPaths = new HashSet<>();
        excludedProperties = new HashSet<>();
    }

    public Set<String> getIncludedPropertyPaths() {
        return Collections.unmodifiableSet(includedPropertyPaths);
    }

    protected void setIncludedPropertyPaths(
        final Set<String> includePropertyPaths) {
        this.includedPropertyPaths = new HashSet<>(includePropertyPaths);
    }

    public void addIncludedPropertyPath(final String path) {
        includedPropertyPaths.add(path);
    }

    public void removeIncludedPropertyPath(final String path) {
        includedPropertyPaths.remove(path);
    }

    public Set<String> getExcludedPropertyPaths() {
        return Collections.unmodifiableSet(excludedProperties);
    }

    protected void setExcludedPropertyPaths(
        final Set<String> excludedProperties) {
        this.excludedProperties = new HashSet<>(excludedProperties);
    }

    public void addExcludedProperty(final String path) {
        excludedProperties.add(path);
    }

    public void removeExcludedProperty(final String path) {
        excludedProperties.remove(path);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 13 * hash + Objects.hashCode(includedPropertyPaths);
        hash = 13 * hash + Objects.hashCode(excludedProperties);
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
        if (!(obj instanceof ContentItemComponent)) {
            return false;
        }
        final ContentItemComponent other = (ContentItemComponent) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(includedPropertyPaths,
                            other.getIncludedPropertyPaths())) {
            return false;
        }
        return Objects.equals(excludedProperties,
                              other.getExcludedPropertyPaths());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ContentItemComponent;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String
            .format(", includedPropertyPaths = %s, "
                        + "excludedProperties = %s%s",
                    Objects.toString(includedPropertyPaths),
                    Objects.toString(excludedProperties),
                    data));
    }

}
