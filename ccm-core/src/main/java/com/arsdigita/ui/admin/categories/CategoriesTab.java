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
import com.arsdigita.bebop.Tree;
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

    private final StringParameter categoryIdParameter;
    private final ParameterSingleSelectionModel<String> selectedCategoryId;

    private final StringParameter languageParameter;
    private final ParameterSingleSelectionModel<String> selectedLanguage;

    private final Label domainsFilterFormHeader;
    private final Form domainsFilterForm;
    private final BoxPanel domainsTablePanel;

    private final DomainForm domainForm;

    private final DomainDetails domainDetails;
    private final DomainTitleForm domainTitleForm;
    private final DomainDescriptionForm domainDescriptionForm;

    private final Label categoriesTreeHeader;
    private final BoxPanel categoriesTreePanel;

    public CategoriesTab() {
        super();

        setClassAttr("sidebarNavPanel");

        domainIdParameter = new StringParameter("selected_domain_id");
        selectedDomainId = new ParameterSingleSelectionModel<>(
                domainIdParameter);

        categoryIdParameter = new StringParameter("selected_category_id");
        selectedCategoryId = new ParameterSingleSelectionModel<>(
                categoryIdParameter);

        languageParameter = new StringParameter("selected_language");
        selectedLanguage = new ParameterSingleSelectionModel<>(
                languageParameter);

        final SegmentedPanel left = new SegmentedPanel();

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
        left.addSegment(domainsFilterFormHeader, domainsFilterForm);

        categoriesTreeHeader = new Label(new GlobalizedMessage(
                "ui.admin.categories.tree.header",
                ADMIN_BUNDLE));
        categoriesTreePanel = new BoxPanel(BoxPanel.VERTICAL);
        final Tree categoriesTree = new Tree(new CategoriesTreeModelBuilder(
                selectedDomainId));
        categoriesTree.addActionListener(e -> {

        });
        final ActionLink backToDomain = new ActionLink(new GlobalizedMessage(
                "ui.admin.categories.tree.back",
                ADMIN_BUNDLE));
        backToDomain.addActionListener(e -> {
            final PageState state = e.getPageState();
            categoriesTree.getSelectionModel().clearSelection(state);
            showDomainDetails(state);
        });
        categoriesTreePanel.add(backToDomain);
        categoriesTreePanel.add(categoriesTree);
        left.addSegment(categoriesTreeHeader, categoriesTreePanel);

        setLeft(left);

        final BoxPanel body = new BoxPanel(BoxPanel.VERTICAL);

        final DomainsTable domainsTable = new DomainsTable(
                this, selectedDomainId, domainsFilter);
        domainsTable.setStyleAttr("min-width: 30em;");
        domainsTablePanel = new BoxPanel(BoxPanel.VERTICAL);
        domainsTablePanel.add(domainsTable);
        final ActionLink addDomain = new ActionLink(new GlobalizedMessage(
                "ui.admin.categories.domains.create_new", ADMIN_BUNDLE));
        addDomain.addActionListener(e -> {
            showDomainForm(e.getPageState());
        });
        domainsTablePanel.add(addDomain);

        body.add(domainsTablePanel);

        domainForm = new DomainForm(this, selectedDomainId);
        body.add(domainForm);

        domainDetails = new DomainDetails(this,
                                          selectedDomainId,
                                          selectedLanguage);
        body.add(domainDetails);

        domainTitleForm = new DomainTitleForm(this,
                                              selectedDomainId,
                                              selectedLanguage);
        body.add(domainTitleForm);

        domainDescriptionForm = new DomainDescriptionForm(this,
                                                          selectedDomainId,
                                                          selectedLanguage);
        body.add(domainDescriptionForm);

        setBody(body);

    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(domainIdParameter);
        page.addGlobalStateParam(categoryIdParameter);
        page.addGlobalStateParam(languageParameter);

        page.setVisibleDefault(domainsFilterFormHeader, true);
        page.setVisibleDefault(domainsFilterForm, true);
        page.setVisibleDefault(domainsTablePanel, true);
        page.setVisibleDefault(domainForm, false);
        page.setVisibleDefault(domainDetails, false);
        page.setVisibleDefault(domainTitleForm, false);
        page.setVisibleDefault(domainDescriptionForm, false);
        page.setVisibleDefault(categoriesTreeHeader, false);
        page.setVisibleDefault(categoriesTreePanel, false);
    }

    protected void showDomainsTable(final PageState state) {
        domainsFilterFormHeader.setVisible(state, true);
        domainsFilterForm.setVisible(state, true);
        domainsTablePanel.setVisible(state, true);
        domainForm.setVisible(state, false);
        domainDetails.setVisible(state, false);
        domainTitleForm.setVisible(state, false);
        domainDescriptionForm.setVisible(state, false);
        categoriesTreeHeader.setVisible(state, false);
        categoriesTreePanel.setVisible(state, false);

    }

    protected void showDomainForm(final PageState state) {
        domainsFilterFormHeader.setVisible(state, false);
        domainsFilterForm.setVisible(state, false);
        domainsTablePanel.setVisible(state, false);
        domainForm.setVisible(state, true);
        domainDetails.setVisible(state, false);
        domainTitleForm.setVisible(state, false);
        domainDescriptionForm.setVisible(state, false);
        categoriesTreeHeader.setVisible(state, false);
        categoriesTreePanel.setVisible(state, false);
    }

    protected void hideDomainForm(final PageState state) {
        if (selectedDomainId.getSelectedKey(state) == null) {
            domainsFilterFormHeader.setVisible(state, true);
            domainsFilterForm.setVisible(state, true);
            domainsTablePanel.setVisible(state, true);
            domainForm.setVisible(state, false);
            domainDetails.setVisible(state, false);
            domainTitleForm.setVisible(state, false);
            domainDescriptionForm.setVisible(state, false);
            categoriesTreeHeader.setVisible(state, false);
            categoriesTreePanel.setVisible(state, false);
        } else {
            showDomainDetails(state);
        }

    }

    protected void showDomainDetails(final PageState state) {
        domainsFilterFormHeader.setVisible(state, false);
        domainsFilterForm.setVisible(state, false);
        domainsTablePanel.setVisible(state, false);
        domainForm.setVisible(state, false);
        domainDetails.setVisible(state, true);
        domainTitleForm.setVisible(state, false);
        domainDescriptionForm.setVisible(state, false);
        categoriesTreeHeader.setVisible(state, true);
        categoriesTreePanel.setVisible(state, true);
    }

    protected void hideDomainDetails(final PageState state) {
        selectedDomainId.clearSelection(state);

        domainsFilterFormHeader.setVisible(state, true);
        domainsFilterForm.setVisible(state, true);
        domainsTablePanel.setVisible(state, true);
        domainForm.setVisible(state, false);
        domainDetails.setVisible(state, false);
        domainTitleForm.setVisible(state, false);
        domainDescriptionForm.setVisible(state, false);
        categoriesTreeHeader.setVisible(state, false);
        categoriesTreePanel.setVisible(state, false);
    }

    protected void showDomainTitleForm(final PageState state) {
        domainsFilterFormHeader.setVisible(state, false);
        domainsFilterForm.setVisible(state, false);
        domainsTablePanel.setVisible(state, false);
        domainForm.setVisible(state, false);
        domainDetails.setVisible(state, false);
        domainTitleForm.setVisible(state, true);
        domainDescriptionForm.setVisible(state, false);
        categoriesTreeHeader.setVisible(state, false);
        categoriesTreePanel.setVisible(state, false);
    }

    protected void hideDomainTitleForm(final PageState state) {
        selectedLanguage.clearSelection(state);

        showDomainDetails(state);
    }

    protected void showDomainDescriptionForm(final PageState state) {
        domainsFilterFormHeader.setVisible(state, false);
        domainsFilterForm.setVisible(state, false);
        domainsTablePanel.setVisible(state, false);
        domainForm.setVisible(state, false);
        domainDetails.setVisible(state, false);
        domainTitleForm.setVisible(state, false);
        domainDescriptionForm.setVisible(state, true);
        categoriesTreeHeader.setVisible(state, false);
        categoriesTreePanel.setVisible(state, false);
    }

    protected void hideDomainDescriptionForm(final PageState state) {
        selectedLanguage.clearSelection(state);

        showDomainDetails(state);
    }

    protected void showCategoryDetails(final PageState state) {

    }

    protected void hideCategoryDetails(final PageState state) {

    }

    protected void showCategoryCreateForm(final PageState state) {

    }

    protected void hideCategoryCreateForm(final PageState state) {

    }

    protected void showCategoryEditForm(final PageState state) {

    }

    protected void hideCategoryEditForm(final PageState state) {

    }

    protected void showCategoryTitleForm(final PageState state) {

    }

    protected void hideCategoryTitleForm(final PageState state) {

    }

    protected void showCategoryDescriptionForm(final PageState state) {

    }

    protected void hideCategoryDescriptionForm(final PageState state) {
    }
}
