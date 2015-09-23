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


import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
// This interface contains the XML element name of this class
// in a constant which is used when generating XML
import com.arsdigita.bebop.util.BebopConstants;


/**
 *     A class
 *    representing a <em>group</em> of associated radio buttons.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Rory Solomon 
 *    @author Michael Pih 
 *    @version $Id$ */
public class RadioGroup extends OptionGroup implements BebopConstants {

    // xml attribute for layout
    private final static String AXIS = "axis";

    /**
     * Specifies that options should be laid out left to right.
     */
    // this is default
    public final static int HORIZONTAL = 1;

    /**
     * Specifies that options should be laid out top to bottom.
     */
    public final static int VERTICAL = 2;

    public RadioGroup(String name) {
        this(new StringParameter(name));
    }

    public RadioGroup(ParameterModel model) {
        super(model);
        //m_xmlElement = BEBOP_RADIO;
    }

    /**
     *  Returns a string naming the type of this widget.
     */
    public String getType() {
        return "radio";
    }

    /** The XML tag.
     *  @return The tag to be used for the top level DOM element
     *  generated for this type of Widget.  */
    protected String getElementTag() {
        return BEBOP_RADIOGROUP;
    }
    
    @Override
    public String getOptionXMLElement() {
        return BEBOP_RADIO;
    }

    /**
     * Is this a multiple (and not single) selection option group?
     *
     * @return true if this OptionGroup can have more than one
     * selected option; false otherwise.
     */
    public boolean isMultiple() {
        return false;
    }

    /**
     * Sets the layout for the options in this radio group.
     *
     * @param layout one of RadioGroup.VERTICAL or RadioGroup.HORIZONTAL
     **/
    public void setLayout(int layout) {
        setAttribute(AXIS, String.valueOf(layout));
    }

    /**
     * Returns the layout for the options in this radio group.
     *
     * @return one of RadioGroup.VERTICAL or RadioGroup.HORIZONTAL
     **/
    public int getLayout() {
        String value = getAttribute(AXIS);
        if (value == null) {
            return HORIZONTAL;
        } else if (value.equals(String.valueOf(HORIZONTAL))) {
            return HORIZONTAL;
        } else if (value.equals(String.valueOf(VERTICAL))) {
            return VERTICAL;
        } else {
            throw new IllegalStateException(
                                            "invalid value for axis attribute: " + value);
        }
    }
}
