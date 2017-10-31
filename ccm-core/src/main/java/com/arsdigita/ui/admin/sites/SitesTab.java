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
package com.arsdigita.ui.admin.sites;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SitesTab extends LayoutPanel {

    private final ParameterSingleSelectionModel<String> selectedSiteId;
    private final SitesTable sitesTable;
    private final SitesForm sitesForm;

    public SitesTab() {
        super();

        super.setClassAttr("sidebarNavPanel");

        final BoxPanel left = new BoxPanel(BoxPanel.VERTICAL);

        selectedSiteId = new ParameterSingleSelectionModel<>(
            new StringParameter("selected_site_id"));

        sitesTable = new SitesTable(this, selectedSiteId);
        sitesForm = new SitesForm(this, selectedSiteId);

        final ActionLink addNewSite = new ActionLink(new GlobalizedMessage(
            "ui.admin.sites.add_new_site_link",
            ADMIN_BUNDLE));
        addNewSite
            .addActionListener(event -> {
                showSiteForm(event.getPageState());
            });

        final BoxPanel right = new BoxPanel(BoxPanel.VERTICAL);
        right.add(addNewSite);
        right.add(sitesTable);
        right.add(sitesForm);

        setLeft(left);
        setRight(right);
    }

    @Override
    public void register(final Page page) {

        super.register(page);

        page.addGlobalStateParam(selectedSiteId.getStateParameter());

        page.setVisibleDefault(sitesTable, true);
        page.setVisibleDefault(sitesForm, false);
    }

    protected void showSiteForm(final PageState state) {
        sitesTable.setVisible(state, false);
        sitesForm.setVisible(state, true);
    }

    protected void hideSiteForm(final PageState state) {
        sitesTable.setVisible(state, true);
        sitesForm.setVisible(state, false);
    }

}
