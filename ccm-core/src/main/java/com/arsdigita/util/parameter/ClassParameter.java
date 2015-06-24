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

import org.apache.commons.beanutils.converters.ClassConverter;
import org.apache.log4j.Logger;

/**
 * A parameter representing a Java <code>Class</code>.
 *
 * Subject to change.
 *
 * @see java.lang.Class
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class ClassParameter extends AbstractParameter {

    private static final Logger logger = Logger.getLogger(ClassParameter.class);

    static {
        logger.debug("Static initalizer starting...");
        Converters.set(Class.class, new ClassConverter());
        logger.debug("Static initalizer finished.");
    }

    public ClassParameter(final String name) {
        super(name, Class.class);
    }

    public ClassParameter(final String name,
                          final int multiplicity,
                          final Object defaalt) {
        super(name, multiplicity, defaalt, Class.class);
    }

    // value != null
    protected Object unmarshal(String value, ErrorList errors) {
        Class theClass = null;
        try {
            theClass = Class.forName(value);
        } catch (ClassNotFoundException e) {
            errors.add(new ParameterError(this, "No such class: " + value));
        }

        return theClass;
    }

    protected String marshal(Object value) {
        Class theClass = ((Class) value);
        if (theClass == null) {
            return null;
        } else {
            return theClass.getName();
        }
    }
}
