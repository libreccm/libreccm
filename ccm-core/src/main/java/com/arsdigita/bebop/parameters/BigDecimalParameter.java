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
import java.math.BigDecimal;

/**
 * A class which represents a BigDecimal
 * @version $Id$
 */
public class BigDecimalParameter extends ParameterModel {

    public BigDecimalParameter(String name) {
        super(name);
    }

    public Object transformValue(HttpServletRequest request)
        throws IllegalArgumentException {
        return transformSingleValue(request);
    }

    @Override
    public Object unmarshal(String encoded)
        throws IllegalArgumentException {

        if (encoded == null || encoded.length() == 0) {
            return null;
        }
        try {
            return new BigDecimal(encoded);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException
                (getName() + " should be a BigDecimal: '" + encoded + "'");
        }
    }

    @Override
    public Class getValueClass() {
        return BigDecimal.class;
    }

}
