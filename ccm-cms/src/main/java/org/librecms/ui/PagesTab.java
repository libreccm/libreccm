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
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.ui.ConfirmDialog;
import org.librecms.CmsConstants;
import org.librecms.pages.Pages;
import org.librecms.pages.PagesRepository;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PagesTab extends CustomComponent {

    private static final long serialVersionUID = 8237082714759130342L;

    private static final String COL_NAME = "name";
    private static final String COL_SITE = "site";
    private static final String COL_EDIT = "edit";
    private static final String COL_DEL = "del";

    private final CmsViewController controller;
    private final LocalizedTextsUtil textsUtil;

    PagesTab(final CmsViewController controller) {

        super();

        this.controller = controller;

        textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(CmsConstants.CMS_BUNDLE);

        final Grid<Pages> pagesGrid = new Grid<>();
//        pagesGrid
//            .addColumn(Pages::getPrimaryUrl)
//            .setCaption("cms.ui.contentcenter.pagestable.columns.name.header")
//            .setId(COL_NAME);
        pagesGrid
            .addComponentColumn(this::buildDetailsLink)
            .setCaption(textsUtil
                .getText("cms.ui.contentcenter.pagestable.columns.name.header"))
            .setId(COL_NAME);
        pagesGrid
            .addColumn(pages -> pages.getSite().getDomainOfSite())
            .setCaption(textsUtil
                .getText("cms.ui.contentcenter.pagestable.columns.site.header"))
            .setId(COL_SITE);
        pagesGrid
            .addComponentColumn(this::buildEditButton)
            .setCaption(textsUtil
                .getText("cms.ui.contentcenter.pagestable.columns.edit.header"))
            .setId(COL_EDIT);
        pagesGrid
            .addComponentColumn(this::buildDeleteButton)
            .setCaption(textsUtil
                .getText("cms.ui.contentcenter.pagestable.columns.delete.header"))
            .setId(COL_DEL);

        final Button addPagesButton = new Button(textsUtil
            .getText("cms.ui.contentcenter.pages.add_link"));
        addPagesButton.addStyleName(ValoTheme.BUTTON_TINY);
        addPagesButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        addPagesButton.addClickListener(this::addPagesButtonClicked);
        final HeaderRow headerRow = pagesGrid.prependHeaderRow();
        final HeaderCell headerCell = headerRow
            .join(COL_NAME, COL_SITE, COL_EDIT, COL_DEL);
        headerCell.setComponent(new HorizontalLayout(addPagesButton));

        pagesGrid.setDataProvider(controller
            .getPagesController()
            .getPagesGridDataProvider());
        pagesGrid.setWidth("100%");

        super.setCompositionRoot(new VerticalLayout(pagesGrid));
    }

    private Component buildDetailsLink(final Pages pages) {

        final Button button = new Button(pages.getPrimaryUrl());
        button.addStyleName(ValoTheme.BUTTON_LINK);
        button.addClickListener(event -> detailsLinkClicked(event, pages));

        return button;
    }

    private Component buildEditButton(final Pages pages) {

        final Button button = new Button(textsUtil
            .getText("cms.ui.contentcenter.pages.edit.label"));
        button.setIcon(VaadinIcons.EDIT);
        button.addStyleName(ValoTheme.BUTTON_TINY);
        button.addClickListener(event -> editButtonClicked(event, pages));

        return button;
    }

    private Component buildDeleteButton(final Pages pages) {

        final Button button = new Button(textsUtil
            .getText("cms.ui.contentcenter.pages.delete.label"));
        button.setIcon(VaadinIcons.MINUS_CIRCLE_O);
        button.addStyleNames(ValoTheme.BUTTON_TINY,
                             ValoTheme.BUTTON_DANGER);
        button.addClickListener(event -> deleteButtonClicked(event, pages));

        return button;
    }

    private void addPagesButtonClicked(final Button.ClickEvent event) {

        final PagesEditor editor = new PagesEditor(controller);
        editor.setModal(true);
        editor.setWidth("40%");
        editor.setHeight("60%");

        UI.getCurrent().addWindow(editor);
    }

    private void detailsLinkClicked(final Button.ClickEvent event,
                                    final Pages pages) {

        final PagesDetails pagesDetails = new PagesDetails(pages, controller);
        pagesDetails.setModal(true);
        pagesDetails.setWidth("90%");
        pagesDetails.setHeight("90%");

        UI.getCurrent().addWindow(pagesDetails);
    }

    private void editButtonClicked(final Button.ClickEvent event,
                                   final Pages pages) {

        final PagesEditor pagesEditor = new PagesEditor(pages, controller);
        pagesEditor.setModal(true);
        pagesEditor.setWidth("40%");
        pagesEditor.setHeight("80%");

        UI.getCurrent().addWindow(pagesEditor);
    }

    private void deleteButtonClicked(final Button.ClickEvent event,
                                     final Pages pages) {

        final ConfirmDialog confirmDialog
                                = new ConfirmDialog(() -> deletePages(pages));
        confirmDialog.setMessage(textsUtil
            .getText("cms.ui.contentcenter.pages.delete.confirm"));
        confirmDialog.setModal(true);

        UI.getCurrent().addWindow(confirmDialog);
    }

    private Void deletePages(final Pages pages) {

        final PagesRepository pagesRepo = controller
            .getPagesController()
            .getPagesRepo();

        pagesRepo.delete(pages);
        controller
            .getPagesController()
            .getPagesGridDataProvider()
            .refreshAll();

        return null;
    }

}
