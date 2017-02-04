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
package com.arsdigita.util.servlet;

import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.StringParameter;

/**
 * This class represents info about a single host running a server in a webapp
 * cluster.
 */
public class HttpHostParameter extends StringParameter {

    public HttpHostParameter(final String name) {
        super(name);
    }

    public HttpHostParameter(final String name,
                             final int multiplicity,
                             final Object defaalt) {
        super(name, multiplicity, defaalt);
    }

    protected Object unmarshal(final String value, final ErrorList errors) {
        if (value.indexOf("://") != -1) {
            final ParameterError error = new ParameterError(this,
                                                            "The value must not have a scheme prefix");
            errors.add(error);
        }

        if (value.indexOf("/") != -1) {
            final ParameterError error = new ParameterError(this,
                                                            "The value must not contain slashes");
            errors.add(error);
        }

        final int sep = value.indexOf(":");

        if (sep == -1) {
            final ParameterError error = new ParameterError(this,
                                                            "The value must contain a colon");
            errors.add(error);
        }

        if (!errors.isEmpty()) {
            return null;
        }

        try {
            final String name = value.substring(0, sep);
            final String port = value.substring(sep + 1);

            return new HttpHost(name, Integer.parseInt(port));
        } catch (IndexOutOfBoundsException ioobe) {
            final ParameterError error = new ParameterError(this,
                                                            "The host spec is invalid; it must take the form "
                                                            + "hostname:hostport");
            errors.add(error);

            return null;
        } catch (NumberFormatException nfe) {
            final ParameterError error = new ParameterError(this,
                                                            "The port number must be an integer with no "
                                                            + "extraneous spaces or punctuation");
            errors.add(error);

            return null;
        }
    }

    protected String marshal(Object value) {
        if (value == null) {
            return null;
        } else {
            final HttpHost host = (HttpHost) value;
            return host.getName() + ":" + host.getPort();
        }
    }

}
