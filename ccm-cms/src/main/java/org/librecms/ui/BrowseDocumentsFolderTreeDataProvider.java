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
package org.librecms.ui;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Data provider for the tree component of the {@link BrowseDocuments}
 * component.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
public class BrowseDocumentsFolderTreeDataProvider
    extends AbstractBackEndHierarchicalDataProvider<Folder, String> {

    private static final long serialVersionUID = 5330319780008907163L;

    @Inject
    private ContentSectionViewState contentSectionViewState;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Override
    protected Stream<Folder> fetchChildrenFromBackEnd(
        HierarchicalQuery<Folder, String> query) {

        final Optional<Folder> selectedParent = query.getParentOptional();

        final Folder parent;
        if (selectedParent.isPresent()) {
            parent = folderRepo
                .findById(selectedParent.get().getObjectId())
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No folder with ID %d in the database.",
                        selectedParent.get().getObjectId())));
            
            return folderRepo.findSubFolders(parent).stream();
        } else {
            final ContentSection section = sectionRepo
                .findById(contentSectionViewState
                    .getSelectedContentSection()
                    .getObjectId())
                .orElseThrow(() -> new UnexpectedErrorException(String
                .format("No ContentSection with ID %d in the database.",
                        contentSectionViewState
                            .getSelectedContentSection()
                            .getObjectId())));

            final List<Folder> result = new ArrayList<>();
            result.add(section.getRootDocumentsFolder());
            return result.stream();
        }

    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public int getChildCount(
        final HierarchicalQuery<Folder, String> query) {

        final Optional<Folder> selectedParent = query.getParentOptional();

        final Folder parent;
        if (selectedParent.isPresent()) {
            parent = folderRepo
                .findById(selectedParent.get().getObjectId())
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No folder with ID %d in the database.",
                        selectedParent.get().getObjectId())));
        } else {
            final ContentSection section = sectionRepo
                .findById(contentSectionViewState
                    .getSelectedContentSection()
                    .getObjectId())
                .orElseThrow(() -> new UnexpectedErrorException(String
                .format("No ContentSection with ID %d in the database.",
                        contentSectionViewState
                            .getSelectedContentSection()
                            .getObjectId())));

            parent = section.getRootDocumentsFolder();
        }

        return (int) folderRepo.countSubFolders(parent);

    }

    @Override
    public boolean hasChildren(final Folder item) {
        return folderRepo.countSubFolders(item) > 0;
    }

}
