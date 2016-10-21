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
import org.libreccm.categorization.Category;
import org.libreccm.modules.InstallEvent;
import org.libreccm.security.Role;
import org.libreccm.web.AbstractCcmApplicationSetup;
import org.librecms.CmsConstants;

import java.util.UUID;

import static org.librecms.CmsConstants.*;
import static org.librecms.contentsection.ContentSection.*;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.contentsection.privileges.AssetPrivileges;
import org.librecms.contentsection.privileges.ItemPrivileges;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSectionSetup extends AbstractCcmApplicationSetup {

    private static final Logger LOGGER = LogManager.getLogger(
        ContentSectionSetup.class);

    private static final String INITIAL_CONTENT_SECTIONS
                                    = "org.librecms.initial_content_sections";

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
        rootFolder.setSection(section);

        final Folder rootAssetFolder = new Folder();
        rootAssetFolder.setName(String.format("%s_" + ASSETS, sectionName));
        rootAssetFolder.setUuid(UUID.randomUUID().toString());
        rootAssetFolder.setUniqueId(rootAssetFolder.getUuid());
        rootAssetFolder.setSection(section);

        section.setRootDocumentFolder(rootFolder);
        section.setRootAssetsFolder(rootAssetFolder);

        getEntityManager().persist(section);
        getEntityManager().persist(rootFolder);
        getEntityManager().persist(rootAssetFolder);

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

    }

}
