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
package org.libreccm.admin.ui;

import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.sites.Site;
import org.libreccm.theming.ThemeInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class SiteEditor extends Window {

    private static final long serialVersionUID = 7016158691791408762L;

    private final Site site;
    private final AdminViewController controller;

    private boolean dataHasChanged = false;

    public SiteEditor(final AdminViewController controller) {
        super();

        this.site = null;
        this.controller = controller;

        addWidgets();
    }

    public SiteEditor(final Site site, final AdminViewController controller) {

        super();

        this.site = site;
        this.controller = controller;

        addWidgets();
    }

    private void addWidgets() {

        final LocalizedTextsUtil adminTextsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        if (site == null) {
            setCaption(adminTextsUtil.getText("ui.admin.sites.create_new"));
        } else {
            setCaption(adminTextsUtil.getText("ui.admin.sites.edit"));
        }

        final TextField domainOfSiteField = new TextField(
            adminTextsUtil.getText("ui.admin.sites.domain_of_site"));
        domainOfSiteField.addValueChangeListener(event -> {
            dataHasChanged = true;
        });

        final CheckBox isDefaultSiteCheckBox = new CheckBox(
            adminTextsUtil.getText("ui.admin.sites.is_default_site"));
        isDefaultSiteCheckBox.addValueChangeListener(event -> {
            dataHasChanged = true;
        });

        final List<String> themes = controller
            .getSitesController()
            .getThemes()
            .getAvailableThemes()
            .stream()
            .map(ThemeInfo::getName)
            .collect(Collectors.toList());

        final NativeSelect<String> defaultThemeSelect = new NativeSelect<>(
            adminTextsUtil.getText("ui.admin.sites.default_theme"), themes);
        defaultThemeSelect.addValueChangeListener(event -> {
            dataHasChanged = true;
        });

        final Button saveButton = new Button();
        if (site == null) {
            saveButton.setCaption(adminTextsUtil.getText(
                "ui.admin.sites.buttons.save.create"));
        } else {
            saveButton.setCaption(adminTextsUtil.getText(
                "ui.admin.sites.buttons.save.changed"));
        }
        saveButton.addClickListener(event -> {
            if (dataHasChanged) {

                final String domainOfSite = domainOfSiteField.getValue();
                if (domainOfSite == null
                        || domainOfSite.isEmpty()
                        || domainOfSite.matches("\\s*")) {

                    domainOfSiteField.setComponentError(new UserError(
                        adminTextsUtil.getText(
                            "ui.admin.sites.domain_of_site.error.empty")));
                    return;
                }
                
                if (!controller.getSitesController().isUnique(domainOfSite)) {
                    domainOfSiteField.setComponentError(new UserError(
                        adminTextsUtil.getText(
                            "ui.admin.sites.domain_of_site.error.not_unique")));
                    return;
                }
                
                site.setDomainOfSite(domainOfSite);
                site.setDefaultSite(isDefaultSiteCheckBox.getValue());
                site.setDefaultTheme(defaultThemeSelect.getValue());
                
                controller
                    .getSitesController()
                    .getSiteRepository()
                    .save(site);
                controller
                    .getSitesController()
                    .getSitesTableDataProvider()
                    .refreshAll();
                close();
            }
        });

        final Button cancelButton = new Button(adminTextsUtil
            .getText("ui.admin.sites.buttons.cancel"));
        cancelButton.addClickListener(event -> {
            close();
        });

        super.setContent(new VerticalLayout(
            new FormLayout(domainOfSiteField,
                           isDefaultSiteCheckBox,
                           defaultThemeSelect),
            new HorizontalLayout(saveButton, cancelButton)));
    }

}
