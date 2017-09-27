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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.librecms.contentsection.Folder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FolderBrowser extends CustomComponent {

    private static final long serialVersionUID = -7241214812224026430L;

    private static final String COL_DOCUMENT_CREATED = "created";
    private static final String COL_DOCUMENT_LAST_MODIFIED = "lastmodified";
    private static final String COL_DOCUMENT_NAME = "name";
    private static final String COL_DOCUMENT_TITLE = "title";
    private static final String COL_DOCUMENT_TYPE = "ttype";

    private final ContentSectionViewController controller;

    private final Tree<Folder> folderTree;
    private final Grid<FolderBrowserItem> documentsGrid;

    private final Button renameCurrentFolderButton;

    private final FolderBrowserDataProvider documentsDataProvider;
    private final FolderBrowserFolderTreeDataProvider folderTreeDataProvider;

    public FolderBrowser(final ContentSectionViewController controller) {

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
            .addComponentColumn(item -> buildFolderItemLink(item))
            .setCaption("Name")
            .setId(COL_DOCUMENT_NAME);
        documentsGrid
            .addColumn(FolderBrowserItem::getTitle)
            .setCaption("Title")
            .setId(COL_DOCUMENT_TITLE);
        documentsGrid
            .addColumn(FolderBrowserItem::getType)
            .setCaption("Type")
            .setId(COL_DOCUMENT_TYPE);
        documentsGrid
            .addColumn(FolderBrowserItem::getCreationDate,
                       new DateRenderer("%tF"))
            .setCaption("Created")
            .setId(COL_DOCUMENT_CREATED);
        documentsGrid
            .addColumn(FolderBrowserItem::getLastModified,
                       new DateRenderer("%tF"))
            .setCaption("Last modified")
            .setId(COL_DOCUMENT_LAST_MODIFIED);
        documentsDataProvider = controller.getBrowseDocumentsDataProvider();
        documentsGrid.setDataProvider(documentsDataProvider);
        documentsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        documentsGrid.setWidth("100%");
        documentsGrid.setHeight("100%");

        final Button createSubFolderButton = new Button("New subfolder",
                                                        VaadinIcons.PLUS_CIRCLE_O);
        createSubFolderButton.addStyleName(ValoTheme.BUTTON_TINY);
        renameCurrentFolderButton = new Button("Rename current folder",
                                               VaadinIcons.EDIT);
        renameCurrentFolderButton.addStyleName(ValoTheme.BUTTON_TINY);
        renameCurrentFolderButton.setEnabled(false);
        renameCurrentFolderButton.setVisible(false);
        final HeaderRow headerRow = documentsGrid.prependHeaderRow();
        final HeaderCell actionsCell = headerRow.join(COL_DOCUMENT_NAME,
                                                      COL_DOCUMENT_TITLE,
                                                      COL_DOCUMENT_TYPE,
                                                      COL_DOCUMENT_CREATED,
                                                      COL_DOCUMENT_LAST_MODIFIED);
        actionsCell.setComponent(new HorizontalLayout(createSubFolderButton,
                                                      renameCurrentFolderButton));

        folderTree.addItemClickListener(event -> {
//            documentsDataProvider.setCurrentFolder(event.getItem());
//            documentsDataProvider.refreshAll();
            setCurrentFolder(event.getItem());
        });
        folderTree.setItemCollapseAllowedProvider(folder -> {
            return folder.getParentCategory() != null;
        });

//        final Button root = new Button("/  ");
//        root.addStyleName(ValoTheme.BUTTON_LINK);
//        root.addClickListener(event -> {
//            folderTree.getSelectionModel().deselectAll();
//            documentsDataProvider.setCurrentFolder(null);
//            documentsDataProvider.refreshAll();
//        });
//        final VerticalLayout folders = new VerticalLayout(root, folderTree);
        final VerticalLayout folderTreeLayout = new VerticalLayout(folderTree);

        final VerticalLayout documentsGridLayout = new VerticalLayout();
        documentsGridLayout.addComponentsAndExpand(documentsGrid);

        final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(
            folderTreeLayout, documentsGridLayout);
        splitPanel.setSplitPosition(17.5f, Unit.PERCENTAGE);
        splitPanel.setHeight("100%");
        final VerticalLayout layout = new VerticalLayout();
        layout.addComponentsAndExpand(splitPanel);
        super.setCompositionRoot(layout);
    }

    private Component buildFolderItemLink(final FolderBrowserItem item) {

        final Button itemLink = new Button();
        itemLink.setCaption(item.getName());
        itemLink.setStyleName(ValoTheme.BUTTON_LINK);
        itemLink.addClickListener(event -> folderBrowserItemClicked(event,
                                                                    item));
        if (item.isFolder()) {
            itemLink.setIcon(VaadinIcons.FOLDER);
        }

        return itemLink;
    }

    private void folderBrowserItemClicked(final Button.ClickEvent event,
                                          final FolderBrowserItem item) {

        if (item.isFolder()) {
            final Folder folder = controller
                .getFolderRepository()
                .findById(item.getItemId())
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                "No Folder with ID %d in the database.",
                item.getItemId())));
//            folderTree.expand(folder);
//            documentsDataProvider.setCurrentFolder(folder);
//            documentsDataProvider.refreshAll();
            setCurrentFolder(folder);
        }
    }

    private void setCurrentFolder(final Folder folder) {
        folderTree.expand(folder);
        documentsDataProvider.setCurrentFolder(folder);
        documentsDataProvider.refreshAll();

        renameCurrentFolderButton.setEnabled(folder.getParentCategory() != null);
        renameCurrentFolderButton.setVisible(folder.getParentCategory() != null);
    }

    public Tree<Folder> getFolderTree() {
        return folderTree;
    }

    public Grid<FolderBrowserItem> getDocumentsGrid() {
        return documentsGrid;
    }

    private class FolderDialog extends Window {

        private static final long serialVersionUID = -6767403288966354533L;

        private final TextField nameField;
        private final TextField titleField;
        private final Button submitButton;

        private Folder currentFolder;

        public FolderDialog() {
            super();

            nameField = new TextField("Name");
            nameField.setDescription("The name (URL-Fragment) of the folder.");
            nameField.setRequiredIndicatorVisible(true);
            nameField.setMaxLength(256);

            titleField = new TextField("Title");
            titleField.setDescription("The title of the folder.");
            titleField.setRequiredIndicatorVisible(true);
            titleField.setMaxLength(256);

            final Button cancelButton = new Button("Cancel");
            cancelButton.addClickListener(event -> close());

            submitButton = new Button("Submit");
            submitButton.addClickListener(event -> save());

            final FormLayout layout = new FormLayout(nameField,
                                                     titleField,
                                                     cancelButton,
                                                     submitButton);

            super.setContent(layout);
        }

        public FolderDialog(final Folder folder) {
            this();

            currentFolder = folder;
            nameField.setValue(currentFolder.getName());

        }

        private void save() {
            this.close();
        }

    }
}