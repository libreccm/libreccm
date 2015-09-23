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
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.util.Assert;

/**
 *  Verifies that the
 * string length is less than or equal to the specified value
 *
 * @author Stanislav Freidin 
 * @version $Id$
 */
public class StringLengthValidationListener implements ParameterListener {

    private final int m_maxLength;
    private final String m_errHead;

    public static final StringLengthValidationListener FOUR_K = new StringLengthValidationListener(4000);

    public StringLengthValidationListener(final int maxLength) {
        Assert.isTrue(maxLength > 0, "Max length must be greater than 0");
        m_maxLength = maxLength;
        m_errHead = "The following strings are longer than " + maxLength +
            " characters: ";
    }

    public void validate(ParameterEvent e) throws FormProcessException {

        ParameterData data = e.getParameterData();
        Object obj = data.getValue();

        // Another listener will validate that these values are
        // present if required, but we don't want any null pointer
        // exceptions.
        if (obj == null) {
            return;
        }

        boolean isValid = true;
        String value = "";
        if (obj instanceof String[]) {
            String[] values = (String[]) obj;

            for (int i = 0; i < values.length && isValid; i++) {
                value = values[i];
                isValid = isValid(value);
            }
        } else {
            try {
                value = (String) obj;
                isValid = isValid(value);
            } catch (ClassCastException cce) {
                throw new FormProcessException(cce);
            }
        }


        if (!isValid) {
            StringBuffer msg = new StringBuffer(m_errHead);
            msg.append("'").append(value).append("' ");
            data.addError(msg.toString());
        }
    }

    /**
     *
     * @param value String's length to validate
     * @return false if > m_max_length
     */
    private boolean isValid(final String value) {
        final int length = value.length();
        return length <= m_maxLength;
    }

}
