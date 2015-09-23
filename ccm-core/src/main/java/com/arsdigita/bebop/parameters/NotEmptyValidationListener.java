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

import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.commons.lang.StringUtils;

/**
 * Verifies that the parameter's value is non-empty. A value is considererd
 * non-empty if it exists in the page state, and it contains some data besides
 * whitespace.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Stas Freidin
 * @author Rory Solomon
 * @author Bill Schneider
 * @version $Id: NotEmptyValidationListener.java 1502 2007-03-20 11:38:53Z
 * chrisgilbert23 $
 */
public class NotEmptyValidationListener extends GlobalizedParameterListener {

    /**
     * Default Constructor setting a predefined label as error message.
     */
    public NotEmptyValidationListener() {
        setError(new GlobalizedMessage("bebop.parameters.parameter_not_empty",
                                       getBundleBaseName())
        );
    }

    /**
     * Constructor taking a label specified as key into a resource bundle to
     * customize the error message.
     *
     * @param label key into the resource bundle
     */
    public NotEmptyValidationListener(String label) {
        setError(new GlobalizedMessage(label, getBundleBaseName()));
    }

    /**
     * Constructor taking a GlobalizedMessage as error message to display.
     *
     * @param error GloblizedMessage taken as customized error message.
     */
    public NotEmptyValidationListener(GlobalizedMessage error) {
        setError(error);
    }

    /**
     * Validate Method required and used to validate input.
     *
     * @param e ParameterEvent containing the data
     */
    @Override
    public void validate(ParameterEvent e) {

        ParameterData data = e.getParameterData();
        Object value = data.getValue();

        if (value != null) {
        	// all these are possible values:
            // "&nbsp;"
            // "    &nbsp;"
            // "    &nbsp;     "
            // need to validate for all these possibilities
            //
            // take out whitespace at the edges
            String valueString = value.toString().trim();
            // then take out &nbsp; at the edges
            
            valueString = StringUtils.strip(valueString, Character.toString(
                                            '\u00A0'));
            valueString = StringUtils.strip(valueString, Character.toString(
                                            '\u2007'));
            if (valueString.length() > 0) {
                // non-empty value, just return
                return;
            }
        }

        // Empty or null value, add error message to parameter data object.
        data.addError(getError());
    }

}
