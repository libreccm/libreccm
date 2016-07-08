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

import static org.librecms.CmsConstants.*;

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
        final String contentSectionNames;
        if (getIntegrationProps().containsKey(INITIAL_CONTENT_SECTIONS)) {
            contentSectionNames = getIntegrationProps().getProperty(
                INITIAL_CONTENT_SECTIONS);
        } else {
            contentSectionNames = "info";
        }

        for (final String contentSectionName : contentSectionNames.split(",")) {
            createContentSection(contentSectionName);
        }
    }

    private void createContentSection(final String contentSectionName) {
        final ContentSection section = new ContentSection();
        section.setLabel(contentSectionName);

        final Category rootFolder = new Category();
        rootFolder.setName(String.format("%s_root", contentSectionName));

        final Category rootAssetFolder = new Category();
        rootFolder.setName(String.format("%s_assets", contentSectionName));

        section.setRootDocumentFolder(rootFolder);
        section.setRootAssetsFolder(rootAssetFolder);

        getEntityManager().persist(section);
        getEntityManager().persist(rootFolder);
        getEntityManager().persist(rootAssetFolder);

        final Role alertRecipient = createRole(String.format(
            "%s_alert_recipient", contentSectionName));
        final Role author = createRole(String.format("%s_author",
                                                     contentSectionName));
        final Role editor = createRole(String.format("%s_editor",
                                                     contentSectionName));
        final Role manager = createRole(String.format("%s_manager",
                                                      contentSectionName));
        final Role publisher = createRole(String.format("%s_publisher",
                                                        contentSectionName));
        final Role contentReader = createRole(String.format("%s_content_reader",
                                                            contentSectionName));

        grantPermissions(author,
                         rootFolder,
                         PRIVILEGE_ITEMS_CATEGORIZE,
                         PRIVILEGE_ITEMS_CREATE_NEW,
                         PRIVILEGE_ITEMS_EDIT,
                         PRIVILEGE_ITEMS_VIEW_PUBLISHED,
                         PRIVILEGE_ITEMS_PREVIEW);

        grantPermissions(editor, 
                         rootFolder, 
                         PRIVILEGE_ITEMS_CATEGORIZE,
                         PRIVILEGE_ITEMS_CREATE_NEW,
                         PRIVILEGE_ITEMS_EDIT,
                         PRIVILEGE_ITEMS_APPROVE,
                         PRIVILEGE_ITEMS_DELETE,
                         PRIVILEGE_ITEMS_VIEW_PUBLISHED,
                         PRIVILEGE_ITEMS_PREVIEW);
        
        grantPermissions(manager,
                         rootFolder, 
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
        
        grantPermissions(editor, 
                         rootFolder, 
                         PRIVILEGE_ITEMS_CATEGORIZE,
                         PRIVILEGE_ITEMS_CREATE_NEW,
                         PRIVILEGE_ITEMS_EDIT,
                         PRIVILEGE_ITEMS_APPROVE,
                         PRIVILEGE_ITEMS_PUBLISH,
                         PRIVILEGE_ITEMS_DELETE,
                         PRIVILEGE_ITEMS_VIEW_PUBLISHED,
                         PRIVILEGE_ITEMS_PREVIEW);
        
        grantPermissions(contentReader, 
                        rootFolder, 
                        PRIVILEGE_ITEMS_VIEW_PUBLISHED);
        

        getEntityManager().persist(alertRecipient);
        getEntityManager().persist(author);
        getEntityManager().persist(editor);
        getEntityManager().persist(manager);
        getEntityManager().persist(publisher);
        getEntityManager().persist(contentReader);

    }

}
