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

import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.categorization.Domain;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.sites.Site;
import org.librecms.CmsConstants;
import org.librecms.pages.Pages;
import org.librecms.pages.PagesManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PagesEditor extends Window {

    private static final long serialVersionUID = -7895690663139812879L;

    private final CmsViewController controller;
    private final LocalizedTextsUtil textsUtil;

    private Pages pages;

    private final TextField nameField;
    private final ComboBox<Site> siteSelect;
    private final ComboBox<Domain> categorySystemSelect;

    PagesEditor(final CmsViewController controller) {

        super();

        this.controller = controller;

        textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(CmsConstants.CMS_BUNDLE);

        nameField = new TextField(textsUtil
            .getText("cms.ui.pages.form.primary_url_field.label"));

        siteSelect = new ComboBox<>(textsUtil
            .getText("cms.ui.pages.form.site_select.error"));
        siteSelect.setDataProvider(controller
            .getPagesController()
            .getPagesEditorSiteSelectDataProvider());
        siteSelect.setEmptySelectionAllowed(false);
        siteSelect.setItemCaptionGenerator(this::buildSiteCaption);

        categorySystemSelect = new ComboBox<>(textsUtil
            .getText("cms.ui.pages.form.category_domain_select.label"));
        categorySystemSelect.setEmptySelectionAllowed(false);
        categorySystemSelect.setDataProvider(controller
            .getPagesController()
            .getPagesEditorDomainSelectDataProvider());
        categorySystemSelect.setItemCaptionGenerator(Domain::getDomainKey);

        final FormLayout formLayout = new FormLayout(nameField,
                                                     siteSelect,
                                                     categorySystemSelect);

        final Button saveButton = new Button(textsUtil
            .getText("cms.ui.pages.editor.save"));
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(this::saveButtonClicked);

        final Button cancelButton = new Button(textsUtil
            .getText("cms.ui.pages.editor.cancel"));
        cancelButton.addStyleName(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(event -> close());

        super.setContent(new VerticalLayout(formLayout,
                                            new HorizontalLayout(saveButton,
                                                                 cancelButton)));
    }

    PagesEditor(final Pages pages,
                final CmsViewController controller) {

        this(controller);

        this.pages = pages;

        nameField.setValue(pages.getPrimaryUrl());
        siteSelect.setEnabled(false);
        categorySystemSelect.setEnabled(false);
    }

    private String buildSiteCaption(final Site site) {

        if (site.isDefaultSite()) {
            return String.format("%s *",
                                 site.getDomainOfSite());
        } else {
            return site.getDomainOfSite();
        }
    }

    private void saveButtonClicked(final Button.ClickEvent event) {

        if (nameField.getValue() == null
                || nameField.getValue().isEmpty()
                || nameField.getValue().matches("\\s*")) {

            nameField.setComponentError(new UserError(textsUtil
                .getText("cms.ui.pages.form.primary_url_field.error")));
            return;
        }

        if (pages == null
                && !siteSelect.getSelectedItem().isPresent()) {

            siteSelect.setComponentError(new UserError(textsUtil
                .getText("cms.ui.pages.form.site_select.error")));

            return;
        }

        if (pages == null
                && !categorySystemSelect.getSelectedItem().isPresent()) {

            categorySystemSelect.setComponentError(new UserError(textsUtil
                .getText("cms.ui.pages.form.category_domain_select.error")));

            return;
        }

        if (pages == null) {
            final PagesController pagesController = controller
                .getPagesController();

            pages = pagesController
                .createPages(generatePrimaryUrl(nameField.getValue()),
                             siteSelect.getValue(),
                             categorySystemSelect.getValue());
        } else {

            pages.setPrimaryUrl(generatePrimaryUrl(nameField.getValue()));
            controller.getPagesController().getPagesRepo().save(pages);
        }

        controller.getPagesController().getPagesGridDataProvider().refreshAll();
        close();
    }

    private String generatePrimaryUrl(final String name) {

        if (name.startsWith("/") && name.endsWith("/")) {
            return name;
        } else if (name.startsWith("/") && !name.endsWith("/")) {
            return String.format("%s/", name);
        } else if (!name.startsWith("/") && name.endsWith("/")) {
            return String.format("/%s", name);
        } else {
            return String.format("/%s/", name);
        }
    }

}
