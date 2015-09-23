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



/**
 *    A class representing a text field in an HTML form.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Rory Solomon 
 *    @author Michael Pih 
 *    @version $Id$
 */
public class TextField extends Widget {

    public TextField(String name) {
        super(name);
    }

    public TextField(ParameterModel model) {
        super(model);
    }

    /**
     *      Returns a string naming the type of this widget.
     */
    public String getType() {
        return "text";
    }

    /**
     *      Sets the <tt>MAXLENGTH</tt> attribute for the <tt>INPUT</tt> tag
     *      used to render this form element.
     */
    public void setMaxLength(int length) {
        setAttribute("MAXLENGTH", String.valueOf(length));
    }

    /**
     *      Sets the <tt>SIZE</tt> attribute for the <tt>INPUT</tt> tag
     *      used to render this form element.
     */
    public void setSize(int size) {
        setAttribute("SIZE", String.valueOf(size));
    }

    public boolean isCompound() {
        return false;
    }


}
