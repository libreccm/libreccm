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
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.ui.ConfirmDialog;
import org.libreccm.web.CcmApplication;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelsTab extends CustomComponent {

    private static final long serialVersionUID = -1116995764418892909L;

    private static final String COL_NAME = "name";
    private static final String COL_TITLE = "title";
    private static final String COL_DESC = "description";
    private static final String COL_LIVE = "live";
    private static final String COL_EDIT = "edit";
    private static final String COL_DELETE = "delete";

    private final Tree<ApplicationTreeNode> applicationTree;
    private final Grid<PageModelsTableRow> pageModelsGrid;

    protected PageModelsTab(final AdminViewController adminViewController) {

        super();

        applicationTree = new Tree<>(adminViewController
            .getApplicationTreeDataProvider());
        applicationTree.setItemCaptionGenerator(ApplicationTreeNode::getTitle);
        applicationTree.setItemCollapseAllowedProvider(node -> {
            return !node.getNodeType().equals(ApplicationTreeNodeType.ROOT_NODE);
        });

        final LocalizedTextsUtil localizedTextsUtil = adminViewController
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        pageModelsGrid = new Grid<>();
        pageModelsGrid.setDataProvider(adminViewController
            .getPageModelsController()
            .getPageModelsTableDataProvider());
        pageModelsGrid
            .addColumn(PageModelsTableRow::getName)
            .setCaption(localizedTextsUtil
                .getText("ui.admin.pagemodels.table.columns.headers.name"))
            .setId(COL_NAME);
        pageModelsGrid
            .addColumn(PageModelsTableRow::getTitle)
            .setCaption(localizedTextsUtil
                .getText("ui.admin.pagemodels.table.columns.headers.title"))
            .setId(COL_TITLE);
        pageModelsGrid
            .addColumn(PageModelsTableRow::getDescription)
            .setCaption(localizedTextsUtil
                .getText("ui.admin.pagemodels.table.columns.headers.desc"))
            .setId(COL_DESC);
        pageModelsGrid
            .addColumn(PageModelsTableRow::isPublished)
            .setCaption(localizedTextsUtil
                .getText("ui.admin.pagemodels.table.columns.headers.islive"))
            .setId(COL_LIVE);
        pageModelsGrid
            .addComponentColumn(row -> buildEditButton(row,
                                                       adminViewController))
            .setId(COL_EDIT);
        pageModelsGrid
            .addComponentColumn(row -> buildDeleteButton(row,
                                                         adminViewController))
            .setId(COL_DELETE);
        pageModelsGrid.setVisible(false);
        pageModelsGrid.setWidth("100%");

        final Button addPageModelButton = new Button(localizedTextsUtil
            .getText("ui.admin.pagemodels.create_new"));
        addPageModelButton.addStyleName(ValoTheme.BUTTON_TINY);
        addPageModelButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        addPageModelButton.addClickListener(event -> {
            final CcmApplication application
                                     = ((PageModelsTableDataProvider) pageModelsGrid
                                        .getDataProvider()).getApplication();
            final PageModelForm pageModelForm = new PageModelForm(
                adminViewController, application);
            pageModelForm.setModal(true);
            pageModelForm.setWidth("40%");
            pageModelForm.setHeight("60%");

            UI.getCurrent().addWindow(pageModelForm);
        });
        final HeaderRow headerRow = pageModelsGrid.prependHeaderRow();
        final HeaderCell headerCell = headerRow.join(COL_NAME,
                                                     COL_TITLE,
                                                     COL_DESC,
                                                     COL_LIVE,
                                                     COL_EDIT,
                                                     COL_DELETE);
        headerCell.setComponent(new HorizontalLayout(addPageModelButton));

        final Label placeholder = new Label(localizedTextsUtil.getText(
            "ui.admin.pagemodels.select_application"));

        final VerticalLayout layout = new VerticalLayout(pageModelsGrid,
                                                         placeholder);
        layout.setWidth("100%");

        applicationTree.addItemClickListener(event -> {

            final ApplicationTreeNode node = event.getItem();
            final ApplicationTreeNodeType nodeType = node.getNodeType();

            if (nodeType == ApplicationTreeNodeType.APPLICATION_NODE
                    || nodeType
                           == ApplicationTreeNodeType.SINGLETON_APPLICATION_NODE) {
                final PageModelsTableDataProvider dataProvider
                                                      = (PageModelsTableDataProvider) pageModelsGrid
                        .getDataProvider();
                dataProvider.setApplicationUuid(node.getNodeId());
                pageModelsGrid.setVisible(true);
                placeholder.setVisible(false);
            } else {
                pageModelsGrid.setVisible(false);
                placeholder.setVisible(true);
            }
        });

        final VerticalLayout treeLayout = new VerticalLayout(applicationTree);

        final HorizontalSplitPanel panel = new HorizontalSplitPanel(
            treeLayout, layout);
        panel.setSplitPosition(20.0f);
        super.setCompositionRoot(panel);
    }

    private Component buildEditButton(final PageModelsTableRow row,
                                      final AdminViewController controller) {

        final LocalizedTextsUtil localizedTextsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        final Button button = new Button(localizedTextsUtil
            .getText("ui.admin.pagemodels.table.columns.edit.label"),
                                         VaadinIcons.EDIT);
        button.addStyleName(ValoTheme.BUTTON_TINY);
        button.addClickListener(event -> {
            final PageModel pageModel = controller
                .getPageModelsController()
                .getPageModelRepo()
                .findById(row.getPageModelId())
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No PageModel with ID %d in the database.",
                        row.getPageModelId())));

            final CcmApplication application
                                     = ((PageModelsTableDataProvider) pageModelsGrid
                                        .getDataProvider()).getApplication();

            final PageModelDetails pageModelDetails = new PageModelDetails(
                pageModel, application, controller);
            pageModelDetails.center();
            pageModelDetails.setModal(true);
            pageModelDetails.setWidth("90%");
            pageModelDetails.setHeight("90%");
            UI.getCurrent().addWindow(pageModelDetails);
        });

        return button;
    }

    private Component buildDeleteButton(final PageModelsTableRow row,
                                        final AdminViewController controller) {

        final LocalizedTextsUtil localizedTextsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);

        final Button button = new Button(localizedTextsUtil
            .getText("ui.admin.pagemodels.table.columns.delete.label"),
                                         VaadinIcons.CLOSE_CIRCLE_O);
        button.addStyleNames(ValoTheme.BUTTON_TINY,
                             ValoTheme.BUTTON_DANGER);
        button.addClickListener(event -> {
            final ConfirmDialog confirmDialog = new ConfirmDialog(
                () -> {
                    controller
                        .getPageModelsController()
                        .deletePageModel(row.getPageModelId());

                    return null;
                });
            confirmDialog.setMessage(localizedTextsUtil
                .getText("ui.admin.pagemodels.delete.confirm"));
            confirmDialog.setModal(true);
            confirmDialog.center();
            UI.getCurrent().addWindow(confirmDialog);
        });
        button.setEnabled(!row.isPublished());

        return button;
    }

}
