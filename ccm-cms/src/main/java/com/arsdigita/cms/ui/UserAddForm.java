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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import org.libreccm.security.User;
import org.librecms.CmsConstants;

import java.math.BigDecimal;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * Form for adding multiple users to a role.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:pihman@arsdigita.com">Michael Pih</a>
 * @author <a href="mailto:umathur@arsdigita.com">Uday Mathur</a>
 */
public abstract class UserAddForm extends SimpleContainer
    implements FormProcessListener {

    private final static String SEARCH_QUERY = "searchQuery";
    private final static String USERS = "users";
    private final static String SUBMIT = "addSubmit";
    private final static String CANCEL = "addCancel";

    private final static String DQ_USER_ID = "userId";
    private final static String DQ_NAME = "name";

    private Widget m_search;
    private RequestLocal m_query;
    private String m_label;
    private String m_submitText;

    private CMSContainer m_noMatches;
    private CMSContainer m_matches;

    private Form m_form;
    private Hidden m_searchQuery;
    private CheckboxGroup m_users;
    private Submit m_submit;
    private Submit m_cancel;

    /**
     * Constructor.
     *
     * @param search The widget on the search form that contains the value of
     *               the search string.
     */
    public UserAddForm(final Widget search) {
        this(search, "AddUsers");
    }

    public UserAddForm(final Widget search, final String name) {
        this(search, name,
             "Check the box next to the name of the person(s) to assign.",
             "Add Members");
    }

    public UserAddForm(final Widget search,
                       final String name,
                       final String text,
                       final String submitText) {
        m_label = text;
        m_submitText = submitText;
        m_search = search;

        m_query = new RequestLocal() {

            @Override
            protected Object initialValue(final PageState state) {
                return makeQuery(state);
            }

        };

        m_form = makeForm(name);

        Label title = new Label(new GlobalizedMessage("cms.ui.matches",
                                                      CmsConstants.CMS_BUNDLE));
        title.setFontWeight(Label.BOLD);

        Label label = new Label(new GlobalizedMessage(
            "cms.ui.there_was_no_one_matching_the_search_criteria",
            CmsConstants.CMS_BUNDLE));
        label.setFontWeight("em");

        m_noMatches = new CMSContainer();
        m_noMatches.add(title);
        m_noMatches.add(label);
        add(m_noMatches);

        m_matches = new CMSContainer();
        m_matches.add(title);
        m_matches.add(m_form);
        add(m_matches);
    }

    /**
     * Build the form used to add users.
     *
     * @param name
     *
     * @return The form
     */
    protected Form makeForm(final String name) {
        final CMSForm form = new CMSForm(name) {

            public final boolean isCancelled(final PageState state) {
                return m_cancel.isSelected(state);
            }

        };

        // This hidden field will store the search query. A hidden widget is
        // used instead of a request local variable because the search query
        // should only be updated when the search form is submitted.
        m_searchQuery = new Hidden(SEARCH_QUERY);
        form.add(m_searchQuery, ColumnPanel.FULL_WIDTH);

        Label l = new Label(m_label);
        form.add(l, ColumnPanel.FULL_WIDTH);

        // Add the list of users that can be added.
        m_users = new CheckboxGroup(USERS);
        m_users.addValidationListener(new NotNullValidationListener());
        try {
            m_users.addPrintListener(new PrintListener() {

                @Override
                public void prepare(PrintEvent event) {
                    CheckboxGroup target = (CheckboxGroup) event.getTarget();
                    PageState state = event.getPageState();
                    // Ensures that the init listener gets fired before the
                    // print listeners.
                    FormData data = m_form.getFormData(state);
                    addUsers(state, target);
                }

            });
        } catch (TooManyListenersException ex) {
            throw new RuntimeException(ex);
        }
        form.add(m_users, ColumnPanel.FULL_WIDTH);

        // Submit and Cancel buttons.
        SimpleContainer s = new SimpleContainer();
        m_submit = new Submit(SUBMIT, m_submitText);
        s.add(m_submit);
        m_cancel = new Submit(CANCEL, "Cancel");
        s.add(m_cancel);
        form.add(s, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        form.addProcessListener(this);

        return form;
    }

    /**
     * Fetches the form for adding users.
     *
     * @return The "add user" form
     */
    public Form getForm() {
        return m_form;
    }

    /**
     * Fetches the widget that contains the search string.
     *
     * @return The widget that contains the search string
     */
    protected Widget getSearchWidget() {
        return m_searchQuery;
    }

    /**
     * Return true if the form is cancelled, false otherwise.
     *
     * @param state The page state
     *
     * @return true if the form is cancelled, false otherwise.
     *
     * @pre ( state != null )
     */
    public boolean isCancelled(final PageState state) {
        return m_cancel.isSelected(state);
    }

    /**
     * Adds users to the option group.
     *
     * @param state  The page state
     * @param target The option group
     *
     * @pre ( state != null && target != null )
     */
    protected void addUsers(final PageState state, final OptionGroup target) {

        @SuppressWarnings("unchecked")
        final List<User> users = (java.util.List<User>) m_query.get(state);

        users.forEach(user -> target.addOption(
            new Option(Long.toString(user.getPartyId()),
                       user.getName())));
    }

    /**
     * Generates a {@link com.arsdigita.persistence.DataQuery} that encapsulates
     * search results.
     *
     * @param state The page state
     *
     * @return
     */
    protected abstract List<User> makeQuery(final PageState state);

    /**
     * Process listener for the "Add users" form.
     *
     * @param event The form event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public abstract void process(final FormSectionEvent event)
        throws FormProcessException;

    /**
     * Displays the appropriate frame.
     *
     * @param state  The page state
     * @param parent The parent DOM element
     */
    @Override
    public void generateXML(final PageState state,
                            final Element parent) {

        m_searchQuery.setValue(state, m_search.getValue(state));
        @SuppressWarnings("unchecked")
        final List<User> searchResults = (List<User>) m_query.get(state);

        if (searchResults.size() > 0) {
            m_matches.generateXML(state, parent);
        } else {
            m_noMatches.generateXML(state, parent);
        }
    }

}
