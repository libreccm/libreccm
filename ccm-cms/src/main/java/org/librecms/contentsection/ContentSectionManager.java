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

import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.Permission;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.workflow.WorkflowTemplate;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.librecms.CmsConstants;

import org.librecms.lifecycle.LifecycleDefinition;

import static org.librecms.CmsConstants.*;
import static org.librecms.contentsection.ContentSection.*;

/**
 * Provides several functions for managing content sections.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentSectionManager {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private RoleRepository roleRepo;

//    @Inject
//    private RoleManager roleManager;
    @Inject
    private PermissionManager permissionManager;

    @Inject
    private ConfigurationManager confManager;

    /**
     * Creates a new content section including the default roles. This operation
     * requries {@code admin} privileges.
     *
     * @param name The name of the new content section.
     *
     * @return The new content section.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public ContentSection createContentSection(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(
                "The name of a ContentSection can't be blank.");
        }

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defautLocale = kernelConfig.getDefaultLocale();

        final ContentSection section = new ContentSection();
        section.setLabel(name);
        section.setDisplayName(name);
        section.setPrimaryUrl(name);
        section.getTitle().addValue(defautLocale, name);

        final Folder rootFolder = new Folder();
        rootFolder.setName(String.format("%s_root", name));
        rootFolder.getTitle().addValue(defautLocale, rootFolder.getName());
        rootFolder.setDisplayName(rootFolder.getName());
        rootFolder.setUuid(UUID.randomUUID().toString());
        rootFolder.setUniqueId(rootFolder.getUuid());
        rootFolder.setCategoryOrder(1L);
        rootFolder.setSection(section);

        final Folder rootAssetFolder = new Folder();
        rootAssetFolder.setName(String.format("%s_assets", name));
        rootAssetFolder.getTitle().addValue(defautLocale,
                                            rootAssetFolder.getName());
        rootAssetFolder.setDisplayName(rootAssetFolder.getName());
        rootAssetFolder.setUuid(UUID.randomUUID().toString());
        rootAssetFolder.setUniqueId(rootAssetFolder.getUuid());
        rootAssetFolder.setCategoryOrder(1L);
        rootAssetFolder.setSection(section);

        section.setRootDocumentFolder(rootFolder);
        section.setRootAssetsFolder(rootAssetFolder);

        categoryRepo.save(rootFolder);
        categoryRepo.save(rootAssetFolder);
        sectionRepo.save(section);

        addRoleToContentSection(section,
                                ALERT_RECIPIENT);
        addRoleToContentSection(section,
                                AUTHOR,
                                PRIVILEGE_ITEMS_CATEGORIZE,
                                PRIVILEGE_ITEMS_CREATE_NEW,
                                PRIVILEGE_ITEMS_EDIT,
                                PRIVILEGE_ITEMS_VIEW_PUBLISHED,
                                PRIVILEGE_ITEMS_PREVIEW);
        addRoleToContentSection(section,
                                EDITOR,
                                PRIVILEGE_ITEMS_CATEGORIZE,
                                PRIVILEGE_ITEMS_CREATE_NEW,
                                PRIVILEGE_ITEMS_EDIT,
                                PRIVILEGE_ITEMS_APPROVE,
                                PRIVILEGE_ITEMS_DELETE,
                                PRIVILEGE_ITEMS_VIEW_PUBLISHED,
                                PRIVILEGE_ITEMS_PREVIEW);
        addRoleToContentSection(section,
                                MANAGER,
                                PRIVILEGE_ADMINISTER_ROLES,
                                PRIVILEGE_ADMINISTER_WORKFLOW,
                                PRIVILEGE_ADMINISTER_LIFECYLES,
                                PRIVILEGE_ADMINISTER_CATEGORIES,
                                PRIVILEGE_ADMINISTER_CONTENT_TYPES,
                                PRIVILEGE_ITEMS_CATEGORIZE,
                                PRIVILEGE_ITEMS_CREATE_NEW,
                                PRIVILEGE_ITEMS_EDIT,
                                PRIVILEGE_ITEMS_APPROVE,
                                PRIVILEGE_ITEMS_PUBLISH,
                                PRIVILEGE_ITEMS_DELETE,
                                PRIVILEGE_ITEMS_VIEW_PUBLISHED,
                                PRIVILEGE_ITEMS_PREVIEW);
        addRoleToContentSection(section,
                                PUBLISHER,
                                PRIVILEGE_ITEMS_CATEGORIZE,
                                PRIVILEGE_ITEMS_CREATE_NEW,
                                PRIVILEGE_ITEMS_EDIT,
                                PRIVILEGE_ITEMS_APPROVE,
                                PRIVILEGE_ITEMS_PUBLISH,
                                PRIVILEGE_ITEMS_DELETE,
                                PRIVILEGE_ITEMS_VIEW_PUBLISHED,
                                PRIVILEGE_ITEMS_PREVIEW);
        addRoleToContentSection(section,
                                CONTENT_READER,
                                PRIVILEGE_ITEMS_VIEW_PUBLISHED);

        return section;
    }

    /**
     * Renames a content section and all roles associated with it (roles
     * starting with the name of the content section). Note that you have to
     * rename the localised titles of the content section and the root folders
     * manually. This operation requires {@code admin} privileges.
     *
     * @param section The section to rename.
     *
     * @@param name The new name of the content section.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public void renameContentSection(final ContentSection section,
                                     final String name) {
        final String oldName = section.getLabel();

        section.setLabel(name);
        section.setDisplayName(name);
        section.setPrimaryUrl(name);

        section.getRoles().forEach(r -> renameSectionRole(r, oldName, name));
    }

    private void renameSectionRole(final Role role,
                                   final String oldName,
                                   final String newName) {
        if (role.getName().startsWith(oldName, 0)) {
            final String suffix = role.getName().substring(oldName.length());
            role.setName(String.join("", newName, suffix));

            roleRepo.save(role);
        }
    }

    /**
     * Adds new role to a content section. the new role will not have any
     * members, they have to be added separatly. This operation requires
     * {@link CmsConstants#PRIVILEGE_ADMINISTER_ROLES} for the provided content
     * section.
     *
     * @param section    The {@link ContentSection} to which the role is added.
     * @param roleName   The name of the new role.
     * @param privileges The privileges of the new role.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addRoleToContentSection(
        @RequiresPrivilege(PRIVILEGE_ADMINISTER_ROLES)
        final ContentSection section,
        final String roleName,
        final String... privileges) {

        final Role role = new Role();
        role.setName(String.join("_", section.getLabel(), roleName));
        roleRepo.save(role);

        final Category rootFolder = section.getRootDocumentsFolder();
        for (String privilege : privileges) {
            permissionManager.grantPrivilege(privilege, role, rootFolder);
        }

        addRoleToContentSection(role, section);
    }

    /**
     * Associates an existing role to with a content section. This will not
     * grant any permissions for the content section to the role. This operation
     * requires {@link CmsConstants#PRIVILEGE_ADMINISTER_ROLES} for the provided
     * content section.
     *
     * @param role    The role to add.
     * @param section The section the role is associated with.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addRoleToContentSection(
        final Role role,
        @RequiresPrivilege(PRIVILEGE_ADMINISTER_ROLES)
        final ContentSection section) {

        section.addRole(role);
        sectionRepo.save(section);
    }

    /**
     * Removes a role from a content section and deletes all permissions of the
     * role which are associated with the content section. The role itself is
     * <strong>not</strong> deleted because the role is maybe is used in other
     * places. This operation requires
     * {@link CmsConstants#PRIVILEGE_ADMINISTER_ROLES} for the provided content
     * section.
     *
     * @param contentSection The section from which the role is removed.
     * @param role           The role to remove from the content section.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeRoleFromContentSection(
        @RequiresPrivilege(PRIVILEGE_ADMINISTER_ROLES)
        final ContentSection contentSection,
        final Role role) {

        if (contentSection == null) {
            throw new IllegalArgumentException(
                "Can't remove role from ContentSection null");
        }

        if (role == null) {
            throw new IllegalArgumentException("Role to delete can't be null.");
        }

        contentSection.removeRole(role);
        sectionRepo.save(contentSection);

        final TypedQuery<Permission> query = entityManager
            .createNamedQuery("ContentSection.findPermissions",
                              Permission.class);
        query.setParameter("section", contentSection);
        query.setParameter("rootDocumentsFolder",
                           contentSection.getRootDocumentsFolder());
        query.setParameter("role", role);

        final List<Permission> permissions = query.getResultList();
        permissions.forEach(p -> entityManager.remove(p));
    }

    /**
     * Associates a content type with a content section making the type
     * available for use in the content section. This operation requires
     * {@link CmsConstants#PRIVILEGE_ADMINISTER_CONTENT_TYPES} for the provided
     * content section.
     *
     * @param type    The {@link ContentItem} class representing the type to
     *                add.
     * @param section The section to which to type is added.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addTypeToSection(
        final Class<? extends ContentItem> type,
        @RequiresPrivilege(PRIVILEGE_ADMINISTER_CONTENT_TYPES)
        final ContentSection section) {

        throw new UnsupportedOperationException();
    }

    /**
     * Removes a content type from a content section. After this it is not
     * possible to create new items of this type in the content section.
     * Existing items are left untouched. This operation requires
     * {@link CmsConstants#PRIVILEGE_ADMINISTER_CONTENT_TYPES} for the provided
     * content section.
     *
     * @param type    The type to remove.
     * @param section The section from which the type is removed.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeTypeFromSection(
        final ContentType type,
        @RequiresPrivilege(PRIVILEGE_ADMINISTER_CONTENT_TYPES)
        final ContentSection section) {

        throw new UnsupportedOperationException();
    }

    /**
     * Adds a lifecycle definition to a content section. This operation requires
     * {@link CmsConstants#PRIVILEGE_ADMINISTER_LIFECYLES} for the provided
     * content section.
     *
     * @param definition The lifecycle definition to add.
     * @param section    The section to which the definition is added.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addLifecycleDefinitionToContentSection(
        final LifecycleDefinition definition,
        @RequiresPrivilege(PRIVILEGE_ADMINISTER_LIFECYLES)
        final ContentSection section) {

        section.addLifecycleDefinition(definition);
        sectionRepo.save(section);
    }

    /**
     * Removes a lifecycle definition from a content section. This operation
     * requires {@link CmsConstants#PRIVILEGE_ADMINISTER_LIFECYLES} for the
     * provided content section.
     *
     * @param definition The definition to remove.
     * @param section    The section from which the definition is removed.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeLifecycleDefinitionFromContentSection(
        final LifecycleDefinition definition,
        @RequiresPrivilege(PRIVILEGE_ADMINISTER_LIFECYLES)
        final ContentSection section) {

        section.removeLifecycleDefinition(definition);
        sectionRepo.save(section);
    }

    /**
     * Adds a workflow template to a content section. This operation requires
     * {@link CmsConstants#PRIVILEGE_ADMINISTER_WORKFLOW} for the provided
     * content section.
     *
     * @param template The template to add.
     * @param section  The content section to which the template is added.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addWorkflowTemplateToContentSection(
        final WorkflowTemplate template,
        @RequiresPrivilege(PRIVILEGE_ADMINISTER_WORKFLOW)
        final ContentSection section) {

        section.addWorkflowTemplate(template);
        sectionRepo.save(section);
    }

    /**
     * Removes a workflow template from a content section. This operation
     * requires {@link CmsConstants#PRIVILEGE_ADMINISTER_WORKFLOW} for the
     * provided content section.
     *
     * @param template The template to remove.
     * @param section  The section from which the template is removed.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeWorkflowTemplateFromContentSection(
        final WorkflowTemplate template,
        @RequiresPrivilege(PRIVILEGE_ADMINISTER_WORKFLOW)
        final ContentSection section) {

        section.removeWorkflowTemplate(template);
        sectionRepo.save(section);
    }

    /**
     * Retrieves the {@link ItemResolver} for the provided content section.
     *
     * @param section The section for which the {@link ItemResolver} is
     *                retrieved.
     *
     * @return The {@link ItemResolver} for the provided content section.
     */
    public ItemResolver getItemResolver(final ContentSection section) {
        try {
            @SuppressWarnings("unchecked")
            final Class<ItemResolver> itemResolverClazz
                                          = (Class<ItemResolver>) Class.
                    forName(section.getItemResolverClass());
            return itemResolverClazz.newInstance();
        } catch (ClassNotFoundException
                 | IllegalAccessException
                 | InstantiationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Adds a new {@link ContentType} to a content section, making items of that
     * type available in the content section.
     *
     * @param type             The type to add (a subclass of
     *                         {@link ContentItem}.
     * @param section          The section to which the type is added.
     * @param defaultLifecycle The default lifecycle for items of the provided
     *                         type in the provided content section. The
     *                         lifecycle must be part of the provided section.
     *                         Otherwise an {@link IllegalArgumentException} is
     *                         thrown.
     * @param defaultWorkflow  The default workflow for items of the provided
     *                         type in the provided content section. The
     *                         workflow must be part of the provided section.
     *                         Otherwise an {@link IllegalArgumentException} is
     *                         thrown.
     *
     * @return The new {@link ContentType} instance.
     */
    public ContentType addContentTypeToSection(
        final Class<? extends ContentItem> type,
        final ContentSection section,
        final LifecycleDefinition defaultLifecycle,
        final WorkflowTemplate defaultWorkflow) {

        throw new UnsupportedOperationException();
    }

    /**
     * Checks if a content section has a {@link ContentType} for a specific
     * subclass {@link ContentItem}.
     *
     * @param type    The type to check for.
     * @param section The section to check for the {@link ContentType}.
     *
     * @return {@code true} if the section has a {@link ContentType} for
     *         {@code type}, {@code false} if not.
     */
    public boolean hasContentType(final Class<? extends ContentItem> type,
                                  final ContentSection section) {

        throw new UnsupportedOperationException();
    }

    /**
     * Removes an <em>unused</em> {@link ContentType} from a
     * {@link ContentSection}.
     *
     * @param type The type to remove from the section.
     * @param section The section from which the type is removed.
     *
     * @throws IllegalArgumentException if the provided {@link ContentType} is
     *                                  in use or the parameters or otherwise
     *                                  illegal.
     * @see ContentTypeRepository#delete(org.librecms.contentsection.ContentType) 
     */
    public void removeContentTypeFromSection(
        final Class<? extends ContentItem> type,
        final ContentSection section) {

        throw new UnsupportedOperationException();
    }

}
