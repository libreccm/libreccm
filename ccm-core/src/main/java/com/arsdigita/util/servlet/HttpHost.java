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

import com.arsdigita.util.Assert;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Represents a host computer.  The host may in fact be a "virtual"
 * host, one of several on the same physical machine.
 *
 * @author Dan Berrange
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class HttpHost {

    private static final Logger s_log = Logger.getLogger(HttpHost.class);

    private final String m_name;
    private final int m_port;

    /**
     * Constructs a new host named <code>name</code> and on port
     * <code>port</code>.
     *
     * @param name A <code>String</code> host name, for example
     * <code>"ccm.redhat.com"</code>; see {@link
     * javax.servlet.ServletRequest#getServerName()}; it cannot be
     * null
     * @param port An <code>int</code> port number; <code>8080</code>,
     * for instance; see {@link
     * javax.servlet.ServletRequest#getServerPort()}; it must be
     * greater than 0
     */
    public HttpHost(final String name, final int port) {
        if (Assert.isEnabled()) {
            Assert.exists(name, String.class);
            Assert.isTrue(port > 0,
                         "The port must be greater than 0; " +
                         "I got " + port);
        }

        m_name = name;
        m_port = port;
    }

    /**
     * Constructs a host representing the host-specific part of
     * <code>sreq</code>.
     *
     * @param sreq An <code>HttpServletRequest</code> representation
     * of a request; it cannot be null
     */
    public HttpHost(final HttpServletRequest sreq) {
        final String header = sreq.getHeader("Host");

        if (header == null) {
            if (s_log.isInfoEnabled()) {
                s_log.info("No 'Host:' header present; falling back " +
                           "on values from servlet request");
            }

            m_name = sreq.getServerName(); // XXX use httpserver
            m_port = sreq.getServerPort();
        } else {
            final int colon = header.indexOf( ':' );

            if (colon == -1) {
                m_name = header;

                // Internet Explorer doesn't include the port number
                // in the Host: header, so if your server *appears* to
                // be on port 80, we take a look at the actual server
                // port to verify.
                //
                // NB. So for vHosting to work, you must make sure
                // your web server is using the same port as your
                // squid proxy
                final String agent = sreq.getHeader("User-Agent");

                if (agent != null
                        && agent.toLowerCase().indexOf("msie") >= 0) {
                    m_port = sreq.getServerPort(); // XXX use httpserver
                } else {
                    m_port = 80;
                }
            } else {
                m_name = header.substring(0, colon);
                m_port = Integer.parseInt
                    (header.substring(colon + 1, header.length()));
            }
        }
    }

    /**
     * Gets the host name.
     *
     * @return A <code>String</code> naming the host; it cannot be
     * null
     */
    public final String getName() {
        return m_name;
    }

    /**
     * Gets the port of this host.
     *
     * @return A <code>int</code> port number
     */
    public final int getPort() {
        return m_port;
    }

    final void toString(final StringBuffer buffer) {
        buffer.append(getName());

        final int port = getPort();

        if (port != 80) {
            buffer.append(":");
            buffer.append(port);
        }
    }

    /**
     * Returns a <code>String</code> representation of this host.
     *
     * @return <code>getName() + ":" + getPort()</code> or simply
     * <code>getName()</code> if the port is 80
     */
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(24);

        toString(buffer);

        return buffer.toString();
    }
}
