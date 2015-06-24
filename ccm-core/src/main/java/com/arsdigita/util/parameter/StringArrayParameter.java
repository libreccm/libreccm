/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.parameter;

// import com.arsdigita.util.parameter.StringParameter;
// import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.StringUtils;

/**
 * StringArrayParameter
 *
 * Usage Example:
 * <pre>
 * private static parameter exampleName ;
 * exampleName = new StringArrayParameter(
 *                   "com.arsdigita.package.example_name",
 *                   Parameter.REQUIRED,
 *                   new String[] {"String Example 01","String Example 02"}
 *                                       );
 * </pre>
 *
 * @version $Id$
 */
public class StringArrayParameter extends StringParameter {

    /**
     * 
     * @param name: String literal 
     * @param multiplicity Indicator wether required (1) or not (0) (nullable)
     * @param defaalt default value
     */
    public StringArrayParameter(final String name,
                                final int multiplicity,
                                final Object defaalt) {
        super(name, multiplicity, defaalt);

    }

    /**
     * Converts a String[] object into a literal representation.
     *
     * @param value
     * @return
     */
    @Override
    protected String marshal(final Object value) {
        if (value == null) {
            return null;
        } else {
            return StringUtils.join((String[])value, ',');
        }
    }

    /**
     * 
     * @param literal
     * @param errors
     * @return
     */
    @Override
    protected Object unmarshal(final String literal,
                               final ErrorList errors) {
        final String[] literals = StringUtils.split(literal, ',');
        final String[] strings = new String[literals.length];

        for (int i = 0; i < literals.length; i++) {
            final String elem = literals[i];

            strings[i] = (String) super.unmarshal(elem, errors);

            if (!errors.isEmpty()) {
                break;
            }
        }
        return strings;
    }

    @Override
    protected void doValidate(final Object value,
                              final ErrorList errors) {
        if (value != null) {
            final String[] strings = (String[]) value;

            for (int i = 0; i < strings.length; i++) {
                super.doValidate(strings[i], errors);

                if (!errors.isEmpty()) {
                    break;
                }
            }
        }
    }
}
