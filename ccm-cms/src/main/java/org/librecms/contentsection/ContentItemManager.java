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
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowManager;

import static org.librecms.CmsConstants.*;

import org.librecms.contentsection.privileges.ItemPrivileges;
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
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.TypePrivileges;

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
    private FolderManager folderManager;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private ContentItemRepository contentItemRepo;

    @Inject
    private ContentTypeRepository typeRepo;

    @Inject
    private LifecycleManager lifecycleManager;

    @Inject
    private WorkflowManager workflowManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private AssetManager assetManager;

    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Creates a new content item in the provided content section and folder
     * with the default workflow for the content type of the item.
     *
     * The folder must be a subfolder of the
     * {@link ContentSection#rootDocumentsFolder} of the provided content
     * section. Otherwise an {@link IllegalArgumentException} is thrown.
     *
     * @param <T>     The type of the content item.
     * @param name    The name (URL stub) of the new content item.
     * @param section The content section in which the item is generated.
     * @param folder  The folder in which in the item is stored.
     * @param type    The type of the new content item.
     * @param locale  Initial locale of the new item
     *
     * @return The new content item.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends ContentItem> T createContentItem(
        final String name,
        final ContentSection section,
        @RequiresPrivilege(ItemPrivileges.CREATE_NEW)
        final Folder folder,
        final Class<T> type,
        final Locale locale) {

        return createContentItem(name,
                                 section,
                                 folder,
                                 type,
                                 item -> {
                                 },
                                 locale);

    }

    /**
     * Creates a new content item in the provided content section and folder
     * with the default workflow for the content type of the item.
     *
     * The folder must be a subfolder of the
     * {@link ContentSection#rootDocumentsFolder} of the provided content
     * section. Otherwise an {@link IllegalArgumentException} is thrown.
     *
     * @param <T>        The type of the content item.
     * @param name       The name (URL stub) of the new content item.
     * @param section    The content section in which the item is generated.
     * @param folder     The folder in which in the item is stored.
     * @param type       The type of the new content item.
     * @param initalizer A {@link ContentItemInitializer} for setting mandatory
     *                   values
     * @param locale     Initial locale of the new item
     *
     * @return The new content item.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends ContentItem> T createContentItem(
        final String name,
        final ContentSection section,
        @RequiresPrivilege(ItemPrivileges.CREATE_NEW)
        final Folder folder,
        final Class<T> type,
        final ContentItemInitializer<T> initalizer,
        final Locale locale) {

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
                                 type,
                                 initalizer,
                                 locale);
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
     * @param <T>              The type of the content item.
     * @param name             The name (URL stub) of the new content item.
     * @param section          The content section in which the item is
     *                         generated.
     * @param folder           The folder in which in the item is stored.
     * @param workflowTemplate The template for the workflow to apply to the new
     *                         item.
     * @param type             The type of the new content item.
     * @param locale
     *
     * @return The new content item.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends ContentItem> T createContentItem(
        final String name,
        final ContentSection section,
        @RequiresPrivilege(ItemPrivileges.CREATE_NEW)
        final Folder folder,
        final WorkflowTemplate workflowTemplate,
        final Class<T> type,
        final Locale locale) {

        return createContentItem(name,
                                 section,
                                 folder,
                                 workflowTemplate,
                                 type,
                                 item -> {
                                 },
                                 locale);

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
     * @param <T>              The type of the content item.
     * @param name             The name (URL stub) of the new content item.
     * @param section          The content section in which the item is
     *                         generated.
     * @param folder           The folder in which in the item is stored.
     * @param workflowTemplate The template for the workflow to apply to the new
     *                         item.
     * @param type             The type of the new content item.
     * @param initializer      Initialiser implementation for setting mandatory
     *                         properties of the new item.
     * @param locale           Initial locale of the new item
     *
     * @return The new content item.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public <T extends ContentItem> T createContentItem(
        final String name,
        final ContentSection section,
        @RequiresPrivilege(ItemPrivileges.CREATE_NEW)
        final Folder folder,
        final WorkflowTemplate workflowTemplate,
        final Class<T> type,
        final ContentItemInitializer<T> initializer,
        final Locale locale) {

        final Optional<ContentType> contentType = typeRepo
            .findByContentSectionAndClass(section, type);

        if (!contentType.isPresent()) {
            throw new IllegalArgumentException(String.format(
                "ContentSection \"%s\" has no content type for \"%s\".",
                section.getLabel(),
                type.getName()));
        }

        //Check if the current user is allowed to use the content type
        permissionChecker.checkPermission(TypePrivileges.USE_TYPE,
                                          contentType.get());

        if (name == null || name.trim().isEmpty()) {
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
        item.getName().addValue(locale,
                                name);

        item.setVersion(ContentItemVersion.DRAFT);
        item.setContentType(contentType.get());

        if (workflowTemplate != null) {
            final Workflow workflow = workflowManager
                .createWorkflow(workflowTemplate, item);
            item.setWorkflow(workflow);
        }

        if (initializer != null) {
            initializer.initializeValues(item);
        }

        contentItemRepo.save(item);

        categoryManager.addObjectToCategory(
            item,
            folder,
            CATEGORIZATION_TYPE_FOLDER);

        contentItemRepo.save(item);

        if (item.getWorkflow() != null) {
            workflowManager.start(item.getWorkflow());
        }

        return item;
    }

    /**
     * Moves a content item to another folder. If moving an item to another
     * content section the caller should first check if the type of the item is
     * registered in the target version using
     * {@link ContentSectionManager#hasContentType(java.lang.Class, org.librecms.contentsection.ContentSection)}.
     * If this method is called with for item and a folder in another content
     * section and the type of the item is not registered for target section
     * this method will throw an {@link IllegalArgumentException}. This method
     * only moves the draft version of the item. The live version is moved after
     * a the item is republished.
     *
     * @param item         The item to move.
     * @param targetFolder The folder to which the item is moved.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void move(
        @RequiresPrivilege(ItemPrivileges.EDIT)
        final ContentItem item,
        @RequiresPrivilege(ItemPrivileges.CREATE_NEW)
        final Folder targetFolder) {
        if (item == null) {
            throw new IllegalArgumentException("The item to move can't be null.");
        }

        if (targetFolder == null) {
            throw new IllegalArgumentException(
                "The target folder can't be null.");
        }

        final ContentItem draftItem = getDraftVersion(item, item.getClass());
        final Optional<Folder> currentFolder = getItemFolder(item);

        if (!sectionManager.hasContentType(draftItem.getClass(),
                                           targetFolder.getSection())) {
            throw new IllegalArgumentException(String.format(
                "Can't move item %d:\"%s\" to folder \"%s\"."
                    + "The target folder %d:\"%s\" belongs to content section "
                    + "%d:\"%s\". The content type \"%s\" has not registered"
                    + "for this section.",
                draftItem.getObjectId(),
                draftItem.getDisplayName(),
                folderManager.getFolderPath(targetFolder, true),
                targetFolder.getObjectId(),
                targetFolder.getDisplayName(),
                targetFolder.getSection().getObjectId(),
                targetFolder.getSection().getDisplayName(),
                draftItem.getClass().getName()));
        }

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
            CATEGORIZATION_TYPE_FOLDER);

    }

    /**
     * Creates an copy of the draft version of the item in the provided
     * {@code targetFolder}. If the target folder belongs to another content
     * section the caller should first check if the type of the item is
     * registered for the target section by using
     * {@link ContentSectionManager#hasContentType(java.lang.Class, org.librecms.contentsection.ContentSection)}.
     * If this method is called for an item and a folder in another content
     * section and the type of the item is not registered for the target section
     * an {@link IllegalArgumentException} is thrown.
     *
     * @param item         The item to copy.
     * @param targetFolder The folder in which the copy is created. If the
     *                     target folder is the same folder as the folder of the
     *                     original item an index is appended to the name of the
     *                     item.
     *
     * @return The copy of the item
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @SuppressWarnings("unchecked")
    public ContentItem copy(
        final ContentItem item,
        @RequiresPrivilege(ItemPrivileges.CREATE_NEW)
        final Folder targetFolder) {

        if (item == null) {
            throw new IllegalArgumentException("The item to copy can't be null.");
        }

        if (targetFolder == null) {
            throw new IllegalArgumentException(
                "The target folder to which the item is copied can't be null");
        }

        final Optional<ContentType> contentType = typeRepo
            .findByContentSectionAndClass(
                targetFolder.getSection(), item.getClass());

        if (!contentType.isPresent()) {
            throw new IllegalArgumentException(String.format(
                "ContentSection \"%s\" has no content type for \"%s\".",
                item.getContentType().getContentSection(),
                item.getClass().getName()));
        }

        final ContentItem draftItem = getDraftVersion(item, item.getClass());
        if (!sectionManager.hasContentType(draftItem.getClass(),
                                           targetFolder.getSection())) {
            throw new IllegalArgumentException(String.format(
                "Can't copy item %d:\"%s\" to folder \"%s\"."
                    + "The target folder %d:\"%s\" belongs to content section "
                    + "%d:\"%s\". The content type \"%s\" has not registered"
                    + "for this section.",
                draftItem.getObjectId(),
                draftItem.getDisplayName(),
                folderManager.getFolderPath(targetFolder, true),
                targetFolder.getObjectId(),
                targetFolder.getDisplayName(),
                targetFolder.getSection().getObjectId(),
                targetFolder.getSection().getDisplayName(),
                draftItem.getClass().getName()));
        }

        final ContentItem copy;
        try {
            copy = draftItem.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

//        final String uuid = UUID.randomUUID().toString();
//        copy.setUuid(uuid);
//        copy.setItemUuid(uuid);
        copy.setContentType(contentType.get());

        if (draftItem.getWorkflow() != null) {
            final WorkflowTemplate template = draftItem.getWorkflow()
                .getTemplate();
            final Workflow copyWorkflow = workflowManager.createWorkflow(
                template, item);
            copy.setWorkflow(copyWorkflow);
        }

        for (AttachmentList attachmentList : item.getAttachments()) {
            copyAttachmentList(attachmentList, copy);
        }

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
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                copyLocalizedString(source, target);
            } else if (propType != null
                           && propType.isAssignableFrom(ContentItem.class)) {

                final ContentItem linkedItem;
                try {
                    linkedItem = (ContentItem) readMethod.invoke(draftItem);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                final ContentItem linkedDraftItem = getDraftVersion(
                    linkedItem, linkedItem.getClass());

                try {
                    writeMethod.invoke(copy, linkedDraftItem);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (propType != null
                           && propType.isAssignableFrom(List.class)) {
                final List<Object> source;
                final List<Object> target;
                try {
                    source = (List<Object>) readMethod.invoke(draftItem);
                    target = (List<Object>) readMethod.invoke(copy);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
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
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
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
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                target.addAll(source);
            } else {
                final Object value;
                try {
                    value = readMethod.invoke(draftItem);
                    writeMethod.invoke(copy, value);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        contentItemRepo.save(copy);

        draftItem.getCategories().forEach(categorization -> categoryManager
            .addObjectToCategory(copy, categorization.getCategory()));
        final Optional<Folder> itemFolder = getItemFolder(draftItem);
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
            CATEGORIZATION_TYPE_FOLDER);

        if (targetFolder.equals(getItemFolder(item).orElse(null))) {
            final long number = contentItemRepo.countFilterByFolderAndName(
                targetFolder, String.format("%s_copy",
                                            item.getDisplayName()));
            final long index = number + 1;
            copy.setDisplayName(String.format("%s_copy%d",
                                              copy.getDisplayName(),
                                              index));
        }

        return copy;
    }

    private boolean propertyIsExcluded(final String name) {
        final String[] excluded = new String[]{
            "attachments",
            "categories",
            "contentType",
            "lifecycle",
            "objectId",
            "uuid",
            "workflow",};

        boolean result = false;
        for (final String current : excluded) {
            if (current.equals(name)) {
                result = true;
            }
        }

        return result;
    }

    private void copyAttachmentList(final AttachmentList sourceList,
                                    final ContentItem target) {
        final AttachmentList targetList = new AttachmentList();
        copyLocalizedString(sourceList.getDescription(), targetList
                            .getDescription());
        targetList.setItem(target);
        targetList.setName(sourceList.getName());
        targetList.setOrder(sourceList.getOrder());
        copyLocalizedString(sourceList.getTitle(), targetList.getTitle());
        targetList.setUuid(UUID.randomUUID().toString());

        entityManager.persist(sourceList);

        for (ItemAttachment<?> attachment : sourceList.getAttachments()) {
            if (assetManager.isShared(attachment.getAsset())) {
                copySharedAssetAttachment(attachment, targetList);
            } else {
                copyAssetAttachment(attachment, targetList);
            }
        }

        entityManager.merge(sourceList);
    }

    private void copySharedAssetAttachment(final ItemAttachment<?> attachment,
                                           final AttachmentList target) {
        final ItemAttachment<Asset> itemAttachment = new ItemAttachment<>();
        itemAttachment.setAsset(attachment.getAsset());
        itemAttachment.setAttachmentList(target);
        itemAttachment.setSortKey(attachment.getSortKey());
        itemAttachment.setUuid(UUID.randomUUID().toString());

        entityManager.persist(itemAttachment);
        target.addAttachment(itemAttachment);
        entityManager.merge(target);
    }

    private void copyAssetAttachment(final ItemAttachment<?> attachment,
                                     final AttachmentList targetList) {
        final Asset source = attachment.getAsset();
        final Asset target;
        try {
            target = source.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new UnexpectedErrorException(ex);
        }

        copyAsset(source, target);

        entityManager.persist(target);
        final ItemAttachment<Asset> targetAttachment = new ItemAttachment<>();
        targetAttachment.setAsset(target);
        targetAttachment.setSortKey(attachment.getSortKey());
        targetAttachment.setUuid(UUID.randomUUID().toString());
        entityManager.persist(targetAttachment);

        targetAttachment.setAttachmentList(targetList);
        targetList.addAttachment(targetAttachment);
        entityManager.merge(targetAttachment);
    }

    public void copyAsset(final Asset source, final Asset target) {
        if (source == null) {
            throw new IllegalArgumentException("Source Asset can't be null.");
        }

        if (target == null) {
            throw new IllegalArgumentException("Target Asset can't be null.");
        }

        if (!source.getClass().equals(target.getClass())) {
            throw new IllegalArgumentException(String.format(
                "Asset belong to different classes. source is instance of "
                    + "\"%s\", target is instance of \"%s\". It is not "
                    + "possible to use assets of diffierent classes "
                    + "with this method",
                source.getClass().getName(),
                target.getClass().getName()));
        }

        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(source.getClass());
        } catch (IntrospectionException ex) {
            throw new UnexpectedErrorException(ex);
        }

        for (final PropertyDescriptor propertyDescriptor : beanInfo.
            getPropertyDescriptors()) {
            final String propertyName = propertyDescriptor.getName();
            if ("objectId".equals(propertyName)
                    || "uuid".equals(propertyName)
                    || "itemAttachments".equals(propertyName)
                    || "categories".equals(propertyName)) {
                continue;
            }

            final Class<?> propType = propertyDescriptor.getPropertyType();
            final Method readMethod = propertyDescriptor.getReadMethod();
            final Method writeMethod = propertyDescriptor.getWriteMethod();

            if (writeMethod == null) {
                continue;
            }

            if (LocalizedString.class.equals(propType)) {
                final LocalizedString sourceStr;
                final LocalizedString targetStr;
                try {
                    sourceStr = (LocalizedString) readMethod.invoke(source);
                    targetStr = (LocalizedString) readMethod.invoke(target);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
                }

                copyLocalizedString(sourceStr, targetStr);
            } else {
                final Object value;
                try {
                    value = readMethod.invoke(source);
                    writeMethod.invoke(target, value);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new UnexpectedErrorException(ex);
                }
            }
        }

        if (target.getUuid() == null || target.getUuid().isEmpty()) {
            target.setUuid(UUID.randomUUID().toString());
        }
    }

    private void copyLocalizedString(final LocalizedString source,
                                     final LocalizedString target) {
        for (final Locale locale : source.getAvailableLocales()) {
            target.addValue(locale, source.getValue(locale));
        }
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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public ContentItem publish(
        @RequiresPrivilege(ItemPrivileges.PUBLISH)
        final ContentItem item) {

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
     * @param item                The content item to publish.
     * @param lifecycleDefinition The definition of the lifecycle to use for the
     *                            new item.
     *
     * @return The published content item.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("unchecked")
    public ContentItem publish(
        @RequiresPrivilege(ItemPrivileges.PUBLISH)
        final ContentItem item,
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

        liveItem.setItemUuid(draftItem.getItemUuid());
        liveItem.setContentType(draftItem.getContentType());

        final Lifecycle lifecycle = lifecycleManager.createLifecycle(
            lifecycleDefinition);

        liveItem.setLifecycle(lifecycle);
        liveItem.setWorkflow(draftItem.getWorkflow());

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
                    target = (LocalizedString) readMethod.invoke(liveItem);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                copyLocalizedString(source, target);
            } else if (propType != null
                           && propType.isAssignableFrom(ContentItem.class)) {
                final ContentItem linkedItem;
                try {
                    linkedItem = (ContentItem) readMethod.invoke(draftItem);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
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
                    } catch (IllegalAccessException
                                 | IllegalArgumentException
                                 | InvocationTargetException ex) {
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
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
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
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
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
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

                target.addAll(source);
            } else {
                final Object value;
                try {
                    value = readMethod.invoke(item);
                    writeMethod.invoke(liveItem, value);
                } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        liveItem.setVersion(ContentItemVersion.LIVE);
        contentItemRepo.save(liveItem);

        final List<Category> oldCategories = liveItem
            .getCategories()
            .stream()
            .map(categorization -> categorization.getCategory())
            .collect(Collectors.toList());
        oldCategories.forEach(category -> {
            try {
                categoryManager.removeObjectFromCategory(liveItem, category);
            } catch (ObjectNotAssignedToCategoryException ex) {
                throw new RuntimeException(ex);
            }
        });

        draftItem.getCategories().forEach(categorization -> categoryManager
            .addObjectToCategory(liveItem,
                                 categorization.getCategory(),
                                 categorization.getType()));

        for (int i = 0; i < draftItem.getAttachments().size(); i++) {
            final AttachmentList sourceList = draftItem.getAttachments().get(i);

            final AttachmentList targetList;
            if (liveItem.getAttachments().size() < i + 1) {
                targetList = new AttachmentList();
                copyLocalizedString(sourceList.getDescription(),
                                    targetList.getDescription());

                targetList.setItem(liveItem);
                liveItem.addAttachmentList(targetList);
                targetList.setName(sourceList.getName());
                copyLocalizedString(sourceList.getTitle(),
                                    targetList.getTitle());
                targetList.setOrder(sourceList.getOrder());
                targetList.setUuid(UUID.randomUUID().toString());
            } else {
                targetList = liveItem.getAttachments().get(i);
            }

            for (int j = 0; j < sourceList.getAttachments().size(); j++) {
                final ItemAttachment<?> sourceAttachment = sourceList.
                    getAttachments().get(j);
                final ItemAttachment<Asset> targetAttachment;
                if (targetList.getAttachments().size() < j + 1) {
                    targetAttachment = new ItemAttachment<>();
                } else {
                    targetAttachment = (ItemAttachment<Asset>) targetList.
                        getAttachments().get(j);
                }

                if (!sourceAttachment.getAsset().equals(targetAttachment)) {
                    final Asset oldTargetAsset = targetAttachment.getAsset();
                    if (oldTargetAsset != null
                            && !assetManager.isShared(oldTargetAsset)) {
                        targetAttachment.setAsset(null);
                        oldTargetAsset.removeItemAttachment(targetAttachment);
                        entityManager.remove(oldTargetAsset);
                    }

                    final Asset sourceAsset = sourceAttachment.getAsset();
                    final Asset targetAsset;
                    if (assetManager.isShared(sourceAttachment.getAsset())) {
                        targetAsset = sourceAttachment.getAsset();
                    } else {
                        try {
                            targetAsset = sourceAttachment.getAsset().getClass()
                                .newInstance();
                        } catch (InstantiationException | IllegalAccessException ex) {
                            throw new UnexpectedErrorException(ex);
                        }
                        copyAsset(sourceAsset, targetAsset);

                        entityManager.persist(targetAsset);
                    }

                    targetAttachment.setAsset(targetAsset);
                    targetAttachment.setAttachmentList(targetList);
                    targetAttachment.setSortKey(sourceAttachment.getSortKey());
                    targetAttachment.setUuid(UUID.randomUUID().toString());
                }

                targetList.addAttachment(targetAttachment);

                entityManager.persist(targetAttachment);
                entityManager.merge(targetList);
            }
        }

        return liveItem;
    }

    /**
     * Publishes all items in a folder. Items which are already live will be
     * republished. Note: Items in sub folders will <strong>not</strong> be
     * published!
     *
     * @param folder The folder which items should be published.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void publish(
        @RequiresPrivilege(ItemPrivileges.PUBLISH)
        final Folder folder) {

        // Ensure that we are using a fresh folder and that the folder was 
        // retrieved in this transaction to avoid problems with lazy fetched 
        // data.
        final Folder theFolder = folderRepo.findById(folder.getObjectId()).get();

        theFolder.getObjects()
            .stream()
            .map(categorization -> categorization.getCategorizedObject())
            .filter(object -> object instanceof ContentItem)
            .forEach(item -> publish((ContentItem) item));
    }

    /**
     * Unpublishes a content item by deleting its live version if there is a
     * live version.
     *
     * @param item
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void unpublish(
        @RequiresPrivilege(ItemPrivileges.PUBLISH)
        final ContentItem item) {
        if (item == null) {
            throw new IllegalArgumentException(
                "The item to unpublish can't be null");
        }

        LOGGER.debug("Unpublishing item {}...", item.getItemUuid());

        final Optional<ContentItem> liveItem = getLiveVersion(
            item, ContentItem.class);

        if (!liveItem.isPresent()) {
            LOGGER.info("ContentItem {} has no live version.",
                        item.getItemUuid());
            return;
        }

        final List<AttachmentList> attachmentLists = liveItem.get()
            .getAttachments();
        for (final AttachmentList attachmentList : attachmentLists) {
            attachmentList.getAttachments().forEach(
                attachment -> {
                    unpublishAttachment(attachment);
                });
        }

        final List<Category> categories = liveItem
            .get()
            .getCategories()
            .stream()
            .map(categorization -> categorization.getCategory())
            .collect(Collectors.toList());

        categories.forEach(category -> {
            try {
                categoryManager.removeObjectFromCategory(liveItem.get(),
                                                         category);
            } catch (ObjectNotAssignedToCategoryException ex) {
                throw new RuntimeException(ex);
            }
        });

        if (liveItem.isPresent()) {
            entityManager.remove(liveItem.get());
        }

    }

    private void unpublishAttachment(final ItemAttachment<?> itemAttachment) {
        final Asset asset = itemAttachment.getAsset();

        asset.removeItemAttachment(itemAttachment);
        itemAttachment.setAsset(null);

        if (assetManager.isShared(asset)) {
            entityManager.merge(asset);
        } else {
            entityManager.remove(asset);
        }

        entityManager.remove(itemAttachment);
    }

    /**
     * Unpublishes all live items in a folder. Items in sub folders will
     * <strong>not</strong> be unpublished!.
     *
     * @param folder The folders which items are unpublished.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void unpublish(
        @RequiresPrivilege(ItemPrivileges.PUBLISH)
        final Folder folder) {

        // Ensure that we are using a fresh folder and that the folder was 
        // retrieved in this transaction to avoid problems with lazy fetched 
        // data.
        final Folder theFolder = folderRepo.findById(folder.getObjectId()).get();

        theFolder.getObjects()
            .stream()
            .map(categorization -> categorization.getCategorizedObject())
            .filter(object -> object instanceof ContentItem)
            .map(object -> (ContentItem) object)
            .filter(item -> isLive(item))
            .forEach(item -> unpublish(item));
    }

    /**
     * Determines if a content item has a live version.
     *
     * @param item The item
     *
     * @return {@code true} if the content item has a live version,
     *         {@code false} if not.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean isLive(final ContentItem item) {
        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
            "ContentItem.hasLiveVersion", Boolean.class);
        query.setParameter("uuid", item.getItemUuid());

        return query.getSingleResult();
    }

    /**
     * Retrieves the live version of the provided content item if any.
     *
     * @param <T>  Type of the content item.
     * @param item The item of which the live version should be retrieved.
     * @param type Type of the content item.
     *
     * @return The live version of an item. If the item provided is already the
     *         live version the provided item is returned, otherwise the live
     *         version is returned. If there is no live version an empty
     *         {@link Optional} is returned.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings({"unchecked"})
    public <T extends ContentItem> Optional<T> getLiveVersion(
        @RequiresPrivilege(ItemPrivileges.VIEW_PUBLISHED)
        final ContentItem item,
        final Class<T> type) {

        if (!ContentItem.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.format(
                "The provided type \"%s\" does match the type of the provided "
                    + "item (\"%s\").",
                type.getName(),
                item.getClass().getName()));
        }

        if (isLive(item)) {
            final TypedQuery<ContentItem> query = entityManager
                .createNamedQuery(
                    "ContentItem.findLiveVersion", ContentItem.class);
            query.setParameter("uuid", item.getItemUuid());

            final ContentItem result = query.getSingleResult();
            if (type.isAssignableFrom(result.getClass())) {
                return Optional.of((T) result);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Retrieves the pending versions of an item if there are any.
     *
     * @param <T>  Type of the content item to retrieve.
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
     * @param <T>  Type of the item.
     * @param item The item of which the draft version is retrieved.
     * @param type Type of the item.
     *
     * @return The draft version of the provided content item. If the provided
     *         item is the draft version the provided item is simply returned.
     *         Otherwise the draft version is retrieved from the database and is
     *         returned. Each content item has a draft version (otherwise
     *         something is seriously wrong with the database) this method will
     * <b>never</b> return {@code null}.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("unchecked")
    public <T extends ContentItem> T getDraftVersion(
        @RequiresPrivilege(ItemPrivileges.PREVIEW)
        final ContentItem item,
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
        query.setParameter("uuid", item.getItemUuid());

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
     * @param item The item for which path is generated.
     *
     * @return The path of the content item
     *
     * @see #getItemPath(org.librecms.contentsection.ContentItem, boolean)
     */
    public String getItemPath(final ContentItem item) {
        return getItemPath(item, false);
    }

    /**
     * Returns the path of an item as String. The path of an item is the path of
     * the folder category the item is a member of concatenated with the name of
     * the item. The path is relative to the content section. For instance, the
     * path of an item in the folder category
     * {@code /research/computer-science/artifical-intelligence} and with the
     * name {@code neural-nets} has the path
     * {@code /research/computer-science/artificial-intelligence/neural-nets}.
     * If the parameter {@code withContentSection} is set to {@code true} the
     * the path will be prefixed with the name of the content section. For
     * instance if the item {@code neural-nets} is part of the content section
     * {@code info}, the path including the content section would be
     * {@code info:/research/computer-science/artificial-intelligence/neural-nets}.
     *
     * @param item               The item whose path is generated.
     * @param withContentSection Whether to include the content section into the
     *                           path.
     *
     * @return The path of the content item
     *
     * @see #getItemPath(org.librecms.contentsection.ContentItem, boolean)
     */
    public String getItemPath(final ContentItem item,
                              final boolean withContentSection) {
        final List<Categorization> result = item.getCategories().stream()
            .filter(categorization -> {
                return CATEGORIZATION_TYPE_FOLDER.equals(
                    categorization.getType());
            })
            .collect(Collectors.toList());

        if (result.isEmpty()) {
            return item.getDisplayName();
        } else {
            final List<String> tokens = new ArrayList<>();
            tokens.add(item.getDisplayName());

            Category current = result.get(0).getCategory();
            while (current.getParentCategory() != null) {
                tokens.add(current.getName());
                current = current.getParentCategory();
            }

            Collections.reverse(tokens);
            final String path = String.join("/", tokens);

            if (withContentSection) {
                final String sectionName = item.getContentType().
                    getContentSection().getDisplayName();
                return String.format(
                    "%s:/%s", sectionName, path);
            } else {
                return String.format("/%s", path);
            }
        }
    }

    /**
     * Creates a list of the folders in which an item is placed.
     *
     * @param item
     *
     * @return
     */
    public List<Folder> getItemFolders(final ContentItem item) {
        final List<Categorization> result = item.getCategories().stream()
            .filter(categorization -> {
                return CATEGORIZATION_TYPE_FOLDER.equals(
                    categorization.getType());
            })
            .collect(Collectors.toList());

        final List<Folder> folders = new ArrayList<>();
        if (!result.isEmpty()) {
            Category current = result.get(0).getCategory();
            if (current instanceof Folder) {
                folders.add((Folder) current);
            } else {
                throw new IllegalArgumentException(String.format(
                    "The item %s is assigned to the category %s with the"
                        + "categorization type \"%s\", but the Category is not"
                        + "a folder. This is no supported.",
                    item.getUuid(),
                    current.getUuid(),
                    CATEGORIZATION_TYPE_FOLDER));
            }

            while (current.getParentCategory() != null) {
                current = current.getParentCategory();
                if (current instanceof Folder) {
                    folders.add((Folder) current);
                } else {
                    throw new IllegalArgumentException(String.format(
                        "The item %s is assigned to the category %s with the"
                            + "categorization type \"%s\", but the Category is not"
                        + "a folder. This is no supported.",
                        item.getUuid(),
                        current.getUuid(),
                        CATEGORIZATION_TYPE_FOLDER));
                }
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
     *         is part of a folder.
     */
    public Optional<Folder> getItemFolder(final ContentItem item) {
        final List<Categorization> result = item.getCategories().stream()
            .filter(categorization -> CATEGORIZATION_TYPE_FOLDER.equals(
            categorization.getType()))
            .collect(Collectors.toList());

        if (result.size() > 0) {
            final Category category = result.get(0).getCategory();
            if (category instanceof Folder) {
                return Optional.of((Folder) category);
            } else {
                throw new IllegalArgumentException(String.format(
                    "The item %s is assigned to the category %s with the"
                        + "categorization type \"%s\", but the Category is not"
                        + "a folder. This is no supported.",
                    item.getUuid(),
                    category.getUuid(),
                    CATEGORIZATION_TYPE_FOLDER));
            }
        } else {
            return Optional.empty();
        }
    }

}
