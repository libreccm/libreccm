/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.bebop.event.ChangeListener;

import static com.arsdigita.ui.admin.AdminConstants.*;

/**
 *
 * @author David Dao
 */
class SearchAndList extends SimpleContainer
    implements AdminConstants,
               Resettable {

    /**
     * String catalog.
     */
    private static final String FORM_INPUT_NAME = "query";

    private static final GlobalizedMessage LABEL_SUBMIT = new GlobalizedMessage(
        "ui.admin.searchAndList.submit",
        BUNDLE_NAME);
    private static final GlobalizedMessage SEARCH_AGAIN = new GlobalizedMessage(
        "ui.admin.searchAndList.submitAgain",
        BUNDLE_NAME);

    private Form m_searchForm;

    private List m_searchResultList;

    private ParameterModel m_queryModel;

    private SearchAndListModel m_listModel;

    private SimpleContainer m_searchResultContainer;

    private class SearchListModelBuilder extends LockableImpl
        implements ListModelBuilder {

        public ListModel makeModel(List l, PageState state) {
            return m_listModel;
        }

    }

    private ListModelBuilder m_listModelBuilder = new SearchListModelBuilder();

    private FormProcessListener m_formProcessListener
                                = new FormProcessListener() {

            public void process(FormSectionEvent e) throws FormProcessException {

                FormData data = e.getFormData();
                PageState state = e.getPageState();
                String query = (String) data.get(FORM_INPUT_NAME);

                boolean visible = false;
                if (query == null || query.equals("")) {
                    visible = true;
                } else {
                    visible = false;
                }

                m_listModel.setQuery(query);
                m_searchForm.setVisible(state, visible);
                m_searchResultContainer.setVisible(state, !visible);

            }

        };

    public SearchAndList(String name) {
        super();

        /**
         * Create a search form.
         */
        m_searchForm = new Form(name, new BoxPanel(BoxPanel.HORIZONTAL));
        m_queryModel = new StringParameter(FORM_INPUT_NAME);
        TextField query = new TextField(m_queryModel);
        query.addValidationListener(new NotNullValidationListener());
        m_searchForm.add(query);
        m_searchForm.add(new Submit(LABEL_SUBMIT));
        m_searchForm.addProcessListener(m_formProcessListener);
        add(m_searchForm);

        /**
         * Create a search result container.
         */
        m_searchResultContainer = new SimpleContainer();
        add(m_searchResultContainer);

        m_searchResultList = new List();
        m_searchResultList.setClassAttr("SearchResultList");
        m_searchResultList.setModelBuilder(m_listModelBuilder);

        ActionLink link = new ActionLink(new Label(SEARCH_AGAIN));
        link.setClassAttr("actionLink");
        link.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PageState state = e.getPageState();
                m_searchForm.setVisible(state, true);
                m_searchResultContainer.setVisible(state, false);
            }

        });

        m_searchResultContainer.add(m_searchResultList);
        m_searchResultContainer.add(link);
    }

    public void addChangeListener(ChangeListener l) {
        m_searchResultList.addChangeListener(l);
    }

    public Object getSelectedKey(PageState ps) {
        return m_searchResultList.getSelectedKey(ps);
    }

    public void clearSelection(PageState ps) {
        m_searchResultList.clearSelection(ps);
    }

    public void setListModel(SearchAndListModel model) {
        m_listModel = model;
    }

    public void setResultCellRenderer(ListCellRenderer r) {
        m_searchResultList.setCellRenderer(r);
    }

    public void register(Page p) {
        p.setVisibleDefault(m_searchForm, true);
        p.setVisibleDefault(m_searchResultContainer, false);

    }

    public void reset(PageState ps) {
        m_searchResultContainer.setVisible(ps, false);
        m_searchForm.setVisible(ps, true);
        clearSelection(ps);
    }

}
