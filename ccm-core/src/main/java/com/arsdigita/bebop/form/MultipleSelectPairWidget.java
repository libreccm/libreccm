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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.FormStep;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Multiple select widget pair for knowledge types. This FormStep
 * displays two multiple select widgets, one which contains possible
 * the user may want to add, and the right displays the options that
 * are currently applicable. </p>
 *
 * <p>To use the widget, you should call {@link
 * #setLeftMultipleSelect(RequestLocal)} and {@link
 * #setRightMultipleSelect(RequestLocal)} and pass in the appropriate
 * collections to initialize the MutlipleSelect options. Then, in the
 * process listener of the form in which the MultipleSelectPairWidget
 * is embedded, call {@link #getSelectedOptions(PageState)} and {@link
 * #getUnselectedOptions(PageState)} to get the chosen values. The process
 * listener for the parent form must use the Submit.isSelected(ps) so
 * that the process listener can distinguish between different types
 * of form submits.</p>
 *
 * <p>Note that the right multiple select can be empty and does not need
 * to be set. This class also uses a relatively inefficient
 * implementation of removeOption in {@link OptionGroup OptionGroup}
 * so that operations run in O(N^2). This can be reduced to O(N) with
 * a more optimal implementation of OptionGroup.</p>
 *
 * @see Option
 * @see OptionGroup
 * @version $Id$
 */
public class MultipleSelectPairWidget extends FormStep {

    private Hidden m_addSelectOptions;
    private Hidden m_removeSelectOptions;
    private MultipleSelect m_addSelect;
    private MultipleSelect m_removeSelect;
    private Submit m_addSubmit;
    private Submit m_removeSubmit;
    private RequestLocal m_addSelectDataSource;
    private RequestLocal m_removeSelectDataSource;
    private RequestLocal m_selectsPopulated;
    private RequestLocal m_leftSelectMap = null;
    private RequestLocal m_rightSelectMap = null;
    private boolean m_leftSideChanges;

    private String m_qualifier;

    private final static int RIGHT = 1;
    private final static int LEFT = 2;

    // Empty array for internal use. Should be part of a generic utility class.
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    // Configuration options.
    private int m_multipleSelectSize = 20;

    /**
     *  This create a standard MultipleSelectPairWidget with the
     *  default names used for internal widgets.
     */
    public MultipleSelectPairWidget() {
        this(null);
    }

    public MultipleSelectPairWidget(String nameQualifier) {
        super(nameQualifier + "MultipleSelectPairWidget", new GridPanel(3));
        m_qualifier = nameQualifier;

        m_addSelectOptions =
            new Hidden(new ArrayParameter(qualify("addSelectOptions")));
        m_removeSelectOptions = new Hidden
            (new ArrayParameter(qualify("removeSelectOptions")));

        m_addSelect = new MultipleSelect(qualify("leftSelect"));
        m_addSelect.setSize(m_multipleSelectSize);
        m_removeSelect = new MultipleSelect(qualify("rightSelect"));
        m_removeSelect.setSize(m_multipleSelectSize);
        setLeftSideChanges(true);

        m_addSubmit = new Submit(qualify("->"), " -> ");
        m_removeSubmit = new Submit(qualify("<-"), " <- ");

        GridPanel centerPanel = new GridPanel(1);
        centerPanel.add(m_addSubmit);
        centerPanel.add(m_removeSubmit);

        add(m_addSelect, GridPanel.LEFT);
        add(centerPanel, GridPanel.CENTER);
        add(m_removeSelect, GridPanel.RIGHT);
        add(m_addSelectOptions);
        add(m_removeSelectOptions);

        m_selectsPopulated = new RequestLocal();
        addInitListener(new MultipleSelectPairFormInitListener
                        (m_selectsPopulated));
        addProcessListener(new MultipleSelectPairFormProcessListener
                           (m_selectsPopulated));
    }

    public boolean isSelected(PageState ps) {
        return m_addSubmit.isSelected(ps) || m_removeSubmit.isSelected(ps);
    }

    /**
     * @param collection A collection of Option objects
     */
    public void setLeftMultipleSelect(RequestLocal collection) {
        m_addSelectDataSource = collection;
    }

    /**
     *  This lets the user pass in a RequestLocal that returns
     *  a java.util.Map that contains the option value as the
     *  key and the actual option as the map value.
     *  When populating the left select, the system will use
     *  this map before falling back to the default map.
     */
    public void setLeftMultipleSelectMap(RequestLocal map) {
        m_leftSelectMap = map;
    }

    /**
     *  This lets the user pass in a RequestLocal that returns
     *  a java.util.Map that contains the option value as the
     *  key and the actual option as the map value.
     *  When populating the left select, the system will use
     *  this map before falling back to the default map.
     */
    public void setRightMultipleSelectMap(RequestLocal map) {
        m_rightSelectMap = map;
    }

    /**
     *  This returns the left select widget so that callers
     *  can have access to the underlying parameters and
     *  other features (e.g. in case they need to add a
     *  ParameterListener)
     */
    public Widget getLeftSelect() {
        return m_addSelect;
    }

    /**
     *  This returns the left select widget so that callers
     *  can have access to the underlying parameters and
     *  other features (e.g. in case they need to add a
     *  ParameterListener)
     */
    public Widget getRightSelect() {
        return m_removeSelect;
    }


    /**
     * @param doesChange This indicates whether the items in the
     *        "to add" select box are removed as they are added to
     *        the "to remove" select box.  That is, as choices are
     *        selected, should they be removed from the list of
     *        choices?  This defaults to true.
     */
    public void setLeftSideChanges(boolean doesChange) {
        m_leftSideChanges = doesChange;
    }

    /**
     *  This returns an indication of whether or not the left
     *  multiple select changes as items are moved from the left
     *  to the right.
     */
    public boolean leftSideChanges() {
        return m_leftSideChanges;
    }


    /**
     * @param collection A collection of Option objects
     */
    public void setRightMultipleSelect(RequestLocal collection) {
        m_removeSelectDataSource = collection;
    }

    /**
     * Returns the selected options, those selected from the left hand widget
     *
     * @return array of options
     * @post return != null
     */
    public String[] getSelectedOptions(PageState ps) {
        String[] options = (String[]) m_removeSelectOptions.getValue(ps);
        // Probably unneccessary, as widget should be populated with EMPTY_STRING_ARRAY in init listener
        // if there is no data.
        if (null == options) {
            options = EMPTY_STRING_ARRAY;
        }
        return options;
    }

    /**
     * Returns the unselected options, those removed from the right hand widget
     *
     * @return array of options
     * @post return != null
     */
    public String[] getUnselectedOptions(PageState ps) {
        String[] options = (String[]) m_addSelectOptions.getValue(ps);
        // Probably unneccessary, as widget should be populated with EMPTY_STRING_ARRAY in init listener
        // if there is no data.
        if (null == options) {
            options =  EMPTY_STRING_ARRAY;
        }
        return options;
    }

    public void generateXML(PageState state, Element element) {
        // if the page has not been populated then it need to
        // be populated from the hidden variables.  Otherwise,
        // nothing will be displayed in the multi-select boxes.
        if (!Boolean.TRUE.equals(m_selectsPopulated.get(state)) &&
            isInitialized(state)) {
            List addOptions = new ArrayList();
            List removeOptions = new ArrayList();

            String[] unselected = getUnselectedOptions(state);
            for (int i = 0; i < unselected.length; i++) {
                String option = unselected[i];
                addOptions.add(option);
            }

            String[] selected = getSelectedOptions(state);
            for (int i = 0; i < selected.length; i++) {
                String option = selected[i];
                removeOptions.add(option);
            }


            m_selectsPopulated.set(state, Boolean.TRUE);
            generateOptionValues(state, addOptions, removeOptions,
                                 setupOptionMap(state));
            m_addSelect.addOption(getEmptyOption(), state);
            m_removeSelect.addOption(getEmptyOption(), state);
        }
        super.generateXML(state, element);
    }

    /**
     *  This changes the name of the parameter so that it is possible
     *  to include several of these on the same page.
     */
    private String qualify(String property) {
        if (m_qualifier != null) {
            return m_qualifier + "_" + property;
        } else {
            return property;
        }
    }


    /**
     * @size The number of rows to display in the multiple selects.
     */
    public void setMultipleSelectSize(int size) {
        m_multipleSelectSize = size;
        m_removeSelect.setSize(m_multipleSelectSize);
        m_addSelect.setSize(m_multipleSelectSize);
    }

    private HashMap setupOptionMap(PageState ps) {
        // We put all of our options into a HashMap so that we can add the
        // Option object to the destination MultipleSelect.
        HashMap optionsMap = new HashMap();
        Collection addOptions = (Collection) m_addSelectDataSource.get(ps);

        Iterator i;
        Option option;

        i = addOptions.iterator();
        while ( i.hasNext() ) {
            option = (Option) i.next();
            optionsMap.put(option.getValue(), option);
        }

        if ( m_removeSelectDataSource != null ) {
            Collection removeOptions = (Collection) m_removeSelectDataSource.get(ps);
            if ( removeOptions != null ) {
                i = removeOptions.iterator();
                while ( i.hasNext() ) {
                    option = (Option) i.next();
                    if (optionsMap.get(option.getValue()) == null) {
                        optionsMap.put(option.getValue(), option);
                    }
                }
            }
        }

        return optionsMap;
    }

    private void generateOptionValues(PageState ps, List addOptions,
                                      List removeOptions,
                                      HashMap m_optionsMap) {
        Iterator iter;

        iter = addOptions.iterator();
        while ( iter.hasNext() ) {
            String s = (String) iter.next();
            Option o = getOption(ps, m_optionsMap, s, LEFT);
            // it is possible to be null if for some reason the key, s, is
            // not found any of the maps
            if (o != null) {
                m_addSelect.addOption(o, ps);
            }
        }

        iter = removeOptions.iterator();
        while ( iter.hasNext() ) {
            String s = (String) iter.next();
            Option o = getOption(ps, m_optionsMap, s, RIGHT);
            // it is possible to be null if for some reason the key, s, is
            // not found any of the maps
            if (o != null) {
                m_removeSelect.addOption(o, ps);
            }
        }
    }


    /**
     *  This looks at the request locals set in setRightMultipleSelectMap
     *  and setLeftMultipleSelectMap before falling back on the default
     *  mapping that was auto-generated.  If the value is found
     *  in the passed in map then that value is used.  Otherwise, the
     *  value is located in the default mapping
     */
    private Option getOption(PageState state, Map optionMapping, String key,
                             int side) {
        if (side == RIGHT) {
            if (m_rightSelectMap != null) {
                Map map = (Map)m_rightSelectMap.get(state);
                if (map.get(key) != null) {
                    return (Option)map.get(key);
                }
            }
        } else {
            if (m_leftSelectMap != null) {
                Map map = (Map)m_leftSelectMap.get(state);
                if (map.get(key) != null) {
                    return (Option)map.get(key);
                }
            }
        }
        return (Option)optionMapping.get(key);
    }

    private class MultipleSelectPairFormProcessListener
        implements FormProcessListener {

        // This is to allow a call back to set an item as being
        // initialized
        private RequestLocal m_processed;
        MultipleSelectPairFormProcessListener(RequestLocal processed) {
            m_processed = processed;
        }

        public void process(FormSectionEvent evt) {
            PageState ps = evt.getPageState();

            if (!m_addSubmit.isSelected(ps)
                && !m_removeSubmit.isSelected(ps)) {
                return;
            }

            m_processed.set(ps, Boolean.TRUE);

            HashMap m_optionsMap;
            List addOptions = new ArrayList();
            List removeOptions = new ArrayList();

            m_optionsMap = setupOptionMap(ps);

            // We first update the array lists that contain the list
            // of unselected options based on the contents of the
            // hidden form variables.
            updateUnselectedOptions(ps, addOptions, removeOptions);

            // Then we update those array lists based on what the user
            // moves.
            if ( m_addSubmit.isSelected(ps) ) {
                String[] selectedArray = (String[]) m_addSelect.getValue(ps);
                if ( selectedArray != null ) {
                    List selectedAddOptions = Arrays.asList(selectedArray);
                    Iterator iter = selectedAddOptions.iterator();
                    while ( iter.hasNext() ) {
                        String s = (String) iter.next();
                        //  we only want to add the item if it has not
                        //  already been added
                        if (!removeOptions.contains(s)) {
                            removeOptions.add(s);
                        }
                        if (leftSideChanges()) {
                            addOptions.remove(s);
                        }
                    }
                }
            }

            if ( m_removeSubmit.isSelected(ps) ) {
                String[] selectedArray = (String[]) m_removeSelect.getValue(ps);
                if ( selectedArray != null ) {
                    List selectedRemoveOptions = Arrays.asList(selectedArray);
                    Iterator iter = selectedRemoveOptions.iterator();
                    while ( iter.hasNext() ) {
                        String s = (String) iter.next();
                        removeOptions.remove(s);
                        // if the left side does not change then the
                        // item was never removed from the addOptions so
                        // it does not need to be added back.
                        if (leftSideChanges()) {
                            addOptions.add(s);
                        }
                    }
                }
            }

            // Next, we put the full list of options back into the hidden.
            // we have to convert this to a String[]...otherwise we
            // can get a ClassCastException when used within a Wizard
            String[] newValues = (String[]) addOptions.toArray(EMPTY_STRING_ARRAY);
            m_addSelectOptions.setValue(ps, newValues);

            // We do the same conversion for the new values
            newValues = (String[]) removeOptions.toArray(EMPTY_STRING_ARRAY);
            m_removeSelectOptions.setValue(ps, newValues);

            // We finally generate the option values.
            generateOptionValues(ps, addOptions, removeOptions, m_optionsMap);
            m_addSelect.addOption(getEmptyOption(), ps);
            m_removeSelect.addOption(getEmptyOption(), ps);
        }

        private void updateUnselectedOptions(PageState ps, List addOptions,
                                             List removeOptions) {
            // We add the unselected options back to the MultipleSelects.
            String[] unselected = getUnselectedOptions(ps);
            for (int i = 0; i < unselected.length; i++) {
                String s = unselected[i];
                addOptions.add(s);
            }

            String[] selected = getSelectedOptions(ps);
            for (int i = 0; i < selected.length; i++) {
                String s = selected[i];
                removeOptions.add(s);
            }


        }
    }

    private class MultipleSelectPairFormInitListener implements FormInitListener {
        private RequestLocal m_initialized;
        MultipleSelectPairFormInitListener(RequestLocal initialized ) {
            m_initialized = initialized;
        }

        public void init(FormSectionEvent evt) {
            PageState ps = evt.getPageState();
            m_initialized.set(ps, Boolean.TRUE);

            String[] addOptionsForHidden = EMPTY_STRING_ARRAY;
            String[] removeOptionsForHidden = EMPTY_STRING_ARRAY;

            Assert.exists(m_addSelectDataSource,
                                 "You must provide some options for the " +
                                 "user to choose!");

            Collection addOptions = (Collection) m_addSelectDataSource.get(ps);
            if (addOptions.size() > 0) {
                Iterator iter = addOptions.iterator();
                addOptionsForHidden = new String[addOptions.size()];
                int idx = 0;
                while ( iter.hasNext() ) {
                    Option option = (Option) iter.next();
                    m_addSelect.addOption(option, ps);
                    addOptionsForHidden[idx++] = option.getValue();
                }

            }

            if ( m_removeSelectDataSource != null ) {
                Collection c = (Collection) m_removeSelectDataSource.get(ps);
                if ( c != null && c.size() > 0 ) {
                    removeOptionsForHidden = new String[c.size()];
                    Iterator iter = c.iterator();
                    int idx = 0;
                    while ( iter.hasNext() ) {
                        Option option = (Option) iter.next();
                        m_removeSelect.addOption(option, ps);
                        removeOptionsForHidden[idx++] = option.getValue();
                    }
                }
            }

            m_addSelectOptions.setValue(ps, addOptionsForHidden);
            m_removeSelectOptions.setValue(ps, removeOptionsForHidden);
            m_addSelect.addOption(getEmptyOption(), ps);
            m_removeSelect.addOption(getEmptyOption(), ps);
        }
    }

    private Option getEmptyOption() {
        return new Option("",
                          new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                                    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                                    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                                    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                                    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                                    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;",
                                    false));
    }
}
