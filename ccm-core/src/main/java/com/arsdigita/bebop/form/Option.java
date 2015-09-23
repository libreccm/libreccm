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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.DescriptiveComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.util.Assert;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.xml.Element;

/**
 * A class representing an option of a widget.
 * 
 * The Option consist of two parts:
 * - a value, used by the background task to process the option
 * - a display component, used to display the option to the user in the GUI,
 *   usually a Label (title), but may be e.g. an image as well.

 * @author Rory Solomon   
 * @author Michael Pih    
 *
 * $Id$
 */
public class Option extends DescriptiveComponent {

    /** The value of the option, used by the background task to process the
     *  option.
     *  NOTE: The display component, the label, is provided by parent class! */
    private String m_value;
    /** The display component for the user in the GUI. It's usually a Label,
     *  but may be e.g. an image as well.                                    */
    private Component m_component;
    private OptionGroup m_group;
    private boolean m_isSelectOption;

    //  ///////////////////////////////////////////////////////////////////////
    //  Constructor Section
    //
    
    /**
     * A (too) simple Constructor which uses a String as value as well as
     * display component.
     * 
     * @param value A String used as value as well as display component.
     * @deprecated use Option(value,component) instead
     */
    public Option(String value) {
        this(value, value);
    }

    /**
     * Constructor creates an Option whose label part consisting of a string.
     * This results in a badly globalized label part. The localization depends
     * on the language selected at the time the Option is created.
     * 
     * @param value
     * @param label
     * @deprecated  use Option(value,component) instead
     */
    public Option(String value, String label) {
        setValue(value);
        setLabel(label);
    }

    /**
     * Constructor creates an Option whose label part consisting of a Component,
     * usually a Label(GlobalizedMessage).
     * This constructor should be used to create a fully globalized and
     * localized user interface.
     * 
     * @param value
     * @param label 
     */
    public Option(String value, Component label) {
        setValue(value);
        setComponent(label);
    }

    //  ///////////////////////////////////////////////////////////////////////
    //  Getter/Setter Section
    //

    /**
     * Retrieves the value part of an option.
     * @return the value part of this option.
     */
    public final String getValue() {
        return m_value;
    }

    /**
     * Sets of modifies the value of on option.
     * @param value new value part of the option
     */
    public final void setValue(String value) {
        m_value = value;
    }


    /**
     * Retrieves the display part of the option. 
     * @return the display component for this option
     */
    public final Component getComponent() {
        return m_component;
    }

    /**
     * Sets of modifies the display component of an option.
     * 
     * @param component the display component for this option
     */
    public final void setComponent(Component component) {
        Assert.isUnlocked(this);
        m_component = component;
    }

    /**
     * Sets of modifies the display component of an option providing a Label.
     * The label is internally stored as a component.
     * 
     * @param label
     */
    public final void setLabel(Label label) {
        setComponent(label);
    }

    /**
     * This sets the display component using a String. It results in a badly
     * globalized UI
     * 
     * @param label String to use as the display component
     * @deprecated Use {@link #setComponent(Component component)} instead
     */
    public final void setLabel(String label) {
        setComponent(new Label(label));
    }


    /**
     *  
     * @param group 
     */
    public final void setGroup(OptionGroup group) {
        Assert.isUnlocked(this);
        Assert.exists(group);
        m_group = group;
        m_isSelectOption = BebopConstants.BEBOP_OPTION.equals(m_group.getOptionXMLElement());
    }

    /**
     * 
     * @return 
     */
    public final OptionGroup getGroup() {
        return m_group;
    }

    /**
     * Retrieves the name (identifier) of the option group containing this
     * option. Don't know the purpose of this.
     * 
     * @return The name (identifier) of the option group this option belongs
     *         to
     */
    public String getName() {
        return m_group.getName();
    }


    /**
     * Sets the <tt>ONFOCUS</tt> attribute for the HTML tags that compose
     * this element.
     * @param javascriptCode
     */
    public void setOnFocus(String javascriptCode) {
        setAttribute(Widget.ON_FOCUS,javascriptCode);
    }

    /**
     * Sets the <tt>ONBLUR</tt> attribute for the HTML tags that compose
     * this element.
     * @param javascriptCode
     */
    public void setOnBlur(String javascriptCode) {
        setAttribute(Widget.ON_BLUR,javascriptCode);
    }

    /**
     * Sets the <tt>ONSELECT</tt> attribute for the HTML tags that compose
     * this element.
     * @param javascriptCode
     */
    public void setOnSelect(String javascriptCode) {
        setAttribute(Widget.ON_SELECT,javascriptCode);
    }

    /**
     * Sets the <tt>ONCHANGE</tt> attribute for the HTML tags that compose
     * this element.
     * @param javascriptCode
     */
    public void setOnChange(String javascriptCode) {
        setAttribute(Widget.ON_CHANGE,javascriptCode);
    }


    /**
     * Sets the <tt>ON_KEY_UP</tt> attribute for the HTML tags that compose
     * this element.
     * @param javascriptCode
     **/

    public void setOnKeyUp(String javascriptCode) {
        setAttribute(Widget.ON_KEY_UP, javascriptCode);
    }

    /**
     * Sets the <tt>ONCLICK</tt> attribute for the HTML tags that compose
     * this element.
     * @param javascriptCode
     */
    public void setOnClick(String javascriptCode) {
        setAttribute(Widget.ON_CLICK,javascriptCode);
    }

    private ParameterData getParameterData(PageState s) {
        return m_group.getParameterData(s);
    }

    public boolean isSelected(ParameterData data) {
        if (data == null || data.getValue() == null) {
            return false;
        }
        Object value = data.getValue();

        Object[] selectedValues;
        if (value instanceof Object[]) {
            selectedValues = (Object[])value;
        } else {
            selectedValues = new Object[] {value};
        }
        String optionValue = getValue();

        if (optionValue == null || selectedValues == null) {
            return false;
        }
        for (Object selectedValue : selectedValues) {
            if (selectedValue != null 
                && optionValue.equalsIgnoreCase(selectedValue.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate XML depending on what OptionGr.
     * 
     * @param s
     * @param e
     */
    @Override
    public void generateXML(PageState s, Element e) {
        Element option = e.newChildElement(m_group.getOptionXMLElement(), BEBOP_XML_NS);
        if ( ! m_isSelectOption ) {
            option.addAttribute("name", getName());
        }
        option.addAttribute("value", getValue());

        if (m_component != null) {
            m_component.generateXML(s, option);
        } else {
            (new Label()).generateXML(s, option);
        }

        exportAttributes(option);
        if ( isSelected(getParameterData(s)) ) {
            if ( m_isSelectOption ) {
                option.addAttribute("selected", "selected");
            } else {
                option.addAttribute("checked", "checked");
            }
        }
    }

    /**
     * Kludge to live with the fact that options don't do their own
     * printing. Don't use this method, it will go away !
     * 
     * @deprecated Will be removed without replacement once option handling
     *             has been refactored.
     */
    final void generateAttributes(Element target) {
        exportAttributes(target);
    }

}
