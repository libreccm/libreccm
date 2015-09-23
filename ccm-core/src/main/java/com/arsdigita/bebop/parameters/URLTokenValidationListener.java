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
//import com.arsdigita.bebop.parameters.ParameterData;

import org.apache.oro.text.perl.Perl5Util;

/**
 * Verifies that the parameter's value is composed only of character which are
 * valid as part on an URL name (the token).
 * That is only alpha-numeric, underscore, or hyphen characters. 
 * [a-zA-Z_0-9\-]
 *
 * Note: An empty string will pass the validation tests.
 *
 * @author Michael Pih 
 * @version $Id$
 */
public class URLTokenValidationListener extends GlobalizedParameterListener {

    /** match 1 or more instances of a non-alpha-numeric character  */
    private static final String NON_KEYWORD_PATTERN = "/[^a-zA-Z_0-9\\-]+/";

    /**
     * Default Constructor setting a predefined label as error message.
     */
    public URLTokenValidationListener() {
        setError(new GlobalizedMessage("bebop.parameters.must_be_valid_part_of_url", 
                                       getBundleBaseName() )
                );
    }

    /**
     * Constructor taking a label specified as key into a resource bundle to
     * customize the error message.
     * 
     * @param label key into the resource bundle
     * @deprecated use URLTokenValidationListener(GlobalizedMessage error) 
     */
    public URLTokenValidationListener(String label) {
        setError(new GlobalizedMessage(label, getBundleBaseName()));
    }

    /**
     * Constructor taking a GlobalizedMessage as error message to display.
     * 
     * @param error GloblizedMessage taken as customized error message.
     */
    public URLTokenValidationListener(GlobalizedMessage error) {
        setError(error);
    }


    /**
     * Validates the parameter by checking if the value is a valid keyword.
     * A keyword is defined as any combination of alph-numeric characters,
     * hyphens, and/or underscores.  [a-zA-Z_0-9\-]
     *
     * Note: An empty string will pass the validation tests.
     *
     * @param event The parameter event
     */
    @Override
    public void validate(ParameterEvent event) {
        ParameterData data = event.getParameterData();
        Object value = data.getValue();

        Perl5Util util = new Perl5Util();
        if ( !util.match(NON_KEYWORD_PATTERN, value.toString()) ) {
            return;
        }
        data.addError(getError());
    }
}
