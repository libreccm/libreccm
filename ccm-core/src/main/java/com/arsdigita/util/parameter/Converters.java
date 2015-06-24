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

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.beanutils.Converter;

/**
 * Subject to change.
 *
 * Collects together BeanUtils converters for use by the base
 * <code>Parameter</code>s.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class Converters {

    private static Map s_converters = Collections.synchronizedMap
        (new HashMap());

    /**
     * Gets the <code>Converter</code> registered for
     * <code>clacc</code>.  This method will fail if no converter is
     * found.
     *
     * @param clacc The <code>Class</code> of the parameter value; it
     * cannot be null
     * @return A <code>Converter</code> instance; it cannot be null
     */
    public static final Converter get(final Class clacc) {
        Assert.exists(clacc, Class.class);

        final Converter converter = (Converter) s_converters.get(clacc);

        Assert.exists(converter, Converter.class);

        return converter;
    }

    /**
     * Registers <code>converter</code> for <code>clacc</code>.
     *
     * @param clacc The <code>Class</code> of the parameter value; it
     * cannot be null
     * @param converter The <code>Converter</code> to register to
     * <code>clacc</code>; it cannot be null
     */
    public static final void set(final Class clacc, final Converter converter) {
        if (Assert.isEnabled()) {
            Assert.exists(clacc, Class.class);
            Assert.exists(converter, Converter.class);
        }

        s_converters.put(clacc, converter);
    }

    /**
     * Converts <code>value</code> using the converter registered for
     * <code>clacc</code>.
     *
     * @param clacc The <code>Class</code> of the parameter value; it
     * cannot be null
     * @param value The <code>String</code>-encoded value of the
     * parameter; it may be null
     * @return The Java object conversion for <code>value</code>; it
     * may be null
     */
    public static final Object convert(final Class clacc, final String value) {
        Assert.exists(clacc, Class.class);

        return get(clacc).convert(clacc, value);
    }
}
