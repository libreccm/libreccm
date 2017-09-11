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

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.ItemCollapseAllowedProvider;
import com.vaadin.ui.Tree;
import org.librecms.contentsection.Folder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class BrowseDocuments extends CustomComponent {

    private static final long serialVersionUID = -7241214812224026430L;

    private static final String COL_DOCUMENT_CREATED = "created";
    private static final String COL_DOCUMENT_LAST_MODIFIED = "lastmodified";
    private static final String COL_DOCUMENT_NAME = "name";
    private static final String COL_DOCUMENT_TITLE = "title";
    private static final String COL_DOCUMENT_TYPE = "ttype";

    private final ContentSectionViewController controller;

    private final Tree<Folder> folderTree;
    private final Grid<BrowseDocumentsItem> documentsGrid;
    private final BrowseDocumentsDataProvider documentsDataProvider;
    private final BrowseDocumentsFolderTreeDataProvider folderTreeDataProvider;

    public BrowseDocuments(final ContentSectionViewController controller) {

        super();

        this.controller = controller;

        folderTreeDataProvider = controller.getFolderTreeDataProvider();
        folderTree = new Tree<>(folderTreeDataProvider);
        folderTree.setItemCaptionGenerator(folder -> {
            return controller
                .getGlobalizationHelper()
                .getValueFromLocalizedString(folder.getTitle());
        });

        documentsGrid = new Grid<>();
        documentsGrid
            .addColumn(BrowseDocumentsItem::getName)
            .setCaption("Name")
            .setId(COL_DOCUMENT_NAME);
        documentsGrid
            .addColumn(BrowseDocumentsItem::getTitle)
            .setCaption("Title")
            .setId(COL_DOCUMENT_TITLE);
        documentsGrid
            .addColumn(BrowseDocumentsItem::getType)
            .setCaption("Type")
            .setId(COL_DOCUMENT_TYPE);
        documentsGrid
            .addColumn(BrowseDocumentsItem::getCreationDate)
            .setCaption("Created")
            .setId(COL_DOCUMENT_CREATED);
        documentsGrid
            .addColumn(BrowseDocumentsItem::getLastModified)
            .setCaption("Last modified")
            .setId(COL_DOCUMENT_LAST_MODIFIED);
        documentsDataProvider = controller.getBrowseDocumentsDataProvider();
        documentsGrid.setDataProvider(documentsDataProvider);
        documentsGrid.setWidth("100%");
        documentsGrid.setHeight("100%");

        folderTree.addItemClickListener(event -> {
            documentsDataProvider.setCurrentFolder(event.getItem());
            documentsDataProvider.refreshAll();
        });
        folderTree.setItemCollapseAllowedProvider(folder -> {
            return folder.getParentCategory() != null;
        });

        final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(
            folderTree, documentsGrid);
        splitPanel.setSplitPosition(17.5f, Unit.PERCENTAGE);
        splitPanel.setHeight("100%");
        super.setCompositionRoot(splitPanel);
    }

    public Tree<Folder> getFolderTree() {
        return folderTree;
    }

    public Grid<BrowseDocumentsItem> getDocumentsGrid() {
        return documentsGrid;
    }

}
