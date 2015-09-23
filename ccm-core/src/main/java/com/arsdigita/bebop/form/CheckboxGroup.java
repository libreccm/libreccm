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




import com.arsdigita.bebop.parameters.ArrayParameter;
// This interface contains the XML element name of this class
// in a constant which is used when generating XML
import com.arsdigita.bebop.util.BebopConstants;

/**
 * A class representing a <em>group</em> of associated checkboxes.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Rory Solomon
 * @author Michael Pih
 * @version $Id$ 
 */
public class CheckboxGroup extends OptionGroup implements BebopConstants {

    public CheckboxGroup(String name) {
        this(new ArrayParameter(name));
    }

    public CheckboxGroup(ArrayParameter param) {
        super(param);
        //m_xmlElement = BEBOP_CHECKBOX;
    }

    /**
     *  Returns a string naming the type of this widget.
     */
    public String getType() {
        return "checkbox";
    }

    /** The XML tag.
     *  @return The tag to be used for the top level DOM element
     *  generated for this type of Widget.  */
    @Override
    protected String getElementTag() {
        return BEBOP_CHECKBOXGROUP;
    }
    
    @Override
    public String getOptionXMLElement() {
        return BEBOP_CHECKBOX;
    }
}
