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

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.Optional;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@CDIView(value = ContentSectionView.VIEWNAME,
         uis = {CmsUI.class})
class ContentSectionView extends CustomComponent implements View {

    private static final long serialVersionUID = 602851260519364741L;

    public static final String VIEWNAME = "ContentSection";

    private static final String TAB_DOCUMENTS = "tab_documents";
    private static final String TAB_SEARCH = "tab_search";
    private static final String TAB_MEDIA = "tab_media";
    private static final String TAB_ROLES = "tab_roles";
    private static final String TAB_WORKFLOWS = "tab_workflows";
    private static final String TAB_LIFECYCLES = "tab_lifecycles";
    private static final String TAB_DOCUMENT_TYPES = "tab_document_types";

    private final ContentSectionViewController controller;

    private ContentSection selectedSection;

    private final TabSheet tabSheet;
    private final Panel noSectionPanel;

    private final FolderBrowser folderBrowser;

    @Inject
    ContentSectionView(final ContentSectionViewController controller) {

        super();

        this.controller = controller;

        folderBrowser = new FolderBrowser(controller);
        final VerticalLayout folderBrowserLayout = new VerticalLayout();
        folderBrowserLayout.setHeight("100%");
        folderBrowserLayout.addComponentsAndExpand(folderBrowser);
        
        tabSheet = new TabSheet();
        tabSheet
            .addTab(folderBrowser, "Documents")
            .setId(TAB_DOCUMENTS);
        tabSheet
            .addTab(new Label("Search placeholder"), "Search")
            .setId(TAB_SEARCH);
        tabSheet
            .addTab(new Label("Media & Records placeholder"), "Media & Records")
            .setId(TAB_MEDIA);
        tabSheet
            .addTab(new Label("Roles placeholder"), "Roles")
            .setId(TAB_ROLES);
        tabSheet
            .addTab(new Label("Workflows Placeholder"), "Workflows")
            .setId(TAB_WORKFLOWS);
        tabSheet
            .addTab(new Label("Lifecycles placeholder"), "Lifecycles")
            .setId(TAB_LIFECYCLES);
        tabSheet
            .addTab(new Label("Document types placeholder"), "Documents types")
            .setId(TAB_DOCUMENT_TYPES);

        tabSheet.addSelectedTabChangeListener(event -> {

            final Component selectedTab = event.getTabSheet().getSelectedTab();

            if (TAB_DOCUMENTS.equals(selectedTab.getId())) {

                final FolderBrowser browser = (FolderBrowser) selectedTab;

                browser
                    .getFolderTree()
                    .expand(controller
                        .getContentSectionViewState()
                        .getSelectedContentSection()
                        .getRootDocumentsFolder());
            }
        });

        tabSheet.setHeight("100%");
        
        noSectionPanel = new Panel();
        noSectionPanel.setVisible(false);

        final VerticalLayout layout = new VerticalLayout();
        layout.addComponentsAndExpand(tabSheet, noSectionPanel);
        layout.setHeight("100%");
        layout.addStyleName("content-section-view-layout");

        super.setCompositionRoot(layout);
        super.setHeight("100%");
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        final String parameters = event.getParameters();

        if (parameters == null || parameters.trim().isEmpty()) {
            tabSheet.setVisible(false);
            noSectionPanel.setCaption("No content section selected");
            noSectionPanel.setContent(new Label("No content section selected"));
            noSectionPanel.setVisible(true);
        } else {
            final ContentSectionRepository sectionRepo = controller
                .getSectionRepository();

            final Optional<ContentSection> contentSection = sectionRepo
                .findByLabel(parameters);

            if (contentSection.isPresent()) {
                selectedSection = contentSection.get();
                controller
                    .getContentSectionViewState()
                    .setSelectedContentSection(selectedSection);

                folderBrowser
                    .getFolderTree()
                    .expand(controller
                        .getContentSectionViewState()
                        .getSelectedContentSection()
                        .getRootDocumentsFolder());
            } else {
                tabSheet.setVisible(false);
                noSectionPanel.setCaption(String
                    .format("No content section \"%s\"", parameters));
                noSectionPanel.setContent(new Label(String
                    .format("No content section with label \"%s\" found.",
                            parameters)));
                noSectionPanel.setVisible(true);
            }
        }

    }

}
