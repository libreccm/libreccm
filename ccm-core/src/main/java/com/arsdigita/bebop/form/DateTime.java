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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.DateTimeParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.xml.Element;

/**
 *    A class representing a date and time field in an HTML form.
 *    (based on the code in Date.java)
 *
 *    @author Sören Bernstein <quasi@quasiweb.de>
 *    @version $Id$
 */
public class DateTime extends Widget implements BebopConstants {

    private Date m_date;
    private Time m_time;

    /**
     * Construct a new DateTime. The model must be a DateTimeParameter
     * @param model
     */
    public DateTime(ParameterModel model) {
        this(model, false);
    }

    /**
     * Construct a new DateTime. The model must be a DateTimeParameter
     * @param model
     * @param showSeconds
     */
    public DateTime(ParameterModel model, boolean showSeconds) {
        super(model);

        if (!(model instanceof DateTimeParameter)) {
            throw new IllegalArgumentException(
                    "The DateTime widget " + model.getName() +
                    " must be backed by a DateTimeParameter parmeter model");
        }

        m_date = new Date(model);
        m_time = new Time(model, showSeconds);
    }

    public DateTime(String name) {
        this(new DateTimeParameter(name));
    }

    public void setYearRange(int startYear, int endYear) {
        m_date.setYearRange(startYear, endYear);
    }

    /**
     * Returns a string naming the type of this widget.
     * @return 
     */
    @Override
    public String getType() {
        return "dateTime";
    }

    /**
     * Sets the <tt>MAXLENGTH</tt> attribute for the <tt>INPUT</tt> tag
     * used to render this form element.
     */
    public void setMaxLength(int length) {
        setAttribute("MAXLENGTH", String.valueOf(length));
    }

    public boolean isCompound() {
        return true;
    }

    /** The XML tag for this derived class of Widget.
     */
    @Override
    protected String getElementTag() {
        return BEBOP_DATETIME;
    }

    @Override
    public void generateWidget(PageState ps, Element parent) {

        if (!isVisible(ps)) {
            return;
        }

        Element datetime = parent.newChildElement(getElementTag(), BEBOP_XML_NS);
        datetime.addAttribute("name", getParameterModel().getName());
        m_date.generateLocalizedWidget(ps, datetime);
        m_time.generateLocalizedWidget(ps, datetime);

        generateDescriptionXML(ps, datetime);
        
        // If Element could be null insert a extra widget to clear entry
        if (!hasValidationListener(new NotNullValidationListener())) {
            datetime.newChildElement("NoDateTime");
        }
    }

    @Override
    public void setDisabled() {
        m_date.setDisabled();
        m_time.setDisabled();
    }

    @Override
    public void setReadOnly() {
        m_date.setReadOnly();
        m_time.setReadOnly();
    }

    /**
     * Sets the Form Object for this Widget. This method will throw an
     * exception if the _form pointer is already set. To explicity
     * change the _form pointer the developer must first call
     * setForm(null)
     *
     * @param the <code>Form</code> Object for this Widget.
     * @exception IllegalStateException if form already set.
     */
    @Override
    public void setForm(Form f) {
        super.setForm(f);
        m_date.setForm(f);
        m_time.setForm(f);
    }
}
