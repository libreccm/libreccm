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
 *     A class
 *    representing an HTML <code>SELECT</code> element.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Rory Solomon 
 *    @author Michael Pih 
 *    @version $Id$ */
public class MultipleSelect extends Select implements BebopConstants {

    public MultipleSelect(String name) {
        super(new ArrayParameter(name));
    }

    /** State that this is a multiple select
     *  @return true
     */
    public boolean isMultiple()
    { return true; }

    /** The XML tag for this derived class of Widget. */

    protected String getElementTag() {
        return BEBOP_MULTISELECT;
    }

}
