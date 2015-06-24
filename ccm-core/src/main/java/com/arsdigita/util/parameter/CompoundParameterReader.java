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
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Aggregates a set of <code>ParameterReaders</code> so they may be
 * treated as one.
 * 
 * Subject to change.
 *
 * @see ParameterReader
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class CompoundParameterReader implements ParameterReader {

    private static final Logger s_log = Logger.getLogger
        (CompoundParameterReader.class);

    private final List m_readers;

    /**
     * Constructs a new compound parameter reader.
     */
    public CompoundParameterReader() {
        m_readers = new ArrayList();
    }

    /**
     * Adds <code>reader</code> to the set of component readers.
     *
     * @param reader The <code>ParameterReader</code> being added; it
     * cannot be null
     */
    public void add(final ParameterReader reader) {
        Assert.exists(reader, ParameterReader.class);

        m_readers.add(reader);
    }

    /**
     * @see ParameterReader#read(Parameter,ErrorList)
     */
    public String read(final Parameter param, final ErrorList errors) {
        for (final Iterator it = m_readers.iterator(); it.hasNext(); ) {
            final ParameterReader reader = (ParameterReader) it.next();

            final String result = reader.read(param, errors);

            if (result != null) {
                return result;
            }
        }

        return null;
    }
}
