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


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.modules.InstallEvent;
import org.libreccm.security.Role;
import org.libreccm.web.AbstractCcmApplicationSetup;
import org.librecms.CmsConstants;

import java.util.UUID;

import static org.librecms.contentsection.ContentSection.*;

import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.contentsection.privileges.AssetPrivileges;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.Event;
import org.librecms.contenttypes.MultiPartArticle;
import org.librecms.contenttypes.News;
import org.librecms.dispatcher.MultilingualItemResolver;

import java.util.Arrays;

import org.librecms.contentsection.privileges.TypePrivileges;

import java.util.Locale;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSectionSetup extends AbstractCcmApplicationSetup {

    private static final Logger LOGGER = LogManager.getLogger(
            ContentSectionSetup.class);

    private static final String INITIAL_CONTENT_SECTIONS
                                = "org.librecms.initial_content_sections";
    private static final String DEFAULT_ITEM_RESOLVER
                                = "org.librecms.default_item_resolver";
    private static final String[] DEFAULT_TYPES = new String[]{
        Article.class.getName(),
        Event.class.getName(),
        MultiPartArticle.class.getName(),
        News.class.getName()};

    public ContentSectionSetup(final InstallEvent event) {
        super(event);
    }

    @Override
    public void setup() {
        final String sectionNames;
        if (getIntegrationProps().containsKey(INITIAL_CONTENT_SECTIONS)) {
            sectionNames = getIntegrationProps().getProperty(
                    INITIAL_CONTENT_SECTIONS);
            LOGGER.info(
                    "Found names for initial content sections in integration "
                            + "properties: {}", sectionNames);
        } else {
            sectionNames = "info";
            LOGGER.info("No initial content sections definied integration "
                                + "properties, using default: {}", sectionNames);
        }

        for (final String contentSectionName : sectionNames.split(",")) {
            createContentSection(contentSectionName);
        }
    }

    private void createContentSection(final String sectionName) {
        LOGGER.debug("Creating content section with section name \"{}\"...",
                     sectionName);
        final ContentSection section = new ContentSection();
        section.setUuid(UUID.randomUUID().toString());
        section.setApplicationType(CmsConstants.CONTENT_SECTION_APP_TYPE);
        section.setPrimaryUrl(String.format("/%s/", sectionName));
        section.setDisplayName(sectionName);
        section.setLabel(sectionName);

        if (getIntegrationProps().getProperty(DEFAULT_ITEM_RESOLVER) == null
                    || getIntegrationProps().getProperty(DEFAULT_ITEM_RESOLVER)
                        .trim().isEmpty()) {
            section.setItemResolverClass(getIntegrationProps().getProperty(
                    DEFAULT_ITEM_RESOLVER));
        } else {
            section.setItemResolverClass(MultilingualItemResolver.class
                    .getName());
        }

        LOGGER.debug("New content section properties: "
                             + "uuid = {}; "
                             + "applicationType = \"{}\"; "
                             + "primaryUrl = \"{}\"; "
                             + "displayName = \"{}\"; "
                             + "label = \"{}\"",
                     section.getUuid(),
                     section.getApplicationType(),
                     section.getPrimaryUrl(),
                     section.getDisplayName(),
                     section.getLabel());
        
        final Folder rootFolder = new Folder();
        rootFolder.setUuid(UUID.randomUUID().toString());
        rootFolder.setUniqueId(rootFolder.getUuid());
        rootFolder.setName(String.format("%s_" + ROOT, sectionName));
        rootFolder.getTitle().addValue(new Locale("en"), "/");
        rootFolder.setType(FolderType.DOCUMENTS_FOLDER);
        rootFolder.setSection(section);

        final Folder rootAssetFolder = new Folder();
        rootAssetFolder.setName(String.format("%s_" + ASSETS, sectionName));
        rootAssetFolder.getTitle().addValue(new Locale("en"), "/");
        rootAssetFolder.setUuid(UUID.randomUUID().toString());
        rootAssetFolder.setUniqueId(rootAssetFolder.getUuid());
        rootAssetFolder.setType(FolderType.ASSETS_FOLDER);
        rootAssetFolder.setSection(section);

        section.setRootDocumentFolder(rootFolder);
        section.setRootAssetsFolder(rootAssetFolder);

        getEntityManager().persist(section);
        getEntityManager().persist(rootFolder);
        getEntityManager().persist(rootAssetFolder);

        LOGGER.debug(
                "Creating default roles and permissions for content section "
                        + "'{}'...",
                sectionName);
        final Role alertRecipient = createRole(String.format(
                "%s_" + ALERT_RECIPIENT, sectionName));
        final Role author = createRole(String.format("%s_" + AUTHOR,
                                                     sectionName));
        final Role editor = createRole(String.format("%s_" + EDITOR,
                                                     sectionName));
        final Role manager = createRole(String.format("%s_" + MANAGER,
                                                      sectionName));
        final Role publisher = createRole(String.format("%s_" + PUBLISHER,
                                                        sectionName));
        final Role contentReader = createRole(String.format(
                "%s_" + CONTENT_READER, sectionName));

        grantPermissions(author,
                         rootFolder,
                         ItemPrivileges.CATEGORIZE,
                         ItemPrivileges.CREATE_NEW,
                         ItemPrivileges.EDIT,
                         ItemPrivileges.VIEW_PUBLISHED,
                         ItemPrivileges.PREVIEW);

        grantPermissions(author,
                         rootAssetFolder,
                         AssetPrivileges.USE,
                         AssetPrivileges.CREATE_NEW,
                         AssetPrivileges.EDIT,
                         AssetPrivileges.VIEW,
                         AssetPrivileges.DELETE);

        grantPermissions(editor,
                         rootFolder,
                         ItemPrivileges.CATEGORIZE,
                         ItemPrivileges.CREATE_NEW,
                         ItemPrivileges.EDIT,
                         ItemPrivileges.APPROVE,
                         ItemPrivileges.DELETE,
                         ItemPrivileges.VIEW_PUBLISHED,
                         ItemPrivileges.PREVIEW);

        grantPermissions(editor,
                         rootAssetFolder,
                         AssetPrivileges.USE,
                         AssetPrivileges.CREATE_NEW,
                         AssetPrivileges.EDIT,
                         AssetPrivileges.VIEW,
                         AssetPrivileges.DELETE);

        grantPermissions(manager,
                         section,
                         AdminPrivileges.ADMINISTER_ROLES,
                         AdminPrivileges.ADMINISTER_WORKFLOW,
                         AdminPrivileges.ADMINISTER_LIFECYLES,
                         AdminPrivileges.ADMINISTER_CATEGORIES,
                         AdminPrivileges.ADMINISTER_CONTENT_TYPES);

        grantPermissions(manager,
                         rootFolder,
                         ItemPrivileges.ADMINISTER,
                         ItemPrivileges.CATEGORIZE,
                         ItemPrivileges.CREATE_NEW,
                         ItemPrivileges.EDIT,
                         ItemPrivileges.APPROVE,
                         ItemPrivileges.PUBLISH,
                         ItemPrivileges.DELETE,
                         ItemPrivileges.VIEW_PUBLISHED,
                         ItemPrivileges.PREVIEW);

        grantPermissions(manager,
                         rootAssetFolder,
                         AssetPrivileges.USE,
                         AssetPrivileges.CREATE_NEW,
                         AssetPrivileges.EDIT,
                         AssetPrivileges.VIEW,
                         AssetPrivileges.DELETE);

        grantPermissions(publisher,
                         rootFolder,
                         ItemPrivileges.CATEGORIZE,
                         ItemPrivileges.CREATE_NEW,
                         ItemPrivileges.EDIT,
                         ItemPrivileges.APPROVE,
                         ItemPrivileges.PUBLISH,
                         ItemPrivileges.DELETE,
                         ItemPrivileges.VIEW_PUBLISHED,
                         ItemPrivileges.PREVIEW);

        grantPermissions(publisher,
                         rootAssetFolder,
                         AssetPrivileges.USE,
                         AssetPrivileges.CREATE_NEW,
                         AssetPrivileges.EDIT,
                         AssetPrivileges.VIEW,
                         AssetPrivileges.DELETE);

        grantPermissions(contentReader,
                         rootFolder,
                         ItemPrivileges.VIEW_PUBLISHED);

        grantPermissions(contentReader,
                         rootAssetFolder,
                         AssetPrivileges.VIEW);

        getEntityManager().persist(alertRecipient);
        getEntityManager().persist(author);
        getEntityManager().persist(editor);
        getEntityManager().persist(manager);
        getEntityManager().persist(publisher);
        getEntityManager().persist(contentReader);

        section.addRole(alertRecipient);
        section.addRole(author);
        section.addRole(editor);
        section.addRole(manager);
        section.addRole(publisher);
        section.addRole(contentReader);

        LOGGER.debug("Setting ItemResolver for content section '{}'...",
                     sectionName);
        final String itemResolverClassName;
        if (getIntegrationProps().containsKey(String.format("%s.item_resolver",
                                                            sectionName))) {

            itemResolverClassName = getIntegrationProps().getProperty(
                    String.format("%s.item_resolver",
                                  sectionName));
            LOGGER.debug("integration.properties contains setting for the item "
                                 + "resolver of content section '{}'. Using "
                                 + "item resolver '{}'.",
                         sectionName, itemResolverClassName);
        } else if (getIntegrationProps().containsKey("default_item_resolver")) {
            itemResolverClassName = getIntegrationProps().getProperty(
                    "default_item_resolver_name");
            LOGGER.debug("integration.properties contains setting for the "
                                 + "default item resolver. Using item "
                                 + "resolver '{}'.",
                         itemResolverClassName);
        } else {
            itemResolverClassName = MultilingualItemResolver.class.getName();
            LOGGER.debug("integration.properties contains *no* setting for item "
                    + "resolver. Using default item resolver '{}'.",
                         itemResolverClassName);
        }
        section.setItemResolverClass(itemResolverClassName);

        LOGGER.debug("Adding default content types to content section '{}'...",
                     sectionName);
        final String[] types;
        if (getIntegrationProps().containsKey(String.format("%s.content_types",
                                                            sectionName))) {
            final String typesStr = getIntegrationProps().getProperty(String
                    .format("%s.content_types", sectionName));
            LOGGER.debug("integration.properties contains setting for content "
                    + "types of section '{}': {}",
                         sectionName,
                         typesStr);
            types = typesStr.split(",");
        } else if (getIntegrationProps().containsKey("default_content_types")) {
            final String typesStr = getIntegrationProps().getProperty(
                    "default_content_types");
            LOGGER.debug("integration.properties contains setting for default "
                    + "content types for all sections: {}",
                         typesStr);
            types = typesStr.split(",");
        } else {
            LOGGER.debug("integration.properties contains not settings for "
                    + "default content types. Using internal default: {}",
                         String.join(", ", DEFAULT_TYPES));
            types = DEFAULT_TYPES;
        }
        Arrays.stream(types).forEach(type -> addContentTypeToSection(section,
                                                                     type,
                                                                     author,
                                                                     editor,
                                                                     manager));

        getEntityManager().merge(section);
    }

    private void addContentTypeToSection(final ContentSection section,
                                         final String contentType,
                                         final Role... roles) {
        final String typeClassName = contentType.trim();
        LOGGER.debug("Adding content type '{}' to content section '{}'...",
                     contentType,
                     section.getPrimaryUrl());
        final Class<?> clazz;
        try {
            clazz = Class.forName(typeClassName);
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(String.format(
                    "No class for content type '%s'.", typeClassName));
        }

        if (ContentItem.class.isAssignableFrom(clazz)) {
            LOGGER.warn("'{}' is assignable from '{}'!",
                        ContentItem.class.getName(),
                        clazz.getName());
            final ContentType type = new ContentType();
            type.setContentSection(section);
            type.setUuid(UUID.randomUUID().toString());
            type.setDisplayName(clazz.getSimpleName());
            type.setContentItemClass(clazz.getName());
            getEntityManager().persist(type);
            section.addContentType(type);
            
            Arrays.stream(roles)
                    .forEach(role -> grantPermission(role, 
                                                     TypePrivileges.USE_TYPE, 
                                                     type));
        } else {
            throw new UnexpectedErrorException(String.format(
                    "The class '%s' is not a sub class of '%s'.",
                    clazz.getName(),
                    ContentItem.class.getName()));
        }
    }

}
