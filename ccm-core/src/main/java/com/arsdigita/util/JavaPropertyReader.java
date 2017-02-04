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
package com.arsdigita.util;

import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * An implementation of <code>ParameterReader</code> that uses
 * standard Java properties to retrieve values.
 *
 * Subject to change.
 *
 * @see com.arsdigita.util.parameter.ParameterReader
 * @see JavaPropertyWriter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class JavaPropertyReader implements ParameterReader {

    private static final Logger LOGGER = LogManager.getLogger
        (JavaPropertyReader.class);

    private final Properties m_props;

    /**
     * Constructs a parameter reader that uses <code>props</code>.
     *
     * @param props The <code>Properties</code> object that stores
     * property values; it cannot be null
     */
    public JavaPropertyReader(final Properties props) {
        Assert.exists(props, Properties.class);

        m_props = props;
    }

    /**
     * Loads the internal <code>Properties</code> object using
     * <code>in</code>.
     *
     * @param in The <code>InputStream</code> that has the source
     * properties; it cannot be null
     */
    public final void load(final InputStream in) {
        Assert.exists(in, InputStream.class);

        try {
            m_props.load(in);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    /**
     * Reads a <code>String</code> value back for a
     * <code>param</code>.
     *
     * @param param The <code>Parameter</code> whose value is
     * requested; it cannot be null
     * @param errors An <code>ErrorList</code> to trap any errors
     * encountered when reading; it cannot be null
     * @return The <code>String</code> value for <code>param</code>;
     * it can be null
     */
    @Override
    public final String read(final Parameter param, final ErrorList errors) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Reading " + param + " from " + m_props);
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.exists(errors, ErrorList.class);
        }

        return m_props.getProperty(param.getName());
    }

    /**
     * Returns a <code>String</code> representation of this object.
     *
     * @return super.toString() + "," + properties.size()
     */
    @Override
    public String toString() {
        return super.toString() + "," + m_props.size();
    }
}
