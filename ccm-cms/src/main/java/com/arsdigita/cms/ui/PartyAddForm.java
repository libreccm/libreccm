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
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.xml.Element;

import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.Party;
import org.librecms.CmsConstants;

import java.util.List;
import java.util.TooManyListenersException;

/**
 * Form for adding multiple parties to a role.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Scott Seago (scott@arsdigita.com)
 */
public abstract class PartyAddForm extends SimpleContainer
    implements FormInitListener, FormProcessListener {

    private final static String SEARCH_QUERY = "searchQuery";
    private final static String PARTIES = "parties";
    private final static String SUBMIT = "addSubmit";
    private final static String CANCEL = "addCancel";

    private Widget searchWidget;
    private RequestLocal queryRequestLocal;

    private CMSContainer noMatchesContainer;
    private CMSContainer matchesContainer;

    private Form form;
    private Hidden searchQueryField;
    private Submit cancelButton;

    /**
     * Private access prevents this constructor from ever being called directly.
     */
    private PartyAddForm() {
        super();
    }

    /**
     * Constructor.
     *
     * @param searchWidget The widget on the search form that contains the value
     *                     of the search string.
     */
    public PartyAddForm(final Widget searchWidget) {

        this();

        this.searchWidget = searchWidget;

        queryRequestLocal = new RequestLocal() {

            @Override
            protected Object initialValue(final PageState state) {
                return makeQuery(state);
            }

        };

        form = makeForm();

        final Label title = new Label(new GlobalizedMessage("cms.ui.matches",
                                                            CmsConstants.CMS_BUNDLE));
        title.setFontWeight(Label.BOLD);

        final Label label = new Label(new GlobalizedMessage(
            "cms.ui.there_was_no_one_matching_the_search_criteria",
            CmsConstants.CMS_BUNDLE));
        label.setFontWeight("em");

        noMatchesContainer = new CMSContainer();
        noMatchesContainer.add(title);
        noMatchesContainer.add(label);
        super.add(noMatchesContainer);

        matchesContainer = new CMSContainer();
        matchesContainer.add(title);
        matchesContainer.add(form);
        super.add(matchesContainer);
    }

    /**
     * Build the form used to add parties.
     *
     * @return The form
     */
    private Form makeForm() {

        final CMSForm addPartyForm = new CMSForm("AddParties") {

            @Override
            public final boolean isCancelled(final PageState state) {
                return cancelButton.isSelected(state);
            }

        };

        // This hidden field will store the search query. A hidden widget is
        // used instead of a request local variable because the search query
        // should only be updated when the search form is submitted.
        searchQueryField = new Hidden(SEARCH_QUERY);
        addPartyForm.add(searchQueryField, ColumnPanel.FULL_WIDTH);

        final Label hintLabel = new Label(
            new GlobalizedMessage("cms.ui.party_add_form.hint",
                                  CmsConstants.CMS_BUNDLE));
        addPartyForm.add(hintLabel, ColumnPanel.FULL_WIDTH);

        // Add the list of parties that can be added.
        final CheckboxGroup partyCheckboxes = new CheckboxGroup(PARTIES);
        partyCheckboxes.addValidationListener(new NotNullValidationListener());
        try {
            partyCheckboxes.addPrintListener(event -> {
                final CheckboxGroup target = (CheckboxGroup) event.getTarget();
                final PageState state = event.getPageState();
                // Ensures that the init listener gets fired before the
                // print listeners.
                final FormData data = addPartyForm.getFormData(state);
                addParties(state, target);
            });
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        addPartyForm.add(partyCheckboxes, ColumnPanel.FULL_WIDTH);

        // Submit and Cancel buttons.
        final SimpleContainer buttonContainer = new SimpleContainer();
        final Submit submitButton = new Submit(SUBMIT,
                                               new GlobalizedMessage(
                                                   "cms.ui.save",
                                                   CmsConstants.CMS_BUNDLE));
        buttonContainer.add(submitButton);
        cancelButton = new Submit(CANCEL,
                                  new GlobalizedMessage("cms.ui.cancel",
                                                        CmsConstants.CMS_BUNDLE));
        buttonContainer.add(cancelButton);
        addPartyForm.add(buttonContainer, ColumnPanel.FULL_WIDTH
                                              | ColumnPanel.CENTER);

        addPartyForm.addInitListener(this);
        addPartyForm.addProcessListener(this);

        return addPartyForm;
    }

    /**
     * Fetches the form for adding parties.
     *
     * @return The "add party" form
     */
    public Form getForm() {
        return form;
    }

    /**
     * Fetches the widget that contains the search string.
     *
     * @return The widget that contains the search string
     */
    protected Widget getSearchWidget() {
        return searchQueryField;
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
        return cancelButton.isSelected(state);
    }

    /**
     * Adds parties to the option group.
     *
     * @param state  The page state
     * @param target The option group
     *
     * @pre ( state != null && target != null )
     */
    private void addParties(final PageState state, final OptionGroup target) {
        
        @SuppressWarnings("unchecked")
        final List<Party> parties = (List<Party>) queryRequestLocal.get(state);
        
        target.clearOptions();

        for (final Party party : parties) {
            target.addOption(new Option(
                Long.toString(party.getPartyId()),
                new Label(new GlobalizedMessage(party.getName()))
            ));
        }
    }

    /**
     * Generates a {@link Object} that encapsulates search results.
     *
     * @param state The page state
     *
     * @return
     */
    protected abstract List<Party> makeQuery(PageState state);

    /**
     * Stores the search query in the hidden field.
     *
     * @param event The form event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        
        PageState state = event.getPageState();

        searchQueryField.setValue(state, searchWidget.getValue(state));
    }

    /**
     * Process listener for the "Add parties" form.
     *
     * @param event The form event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public abstract void process(FormSectionEvent event)
        throws FormProcessException;

    /**
     * Displays the appropriate frame.
     *
     * @param state  The page state
     * @param parent The parent DOM element
     */
    @Override
    public void generateXML(final PageState state, final Element parent) {

        @SuppressWarnings("unchecked")
        final List<Party> searchResults = (List<Party>) queryRequestLocal.get(state);

        if (searchResults.size() > 0) {
            matchesContainer.generateXML(state, parent);
        } else {
            noMatchesContainer.generateXML(state, parent);
        }
    }

}
