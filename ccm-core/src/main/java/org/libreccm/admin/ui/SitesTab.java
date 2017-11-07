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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.sites.Site;
import org.libreccm.ui.ConfirmDialog;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class SitesTab extends CustomComponent {

    private static final long serialVersionUID = 9188476373782633282L;

    private static final String COL_DOMAIN_OF_SITE = "domain_of_site";
    private static final String COL_IS_DEFAULT_SITE = "is_default_site";
    private static final String COL_DEFAULT_THEME = "default_theme";
    private static final String COL_APPLICATIONS = "applications";
    private static final String COL_EDIT = "edit";
    private static final String COL_DELETE = "delete";

    protected SitesTab(final AdminViewController adminViewController) {

        super();

        final LocalizedTextsUtil adminTextsUtil = adminViewController
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        final SitesController controller = adminViewController
            .getSitesController();

        final Grid<SitesTableRowData> sitesGrid = new Grid<>();
        sitesGrid
            .addColumn(SitesTableRowData::getDomainOfSite)
            .setCaption(adminTextsUtil.getText(
                "ui.admin.sites.table.columns.domain.header"))
            .setId(COL_DOMAIN_OF_SITE);
        sitesGrid
            .addColumn(SitesTableRowData::isDefaultSite)
            .setCaption(adminTextsUtil.getText(
                "ui.admin.sites.table.columns.default_site.header"))
            .setId(COL_IS_DEFAULT_SITE);
        sitesGrid
            .addColumn(SitesTableRowData::getDefaultTheme)
            .setCaption(adminTextsUtil.getText(
                "ui.admin.sites.table.columns.default_theme.header"))
            .setId(COL_DEFAULT_THEME);
        sitesGrid
            .addComponentColumn(row -> {
                return new Label(String.join("\n",
                                             row.getApplications()),
                                 ContentMode.PREFORMATTED);
            })
            .setCaption(adminTextsUtil
                .getText("ui.admin.sites.table.columns.applications.header"))
            .setId(COL_APPLICATIONS);
        sitesGrid
            .addComponentColumn(row -> {
                final Button editButton = new Button(adminTextsUtil
                    .getText("ui.admin.sites.table.buttons.edit"),
                                                     VaadinIcons.EDIT);
                editButton.addClickListener(event -> {
                    final Site site = adminViewController
                        .getSitesController()
                        .getSiteRepository()
                        .findById(row.getSiteId())
                        .orElseThrow(() -> new IllegalArgumentException(String
                        .format("No Site with ID %d in the database.",
                                row.getSiteId())));
                    final SiteEditor editor = new SiteEditor(
                        site, adminViewController);
                    editor.center();
                    editor.setWidth("66%");
                    editor.setHeight("80%");
                    UI.getCurrent().addWindow(editor);
                });
                editButton.addStyleName(ValoTheme.BUTTON_TINY);

                return editButton;
            })
            .setId(COL_EDIT);
        sitesGrid
            .addComponentColumn(row -> {
                if (row.isDeletable()) {
                    final Button deleteButton = new Button(
                        adminTextsUtil
                            .getText("ui.admin.sites.table.buttons.delete"),
                        VaadinIcons.MINUS_CIRCLE_O);
                    deleteButton.addClickListener(event -> {

                        final ConfirmDialog dialog = new ConfirmDialog(() -> {
                            adminViewController
                                .getSitesController()
                                .delete(row.getSiteId());

                            Notification.show(adminTextsUtil
                                .getText("ui.admin.sites.site_deleted",
                                         new Object[]{row.getDomainOfSite()}),
                                              Notification.Type.TRAY_NOTIFICATION);
                            return null;
                        });
                        dialog.setMessage(adminTextsUtil
                            .getText("ui.admin.sites.site_delete.confirm",
                                     new Object[]{row.getDomainOfSite()}));
                        dialog.setModal(true);
                        dialog.center();
                        UI.getCurrent().addWindow(dialog);
                    });
                    deleteButton.addStyleNames(ValoTheme.BUTTON_TINY,
                                               ValoTheme.BUTTON_DANGER);

                    return deleteButton;
                } else {
                    return new Label("");
                }
            })
            .setId(COL_DELETE);
        final HeaderRow headerRow = sitesGrid.prependHeaderRow();
        final HeaderCell headerCell = headerRow.join(COL_DOMAIN_OF_SITE,
                                                     COL_IS_DEFAULT_SITE,
                                                     COL_DEFAULT_THEME,
                                                     COL_APPLICATIONS,
                                                     COL_EDIT,
                                                     COL_DELETE);
        final Button newSiteButton = new Button(adminTextsUtil
            .getText("ui.admin.sites.add_new_site_link"),
                                                VaadinIcons.PLUS_CIRCLE_O);
        newSiteButton.addStyleName(ValoTheme.BUTTON_TINY);
        newSiteButton.addClickListener(event -> {
            final SiteEditor editor = new SiteEditor(adminViewController);
            editor.center();
            editor.setWidth("66%");
            editor.setHeight("80%");
            UI.getCurrent().addWindow(editor);
        });
        final HorizontalLayout headerLayout
                                   = new HorizontalLayout(newSiteButton);
        headerCell.setComponent(headerLayout);
        sitesGrid.setDataProvider(adminViewController
            .getSitesController()
            .getSitesTableDataProvider());
        sitesGrid.setWidth("100%");

        super.setCompositionRoot(sitesGrid);
    }

}
