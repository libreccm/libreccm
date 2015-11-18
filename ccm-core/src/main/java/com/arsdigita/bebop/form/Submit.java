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
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.util.Assert;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.xml.Element;

/**
 * Submit buttons on HTML forms. The button will only do anything
 * useful if it is contained directly or indirectky in a {@link
 * com.arsdigita.bebop.Form}.
 *
 *    @author Karl Goldstein
 *    @author Uday Mathur
 *    @author Stas Freidin
 *    @author Rory Solomon
 *    @author Michael Pih
 *    @version $Id$
 */
public class Submit extends Widget {

    private GlobalizedMessage m_buttonLabel;

    /**
     * Creates a new submit button. The button will use <code>name</code>
     * for both its name attribute and as the label displayed to the
     * user.
     *
     * @param name the button's name and label
     * @pre name != null
     * @deprecated use Submit(GlobalizedMessage) or even better
     *                 Submit(name, GlobalizedMessage) instead
     */
    public Submit(String name) {
        // To pacify the com.arsdigita.web.ParameterMap#validateName(String)
        // method, get rid of spaces.
        this(name.replace(' ', '_'), name);
    }

    /**
     * Creates a new submit button.
     *
     * @param name the button's name
     * @param label the label displayed on the button
     * @deprecated use Submit(name, GlobalizedMessage) instead
     */
    public Submit(String name, String label) {
        super(name);
        setButtonLabel(label);
    }

    /**
     * <p>
     * Create a new submit button.
     * </p>
     *
     * @param label the label displayed on the button
     */
    public Submit(GlobalizedMessage label) {
        super(label.getKey());
        setButtonLabel(label);
    }

    /**
     * <p>
     * Create a new submit button.
     * </p>
     *
     * @param name the button's name
     * @param label the label displayed on the button
     */
    public Submit(String name, GlobalizedMessage label) {
        super(name);
        setButtonLabel(label);
    }

    /**
     * Create a new submit button.
     *
     * @param model a <code>ParameterModel</code> value
     */
    public Submit(ParameterModel model) {
        super(model);
        setButtonLabel(model.getName());
    }

    public boolean isCompound() {
        return false;
    }

    /**
     *      Returns a string naming the type of this widget.
     */
    public String getType() {
        return "submit";
    }

    /**
     * Determine whether or not javascript should be included in the
     * onClick field to try to prevent doubleclicks.
     *
     * @param avoid true to avoid doubleclicks, false otherwise.
     * @deprecated use configuration parameter waf.bebop.dcp_message
     *             to enable/disable double-click protection globally.
     *             In case you want to disable DCP on per-widget basis,
     *             use {@link #setOnClick(String)}.
     * @see #setOnClick(String)
     */
    public void avoidDoubleClick ( boolean avoid ) {
    }

    /**
     * <p>
     * Sets the text that will be displayed on the actual button.
     * </p>
     *
     * @param buttonLabel The label that shows up on the button.
     * @deprecated Refactor to use setButtonLabel(GlobalizedMessage) instead
     */
    public void setButtonLabel(String buttonLabel) {
        setButtonLabel(new GlobalizedMessage(buttonLabel));
    }

    /**
     * <p>
     * Sets the text that will be displayed on the actual button.
     * </p>
     *
     * @param buttonLabel The label that shows up on the button.
     */
    public void setButtonLabel(GlobalizedMessage buttonLabel) {
        Assert.isUnlocked(this);
        m_buttonLabel = buttonLabel;
    }

    /**
     * Get the value of the submit button. Submit buttons are special,
     * since only the value for the submit button that is clicked is
     * submitted by the browser. Contrary to what <code>getValue</code>
     * does for other widgets, the value returned from this incarnation
     * of <code>getValue</code> is either the value that was included in
     * the current request, or, if there is none, the default value.
     *
     * Must not be final, because globalized Submit needs to override.
     */
    public Object getValue(PageState ps) {
        return getValue().localize(ps.getRequest());
    }

    /**
     * <p>
     * Return the the buttons label.
     * </p>
     *
     * @return GlobalizedMessage The buttons label.
     */
    public GlobalizedMessage getValue() {
        return m_buttonLabel;
    }

    /**
     * <p>
     * Generates the DOM for this widget
     * </p>
     *
     * @param state The current PageState.
     * @param parent This widget's parent.
     */
    protected void generateWidget(PageState state, Element parent) {
        Element widget = parent.newChildElement(getElementTag(), BEBOP_XML_NS);

        widget.addAttribute("type", getType());
        widget.addAttribute("name", getName());
        exportAttributes(widget);
        if (getValue(state) != null) {
            widget.addAttribute("value", (String) getValue(state));
        } else {
            widget.addAttribute("value", "");
        }
    }

    /**
     * Return <code>true</code> if the user clicked on this submit button to
     * submit the form in which the button is contained.
     *
     * @param ps the state of the current request
     * @return <code>true</code> if the user clicked this button to submit
     * the enclosing form.
     */
    public boolean isSelected(PageState ps) {
        ParameterData p = getParameterData(ps);

        return (ps != null) && (p.getValue() != null);
    }


    /**
     * Set the HTML size attribute of this widget.
     *
     * @param n The size of this widget, in characters
     */
    public void setSize(int n) {
        setAttribute("size", Integer.toString(n));
    }

    /**
     * Sets the onclick parameter and <em>disables the Javascript-based double-click protection</em>
     * for this widget.
     * This is the preferred method to disable double-click protection on per-widget basis.
     * @param command The JavaScript to execute when the button is clicked.
     */
    public void setOnClick(String command) {
        setAttribute(ON_CLICK, command);
    }

    /**
     * Get the onclick parameter.
     */
    public String getOnClick() {
        return getAttribute(ON_CLICK);
    }

    /**
     * Registers getName()+".x" and getName()+".y" so that html image
     * submits will work too  
     */
    public void register(Form f, FormModel m) {
	super.register(f,m);
        m.addFormParam(new StringParameter(getParameterModel().getName() + ".x"));
        m.addFormParam(new StringParameter(getParameterModel().getName() + ".y"));
    }

    /**
     * @return the parameter value for this widget
     * @post returns null if the FormData are missing
     */
    protected ParameterData getParameterData(PageState ps) {
	ParameterData data = super.getParameterData(ps);
	if (data != null && data.getValue() != null) {
	    return data;
	} else {
	    FormData fd = getForm().getFormData(ps);
	    if (fd != null) {
		return fd.getParameter(getName()+".x");
	    }
	    return null;
	}
    }

}
