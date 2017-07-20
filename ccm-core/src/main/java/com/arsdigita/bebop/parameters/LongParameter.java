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

/**
 * A class that represents the model for number form parameters.
 *
 * @author <a href="randyg@alum.mit.edu">Randy Graebner</a>
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LongParameter extends NumberParameter {

    public LongParameter(final String name) {
        super(name);
    }

    @Override
    public Object unmarshal(final String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return null;
        } else {
            try {
                return new Long(encoded);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(String
                    .format("%s should be a Long Number, but is '%s'",
                            getName(),
                            encoded));
            }
        }
    }

    @Override
    public Class<?> getValueClass() {
        return Long.class;
    }

}
