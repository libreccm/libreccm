/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

import org.libreccm.categorization.Category;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemManager {

    public void move(final ContentItem item, final Category targetFolder) {
        throw new UnsupportedOperationException();
    }

    public void copy(final ContentItem item, final Category targetFolder) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a live version of content item or updates the live version of a
     * content item if there already a live version.
     *
     * @param item The content item to publish.
     *
     * @return The published content item.
     */
    public ContentItem publish(final ContentItem item) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unpublishes a content item by deleting its live version if any.
     *
     * @param item
     */
    public void unpublish(final ContentItem item) {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines if a content item has a live version.
     *
     * @param item The item 
     * @return {@code true} if the content item has a live version,
     *         {@code false} if not.
     */
    public boolean isLive(final ContentItem item) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Retrieves the live version of the provided content item if any.
     * 
     * @param <T>
     * @param item
     * @param type
     * @return 
     */
    public <T extends ContentItem> Optional<T> getLiveVersion(
        final ContentItem item,
        final Class<T> type) {
        throw new UnsupportedOperationException();
    }
    
    public <T extends ContentItem> T getDraftVersion(final ContentItem item) {
        throw new UnsupportedOperationException();
    }

}
