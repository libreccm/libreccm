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

import com.arsdigita.globalization.Globalization;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;

/**
 * A class that represents the model for URL form parameters.
 *
 * @author Karl Goldstein 
 * @author Uday Mathur 
 * @author Rory Solomon 
 * @version $Id$
 */
public class URLParameter extends StringParameter {

    public URLParameter(String name) {
        super(name);
    }

    @Override
    public Object transformValue(HttpServletRequest request)
                  throws IllegalArgumentException {

        String requestValue = Globalization.decodeParameter(request, getName());
        if (requestValue==null) {
            return null;
        }
        URL URLValue;
        try {
            URLValue = new URL(requestValue);
        } catch (MalformedURLException e) {
            try {
                URLValue = new URL("HTTP://" + requestValue);
            } catch (MalformedURLException e2) {
                throw new IllegalArgumentException
                    (getName() + " is not a valid URL: '" + requestValue +
                     "'; " + e2.getMessage());
            }
        }
        return unmarshal(requestValue);
    }

}
