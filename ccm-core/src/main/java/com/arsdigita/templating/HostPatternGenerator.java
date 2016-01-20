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
package com.arsdigita.templating;

import com.arsdigita.util.servlet.HttpHost;
import com.arsdigita.web.Web;
import com.arsdigita.web.WebConfig;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Generates a set of patterns corresponding to the current host name. (actually
 * just retrieves the current hostname from configuration file, StringArray
 * returned is for sake of methods consistency)
 */
public class HostPatternGenerator implements PatternGenerator {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int the runtime environment and
     * set com.arsdigita.templating.HostPatternGenerator=DEBUG by uncommenting
     * or adding the line.
     */
    private static final Logger s_log = Logger.getLogger(
        HostPatternGenerator.class);

    /**
     * Looks up the hostname from configuration and returns it as String. The
     * Return type is (unneccessarily) String[] due to the current API, but
     * currently never returns more than just one value (one hostname:port).
     *
     * @param key placeholder from the pattern string, without surrounding
     *            colons, constantly "host" here.
     * @param req current HttpServletRequest
     *
     * @return Hostname (including port if any), retrieved from CCM
     *         configuration
     */
    @Override
    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        final HttpHost host = new HttpHost(WebConfig.getConfig().getHostName(),
                                     WebConfig.getConfig().getHostPort());
        final String hostName = host.toString();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating Values for key: " + key + " ["
                        + "Hostname retrieved: >>" + hostName + "<<]");
        }

        return new String[]{host.toString()};
    }

}
