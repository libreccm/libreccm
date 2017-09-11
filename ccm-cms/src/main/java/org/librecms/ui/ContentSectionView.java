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
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
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

    private final ContentSectionViewController controller;

    private ContentSection selectedSection;

    private final TabSheet tabSheet;
    private final Panel noSectionPanel;

    @Inject
    ContentSectionView(final ContentSectionViewController controller) {

        super();

        this.controller = controller;

        final BrowseDocuments browseDocuments = new BrowseDocuments(controller);
        
        tabSheet = new TabSheet();
        tabSheet.addTab(browseDocuments, "Documents");
        tabSheet.addTab(new Label("Search placeholder"), "Search");
        tabSheet.addTab(new Label("Media & Records placeholder"),
                        "Media & Records");
        tabSheet.addTab(new Label("Roles placeholder"), "Roles");
        tabSheet.addTab(new Label("Workflows Placeholder"), "Workflows");
        tabSheet.addTab(new Label("Lifecycles placeholder"), "Lifecycles");
        tabSheet.addTab(new Label("Document types placeholder"),
                        "Documents types");

        noSectionPanel = new Panel();
        noSectionPanel.setVisible(false);
        
        final VerticalLayout layout = new VerticalLayout(tabSheet, 
            noSectionPanel);
        
        super.setCompositionRoot(layout);
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
