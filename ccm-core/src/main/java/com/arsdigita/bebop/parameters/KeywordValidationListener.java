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

import org.apache.oro.text.perl.Perl5Util;

/**
 *     Verifies that the
 *    parameter's value is composed of only alpha-numeric or
 *    underscore characters. [a-zA-Z_0-9]
 *
 *    Note: An empty string will pass the validation tests.
 *
 *    @author Michael Pih 
 *    @version $Id$
 **/
public class KeywordValidationListener implements ParameterListener {

    // match 1 or more instances of a non-alpha-numeric character
    private static final String NON_KEYWORD_PATTERN = "/[^a-zA-Z_0-9]+/";

    private String m_label;

    public KeywordValidationListener(String label) {
        m_label = label;
    }

    public KeywordValidationListener() {
        this("This parameter");
    }

    /**
     * Validates the parameter by checking if the value is a valid keyword.
     * A keyword is defined as any combination of alph-numeric characters,
     * and/or underscores.  [a-zA-Z_0-9]
     *
     * Note: An empty string will pass the validation tests.
     *
     * @param event The parameter event
     */
    public void validate(ParameterEvent event) {
        ParameterData data = event.getParameterData();
        Object value = data.getValue();

        Perl5Util util = new Perl5Util();
        if ( !util.match(NON_KEYWORD_PATTERN, value.toString()) ) {
            return;
        }

        // The error message
        StringBuffer msg = new StringBuffer(128);
        msg
            .append(m_label)
            .append(" must contain only alpha-numeric or underscore characters");
        data.addError(msg.toString());
    }
}
