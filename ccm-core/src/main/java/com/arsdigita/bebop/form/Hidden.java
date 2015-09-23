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
 *    A class representing a hidden HTML form element.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Stas Freidin 
 *    @author Rory Solomon 
 *    @author Michael Pih 
 *    @version $Id$
 */
public class Hidden extends Widget {

    public Hidden(String name) {
        super(name);
    }

    public Hidden(ParameterModel model) {
        super(model);
    }

    /**
     * Returns a string naming the type of this widget.
     */
    public String getType() {
        return "hidden";
    }

    public boolean isCompound() {
        return false;
    }
}
