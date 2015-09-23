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
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.Assert;

/**
 * <p>
 * Check that a string's length falls into a particular range.
 * </p>
 *
 * @version $Id$
 */
public class StringInRangeValidationListener extends GlobalizedParameterListener {

    private final int m_minLength;
    private final int m_maxLength;

    public StringInRangeValidationListener(int minLength, int maxLength) {

        validateRange(minLength, maxLength);

        m_minLength = minLength;
        m_maxLength = maxLength;
        setErrorText("string_in_range", getBundleBaseName());
    }

    public StringInRangeValidationListener(
                                           int minLength, int maxLength, GlobalizedMessage error
                                           ) {
        validateRange(minLength, maxLength);

        m_minLength = minLength;
        m_maxLength = maxLength;
        setError(error);
    }



    public void validate(ParameterEvent e) throws FormProcessException {
        ParameterData data = e.getParameterData();
        Object obj = data.getValue();

        if (StringUtils.emptyString(obj)) {
            if(m_minLength > 0) {
                data.addError(getError());
            }
            return;
        }

        boolean isValid = true;
        if (obj instanceof String[]) {
            String[] values = (String[]) obj;

            for (int i = 0; i < values.length && isValid; i++) {
                String value = values[i];
                isValid = isInRange(value);
            }
        } else if (obj instanceof String) {
            String value = (String) obj;
            isValid = isInRange(value);
        }

        if (!isValid) {
            data.addError(getError());
        }
    }

    private boolean isInRange(final String value) {
        final int length = value.length();
        final boolean isInRange = length >= m_minLength &&
                length <= m_maxLength;
        return isInRange;
    }

    /**
     *  Sets the error text using a given resource bundle.
     *
     * @param text
     * @param bundle
     */
    private void setErrorText(String text, String bundle) {
        setError(new GlobalizedMessage(
                                       text,
                                       bundle,
                                       new Object[] {new Integer(m_minLength), new Integer(m_maxLength)}
                                       ));
    }


    /**
     * Sanity checks range arguments for constructors.
     *
     * @param minLength
     * @param maxLength
     */
    private static void validateRange(int minLength, int maxLength) {
        Assert.isTrue(minLength >= 0, "Minimum length cannot be negative!");
        Assert.isTrue(maxLength > minLength, "Maximum length must be greater than minimum!");
    }


}
