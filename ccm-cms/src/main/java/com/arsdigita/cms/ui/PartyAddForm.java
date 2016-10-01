/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.bebop.event.FormInitListener;
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
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import org.libreccm.security.Party;

import java.math.BigDecimal;
import java.util.List;
import java.util.TooManyListenersException;


/**
 * Form for adding multiple parties to a role.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Scott Seago (scott@arsdigita.com)
 * @version Id: PartyAddForm.java 754 2005-09-02 13:26:17Z sskracic $
 */
public abstract class PartyAddForm extends SimpleContainer
    implements FormInitListener, FormProcessListener {

    private final static String SEARCH_QUERY = "searchQuery";
    private final static String PARTIES = "parties";
    private final static String SUBMIT = "addSubmit";
    private final static String CANCEL = "addCancel";
    private final static String SUBMIT_LABEL = "Add Members";

    private final static String DQ_PARTY_ID = "partyId";
    private final static String DQ_NAME = "name";

    private Widget m_search;
    private RequestLocal m_query;

    private CMSContainer m_noMatches;
    private CMSContainer m_matches;

    private Form m_form;
    private Hidden m_searchQuery;
    private CheckboxGroup m_parties;
    private Submit m_submit;
    private Submit m_cancel;


    /**
     * Private access prevents this constructor from ever being called
     * directly.
     */
    private PartyAddForm() {
        super();
    }


    /**
     * Constructor.
     *
     * @param search The widget on the search form that contains the value
     *   of the search string.
     */
    public PartyAddForm(Widget search) {
        this();

        m_search = search;

        m_query = new RequestLocal() {
                protected Object initialValue(PageState state) {
                    return makeQuery(state);
                }
            };

        m_form = makeForm();

        Label title = new Label(GlobalizationUtil.globalize("cms.ui.matches"));
        title.setFontWeight(Label.BOLD);

        Label label = new Label(GlobalizationUtil.globalize("cms.ui.there_was_no_one_matching_the_search_criteria"));
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
     * Build the form used to add parties.
     *
     * @return The form
     */
    protected Form makeForm() {
        final CMSForm form = new CMSForm("AddParties") {
                public final boolean isCancelled(final PageState state) {
                    return m_cancel.isSelected(state);
                }
            };

        // This hidden field will store the search query. A hidden widget is
        // used instead of a request local variable because the search query
        // should only be updated when the search form is submitted.
        m_searchQuery = new Hidden(SEARCH_QUERY);
        form.add(m_searchQuery, ColumnPanel.FULL_WIDTH);

        Label l = new Label(
                            "Check the box next to the name of the person(s) to assign to this role.");
        form.add(l, ColumnPanel.FULL_WIDTH);

        // Add the list of parties that can be added.
        m_parties = new CheckboxGroup(PARTIES);
        m_parties.addValidationListener(new NotNullValidationListener());
        try {
            m_parties.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent event) {
                        CheckboxGroup target = (CheckboxGroup) event.getTarget();
                        PageState state = event.getPageState();
                        // Ensures that the init listener gets fired before the
                        // print listeners.
                        FormData data = m_form.getFormData(state);
                        addParties(state, target);
                    }
                });
        } catch (TooManyListenersException e) {
            UncheckedWrapperException.throwLoggedException(getClass(), "Too many listeners", e);
        }
        form.add(m_parties, ColumnPanel.FULL_WIDTH);

        // Submit and Cancel buttons.
        SimpleContainer s = new SimpleContainer();
        m_submit = new Submit(SUBMIT, new GlobalizedMessage(SUBMIT_LABEL));
        s.add(m_submit);
        m_cancel = new Submit(CANCEL, new GlobalizedMessage("Cancel"));
        s.add(m_cancel);
        form.add(s, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        form.addInitListener(this);
        form.addProcessListener(this);

        return form;
    }

    /**
     * Fetches the form for adding parties.
     *
     * @return The "add party" form
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
     * @return true if the form is cancelled, false otherwise.
     * @pre ( state != null )
     */
    public boolean isCancelled(PageState state) {
        return m_cancel.isSelected(state);
    }


    /**
     * Adds parties to the option group.
     *
     * @param state The page state
     * @param target The option group
     * @pre ( state != null && target != null )
     */
    protected void addParties(PageState state, OptionGroup target) {
        @SuppressWarnings("unchecked")
        List<Party> parties = (List<Party>) m_query.get(state);

        for (final Party party : parties) {
            target.addOption(new Option(
                    Long.toString(party.getPartyId()),
                    new Label(new GlobalizedMessage(party.getName()))
            ));
        }
    }


    /**
     * Generates a {@link Object} that encapsulates
     * search results.
     *
     * @param state The page state
     */
    protected abstract List<Party> makeQuery(PageState state);


    /**
     * Stores the search query in the hidden field.
     *
     * @param event The form event
     */
    public void init(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();

        m_searchQuery.setValue(state, m_search.getValue(state));
    }

    /**
     * Process listener for the "Add parties" form.
     *
     * @param event The form event
     */
    public abstract void process(FormSectionEvent event)
        throws FormProcessException;


    /**
     * Displays the appropriate frame.
     *
     * @param state The page state
     * @param parent The parent DOM element
     */
    public void generateXML(PageState state, Element parent) {

        @SuppressWarnings("unchecked")
        List<Party> searchResults = (List<Party>) m_query.get(state);

        if ( searchResults.size() > 0 ) {
            m_matches.generateXML(state, parent);
        } else {
            m_noMatches.generateXML(state, parent);
        }
    }

}
