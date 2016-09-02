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
     * Creates a new content section including the default roles.
     *
     * @param name The name of the new content section.
     *
     * @return The new content section.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
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

        final Category rootFolder = new Category();
        rootFolder.setName(String.format("%s_root", name));
        rootFolder.getTitle().addValue(defautLocale, rootFolder.getName());
        rootFolder.setDisplayName(rootFolder.getName());
        rootFolder.setUuid(UUID.randomUUID().toString());
        rootFolder.setUniqueId(rootFolder.getUuid());
        rootFolder.setCategoryOrder(1L);

        final Category rootAssetFolder = new Category();
        rootAssetFolder.setName(String.format("%s_assets", name));
        rootAssetFolder.getTitle().addValue(defautLocale,
                                            rootAssetFolder.getName());
        rootAssetFolder.setDisplayName(rootAssetFolder.getName());
        rootAssetFolder.setUuid(UUID.randomUUID().toString());
        rootAssetFolder.setUniqueId(rootAssetFolder.getUuid());
        rootAssetFolder.setCategoryOrder(1L);

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
     * manually
     *
     * @param section The section to rename.
     *
     * @@param name The new name of the content section.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
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

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addRoleToContentSection(final ContentSection section,
                                        final String roleName,
                                        final String... privileges) {
        final Role role = new Role();
        role.setName(String.join("_", section.getLabel(), roleName));
        roleRepo.save(role);

        final Category rootFolder = section.getRootDocumentsFolder();
        for (String privilege : privileges) {
            permissionManager.grantPrivilege(privilege, role, rootFolder);
        }

        section.addRole(role);
        sectionRepo.save(section);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeRoleFromContentSection(
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

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addTypeToSection(final ContentType type,
                                 final ContentSection section) {
        throw new UnsupportedOperationException();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeTypeFromSection(final ContentType type,
                                      final ContentSection section) {
        throw new UnsupportedOperationException();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addLifecycleDefinitionToContentSection(
        final LifecycleDefinition definition,
        final ContentSection section) {
        throw new UnsupportedOperationException();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeLifecycleDefinitionFromContentSection(
        final LifecycleDefinition definition,
        final ContentSection contentSection) {
        throw new UnsupportedOperationException();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addWorkflowTemplateToContentSection(
        final WorkflowTemplate definition,
        final ContentSection section) {
        throw new UnsupportedOperationException();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)

    public void removeWorkflowTemplateFromContentSection(
        final LifecycleDefinition definition,
        final ContentSection contentSection) {
        throw new UnsupportedOperationException();
    }

    public ItemResolver getItemResolver(final ContentSection section) {
        try {
            final Class<ItemResolver> itemResolverClazz
                                      = (Class<ItemResolver>) Class.
                forName(section.getItemResolverClass());
            return itemResolverClazz.newInstance();
        } catch (ClassNotFoundException |
                 IllegalAccessException |
                 InstantiationException ex) {
            throw new RuntimeException(ex);
        }
    }

}
