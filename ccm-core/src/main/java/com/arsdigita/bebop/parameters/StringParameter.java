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

import javax.servlet.http.HttpServletRequest;

/**
 * A string parameter. The value for this parameter model is copied
 * verbatim from the request. This parameter will never cause a validation
 * error.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @version $Id$
 */
public class StringParameter extends ParameterModel {

    /**
     * Create a new parameter which is filled from the request parameter
     * with the given name.
     *
     * @param name the name of the request parameter from which the string
     * is read.
     */
    public StringParameter(String name) {
        super(name);
    }

    /**
     * Retrieve the string from its request parameter. The returned object
     * is of class {@link java.lang.String}. If the request parameter is
     * not present, <code>null</code> is returned.
     *
     * @param request represents the current request
     * @return the {@link java.lang.String} from the request parameter or
     * <code>null</code> if the parameter does not occur in the request.
     */
    public Object transformValue(HttpServletRequest request) {
        return transformSingleValue(request);
    }

    @Override
    public Object unmarshal(String encoded) {
        return encoded;
    }

    @Override
    public Class getValueClass() {
        return String.class;
    }

}
