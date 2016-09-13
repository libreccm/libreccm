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

import com.arsdigita.kernel.KernelConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;
import org.librecms.CmsConstants;
import org.librecms.lifecycle.Lifecycle;
import org.librecms.lifecycle.LifecycleManager;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Manager class providing several methods to manipulate {@link ContentItem}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemManager {

    private static final Logger LOGGER = LogManager.getLogger(
            ContentItemManager.class);

    @Inject
    private EntityManager entityManager;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private ContentItemRepository contentItemRepo;

    @Inject
    private ContentTypeRepository typeRepo;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private LifecycleManager lifecycleManager;

    @Inject
    private WorkflowManager workflowManager;

    /**
     * Creates a new content item in the provided content section and folder
     * with the workflow.
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
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends ContentItem> T createContentItem(
            final String name,
            final ContentSection section,
            final Category folder,
            final Class<T> type) {

        final Optional<ContentType> contentType = typeRepo
                .findByContentSectionAndClass(section, type);

        if (!contentType.isPresent()) {
            throw new IllegalArgumentException(String.format(
                    "ContentSection \"%s\" has no content type for \"%s\".",
                    section.getLabel(),
                    type.getName()));
        }

        return createContentItem(name,
                                 section,
                                 folder,
                                 contentType.get().getDefaultWorkflow(),
                                 type);
    }

    /**
     * Creates a new content item in the provided content section and folder
     * with specific workflow.
     *
     * The folder must be a subfolder of the
     * {@link ContentSection#rootDocumentsFolder} of the provided content
     * section. Otherwise an {@link IllegalArgumentException} is thrown.
     *
     * Likewise the provided {@link WorkflowTemplate} must be defined in the
     * provided content section. Otherwise an {@link IllegalArgumentException}
     * is thrown.
     *
     * @param <T> The type of the content item.
     * @param name The name (URL stub) of the new content item.
     * @param section The content section in which the item is generated.
     * @param folder The folder in which in the item is stored.
     * @param workflowTemplate The template for the workflow to apply to the new
     * item.
     * @param type The type of the new content item.
     *
     * @return The new content item.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends ContentItem> T createContentItem(
            final String name,
            final ContentSection section,
            final Category folder,
            final WorkflowTemplate workflowTemplate,
            final Class<T> type) {

        final Optional<ContentType> contentType = typeRepo
                .findByContentSectionAndClass(section, type);

        if (!contentType.isPresent()) {
            throw new IllegalArgumentException(String.format(
                    "ContentSection \"%s\" has no content type for \"%s\".",
                    section.getLabel(),
                    type.getName()));
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(
                    "The name of a content item can't be blank.");
        }

        final T item;
        try {
            item = type.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            LOGGER.error("Failed to create new content item of type \"{}\" "
                                 + "in content section \"{}\".",
                         type.getName(),
                         section.getLabel());
            throw new RuntimeException(ex);
        }

        final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);

        item.setDisplayName(name);
        item.getName().addValue(kernelConfig.getDefaultLocale(),
                                name);

        item.setVersion(ContentItemVersion.DRAFT);
        item.setContentType(contentType.get());

        if (workflowTemplate != null) {
            final Workflow workflow = workflowManager.createWorkflow(
                    workflowTemplate);
            item.setWorkflow(workflow);
        }

        categoryManager.addObjectToCategory(
                item,
                folder,
                CmsConstants.CATEGORIZATION_TYPE_FOLDER);

        contentItemRepo.save(item);

        return item;
    }

    /**
     * Moves a content item to another folder in the same content section. This
     * only moves the draft version of the item. The live version is moved after
     * a the item is republished.
     *
     * @param item The item to move.
     * @param targetFolder The folder to which the item is moved.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void move(final ContentItem item, final Category targetFolder) {
        if (item == null) {
            throw new IllegalArgumentException("The item to move can't be null.");
        }

        if (targetFolder == null) {
            throw new IllegalArgumentException(
                    "The target folder can't be null.");
        }

        final ContentItem draftItem = getDraftVersion(item, item.getClass());
        final Optional<Category> currentFolder = getItemFolder(item);

        if (currentFolder.isPresent()) {
            try {
                categoryManager.removeObjectFromCategory(draftItem,
                                                         currentFolder.get());
            } catch (ObjectNotAssignedToCategoryException ex) {
                throw new RuntimeException(ex);
            }
        }

        categoryManager.addObjectToCategory(
                draftItem,
                targetFolder,
                CmsConstants.CATEGORIZATION_TYPE_FOLDER);

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
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("unchecked")
    public void copy(final ContentItem item, final Category targetFolder) {
        if (item == null) {
            throw new IllegalArgumentException("The item to copy can't be null.");
        }

        if (targetFolder == null) {
            throw new IllegalArgumentException(
                    "The target folder to which the item is copied can't be null");
        }

        final Optional<ContentType> contentType = typeRepo
                .findByContentSectionAndClass(
                        item.getContentType().getContentSection(), item.
                        getClass());

        if (!contentType.isPresent()) {
            throw new IllegalArgumentException(String.format(
                    "ContentSection \"%s\" has no content type for \"%s\".",
                    item.getContentType().getContentSection(),
                    item.getClass().getName()));
        }

        final ContentItem draftItem = getDraftVersion(item, item.getClass());

        final ContentItem copy;
        try {
            copy = draftItem.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        copy.setContentType(contentType.get());

        if (draftItem.getWorkflow() != null) {
            final WorkflowTemplate template = draftItem.getWorkflow()
                    .getTemplate();
            final Workflow copyWorkflow = workflowManager.createWorkflow(
                    template);
            copy.setWorkflow(copyWorkflow);
        }

        draftItem.getCategories().forEach(categorization -> categoryManager
                .addObjectToCategory(copy, categorization.getCategory()));

        final Optional<Category> itemFolder = getItemFolder(draftItem);
        if (itemFolder.isPresent()) {
            try {
                categoryManager.removeObjectFromCategory(
                        copy, getItemFolder(draftItem).get());
            } catch (ObjectNotAssignedToCategoryException ex) {
                throw new RuntimeException(ex);
            }
        }

        categoryManager.addObjectToCategory(
                copy,
                targetFolder,
                CmsConstants.CATEGORIZATION_TYPE_FOLDER);

        // !!!!!!!!!!!!!!!!!!!!!
        // ToDo copy Attachments
        // !!!!!!!!!!!!!!!!!!!!!
        //
        //
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(item.getClass());
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }

        for (final PropertyDescriptor propertyDescriptor : beanInfo
                .getPropertyDescriptors()) {
            if (propertyIsExcluded(propertyDescriptor.getName())) {
                continue;
            }

            final Class<?> propType = propertyDescriptor.getPropertyType();
            final Method readMethod = propertyDescriptor.getReadMethod();
            final Method writeMethod = propertyDescriptor.getWriteMethod();

            if (writeMethod == null) {
                continue;
            }

            if (LocalizedString.class.equals(propType)) {
                final LocalizedString source;
                final LocalizedString target;
                try {
                    source = (LocalizedString) readMethod.invoke(draftItem);
                    target = (LocalizedString) readMethod.invoke(copy);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                source.getAvailableLocales().forEach(
                        locale -> target.addValue(locale,
                                                  source.getValue(locale)));
            } else if (propType != null
                               && propType.isAssignableFrom(ContentItem.class)) {

                final ContentItem linkedItem;
                try {
                    linkedItem = (ContentItem) readMethod.invoke(draftItem);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                final ContentItem linkedDraftItem = getDraftVersion(
                        linkedItem, linkedItem.getClass());

                try {
                    writeMethod.invoke(copy, linkedDraftItem);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (propType != null
                               && propType.isAssignableFrom(List.class)) {
                final List<Object> source;
                final List<Object> target;
                try {
                    source = (List<Object>) readMethod.invoke(draftItem);
                    target = (List<Object>) readMethod.invoke(copy);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                target.addAll(source);
            } else if (propType != null
                               && propType.isAssignableFrom(Map.class)) {
                final Map<Object, Object> source;
                final Map<Object, Object> target;

                try {
                    source = (Map<Object, Object>) readMethod.invoke(draftItem);
                    target = (Map<Object, Object>) readMethod.invoke(copy);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                source.forEach((key, value) -> target.put(key, value));
            } else if (propType != null
                               && propType.isAssignableFrom(Set.class)) {
                final Set<Object> source;
                final Set<Object> target;

                try {
                    source = (Set<Object>) readMethod.invoke(draftItem);
                    target = (Set<Object>) readMethod.invoke(copy);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                target.addAll(source);
            } else {
                final Object value;
                try {
                    value = readMethod.invoke(item);
                    writeMethod.invoke(copy, value);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        if (targetFolder.equals(getItemFolder(item).orElse(null))) {
            final long number = contentItemRepo.countFilterByFolderAndName(
                    targetFolder, String.format("%s_copy",
                                                item.getDisplayName()));
            final long index = number + 1;
            copy.setDisplayName(String.format("%s_copy%d", 
                                              copy.getDisplayName(),
                                              index));
        }
        
        contentItemRepo.save(copy);
    }

    private boolean propertyIsExcluded(final String name) {
        final String[] excluded = new String[]{
            "objectId", "uuid", "lifecycle", "workflow", "categories",
            "attachments"
        };

        boolean result = false;
        for (final String current : excluded) {
            if (current.equals(name)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Creates a live version of content item or updates the live version of a
     * content item if there already a live version using the default lifecycle
     * for the content type of the provided item.
     *
     * @param item The content item to publish.
     *
     * @return The published content item.
     */
    public ContentItem publish(final ContentItem item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "The item to publish can't be null.");
        }

        final LifecycleDefinition lifecycleDefinition = item.getContentType()
                .getDefaultLifecycle();

        return publish(item, lifecycleDefinition);
    }

    /**
     * Creates a live version of content item or updates the live version of a
     * content item if there already a live version.
     *
     * @param item The content item to publish.
     * @param lifecycleDefinition The definition of the lifecycle to use for the
     * new item.
     *
     * @return The published content item.
     */
    @SuppressWarnings("unchecked")
    public ContentItem publish(final ContentItem item,
                               final LifecycleDefinition lifecycleDefinition) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "The item to publish can't be null.");
        }

        if (lifecycleDefinition == null) {
            throw new IllegalArgumentException(
                    "The lifecycle definition for the "
                            + "lifecycle of the item to publish can't be null.");
        }

        final ContentItem draftItem = getDraftVersion(item, ContentItem.class);
        final ContentItem liveItem;

        if (isLive(item)) {
            liveItem = getLiveVersion(item, ContentItem.class).get();
        } else {
            try {
                liveItem = draftItem.getClass().newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        liveItem.setContentType(draftItem.getContentType());

        final Lifecycle lifecycle = lifecycleManager.createLifecycle(
                lifecycleDefinition);

        liveItem.setLifecycle(lifecycle);
        liveItem.setWorkflow(draftItem.getWorkflow());

        draftItem.getCategories().forEach(categorization -> categoryManager
                .addObjectToCategory(item, categorization.getCategory()));

        liveItem.setUuid(draftItem.getUuid());

        // !!!!!!!!!!!!!!!!!!!!!
        // ToDo copy Attachments
        // !!!!!!!!!!!!!!!!!!!!!
        //
        //
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(item.getClass());
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }

        for (final PropertyDescriptor propertyDescriptor : beanInfo
                .getPropertyDescriptors()) {

            if (propertyIsExcluded(propertyDescriptor.getName())) {
                continue;
            }

            final Class<?> propType = propertyDescriptor.getPropertyType();
            final Method readMethod = propertyDescriptor.getReadMethod();
            final Method writeMethod = propertyDescriptor.getWriteMethod();

            if (LocalizedString.class.equals(propType)) {
                final LocalizedString source;
                final LocalizedString target;
                try {
                    source = (LocalizedString) readMethod.invoke(draftItem);
                    target = (LocalizedString) readMethod.invoke(liveItem);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                source.getAvailableLocales().forEach(
                        locale -> target.addValue(locale, source.
                                                  getValue(locale)));
            } else if (propType != null
                               && propType.isAssignableFrom(ContentItem.class)) {
                final ContentItem linkedItem;
                try {
                    linkedItem = (ContentItem) readMethod.invoke(draftItem);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                final ContentItem linkedDraftItem = getDraftVersion(
                        linkedItem, linkedItem.getClass());

                if (isLive(linkedDraftItem)) {
                    try {
                        final Optional<ContentItem> linkedLiveItem
                                                    = getLiveVersion(
                                        linkedDraftItem, ContentItem.class);
                        writeMethod.invoke(liveItem, linkedLiveItem);
                    } catch (IllegalAccessException |
                             IllegalArgumentException |
                             InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } else if (propType != null
                               && propType.isAssignableFrom(List.class)) {
                final List<Object> source;
                final List<Object> target;
                try {
                    source = (List<Object>) readMethod.invoke(draftItem);
                    target = (List<Object>) readMethod.invoke(liveItem);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                target.addAll(source);
            } else if (propType != null
                               && propType.isAssignableFrom(Map.class)) {
                final Map<Object, Object> source;
                final Map<Object, Object> target;

                try {
                    source = (Map<Object, Object>) readMethod.invoke(draftItem);
                    target = (Map<Object, Object>) readMethod.invoke(liveItem);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                source.forEach((key, value) -> target.put(key, value));
            } else if (propType != null
                               && propType.isAssignableFrom(Set.class)) {
                final Set<Object> source;
                final Set<Object> target;

                try {
                    source = (Set<Object>) readMethod.invoke(draftItem);
                    target = (Set<Object>) readMethod.invoke(liveItem);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                target.addAll(source);
            } else {
                final Object value;
                try {
                    value = readMethod.invoke(item);
                    writeMethod.invoke(liveItem, value);
                } catch (IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        return liveItem;
    }

    /**
     * Unpublishes a content item by deleting its live version if there is a
     * live version.
     *
     * @param item
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void unpublish(final ContentItem item
    ) {
        final Optional<ContentItem> liveItem = getLiveVersion(
                item, ContentItem.class);

        if (liveItem.isPresent()) {
            entityManager.remove(liveItem);
        }

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
        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
                "ContentItem.hasLiveVersion", Boolean.class);
        query.setParameter("uuid", item.getUuid());

        return query.getSingleResult();
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

        if (isLive(item)) {
            final TypedQuery<T> query = entityManager.createNamedQuery(
                    "ContentItem.findLiveVersion", type);
            query.setParameter("uuid", item.getUuid());

            return Optional.of(query.getSingleResult());
        } else {
            return Optional.empty();
        }
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
    @SuppressWarnings("unchecked")
    public <T extends ContentItem> T getDraftVersion(final ContentItem item,
                                                     final Class<T> type) {
        if (!ContentItem.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.format(
                    "The provided type \"%s\" does match the type of the provided "
                    + "item (\"%s\").",
                    type.getName(),
                    item.getClass().getName()));
        }

        final TypedQuery<ContentItem> query = entityManager.createNamedQuery(
                "ContentItem.findDraftVersion", ContentItem.class);
        query.setParameter("uuid", item.getUuid());

        return (T) query.getSingleResult();
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
     *
     * @return The path of the content item
     *
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
     * instance if the item {@code neural-nets} is part of the content section
     * {@code info}, the path including the content section would be
     * {@code info:/research/computer-science/artificial-intelligence/neural-nets}.
     *
     * @param item The item whose path is generated.
     * @param withContentSection Wether to include the content section into the
     * path.
     *
     * @return The path of the content item
     *
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
     *
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

            while (current.getParentCategory() != null) {
                current = current.getParentCategory();
                folders.add(current);
            }

            Collections.reverse(folders);
            return folders;
        }

        return folders;
    }

    /**
     * Gets the folder in which in item is placed (if the item is part of
     * folder).
     *
     * @param item The item
     *
     * @return An {@link Optional} containing the folder of the item if the item
     * is part of a folder.
     */
    public Optional<Category> getItemFolder(final ContentItem item) {
        final List<Categorization> result = item.getCategories().stream().
                filter(categorization -> CmsConstants.CATEGORIZATION_TYPE_FOLDER.
                        equals(categorization.getType()))
                .collect(Collectors.toList());

        if (result.size() > 0) {
            return Optional.of(result.get(0).getCategory());
        } else {
            return Optional.empty();
        }
    }

}
