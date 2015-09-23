/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Verifies that the parameter's value contains only letters and/or digits.
 *
 * @author Dennis Gregorovic 
 * @version $Id$
 */
public class StringIsLettersOrDigitsValidationListener extends GlobalizedParameterListener {

    /**
     * 
     * @param title 
     */
    public StringIsLettersOrDigitsValidationListener(String title) {
        setError(new GlobalizedMessage(title, getBundleBaseName()));
    }

    /**
     * 
     */
    public StringIsLettersOrDigitsValidationListener() {
        setError(new GlobalizedMessage( "parameter.only.letters.digits", 
                                        getBundleBaseName() ));
    }

    /**
     * 
     * @param error 
     */
    public StringIsLettersOrDigitsValidationListener(GlobalizedMessage error) {
        setError(error);
    }

    /**
     * 
     * @param e
     * @throws FormProcessException 
     */
    @Override
    public void validate (ParameterEvent e) throws FormProcessException {
        ParameterData data = e.getParameterData();
        Object obj = data.getValue();

        if (obj == null) {
            return;
        }

        String value;
        try {
            value = (String) obj;
        } catch (ClassCastException cce) {
            throw new FormProcessException(cce);
        }

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                data.addError(getError());
                return;
            }
        }
    }
}
