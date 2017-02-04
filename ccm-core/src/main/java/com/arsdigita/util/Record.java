/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public abstract class Record {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int the runtime environment and
     * set com.arsdigita.util.Record=DEBUG by uncommenting or adding the line.
     */
    private static final Logger LOGGER = LogManager.getLogger(Record.class);

    private Class m_class;
    private Logger m_log;
    private String[] m_fields;
    private boolean m_undergoingAccess = false;

    protected Record(Class clacc, Logger log, String[] fields) {
        m_class = clacc;
        m_fields = fields;
        m_log = log;
    }

    protected final void accessed(String field) {
        if (m_log.isDebugEnabled()) {
            synchronized (this) {
                if (m_undergoingAccess == false) {
                    final Method accessor = accessor(field);

                    m_undergoingAccess = true;
                    final String value = prettyLiteral(value(accessor));
                    m_undergoingAccess = false;

                    m_log.debug("Returning " + value + " for " + field);
                }
            }
        }
    }

    protected final void mutated(String field) {
        if (m_log.isInfoEnabled()) {
            final Method accessor = accessor(field);

            m_undergoingAccess = true;
            final String value = prettyLiteral(value(accessor));
            m_undergoingAccess = false;

            m_log.info(field + " set to " + value);
        }
    }

    private String prettyLiteral(final Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return "\"" + o + "\"";
        } else {
            return o.toString();
        }
    }

    private Method accessor(final String field) {
        try {
            Method method = m_class.getDeclaredMethod("get" + field,
                                                      new Class[]{});

            return method;
        } catch (NoSuchMethodException nsme) {
            try {
                Method method = m_class.getDeclaredMethod("is" + field,
                                                          new Class[]{});

                return method;
            } catch (NoSuchMethodException me) {
                throw new UncheckedWrapperException(nsme);
            }
        }
    }

    private Object value(final Method m) {
        try {
            return m.invoke(this, new Object[]{});
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        } catch (InvocationTargetException ite) {
            throw new UncheckedWrapperException(ite);
        }
    }

    public final String getCurrentState() {
        final StringBuffer info = new StringBuffer();

        for (int i = 0; i < m_fields.length; i++) {
            final Method method = accessor(m_fields[i]);
            final String name = method.getName();
            final String value = prettyLiteral(value(method));
            final int len = name.length();

            if (len < 30) {
                for (int j = 0; j < 30 - len; j++) {
                    info.append(' ');
                }
            }

            info.append(name);
            info.append("() -> ");
            info.append(value);
            info.append("\n");
        }

        return info.toString();
    }

}
