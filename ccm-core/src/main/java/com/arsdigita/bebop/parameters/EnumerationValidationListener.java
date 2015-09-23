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
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;

/**
 *     Verifies that the
 *    parameter's value is a member of a list of Strings
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Stas Freidin 
 *    @author Rory Solomon
 * @version  $Id$
 */

public class EnumerationValidationListener implements ParameterListener {

    private final String[] m_allowedValues;
    private final boolean m_caseSensitive;
    private final String m_baseErrorMsg;

    public EnumerationValidationListener(String[] allowedValues) {
        this(allowedValues, false);
    }

    public EnumerationValidationListener(String[] allowedValues,
                                         boolean caseSensitive) {
        m_allowedValues = allowedValues;
        m_caseSensitive = caseSensitive;
        StringBuffer msg = new StringBuffer(128);

        msg.append(" must be one of:");
        for (int j = 0; j < m_allowedValues.length;j++) {
            msg.append(m_allowedValues[j]).append(" ");
        }

        m_baseErrorMsg = msg.toString();
    }


    // there are many cool optimizations we plan to put here
    // 1)changing the order of the loops
    // 2)different data structs based on size
    // Another listener will validate that these values are present if
    // required, but we don't want any null pointer exceptions.

    public void validate (ParameterEvent e) {
        /* this loop is theta(n^2), but the constant factor is small.
           using Collection.contains() would also be n^2 with more memory overhead
           Hashmaps are disasters for memory, and good only at large sizes.
        */
        ParameterData data = e.getParameterData();


        if ( m_allowedValues == null) { return; }

        if (data.getValue() instanceof Object[]) {
            String[] names =(String[]) data.getValue();
            for (int i = 0; i < names.length; i += 1) {
                String name = names[i];
                if (!validateOneString(name)) {
                    data.addError(name + m_baseErrorMsg);
                }
            }
        } else {
            String name = (String) data.getValue();
            if (!validateOneString(name)) {
                data.addError(name + m_baseErrorMsg);
            }
        }
    }

    private boolean validateOneString ( String value) {
        if ( value == null ) return true;

        boolean isValid = false;

        for (int j = 0; j < m_allowedValues.length; j++) {
            if (m_caseSensitive) {
                if (value.equals(m_allowedValues[j])) {
                    isValid = true;
                    break;
                }
            } else {
                if (value.equalsIgnoreCase(m_allowedValues[j])) {
                    isValid = true;
                    break;
                }
            }
        }

        return isValid;
    }
}
