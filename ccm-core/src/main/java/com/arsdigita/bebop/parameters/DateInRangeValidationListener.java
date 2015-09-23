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

import java.text.DateFormat;
import java.util.Date;

import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;

/**
 *     Verifies that the
 *    parameter's value is within a specified date range.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Stas Freidin 
 *    @author Rory Solomon 
 * @version $Id$
 */

public class DateInRangeValidationListener implements ParameterListener {

    private final Date m_lowerBound;
    private final Date m_upperBound;
    private final String m_baseErrorMsg;

    public DateInRangeValidationListener(Date lower, Date upper) {
        if ( lower.compareTo(upper) > 0 ) {
            throw new IllegalArgumentException
                ("Lower bound must be earlier than or equal to upper bound.");
        }

        m_lowerBound = lower;
        m_upperBound = upper;

        DateFormat formatter = DateFormat.getDateInstance();
        StringBuffer msg = new StringBuffer(128);
        msg.append("The following dates are out of the specified range of (")
            .append(formatter.format(m_lowerBound))
            .append(",")
            .append(formatter.format(m_upperBound))
            .append(") :");

        m_baseErrorMsg = msg.toString();
    }

    public void validate (ParameterEvent e) {
        // gets the date format for the default style and locale
        DateFormat formatter = DateFormat.getDateInstance();

        ParameterData data = e.getParameterData();
        Object obj = data.getValue();

        // Another listener will validate that these values are present if
        // required, but we don't want any null pointer exceptions.
        if ( obj == null ) return;

        boolean isValid = true;

        StringBuffer msg = null;
        if ( data.getValue() instanceof Object[] ) {
            Date[] values = (Date[]) obj;

            for (int i = 0; i < values.length; i += 1) {
                final Date value = values[i];
                if (isOutOfRange(value)) {
                    if (msg == null) {
                        msg = makeErrorBuffer();
                    }
                    msg.append(" ").append(formatter.format(value));
                    isValid = false;
                }
            }
        } else {
            final Date value = (Date) obj;
            if (isOutOfRange(value)) {
                msg = makeErrorBuffer();
                msg.append(" ").append(formatter.format(value));
                isValid = false;
            }
        }

        if (! isValid) {
            data.addError(msg.toString());
        }
    }

    private boolean isOutOfRange(final Date value) {
        return m_lowerBound.compareTo(value) >= 0
                               || value.compareTo(m_upperBound) >= 0;
    }

    private StringBuffer makeErrorBuffer() {
        return new StringBuffer(m_baseErrorMsg);
    }
}
