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
package org.librecms.ui.contentsections.assets;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractMvcAssetCreateStep<T extends Asset>
    implements MvcAssetCreateStep<T> {

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

    public AbstractMvcAssetCreateStep() {
        messages = new TreeMap<>();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Map<String, String> getAvailableLocales() {
        return globalizationHelper
            .getAvailableLocales()
            .stream()
            .collect(
                Collectors.toMap(
                    locale -> locale.toString(),
                    locale -> locale.toString(),
                    (value1, value2) -> value1,
                    () -> new LinkedHashMap<String, String>()
                )
            );
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public ContentSection getContentSection() {
        return section;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void setContentSection(final ContentSection section) {
        this.section = section;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String getContentSectionLabel() {
        return section.getLabel();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String getContentSectionTitle() {
        return globalizationHelper.getValueFromLocalizedString(
            section.getTitle()
        );
    }

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
        if (folder.getParentFolder() == null) {
            return "";
        } else {
            return folderManager.getFolderPath(folder);
        }
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
