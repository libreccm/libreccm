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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.event.PageEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.SearchAndSelectListener;
import com.arsdigita.bebop.event.SearchAndSelectModel;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.ParameterData;
// This interface contains the XML element name of this class
// in a constant which is used when generating XML
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TooManyListenersException;

/**
 * Search and select Bebop widget. This widget is used to allow a user to search
 * for a particular item over a potentially very large set. Depending on the
 * size of the dataset, the user will either see a search box or a selection box
 * (with all valid items). The search box will then change to a selection box
 * once the user submits the form, allowing them then to choose the items they
 * desire.
 * <p>
 * The data source for SearchAndSelect is provided by an implementation of the
 * SearchAndSelectModel interface. SAMPLE IMPLEMENTATION GOES HERE
 *
 * @author Patrick McNeill
 * @since 4.5
 */
public class SearchAndSelect extends FormSection implements BebopConstants,
                                                            PrintListener {

    private static final Logger LOGGER = LogManager.getLogger(
        SearchAndSelect.class);

    protected String m_name;
    // name of this super-widget

    protected String m_value = "";
    // current value of the widget

    protected String m_query = "";
    // the query to search for this go round.  set by the form validation
    // listener.

    protected int m_maxViewableResults = 10;
    // number of hits before the search widget pops up, eventually should be
    // a system parameter

    protected SearchAndSelectModel m_results = null;
    // interface to the dataset

    protected SearchAndSelectListener m_listener = null;

    protected boolean m_isMultiple = false;
    // multiselect?

    protected boolean m_useCheckboxes = false;
    // use checkboxes or multiple-select for the multiple case

    protected boolean m_isOptional = true;
    // optional?

    protected Object m_this = this;
    // so "this" will work in my anonymous inner classes

    protected TextField m_outputTextWidget;
    protected Widget m_outputSelectWidget;
    // internal widgets used render either a text box, a checkbox, or a select

    protected Hidden m_oldValueWidget;
    // internal Hidden widget used to save the previous search

    protected String m_oldValue = "";
    // the contents of the oldValueWidget, set by the validation listener

    protected boolean m_isSearchLocked = false;
    // true if the user has already seen a checkbox group or select box,
    // false otherwise.  determines if the user is still refining his/her search

    /*
     * Creates the output widgets and adds them all to the form
     */
    private void initializeOutputWidget() {
        m_oldValueWidget = new Hidden(getName() + ".oldvalue");
        add(m_oldValueWidget);

        m_outputTextWidget = new TextField(getName() + ".text");
        add(m_outputTextWidget);

        if (m_isMultiple) {
            if (m_useCheckboxes) {
                m_outputSelectWidget = new CheckboxGroup(getName() + ".select");
            } else {
                m_outputSelectWidget
                    = new MultipleSelect(getName() + ".select");
            }
        } else {
            m_outputSelectWidget = new SingleSelect(getName() + ".select");
        }
        add(m_outputSelectWidget);

        try {
            m_outputSelectWidget.addPrintListener(this);
        } catch (TooManyListenersException e) {
            LOGGER.error("Could not add print listener", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Could not add print listener", e);
        }
    }

    public void prepare(PrintEvent e) {
        if (m_results == null) {
            m_results = m_listener.getModel(
                new PageEvent(this, e.getPageState()));
        }

        if (m_results == null) {
            return;
        }

        m_results.setQuery(m_query);

        if (m_isSearchLocked
                || (((!m_oldValue.equals("")
                      && m_oldValue.equals(m_value))
                     || (m_maxViewableResults >= m_results.resultsCount()))
                    && (m_results.resultsCount() > 0))) {

            OptionGroup outputWidget = (OptionGroup) e.getTarget();
            outputWidget.clearOptions();

            if (m_isOptional && !m_isMultiple) {
                outputWidget.addOption(new Option("", "None"));
            }

            for (int i = 0; i < m_results.resultsCount(); i++) {
                outputWidget.addOption(
                    new Option(m_results.getID(i), m_results.getLabel(i)));

                LOGGER.debug("    " + m_results.getID(i));
            }
        }
    }

    /**
     * Create a new SearchAndSelect widget to select a single value.
     *
     * @param name the name of the widget
     */
    public SearchAndSelect(String name) {
        this(name, false, false);
    }

    /**
     * Create a new SearchAndSelect widget with the specified name and
     * SearchAndSelectModel.
     *
     * @param name          the name of the widget
     * @param isMultiple    whether or not the widget accepts multiple values
     * @param useCheckboxes use checkboxes or a multiselect
     */
    public SearchAndSelect(String name,
                           boolean isMultiple) {
        this(name, isMultiple, false);
    }

    /**
     * Create a new SearchAndSelect widget with the specified name and
     * SearchAndSelectModel.
     *
     * @param name          the name of the widget
     * @param isMultiple    whether or not the widget accepts multiple values
     * @param useCheckboxes use checkboxes or a multiselect
     */
    public SearchAndSelect(String name,
                           boolean isMultiple,
                           boolean useCheckboxes) {

        super(new SimpleContainer());

        m_isMultiple = isMultiple;
        m_useCheckboxes = useCheckboxes;
        m_name = name;

        initializeOutputWidget();

        /*
         * Add a form validation listener so that we can determine whether
         * or not the form is valid.  This is required because this widget
         * needs an arbitrary number of page loads to succeed.  Also note
         * that the error messages generated here are not displayed to the
         * user as the error field is also used as a help field.
         */
        super.addValidationListener(new FormValidationListener() {

            @Override
            public void validate(FormSectionEvent e) {
                FormData data = e.getFormData();

                m_results = m_listener.getModel(
                    new PageEvent(m_this, e.getPageState()));

                if (m_results == null) {
                    return;
                }

                m_oldValue = data.getString(getName() + ".oldvalue");

                m_value = data.getString(getName() + ".text");

                /*
                 * Determine what stage in the process we're at.  If .text
                 * is null, then check what the select/checkbox shows.
                 */
                if (m_value == null) {
                    m_isSearchLocked = true;
                    m_query = m_oldValue;

                    if (m_isMultiple) {
                        String[] tmpArray = (String[]) data
                            .get(getName() + ".select");
                        if (tmpArray == null) {
                            m_value = "";
                        } else {
                            m_value = tmpArray[0];
                        }
                    } else {
                        m_value = data.getString(getName() + ".select");
                    }
                } else {
                    m_query = m_value;
                }

                /*
                 * If optional and nothing selected, we're done
                 */
                if (m_value.equals("") && m_isOptional) {
                    return;
                }

                String oldQuery = m_results.getQuery();

                m_results.setQuery(m_query);

                /*
                 * If search returns only one hit and is a non-optional single
                 * select, it's done.
                 */
                if (!m_isOptional
                        && !m_isMultiple
                        && (m_results.resultsCount() == 1)) {
                    m_isSearchLocked = true;
                    m_value = m_results.getID(0);
                }

                /*
                 * If we're in the results phase, determine what the user
                 * chose
                 */
                if (m_isSearchLocked) {
                    if (!m_isMultiple) {
                        StringParameter param
                                            = new StringParameter(getName());

                        data.setParameter(getName(),
                                          new ParameterData(param, m_value));
                    } else {
                        ArrayParameter param
                                           = new ArrayParameter(getName());
                        String[] tmpArray = (String[]) data
                            .get(getName() + ".select");

                        if (tmpArray == null) {
                            tmpArray = new String[0];
                        }

                        data.setParameter(getName(),
                                          new ParameterData(param, tmpArray));
                    }

                    return;
                } else {
                    data.addError("Search not complete yet.");
                }

                m_results.setQuery(oldQuery);
            }

        });
    }

    public final void setSearchAndSelectListener(
        SearchAndSelectListener listener) {
        m_listener = listener;
    }

    /**
     * Get the name of the widget.
     *
     * @return the name of the widget
     */
    public final String getName() {
        return m_name;
    }

    /*
     * Internal function to retrieve a single text value for the widget.
     */
    private String getTextValue(PageState state) {
        if (m_value != null) {
            return m_value;
        }

        if (m_isSearchLocked) {
            if (m_isMultiple) {
                return ((String[]) m_outputSelectWidget.getValue(state))[0];
            } else {
                return (String) m_outputSelectWidget.getValue(state);
            }
        } else {
            return (String) m_outputTextWidget.getValue(state);
        }
    }

    /**
     * Determine the type of HTML form element to create. This will not
     * necessarily be accurate until generateWidget is called as the query will
     * be unavailable until that point.
     *
     * @return "text" or "select" depending on the result size
     */
    public String getType() {
        if (m_isSearchLocked) {
            return m_outputSelectWidget.getType();
        } else {
            return "text";
        }
    }

    /**
     * Determine if this is a multiple select widget, or single select.
     *
     * @return boolean -- true for multiple, false for single
     */
    public final boolean isMultiple() {
        return m_isMultiple;
    }

    /**
     * Determine if this is an optional widget
     *
     * @return true for optional, false otherwise
     */
    public final boolean isOptional() {
        return m_isOptional;
    }

    /**
     * Specify whether or not the widget is optional.
     *
     * @param isOptional true for optional, false for required
     */
    public SearchAndSelect setOptional(boolean isOptional) {
        m_isOptional = isOptional;

        return this;
    }

    /**
     * Indicates if the widget is composed of multiple HTML elements. Always
     * returns true, as the widget makes use of a hidden element and another
     * element.
     *
     * @return true
     */
    public boolean isCompound() {
        return true;
    }

    /**
     * Generates the XML datastructure for this widget. Adds a hidden, a
     * textbox, checkbox group, or select, and possibly some number of
     * formErrors.
     *
     * @param state  the state of the page
     * @param parent the parent widget
     */
    public void generateXML(PageState state, Element parent) {
        if (m_results == null) {
            m_results = m_listener.getModel(new PageEvent(this, state));
        }

        if (m_results == null) {
            return;
        }

        if (m_isSearchLocked
                || (((!m_oldValue.equals("")
                      && m_oldValue.equals(m_value))
                     || (m_maxViewableResults >= m_results.resultsCount()))
                    && (m_results.resultsCount() > 0))) {
            m_outputSelectWidget.generateXML(state, parent);
        } else {
            m_outputTextWidget.generateXML(state, parent);
        }

        m_oldValueWidget.setValue(state, m_query);

        m_oldValueWidget.generateXML(state, parent);

        generateErrors(state, parent);
    }

    /**
     * Generate the error messages for this widget. This widget has some
     * specialized error messages, so it is necessary to override the default
     * error generator. Basically, the m_results field won't be available
     * outside this class, so this needs to be internal.
     *
     * @param state  the state of the page
     * @param parent the parent widget
     */
    protected void generateErrors(PageState state, Element parent) {
        String curValue = getTextValue(state);

        if (m_results == null) {
            return;
        }

        if (m_results.resultsCount() > m_maxViewableResults) {

            Element error = parent.newChildElement("bebop:formErrors",
                                                   BEBOP_XML_NS);

            if ((curValue == null) || (curValue.equals(""))) {
                error.addAttribute("message",
                                   "Please enter a comma-delimited search");
            } else if ((!m_oldValue.equals(curValue))
                           && !m_isSearchLocked) {
                error.addAttribute("message",
                                   "Your search returned "
                                       + m_results.resultsCount()
                                   + " matches.  "
                                       + "Please refine your search or leave the "
                                   + "search as it is to see all results.");
            }
        }

        if (m_results.resultsCount() == 0) {
            if (!curValue.equals("")) {
                Element error = parent.newChildElement("bebop:formErrors",
                                                       BEBOP_XML_NS);
                error.addAttribute("message",
                                   "Your search returned no matches.  Please "
                                       + "try again");
            } else {
                Element error = parent.newChildElement("bebop:formErrors",
                                                       BEBOP_XML_NS);
                error.addAttribute("message", "WARNING -- NO DATA FOUND");
            }
        }
    }

}
