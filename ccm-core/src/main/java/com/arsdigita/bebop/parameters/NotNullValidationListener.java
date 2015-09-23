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
package com.arsdigita.bebop.parameters;

import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Verifies that the parameter's value is not null.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Stas Freidin 
 *    @author Rory Solomon 
 * @version $Id$
 */
public class NotNullValidationListener extends GlobalizedParameterListener {

    public static final NotNullValidationListener DEFAULT = new NotNullValidationListener();

    /**
     * Default constructor, used a default standard message as unser 
     * information.
     */
    public NotNullValidationListener() {
        setError(new GlobalizedMessage("bebop.parameters.parameter_is_required", 
                                       getBundleBaseName() ));
    }

    /**
     * Constructor, provides the facility to use a custom provided user 
     * information text.
     * 
     * @param titleKey 
     */
    public NotNullValidationListener(String titleKey) {
        setError(new GlobalizedMessage(titleKey, getBundleBaseName()));
    }

    /**
     * Constructor, provides the facility to use a custom provided user 
     * information text (as a GlobalizedMessage object).
     * 
     * @param error 
     */
    public NotNullValidationListener(GlobalizedMessage error) {
        setError(error);
    }

    /**
     * Validate the data. 
     * 
     * @param e Parameter event containing the data to validate.
     */
    @Override
    public void validate (ParameterEvent e) {
        ParameterData data = e.getParameterData();
        Object value = data.getValue();

        if (value != null && value.toString().length() > 0) {
            return;
        }
        // adds the error globalizedMessage to the ParameterData input object.
        data.addError(getError());
    }
}
