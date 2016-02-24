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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.ParameterModelWrapper;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import java.text.Collator;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;

/**
 * A class representing any widget that contains a list of options.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Rory Solomon
 * @author Michael Pih
 * @version $Id$
 */
public abstract class OptionGroup extends Widget implements BebopConstants {

    private static final Logger LOGGER = Logger.getLogger(OptionGroup.class);

    /**
     * The XML element to be used by individual options belonging to this group.
     * This variable has to be initialized by every subclass of OptionGroup.
     * LEGACY: An abstract method would be the better design, but changing it
     * would break the API.
     */
    //protected String m_xmlElement;
    // this only needs to be an ArrayList for multiple selection option groups
    private List<String> m_selected;
    private List<Option> m_options;
    private Widget m_otherOption = null;
    private Form m_form = null;
    private boolean m_isDisabled = false;
    private boolean m_isReadOnly = false;
    /**
     * Sort Mode for options
     */
    private OptionGroup.SortMode sortMode;
    /**
     * Exclude first option from sorting?
     */
    private boolean excludeFirst;
    public static final String OTHER_OPTION = "__other__";
    // this is only used for single selection option groups

    private final static String TOO_MANY_OPTIONS_SELECTED
                                    = "Only one option may be selected by default on this option group.";

    /**
     * request-local copy of selected elements, options
     */
    private final RequestLocal m_requestOptions = new RequestLocal() {

        @Override
        public Object initialValue(final PageState state) {
            return new ArrayList<Option>();
        }

    };

    /**
     *
     * @return
     */
    @Override
    public final boolean isCompound() {
        return true;
    }

    /**
     * The ParameterModel for multiple OptionGroups is always an array parameter
     *
     * @param model
     *
     * @param model
     */
    protected OptionGroup(final ParameterModel model) {
        //super(model);
        //m_options = new ArrayList<Option>();
        //m_selected = new ArrayList<String>();
        this(model, OptionGroup.SortMode.NO_SORT, false);
    }

    protected OptionGroup(final ParameterModel model,
                          final OptionGroup.SortMode sortMode) {
        this(model, sortMode, false);
    }

    protected OptionGroup(final ParameterModel model,
                          final OptionGroup.SortMode sortMode,
                          final boolean excludeFirst) {
        super(model);
        m_options = new ArrayList<Option>();
        m_selected = new ArrayList<String>();
        this.sortMode = sortMode;
        this.excludeFirst = excludeFirst;
    }

    /**
     * Returns an Iterator of all the default Options in this group.
     *
     * @return
     */
    public Iterator<Option> getOptions() {
        return m_options.iterator();
    }

    public enum SortMode {

        NO_SORT,
        ALPHABETICAL_ASCENDING,
        ALPHABETICAL_DESENDING

    }

    public abstract String getOptionXMLElement();

    /**
     * This {@link Comparator} implementation is used to sort the list of
     * options alphabetical. If the sorting is ascending or descending depends
     * on the selected sort mode. The Comparator needs the {@link PageState} for
     * retrieving the localised labels from the options.
     */
    private class AlphabeticalSortComparator implements Comparator<Option> {

        private final PageState state;

        /**
         * Constructor taking the current {@code PageState}.
         *
         * @param state
         */
        public AlphabeticalSortComparator(final PageState state) {
            this.state = state;
        }

        @Override
        public int compare(final Option option1, final Option option2) {
            String label1;
            String label2;

            //Check if the first option to compare has a inner label component. If it has 
            //store the localised text. Otherwise use the name of the option.
            if (option1.getComponent() instanceof Label) {
                final Label label = (Label) option1.getComponent();
                label1 = label.getLabel(state);
            } else {
                label1 = option1.getName();
            }

            // Same for the second option
            if (option2.getComponent() instanceof Label) {
                final Label label = (Label) option2.getComponent();
                label2 = label.getLabel(state);
            } else {
                label2 = option2.getName();
            }

            //We are using a Collator instance here instead of String#compare(String) because
            //String#compare(String) is not local sensitive. For example in german a word starting
            //with the letter 'Ö' should be handled like a word starting with the letter 'O'. 
            //Using String#compare(String) would put them at the end of the list.
            //Depending on the sort mode we compare label1 with label2 (ascending) or label2 with
            //label1 (descending).
            final Collator collator = Collator
                .getInstance(CdiUtil.createCdiUtil().findBean(
                    GlobalizationHelper.class).getNegotiatedLocale());
            if (sortMode == SortMode.ALPHABETICAL_ASCENDING) {
                return collator.compare(label1, label2);
            } else if (sortMode == SortMode.ALPHABETICAL_DESENDING) {
                return collator.compare(label2, label1);
            } else {
                return 0;
            }
        }

    }

    /**
     * Returns an Iterator of all the default Options in this group, plus any
     * request-specific options.
     *
     * @param state
     *
     * @return
     */
    public Iterator<Option> getOptions(final PageState state) {
        List<Option> allOptions = new ArrayList<Option>();
        allOptions.addAll(m_options);
        List<Option> requestOptions = (List<Option>) m_requestOptions.get(state);
        for (Iterator<Option> iterator = requestOptions.iterator(); iterator
             .hasNext();) {
            final Option option = iterator.next();
            if (!allOptions.contains(option)) {
                allOptions.add(option);
            }
        }
        return allOptions.iterator();
    }

    public void clearOptions() {
        Assert.isUnlocked(this);
        m_options = new ArrayList<Option>();
    }

    /**
     * Adds a new option.
     *
     * @param option The {@link Option} to be added. Note: the argument is
     *               modified and associated with this OptionGroup, regardless
     *               of what its group was.
     */
    public void addOption(final Option option) {
        addOption(option, null, false);
    }

    /**
     * Adds a new option.
     *
     * @param opt
     * @param ps
     */
    public void addOption(final Option option, final PageState state) {
        addOption(option, state, false);
    }

    /**
     * Adds a new option at the beginning of the list.
     *
     * @param option The {@link Option} to be added. Note: the argument is
     *               modified and associated with this OptionGroup, regardless
     *               of what its group was.
     */
    public void prependOption(final Option option) {
        addOption(option, null, true);
    }

    public void prependOption(final Option option, final PageState state) {
        addOption(option, state, true);
    }

    public void removeOption(final Option option) {
        removeOption(option, null);
    }

    /**
     * Adds a new option for the scope of the current request, or to the page as
     * a whole if there is no current request.
     *
     * @param option  The {@link Option} to be added. Note: the argument is
     *                modified and associated with this OptionGroup, regardless
     *                of what its group was.
     * @param state   the current page state. if ps is null, adds option to the
     *                default option list.
     * @param prepend If true, prepend option to the list instead of appending
     *                it
     */
    public void addOption(final Option option, final PageState state,
                          final boolean prepend) {
        List<Option> list = m_options;
        if (state == null) {
            Assert.isUnlocked(this);
        } else {
            list = (List<Option>) m_requestOptions.get(state);
        }
        option.setGroup(this);

        if (prepend == true) {
            list.add(0, option);
        } else {
            list.add(option);
        }
    }

    public void removeOption(final Option option, final PageState state) {
        List<Option> list = m_options;
        if (state == null) {
            Assert.isUnlocked(this);
        } else {
            list = (List<Option>) m_requestOptions.get(state);
        }
        list.remove(option);
    }

    public void removeOption(String key) {
        removeOption(key, null);
    }

    /**
     * Removes the first option whose key is isEqual to the key that is passed
     * in.
     *
     * @param key
     * @param state the current page state. if ps is null, adds option to the
     *              default option list.
     */
    public void removeOption(final String key, final PageState state) {
        // This is not an entirely efficient technique. A more
        // efficient solution is to switch to using a HashMap.
        List<Option> list = m_options;
        if (state == null) {
            Assert.isUnlocked(this);
        } else {
            list = (List<Option>) m_requestOptions.get(state);
        }

        final Iterator<Option> iterator = list.iterator();
        Option option;
        while (iterator.hasNext()) {
            option = iterator.next();
            if (option.getValue().equals(key)) {
                list.remove(option);
                break;
            }
        }

    }

    /**
     * Add an "Other (please specify)" type option to the widget
     *
     * @param label
     * @param width  The width, in characters, of the "Other" entry area
     * @param height The height, in characters, of the "Other" entry area. If
     *               this is 1 then a TextField is used. Otherwise a TextArea is
     *               used.
     */
    public void addOtherOption(final String label, final int width,
                               final int height) {
        Assert.isUnlocked(this);

        final Option otherOption = new Option(OTHER_OPTION, label);
        addOption(otherOption);

        final ParameterModel model = getParameterModel();

        if (1 == height) {
            TextField field = new TextField(model.getName() + ".other");
            field.setSize(width);

            m_otherOption = field;
        } else {
            TextArea area = new TextArea(model.getName() + ".other");
            area.setCols(width);
            area.setRows(height);

            m_otherOption = area;
        }

        if (null != m_form) {
            m_otherOption.setForm(m_form);

            if (m_isDisabled) {
                m_otherOption.setDisabled();
            }
            if (m_isReadOnly) {
                m_otherOption.setReadOnly();
            }
        }

        setParameterModel(new ParameterModelWrapper(model) {

            @Override
            public ParameterData createParameterData(
                final HttpServletRequest request,
                Object defaultValue,
                boolean isSubmission) {

                final String[] values = request.getParameterValues(getName());
                String[] otherValues = request.getParameterValues(getName()
                                                                  + ".other");

                String other = (null == otherValues) ? null : otherValues[0];

                if (null != values) {
                    for (int i = 0; i < values.length; i++) {
                        if (OTHER_OPTION.equals(values[i])) {
                            values[i] = other;
                        }
                    }
                }

                LOGGER.debug("createParameterData in OptionGroup");

                return super.createParameterData(new HttpServletRequestWrapper(
                    request) {

                    @Override
                    public String[] getParameterValues(String key) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Getting values for " + key);
                        }

                        if (model.getName().equals(key)) {
                            return values;
                        }
                        return super.getParameterValues(key);
                    }

                }, defaultValue, isSubmission);
            }

            private void replaceOther(String[] values, String other) {
            }

        });
    }

    /**
     * Make an option selected by default. Updates the parameter model for the
     * option group accordingly.
     *
     * @param value the value of the option to be added to the
     *              by-default-selected set.
     */
    public void setOptionSelected(final String value) {
        Assert.isUnlocked(this);
        if (!isMultiple()) {
            // only one option may be selected
            // to this selected list better be empty
            Assert.isTrue(m_selected.isEmpty(), TOO_MANY_OPTIONS_SELECTED);
            m_selected.add(value);
            getParameterModel().setDefaultValue(value);
        } else {
            m_selected.add(value);
            getParameterModel().setDefaultValue(m_selected.toArray());
        }
    }

    /**
     * make an option selected by default
     *
     * @param option the option to be added to the by-default-selected set.
     */
    public void setOptionSelected(Option option) {
        setOptionSelected(option.getValue());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final OptionGroup cloned = (OptionGroup) super.clone();
        //cloned.m_options = m_options.clone();
        //cloned.m_selected = m_selected.clone();
        cloned.m_options.addAll(m_options);
        cloned.m_selected.addAll(m_selected);
        return cloned;
    }

    /**
     * Whether this a multiple (and not single) selection option group. Note
     * that this should really be declared abstract, but we can't because it
     * used to be in the direct subclass Select and making it abstract could
     * break other subclasses that don't declare isMultiple. So we have a
     * trivial implementation instead.
     *
     * @return true if this OptionGroup can have more than one selected option;
     *         false otherwise.
     */
    public boolean isMultiple() {
        return true;
    }

    @Override
    public void setDisabled() {
        m_isDisabled = true;

        if (null != m_otherOption) {
            m_otherOption.setDisabled();
        }

        super.setDisabled();
    }

    @Override
    public void setReadOnly() {
        m_isReadOnly = true;

        if (null != m_otherOption) {
            m_otherOption.setReadOnly();
        }

        super.setReadOnly();
    }

    @Override
    public void setForm(final Form form) {
        m_form = form;
        if (null != m_otherOption) {
            m_otherOption.setForm(form);
        }

        super.setForm(form);
    }

    /**
     * Generates the DOM for the select widget
     * <p>
     * Generates DOM fragment:
     * <p>
     * <
     * pre><code>&lt;bebop:* name=... [onXXX=...]&gt;
     * &lt;bebop:option name=... [selected]&gt; option value &lt;/bebop:option%gt;
     * ...
     * &lt;/bebop:*select&gt;</code></pre>
     */
    @Override
    public void generateWidget(final PageState state, final Element parent) {
        final Element optionGroup = parent.newChildElement(getElementTag(),
                                                           BEBOP_XML_NS);
        optionGroup.addAttribute("name", getName());
        optionGroup.addAttribute("class", getName().replace(".", " "));
        // Localized title for this option group
        if (getLabel() != null) {
            optionGroup.addAttribute("label", (String) getLabel()
                                     .localize(state.getRequest()));
        }
        if (isMultiple()) {
            optionGroup.addAttribute("multiple", "multiple");
        }
        exportAttributes(optionGroup);

        //Build a list of all options we can operator on.
        final List<Option> options = new ArrayList<Option>();
        for (Iterator<Option> iterator = getOptions(state); iterator.hasNext();) {
            options.add(iterator.next());
        }

        //If the sort mode is not {@code NO_SORT}, sort the the list.
        if (sortMode != SortMode.NO_SORT) {

            //If exclude first is sest to true the first option should stay on the top.
            //We simply remove the first option from our list and generate the XML for it here.
            if (excludeFirst && !options.isEmpty()) {
                final Option first = options.remove(0);
                first.generateXML(state, optionGroup);
            }

            //Sort the list using our {@link AlphabeticalSortComparator}.
            Collections.sort(options, new AlphabeticalSortComparator(state));
        }

        //Generate the XML for the options.
        for (Option option : options) {
            option.generateXML(state, optionGroup);
        }

//        for (Iterator<Option> iterator = getOptions(state); iterator.hasNext();) {
//            Option option = iterator.next();
//            option.generateXML(state, optionGroup);
//        }
        if (null != m_otherOption) {
            m_otherOption.generateXML(state, optionGroup);
        }
    }

}
