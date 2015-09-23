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
// This interface contains the XML element name of this class
// in a constant which is used when generating XML
import com.arsdigita.bebop.util.BebopConstants;



/**
 *     A class
 *    representing an HTML <code>SELECT</code> element.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Rory Solomon 
 *    @author Michael Pih 
 *    @version $Id$ */
public abstract class Select extends OptionGroup implements BebopConstants {

    public Select(final ParameterModel model) {
        super(model);
        //m_xmlElement = BEBOP_OPTION;
    }

    public Select(final ParameterModel model,
                  final OptionGroup.SortMode sortMode) {
        super(model, sortMode);
    }
    
    public Select(final ParameterModel model,
                  final OptionGroup.SortMode sortMode,
                  final boolean excludeFirst) {
        super(model, sortMode, excludeFirst);
    }
    
    /**
     *  Returns a string naming the type of this widget.
     */
    public String getType() {
        return "select";
    }

    /**
     * Set the HTML size attribute of this widget.
     *
     * @param n The number of visible rows in the widget
     */
    public void setSize(int n) {
        setAttribute("size", Integer.toString(n));
    }

    /**
     *      Callback method for rendering this Select widget in a visitor.
     */
    /*  public void accept(FormVisitor visitor) throws IOException {
        visitor.visitSelect(this);
        }*/

    /** The XML tag.
     *  @return The tag to be used for the top level DOM element
     *  generated for this type of Widget.  */
    protected abstract String getElementTag();
    
    public String getOptionXMLElement() {
        return BEBOP_OPTION;
    }
}
