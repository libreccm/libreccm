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

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class CreationSelectorController {

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected ContentSection getContentSectionForFolder(final Folder folder) {

        final Folder theFolder = folderRepo
            .findById(folder.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No Folder with ID %d in the database. Where did that ID come from?",
            folder.getObjectId())));

        final ContentSection section = theFolder.getSection();

        return sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database.",
            section.getObjectId())));
    }

}
