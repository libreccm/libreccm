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

import com.arsdigita.util.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A parameter that manages a collection of <code>Parameter</code> to
 * <code>Object</code> value mappings.
 *
 * Subject to change.
 *
 * @see java.util.Map
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class MapParameter extends AbstractParameter {

    private final ArrayList m_params;

    public MapParameter(final String name,
                        final int multiplicity,
                        final Object defaalt) {
        super(name, multiplicity, defaalt, String.class);

        m_params = new ArrayList();
    }

    public MapParameter(final String name) {
        super(name, String.class);

        m_params = new ArrayList();
    }

    public final void add(final Parameter param) {
        Assert.exists(param, Parameter.class);

        m_params.add(param);
    }

    public final boolean contains(final Parameter param) {
        Assert.exists(param, Parameter.class);

        return m_params.contains(param);
    }

    public final Iterator iterator() {
        return m_params.iterator();
    }

    protected Object doRead(final ParameterReader reader,
                            final ErrorList errors) {
        final HashMap map = new HashMap();
        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();
            final Object value = param.read(reader, errors);

            if (value != null) {
                map.put(param, value);
            }
        }

        return map;
    }

    protected void doValidate(final Object value, final ErrorList errors) {
        final HashMap map = (HashMap) value;
        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            if (map.containsKey(param)) {
                param.validate(map.get(param), errors);
            } else {
                param.validate(param.getDefaultValue(), errors);
            }
        }
    }

    protected void doWrite(final ParameterWriter writer, final Object value) {
        final HashMap map = (HashMap) value;
        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            if (map.containsKey(param)) {
                param.write(writer, map.get(param));
            }
        }
    }
}
