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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.util.GlobalizationUtil;

/**
 * Verifies that the  parameter's value is within a specified range.
 *
 * @author Karl Goldstein 
 * @author Uday Mathur 
 * @author Stas Freidin 
 * @author Rory Solomon
 * @version $Id$
 */
public class NumberInRangeValidationListener implements ParameterListener {

    private final double m_lowerBound;
    private final double m_upperBound;
    private final String m_baseErrorMsg;

    /**
     * 
     * @param a
     * @param b 
     */
    public NumberInRangeValidationListener(Number a, Number b) {
        this(a.doubleValue(),b.doubleValue());
    }

    /**
     * 
     * @param lower
     * @param upper 
     */
    public NumberInRangeValidationListener(long lower, long upper) {
        this( (double)lower, (double)upper );
    }

    /**
     * 
     * @param lower
     * @param upper 
     */
    public NumberInRangeValidationListener(double lower, double upper) {
        if ( upper < lower ) {
            throw new IllegalArgumentException
                ("Lower bound must be less than or equal to upper bound.");
        }

        m_lowerBound = lower;
        m_upperBound = upper;

        StringBuilder msg = new StringBuilder(128);
        msg.append("The following values are out of the specified range of (")
            .append(m_lowerBound)
            .append(",")
            .append(m_upperBound)
            .append(") :");

        m_baseErrorMsg = msg.toString();
    }

    /**
     * 
     * @param e
     * @throws FormProcessException 
     */
    @Override
    public void validate (ParameterEvent e) throws FormProcessException {
        // note: The abstract class Number is the superclass of classes
        // Byte, Double, Float, Integer, Long, and Short.

        ParameterData data = e.getParameterData();
        Object obj = data.getValue();
        boolean isValid = true;

        // Another listener will validate that these values are present if
        // required, but we don't want any null pointer exceptions.
        if ( obj == null ) return;


        StringBuffer msg = null;
        if ( obj instanceof Number[] ) {
                Number[] values = (Number[]) obj;
                for (int i = 0; i < values.length; i += 1) {
                    double val = values[i].doubleValue();
                    if ( m_lowerBound > val || val > m_upperBound ) {
                        if (msg == null) {
                            msg = new StringBuffer(m_baseErrorMsg);
                        }
                        msg.append(val);
                        msg.append(" ");
                        isValid = false;
                    }
                }
        } else if (obj instanceof Number) {

                Number value = (Number) obj;
                double val = value.doubleValue();
                if ( m_lowerBound > val || val > m_upperBound ) {
                    msg = new StringBuffer(m_baseErrorMsg);
                    msg.append(val);
                    isValid = false;
                }
        } else {
            String[] errorMsg= new String[1];
            errorMsg[0]=obj.getClass().toString();
            throw new FormProcessException("Unexpected value type: " + obj.getClass(),
            GlobalizationUtil.globalize("bebop.parameter.unexpected_value_type",errorMsg));
        }

        if (!isValid) {
            data.addError(msg.toString());
        }
    }
}
