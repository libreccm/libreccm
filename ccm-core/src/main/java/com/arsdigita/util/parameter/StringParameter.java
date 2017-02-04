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

import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A parameter representing a Java <code>String</code>.
 *
 * Subject to change.
 *
 * @see java.lang.String
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt
 */
public class StringParameter extends AbstractParameter {

    private static final Logger LOGGER = LogManager.getLogger(
        StringParameter.class);

    static {
        LOGGER.debug("Static initalizer starting...");
        Converters.set(String.class, new StringConverter());
        LOGGER.debug("Static initalizer finished.");
    }

    public StringParameter(final String name,
                           final int multiplicity,
                           final Object defaalt) {
        super(name, multiplicity, defaalt, String.class);
    }

    public StringParameter(final String name) {
        super(name, String.class);
    }

}
