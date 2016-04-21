/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.categories;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoriesTab extends LayoutPanel {

    protected static final String DOMAINS_FILTER = "domainsFilter";

    private final StringParameter domainIdParameter;
    private final ParameterSingleSelectionModel<String> selectedDomainId;

    private final Label domainsFilterFormHeader;
    private final Form domainsFilterForm;
    private final BoxPanel domainsTablePanel;
    
    private final DomainForm domainForm;

    public CategoriesTab() {
        super();

        setClassAttr("sidebarNavPanel");

        domainIdParameter = new StringParameter("selected_domain_id");
        selectedDomainId = new ParameterSingleSelectionModel<>(domainIdParameter);

        domainsFilterFormHeader = new Label(new GlobalizedMessage(
                "ui.admin.categories.domains.table.filter.header",
                ADMIN_BUNDLE));
        domainsFilterForm = new Form("domainFilterForm");
        final TextField domainsFilter = new TextField(DOMAINS_FILTER);
        domainsFilterForm.add(domainsFilter);
        domainsFilterForm.add(new Submit(new GlobalizedMessage(
                "ui.admin.categories.domains.table.filter", ADMIN_BUNDLE)));
        final ActionLink clearLink = new ActionLink(new GlobalizedMessage(
                "ui.admin.categories.domains.table.filter.clear",
                ADMIN_BUNDLE));
        clearLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            domainsFilter.setValue(state, null);
        });
        domainsFilterForm.add(clearLink);

        final SegmentedPanel left = new SegmentedPanel();
        left.addSegment(domainsFilterFormHeader, domainsFilterForm);

        setLeft(left);
        
        final BoxPanel body = new BoxPanel(BoxPanel.VERTICAL);

        final DomainsTable domainsTable = new DomainsTable(
                selectedDomainId, domainsFilter);
        domainsTable.setStyleAttr("min-width: 30em;");
        domainsTablePanel = new BoxPanel(BoxPanel.VERTICAL);
        domainsTablePanel.add(domainsTable);
        final ActionLink addDomain = new ActionLink(new GlobalizedMessage(
                "ui.admin.categories.domains.create_new", ADMIN_BUNDLE));
        addDomain.addActionListener(e -> {
            showNewDomainForm(e.getPageState());
        });
        domainsTablePanel.add(addDomain);
        
        body.add(domainsTablePanel);
        
        domainForm = new DomainForm(this, selectedDomainId);
        body.add(domainForm);
        
        setBody(body);
        
        
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(domainIdParameter);
        
        page.setVisibleDefault(domainsFilterFormHeader, true);
        page.setVisibleDefault(domainsFilterForm, true);
        page.setVisibleDefault(domainsTablePanel, true);
        page.setVisibleDefault(domainForm, false);
    }

    protected void showDomainsTable(final PageState state) {
        domainsFilterFormHeader.setVisible(state, true);
        domainsFilterForm.setVisible(state, true);
        domainsTablePanel.setVisible(state, true);
        domainForm.setVisible(state, false);
    }

    protected void showNewDomainForm(final PageState state) {
        domainsFilterFormHeader.setVisible(state, false);
        domainsFilterForm.setVisible(state, false);
        domainsTablePanel.setVisible(state, false);
        domainForm.setVisible(state, true);
    }

    protected void hideNewDomainForm(final PageState state) {
        domainsFilterFormHeader.setVisible(state, true);
        domainsFilterForm.setVisible(state, true);
        domainsTablePanel.setVisible(state, true);
        domainForm.setVisible(state, false);
    }

}
