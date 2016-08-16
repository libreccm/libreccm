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

import java.util.ArrayList;
import java.util.Collections;
import org.libreccm.categorization.Category;
import org.libreccm.workflow.WorkflowTemplate;
import org.librecms.lifecycle.LifecycleDefinition;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.libreccm.categorization.Categorization;
import org.librecms.CmsConstants;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemManager {

    @Inject
    private ContentItemRepository contentItemRepo;

    /**
     * Creates a new content item in the provided content section and folder
     * with the default lifecycle and workflow.
     *
     * The folder must be a subfolder of the
     * {@link ContentSection#rootDocumentsFolder} of the provided content
     * section. Otherwise an {@link IllegalArgumentException} is thrown.
     *
     * @param <T> The type of the content item.
     * @param name The name (URL stub) of the new content item.
     * @param section The content section in which the item is generated.
     * @param folder The folder in which in the item is stored.
     * @param type The type of the new content item.
     *
     * @return The new content item.
     */
    public <T extends ContentItem> T createContentItem(
            final String name,
            final ContentSection section,
            final Category folder,
            final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new content item in the provided content section and folder
     * with the provided lifecycle and workflow.
     *
     * The folder must be a subfolder of the
     * {@link ContentSection#rootDocumentsFolder} of the provided content
     * section. Otherwise an {@link IllegalArgumentException} is thrown.
     *
     * Likewise the provided {@link LifecycleDefinition} and
     * {@link WorkflowTemplate} must be defined in the provided content section.
     * Otherwise an {@link IllegalArgumentException} is thrown.
     *
     * @param <T> The type of the content item.
     * @param name The name (URL stub) of the new content item.
     * @param section The content section in which the item is generated.
     * @param folder The folder in which in the item is stored.
     * @param workflowTemplate
     * @param lifecycleDefinition
     * @param type The type of the new content item.
     *
     * @return The new content item.
     */
    public <T extends ContentItem> T createContentItem(
            final String name,
            final ContentSection section,
            final Category folder,
            final WorkflowTemplate workflowTemplate,
            final LifecycleDefinition lifecycleDefinition,
            final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * Moves a content item to another folder in the same content section. This
     * only moves the draft version of the item. The live version is moved after
     * a the item is republished.
     *
     * @param item The item to move.
     * @param targetFolder The folder to which the item is moved.
     */
    public void move(final ContentItem item, final Category targetFolder) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates an copy of the draft version of the item in the provided
     * {@code targetFolder}.
     *
     * @param item The item to copy.
     * @param targetFolder The folder in which the copy is created. If the
     * target folder is the same folder as the folder of the original item an
     * index is appended to the name of the item.
     */
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
     *
     * @return {@code true} if the content item has a live version,
     * {@code false} if not.
     */
    public boolean isLive(final ContentItem item) {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieves the live version of the provided content item if any.
     *
     * @param <T> Type of the content item.
     * @param item The item of which the live version should be retrieved.
     * @param type Type of the content item.
     *
     * @return The live version of an item. If the item provided is already the
     * live version the provided item is returned, otherwise the live version is
     * returned. If there is no live version an empty {@link Optional} is
     * returned.
     */
    public <T extends ContentItem> Optional<T> getLiveVersion(
            final ContentItem item,
            final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieves the pending versions of an item if there are any.
     *
     * @param <T> Type of the content item to retrieve.
     * @param item The item of which the pending versions are retrieved.
     * @param type Type of the content item to retrieve.
     *
     * @return A list of the pending versions of the item.
     */
    public <T extends ContentItem> List<T> getPendingVersions(
            final ContentItem item,
            final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieves the draft version
     *
     * @param <T> Type of the item.
     * @param item The item of which the draft version is retrieved.
     * @param type Type of the item.
     *
     * @return The draft version of the provided content item. If the provided
     * item is the draft version the provided item is simply returned. Otherwise
     * the draft version is retrieved from the database and is returned. Each
     * content item has a draft version (otherwise something is seriously wrong
     * with the database) this method will
     * <b>never</b> return {@code null}.
     */
    public <T extends ContentItem> T getDraftVersion(final ContentItem item,
                                                     final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the path of an as String. The path of an item is the path of the
     * folder category the item is a member of concatenated with the name of the
     * item. The path is relative to the content section. For instance, the path
     * of an item in the folder category
     * {@code /research/computer-science/artifical-intelligence} and with the
     * name {@code neural-nets} has the path
     * {@code /research/computer-science/artificial-intelligence/neural-nets}.
     *
     * @param item The item which path is generated.
     * @return The path of the content item
     * @see #getItemPath(org.librecms.contentsection.ContentItem, boolean)
     */
    public String getItemPath(final ContentItem item) {
        return getItemPath(item, false);
    }

    /**
     * Return the path of an as String. The path of an item is the path of the
     * folder category the item is a member of concatenated with the name of the
     * item. The path is relative to the content section. For instance, the path
     * of an item in the folder category
     * {@code /research/computer-science/artifical-intelligence} and with the
     * name {@code neural-nets} has the path
     * {@code /research/computer-science/artificial-intelligence/neural-nets}.
     * If the parameter {@code withContentSection} is set to {@code true} the
     * the path will be prefixed with the name of the content section. For
     * instance if the item {@link neural-nets} is part of the content section
     * {@code info}, the path including the content section would be
     * {@link info:/research/computer-science/artificial-intelligence/neural-nets}.
     *
     * @param item The item which path is generated.
     * @param withContentSection Wether to include the content section into the
     * path.
     * @return The path of the content item
     * @see #getItemPath(org.librecms.contentsection.ContentItem, boolean)
     */
    public String getItemPath(final ContentItem item,
                              final boolean withContentSection) {
        final List<Categorization> result = item.getCategories().stream().
                filter(categorization -> CmsConstants.CATEGORIZATION_TYPE_FOLDER.
                        equals(categorization.getType()))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return item.getDisplayName();
        } else {
            final List<String> tokens = new ArrayList<>();
            tokens.add(item.getDisplayName());

            Category current = result.get(0).getCategory();
            tokens.add(current.getName());

            while (current.getParentCategory() != null) {
                current = current.getParentCategory();
                tokens.add(current.getName());
            }

            Collections.reverse(result);
            final String path = String.join("/", tokens);

            if (withContentSection) {
                final String sectionName = item.getContentType().
                        getContentSection().getDisplayName();
                return String.format(
                        "%s/%s", sectionName, path);
            } else {
                return String.format("/%s", path);
            }
        }
    }
    
    /**
     * Creates as list of the folders in which is item is placed.
     * 
     * @param item
     * @return 
     */
    public List<Category> getItemFolders(final ContentItem item) {
        final List<Categorization> result = item.getCategories().stream().
                filter(categorization -> CmsConstants.CATEGORIZATION_TYPE_FOLDER.
                        equals(categorization.getType()))
                .collect(Collectors.toList());
        
        final List<Category> folders = new ArrayList<>();
        if (!result.isEmpty()) {
           Category current = result.get(0).getCategory();
           folders.add(current);
           
           while(current.getParentCategory()!= null) {
               current = current.getParentCategory();
               folders.add(current);
           }
           
           Collections.reverse(folders);
           return folders;
        } 

        return folders;
    }

}
