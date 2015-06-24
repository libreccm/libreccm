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

import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * A parameter that maps keys to values and, given a key, marshals or
 * unmarshals to the corresponding value.
 *
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class EnumerationParameter extends AbstractParameter {

    private static final Logger s_log = Logger.getLogger
        (EnumerationParameter.class);

    private final HashMap m_entries;
    private final HashMap m_reverse;

    public EnumerationParameter(final String name,
                                final int multiplicity,
                                final Object defaalt) {
        super(name, multiplicity, defaalt);

        m_entries = new HashMap();
        m_reverse = new HashMap();
    }

    public EnumerationParameter(final String name) {
        this(name, Parameter.REQUIRED, null);
    }

    public final void put(final String name, final Object value) {
        if (m_entries.containsKey(name)) {
            throw new IllegalArgumentException
                ("name already has a value: " + name);
        }
        if (m_reverse.containsKey(value)) {
            throw new IllegalArgumentException
                ("value already has a name: " + value);
        }
        m_entries.put(name, value);
        m_reverse.put(value, name);
    }

    protected Object unmarshal(final String value, final ErrorList errors) {
        if (m_entries.containsKey(value)) {
            return m_entries.get(value);
        } else {
            final ParameterError error = new ParameterError
                (this, "The value must be one of " + m_entries.keySet());

            errors.add(error);

            return null;
        }
    }

    protected String marshal(Object value) {
        return (String) m_reverse.get(value);
    }

}
