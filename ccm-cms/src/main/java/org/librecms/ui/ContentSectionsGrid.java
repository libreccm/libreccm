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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ContentSectionsGrid extends Grid<ContentSection> {

    private static final long serialVersionUID = -2840544148539285341L;

    private static final String COL_LABEL = "label";
    private static final String COL_EDIT = "edit";
    private static final String COL_DELETE = "delete";

    private final CmsViewController controller;

    public ContentSectionsGrid(final CmsViewController controller) {

        super();

        this.controller = controller;

        addComponentColumn(this::buildSectionLink)
            .setId(COL_LABEL)
            .setCaption("Content Section");
        addComponentColumn(this::buildEditButton)
            .setId(COL_EDIT)
            .setCaption("Edit");
        addComponentColumn(this::buildDeleteButton)
            .setId(COL_DELETE)
            .setCaption("Delete");

        setSelectionMode(SelectionMode.NONE);

        setDataProvider(controller.getSectionsDataProvider());

        if (controller.getPermissionChecker().isPermitted("admin")) {
            final HeaderRow actionsRow = prependHeaderRow();
            final HeaderCell actionsCell = actionsRow.join(COL_LABEL,
                                                           COL_EDIT,
                                                           COL_DELETE);

            final Button createButton = new Button("Create new content section",
                                                   VaadinIcons.PLUS_CIRCLE_O);
            createButton.addStyleName(ValoTheme.BUTTON_TINY);
            createButton.addClickListener(event -> {
            });

            final HorizontalLayout actionsLayout = new HorizontalLayout(
                createButton);
            actionsCell.setComponent(actionsLayout);
        }

    }

    private Component buildSectionLink(final ContentSection section) {

        final PermissionChecker permissionChecker = controller
            .getPermissionChecker();

        if (canAccessSection(section)) {
            final Button button = new Button();
            button.setCaption(section.getLabel());
            button.setStyleName(ValoTheme.BUTTON_LINK);
            button.addClickListener(event -> {
                getUI()
                    .getNavigator()
                    .navigateTo(String.format("%s/%s",
                                              ContentSectionView.VIEWNAME,
                                              section.getLabel()));
            });
            return button;
        } else {
            return new Label(section.getLabel());
        }
    }

    private boolean canAccessSection(final ContentSection section) {
        final List<String> adminPrivileges = controller
            .getPermissionManager()
            .listDefiniedPrivileges(AdminPrivileges.class);
        final List<String> itemPrivileges = controller
            .getPermissionManager()
            .listDefiniedPrivileges(ItemPrivileges.class);

        for (final String privilege : adminPrivileges) {
            if (controller.getPermissionChecker().isPermitted(privilege)) {
                return true;
            }
        }

        for (final String privilege : itemPrivileges) {
            if (controller.getPermissionChecker().isPermitted(privilege)) {
                return true;
            }
        }

        return controller.getPermissionChecker().isPermitted("admin");
    }

    private Component buildEditButton(final ContentSection section) {

        if (controller.getPermissionChecker().isPermitted("admin")) {
            final Button button = new Button("Edit", VaadinIcons.EDIT);
            button.addStyleName(ValoTheme.BUTTON_TINY);
            button.addClickListener(event -> {
            });
            return button;
        } else {
            return new Label("");
        }
    }

    private Component buildDeleteButton(final ContentSection section) {

        if (controller.getPermissionChecker().isPermitted("admin")) {
            final Button button = new Button("Delete", VaadinIcons.DEL);
            button.addStyleName(ValoTheme.BUTTON_TINY);
            button.addStyleName(ValoTheme.BUTTON_DANGER);
            button.addClickListener(event -> {
            });
            return button;
        } else {
            return new Label("");
        }

    }

}
