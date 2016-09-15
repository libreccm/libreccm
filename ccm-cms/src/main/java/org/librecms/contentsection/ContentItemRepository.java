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

import org.libreccm.auditing.AbstractAuditedEntityRepository;
import org.libreccm.categorization.Category;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

/**
 * Repository for content items.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemRepository
        extends AbstractAuditedEntityRepository<Long, ContentItem> {

    @Inject
    private CcmObjectRepository ccmObjectRepo;

    @Override
    public Long getEntityId(final ContentItem item) {
        return item.getObjectId();
    }

    @Override
    public Class<ContentItem> getEntityClass() {
        return ContentItem.class;
    }

    @Override
    public boolean isNew(final ContentItem item) {
        return ccmObjectRepo.isNew(item);
    }
    
    @Override
    public void initNewEntity(final ContentItem item) {
        final String uuid = UUID.randomUUID().toString();
        item.setUuid(uuid);
        if (item.getItemUuid() == null || item.getItemUuid().isEmpty()) {
            item.setItemUuid(uuid);
        }
    }

    /**
     * Finds a content item by is id.
     *
     * @param itemId The id of item to retrieve.
     *
     * @return The content item identified by the provided {@code itemId} or
     * nothing if there is such content item.
     */
    public Optional<ContentItem> findById(final long itemId) {
        final Optional<CcmObject> result = ccmObjectRepo.findObjectById(itemId);
        if (result.isPresent() && result.get() instanceof ContentItem) {
            return Optional.of((ContentItem) result.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds a content item by its ID and ensures that is a the requested type.
     *
     * @param <T> The type of the content item.
     * @param itemId The id of item to retrieve.
     * @param type The type of the content item.
     *
     * @return The content item identified by the provided id or an empty
     * {@link Optional} if there is no such item or if it is not of the
     * requested type.
     */
    @SuppressWarnings("unchecked")
    public <T extends ContentItem> Optional<T> findById(final long itemId,
                                                        final Class<T> type) {
        final CcmObject result = ccmObjectRepo.findById(itemId);
        if (result.getClass().isAssignableFrom(type)) {
            return Optional.of((T) result);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds a content item by is UUID.
     *
     * @param uuid The id of item to retrieve.
     *
     * @return The content item identified by the provided {@code uuid} or
     * nothing if there is such content item.
     */
    public Optional<ContentItem> findByUuid(final String uuid) {
        final Optional<CcmObject> result = ccmObjectRepo.findObjectByUuid(uuid);
        if (result.isPresent() && result.get() instanceof ContentItem) {
            return Optional.of((ContentItem) result.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds a content item by its UUID and ensures that is a the requested
     * type.
     *
     * @param <T> The type of the content item.
     * @param uuid The UUID of item to retrieve.
     * @param type The type of the content item.
     *
     * @return The content item identified by the provided UUID or an empty
     * {@link Optional} if there is no such item or if it is not of the
     * requested type.
     */
    @SuppressWarnings("unchecked")
    public <T extends ContentItem> Optional<T> findByUuid(final String uuid,
                                                          final Class<T> type) {
        final Optional<CcmObject> result = ccmObjectRepo.findObjectByUuid(uuid);

        if (result.isPresent() 
            && result.get().getClass().isAssignableFrom(type)) {
            return Optional.of((T) result.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds all content items of a specific type.
     *
     * @param <T> The type of the items.
     * @param type The type of the items.
     *
     * @return A list of all content items of the requested type.
     */
    @SuppressWarnings("unchecked")
    public <T extends ContentItem> List<T> findByType(final Class<T> type) {
        final TypedQuery<ContentItem> query = getEntityManager()
                .createNamedQuery("ContentItem.findByType", ContentItem.class);
        query.setParameter("type", type);

        return (List<T>) query.getResultList();
    }

    /**
     * Retrieves all content items in the provided folder.
     *
     * @param folder The folder.
     *
     * @return A list of all items in the provided folder.
     */
    public List<ContentItem> findByFolder(final Category folder) {
        final TypedQuery<ContentItem> query = getEntityManager()
                .createNamedQuery("ContentItem.findByFolder",
                                  ContentItem.class);
        query.setParameter("folder", folder);
        
        return query.getResultList();
    }

    /**
     * Counts the items in a folder/category.
     *
     * @param folder The folder/category
     * @return The number of content items in the category/folder.
     */
    public long countItemsInFolder(final Category folder) {
        final TypedQuery<Long> query = getEntityManager()
                .createNamedQuery("ContentItem.countItemsInFolder", Long.class);
        query.setParameter("folder", folder);

        return query.getSingleResult();
    }

    /**
     * Counts the number of items with a specific name in a folder/category.
     *
     * @param folder
     * @param name
     * @return
     */
    public long countByNameInFolder(final Category folder, final String name) {
        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
                "ContentItem.countByNameInFolder", Long.class);
        query.setParameter("folder", folder);
        query.setParameter("name", name);

        return query.getSingleResult();
    }

    /**
     * Retrieves all items in a specific folder where
     * {@link CcmObject#displayName} of the item starts with the provided
     * pattern.
     *
     * @param folder The folder/category whose items are filtered.
     * @param name The name pattern to use.
     * @return A list with all items in the folder matching the provided filter.
     */
    public List<ContentItem> filterByFolderAndName(final Category folder,
                                                   final String name) {
        final TypedQuery<ContentItem> query = getEntityManager()
                .createNamedQuery("ContentItem.filterByFolderAndName",
                                  ContentItem.class);
        query.setParameter("folder", folder);
        query.setParameter("name", name);

        return query.getResultList();
    }

    /**
     * Counts a items in a specfic folder whose {@link CcmObject#displayName}
     * starts with the provided pattern.
     *
     * @param folder The folder/category to use.
     * @param name The name pattern to use.
     * @return The number of items in the folder/category which match the
     * provided pattern.
     */
    public long countFilterByFolderAndName(final Category folder,
                                           final String name) {
        final TypedQuery<Long> query = getEntityManager()
                .createNamedQuery("ContentItem.countFilterByFolderAndName",
                                  Long.class);
        query.setParameter("folder", folder);
        query.setParameter("name", name);

        return query.getSingleResult();
    }

}
