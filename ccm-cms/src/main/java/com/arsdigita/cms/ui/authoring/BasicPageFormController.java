/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.authoring;

import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemInitializer;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;

import java.util.Locale;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class BasicPageFormController {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private WorkflowRepository workflowRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected <T extends ContentItem> T createContentItem(
        final String name,
        final ContentSection section,
        final Folder folder,
        final Class<T> clazz,
        final ContentItemInitializer<T> initializer,
        final Locale locale) {

        return createContentItem(name,
                                 section,
                                 folder,
                                 null,
                                 clazz,
                                 initializer,
                                 locale);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected <T extends ContentItem> T createContentItem(
        final String name,
        final ContentSection section,
        final Folder folder,
        final Workflow workflowTemplate,
        final Class<T> clazz,
        final ContentItemInitializer<T> initializer,
        final Locale locale) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentSection with ID %d in the database.",
                    section.getObjectId())));

        final Folder itemFolder = folderRepo
            .findById(folder.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Folder with ID %d in the database.",
                    folder.getObjectId())));

        final T item;
        if (workflowTemplate == null) {

            item = itemManager.createContentItem(name,
                                                 contentSection,
                                                 itemFolder,
                                                 clazz,
                                                 initializer,
                                                 locale);

        } else {
            final Workflow itemWorkflowTemplate = workflowRepo
                .findById(workflowTemplate.getWorkflowId())
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No WorkflowTemplate with ID %d in the database.",
                        workflowTemplate.getWorkflowId())));

            item = itemManager.createContentItem(name,
                                                 contentSection,
                                                 itemFolder,
                                                 itemWorkflowTemplate,
                                                 clazz,
                                                 initializer,
                                                 locale);
        }

        return item;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected Optional<Folder> getItemFolder(final ContentItem item) {

        final Optional<ContentItem> contentItem = itemRepo
            .findById(item.getObjectId());

        if (contentItem.isPresent()) {
            return itemManager.getItemFolder(contentItem.get());
        } else {
            return Optional.empty();
        }
    }

}
