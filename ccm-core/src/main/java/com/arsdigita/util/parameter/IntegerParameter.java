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

import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.log4j.Logger;

/**
 * A parameter representing a Java <code>Integer</code>.
 *
 * Subject to change.
 *
 * @see java.lang.Integer
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class IntegerParameter extends AbstractParameter {

    private final static Logger logger = Logger.getLogger(IntegerParameter.class);

    static {
        logger.debug("Static initalizer starting...");
        Converters.set(Integer.class, new IntegerConverter());
        logger.debug("Static initalizer finished.");
    }

    public IntegerParameter(final String name) {
        super(name, Integer.class);
    }

    public IntegerParameter(final String name,
                            final int multiplicity,
                            final Object defaalt) {
        super(name, multiplicity, defaalt, Integer.class);
    }
}
