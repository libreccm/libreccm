/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.util.Classes;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * A parameter representing an instance of a Java class.
 *
 * Subject to change.
 *
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class SingletonParameter extends ClassParameter {

    public SingletonParameter(final String name) {
        super(name);
    }

    public SingletonParameter(final String name,
                              final int multiplicity,
                              final Object defaalt) {
        super(name, multiplicity, defaalt);
    }

    protected String marshal(Object value) {
        return super.marshal(value.getClass());
    }

    protected Object unmarshal(final String value, final ErrorList errors) {
        final Class clacc = (Class) super.unmarshal(value, errors);
        if(clacc == null) {
            return null;
        }

        try {
            return Classes.newInstance(clacc);
        } catch (UncheckedWrapperException uwe) {
            errors.add(new ParameterError(this, uwe.getRootCause()));
            return null;
        }
    }
}
