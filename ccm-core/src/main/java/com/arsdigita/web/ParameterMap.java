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
package com.arsdigita.web;

import com.arsdigita.util.Assert;
import com.arsdigita.util.OrderedMap;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id$
 */
public class ParameterMap implements Cloneable {

    private static final Logger s_log = Logger.getLogger(ParameterMap.class);

    private static ArrayList s_listeners = new ArrayList();

    private OrderedMap m_params;

    public ParameterMap() {
        m_params = new OrderedMap();
    }

    // Expects an *encoded* query string, just as
    // request.getQueryString() returns.
    private ParameterMap(final String query) {
        this();

        if (query != null) {
            parseQueryString(query);
        }
    }

    public ParameterMap(final HttpServletRequest sreq) {
        this();

        final Enumeration keys = sreq.getParameterNames();

        while (keys.hasMoreElements()) {
            final String name = (String) keys.nextElement();
            final String[] values = (String[]) sreq.getParameterValues(name);

            setParameterValues(name, values);
        }
    }

    public ParameterMap(final Map params) {
        this();
        
        final Set keySet = params == null ? null : params.keySet();
        if (keySet != null) {
            final Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                final String name = (String)keys.next();
                final String[] values = (String[])params.get(name);
                
                setParameterValues(name, values);
            }
        }
    }

    public static final ParameterMap fromString(final String query) {
        Assert.exists(query, "String query");

        if (query.startsWith("?")) {
            return new ParameterMap(query.substring(1));
        } else {
            return new ParameterMap(query);
        }
    }

    public static final void registerListener
            (final ParameterListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering parameter listener " + listener);
        }

        s_listeners.add(listener);
    }

    public Object clone() throws CloneNotSupportedException {
        final ParameterMap result = (ParameterMap) super.clone();

        result.m_params = (OrderedMap) m_params.clone();

        return result;
    }

    private void parseQueryString(final String query) {
        final int len = query.length();
        int start = 0;

        while (true) {
            int end = -1;

            for (int i = start; i < len - 1; i++) {
                if (query.charAt(i) == '&' || query.charAt(i) == ';') {
                    end = i;

                    break;
                }
            }

            if (end == -1) {
                if (len > start) {
                    try {
                        parseParameter(query, start, len);
                    } catch (DecoderException e) { 
                        throw new UncheckedWrapperException(e);
                    }
                }

                break;
            } else {
                try {
                    parseParameter(query, start, end);
                } catch (DecoderException e) { 
                    throw new UncheckedWrapperException(e);
                }
                start = end + 1;
            }
        }
    }

    private void parseParameter(final String query,
                                final int start,
                                final int end)  throws DecoderException {
        final int sep = query.indexOf('=', start);

        if (Assert.isEnabled()) {
            Assert.isTrue(start > -1);
            Assert.isTrue(end > -1);
        }        

        if (sep > -1) {
            URLCodec codec = new URLCodec();
            final String name = codec.decode(query.substring(start, sep));
            final String value = codec.decode
                (query.substring(sep + 1, end));

            if (s_log.isDebugEnabled()) {
                s_log.debug("Parameter " + name + " = " + value);
            }

            final String[] values = getParameterValues(name);

            if (values == null) {
                setParameter(name, value);
            } else {
                final String[] newValues = new String[values.length + 1];

                for (int i = 0; i < values.length; i++) {
                    newValues[i] = values[i];
                }

                newValues[values.length] = value;

                setParameterValues(name, newValues);
            }
        }
    }

    private void validateName(final String name) {
        Assert.exists(name, "String name");
        Assert.isTrue(!name.equals(""),
                          "The name must not be the empty string");
        Assert.isTrue(name.indexOf(" ") == -1,
                          "The name must not contain any spaces: '" +
                          name + "'");
    }

    public final void clear() {
        m_params.clear();
    }

    public final String getParameter(final String name) {
        final String[] values = (String[]) m_params.get(name);

        if (values == null) {
            return null;
        } else {
            return values[0];
        }
    }

    /**
     * Sets the parameter <code>name<code> to <code>value</code>.  If
     * <code>value</code> is null, this method sets the value to the
     * empty string.
     *
     * Use of this method assumes that the parameter has only one
     * value; if you wish to give a parameter multiple values, use
     * {@link #setParameterValues(String, String[])}.
     *
     * @param name The <code>String</code> name of the parameter
     * @param value The <code>String</code> value of the parameter
     * @see javax.servlet.ServletRequest#getParameter(String)
     * @pre name != null && !name.trim().equals("")
     */
    public final void setParameter(final String name, final String value) {
        if (Assert.isEnabled()) {
            validateName(name);
        }

        if (value == null) {
            m_params.put(name, new String[] {""});
        } else {
            m_params.put(name, new String[] {value});
        }
    }

    /**
     * A convenience method that calls {@link #setParameter(String,
     * String)} using <code>value.toString()</code>.  If
     * <code>value</code> is null, it is converted to the empty
     * string.
     *
     * @param name The <code>String</code> name of the parameter
     * @param value The <code>Object</code> value of the parameter
     * @pre name != null && !name.trim().equals("")
     */
    public final void setParameter(final String name, final Object value) {
        if (value == null) {
            setParameter(name, "");
        } else {
            setParameter(name, value.toString());
        }
    }

    public final String[] getParameterValues(final String name) {
        return (String[]) m_params.get(name);
    }

    public final void setParameterValues(final String name,
                                         final String[] values) {
        if (Assert.isEnabled()) {
            validateName(name);
            Assert.exists(values, "String[] values");
            Assert.isTrue(values.length > 0,
                              "The values array must have at least one value");
        }

        m_params.put(name, values);
    }

    public final void clearParameter(final String name) {
        if (Assert.isEnabled()) {
            validateName(name);
        }
        
        m_params.remove(name);
    }

    public final Map getParameterMap() {
        if (m_params.isEmpty()) {
            return null;
        } else {
            return Collections.unmodifiableMap(m_params);
        }
    }

    public final String toString() {
        if (m_params.isEmpty()) {
            return "";
        } else {
            return "?" + makeQueryString();
        }
    }

    public final String getQueryString() {
        return makeQueryString();
    }

    public final void runListeners(final HttpServletRequest sreq) {
        final Iterator iter = s_listeners.iterator();

        while (iter.hasNext()) {
            final ParameterListener listener = (ParameterListener) iter.next();

            listener.run(sreq, this);
        }
    }

    final String makeQueryString() {
        final StringBuffer buffer = new StringBuffer();
        final Iterator iter = m_params.entrySet().iterator();
        URLCodec codec = new URLCodec();

        while (iter.hasNext()) {
            final Map.Entry entry = (Map.Entry) iter.next();
            final String key = (String) entry.getKey();
            final String[] values = (String[]) entry.getValue();

            if (Assert.isEnabled()) {
                Assert.isTrue(key.indexOf('%') == -1,
                                  "The key '" + key + "' has already been " +
                                  "encoded");
            }

            if (values != null) {
                if (Assert.isEnabled()) {
                    Assert.isTrue(values.toString().indexOf('%') == -1,
                                      "One of the values " +
                                      Arrays.asList(values) + " has " +
                                      "already been encoded");
                }

                for (int i = 0; i < values.length; i++) {
                    try {
                        buffer.append(codec.encode(key));
                    } catch (EncoderException e) { 
                        throw new UncheckedWrapperException(e);
                    }
                    buffer.append('=');

                    final String value = values[i];

                    if (value != null) {
                        try {
                            buffer.append(codec.encode(value));
                        } catch (EncoderException e) { 
                            throw new UncheckedWrapperException(e);
                        }
                    }

                    buffer.append('&');
                }
            }
        }

        int last = buffer.length() - 1;

        if (last > -1 && buffer.charAt(last) == '&') {
            buffer.deleteCharAt(last);
        }

        return buffer.toString();
    }
}
