/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.documents;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.FolderType;
import org.librecms.ui.contentsections.ItemPermissionChecker;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
@Dependent
public abstract class AbstractMvcDocumentCreateStep<T extends ContentItem>
    implements MvcDocumentCreateStep<T> {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private FolderRepository folderRepository;

    /**
     * Provides operations for folders.
     */
    @Inject
    private FolderManager folderManager;

    /**
     * Provides functions for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private IdentifierParser identifierParser;

    private boolean canCreate;

    /**
     * The current folder.
     */
    private Folder folder;

    /**
     * The current content section.
     */
    private ContentSection section;

    /**
     * Messages to be shown to the user.
     */
    private SortedMap<String, String> messages;

    public AbstractMvcDocumentCreateStep() {
        messages = new TreeMap<>();
    }

    @Override
    public ContentSection getContentSection() {
        return section;
    }
    
    @Override
    public void setContentSection(final ContentSection section) {
        this.section = section;
    }

    @Override
    public String getContentSectionLabel() {
        return section.getLabel();
    }

    @Override
    public String getContentSectionTitle() {
        return globalizationHelper.getValueFromLocalizedString(
            section.getTitle()
        );
    }

//    protected final void setContentSectionByIdentifier(
//        final String sectionIdentifierParam
//    ) {
//        final Identifier identifier = identifierParser.parseIdentifier(
//            sectionIdentifierParam
//        );
//        final Optional<ContentSection> sectionResult;
//        switch (identifier.getType()) {
//            case ID:
//                sectionResult = sectionRepo.findById(
//                    Long.parseLong(identifier.getIdentifier())
//                );
//                break;
//            case UUID:
//                sectionResult = sectionRepo.findByUuid(
//                    identifier.getIdentifier()
//                );
//                break;
//            default:
//                sectionResult = sectionRepo.findByLabel(
//                    identifier.getIdentifier()
//                );
//                break;
//        }
//
//        if (sectionResult.isPresent()) {
//            section = sectionResult.get();
//            canCreate = true;
//        } else {
//            messages.put("error", "ContentSection not found.");
//            canCreate = false;
//        }
//    }

//    protected final void setFolderByPath(final String folderPath) {
//        final Optional<Folder> folderResult = folderRepository.findByPath(
//            section,
//            folderPath,
//            FolderType.DOCUMENTS_FOLDER
//        );
//        if (folderResult.isPresent()) {
//            folder = folderResult.get();
//            if (itemPermissionChecker.canCreateNewItems(folder)) {
//                canCreate = true;
//            } else {
//                canCreate = false;
//                messages.put("error", "Not allowed");
//            }
//        } else {
//            messages.put("error", "Folder not found.");
//            canCreate = false;
//        }
//    }
    @Override
    public boolean getCanCreate() {
        return canCreate;
    }

    @Override
    public Folder getFolder() {
        return folder;
    }

    @Override
    public void setFolder(final Folder folder) {
        this.folder = folder;
    }

    @Override
    public String getFolderPath() {
        return folderManager.getFolderPath(folder);
    }

    @Override
    public Map<String, String> getMessages() {
        return Collections.unmodifiableSortedMap(messages);
    }

    public void addMessage(final String context, final String message) {
        messages.put(context, message);
    }

    public void setMessages(final SortedMap<String, String> messages) {
        this.messages = new TreeMap<>(messages);
    }

}
