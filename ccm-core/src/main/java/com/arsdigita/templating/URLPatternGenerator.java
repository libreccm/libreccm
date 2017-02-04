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

import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;

import com.arsdigita.web.Web;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.libreccm.web.CcmApplication;

/**
 * Generates a set of pattern values based on the URL path info for the current
 * request. Slashes in the request are translated into hyphens; the file
 * extension is stripped; any 'index' is removed, except for the top level.
 *
 * So some examples:
 *
 * /content/admin/item.jsp -> { "admin-item", "admin", "index" }
 * /content/admin/index.jsp -> { "admin", "index" } /content/admin/ -> {
 * "admin", "index" } /content/index.jsp -> { "index" } /content/ -> { "index" }
 */
public class URLPatternGenerator implements PatternGenerator {

    private static final Logger LOGGER = LogManager.getLogger(
        URLPatternGenerator.class);

    private static final String DEFAULT_URL_MATCH = "index";

    /**
     *
     * @param key
     * @param req
     *
     * @return
     */
    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        String path = getPath();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Substituting values for url " + path);
        }

        // Check for a file extension & strip it.
        int dotIndex = path.lastIndexOf(".");
        int slashIndex = path.lastIndexOf("/");
        if (dotIndex > -1
                && dotIndex > slashIndex) {
            path = path.substring(0, dotIndex);
        }

        // Strip '/index' if any
        if (path != null && path.endsWith("/" + DEFAULT_URL_MATCH)) {
            path = path.substring(0, path.length() - DEFAULT_URL_MATCH.length());
        }

        // Now strip trailing & leading slash
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path != null && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        if (path == null) {
            path = "";
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Normalized path is '" + path + "'");
        }
        String[] bits = StringUtils.split(path, '/');
        if (LOGGER.isDebugEnabled()) {
            for (int i = 0; i < bits.length; i++) {
                LOGGER.debug(" -> '" + bits[i] + "'");
            }
        }

        // Now we've cut off the file extension, it's time to do the
        // funky concatenation trick. 
        for (int i = 1; i < bits.length; i++) {
            bits[i] = bits[i - 1] + "-" + bits[i];
        }

        // Now we have to reverse it, so matching goes from most specific
        // to most general & add in the default 'index' match
        String[] reverseBits = new String[bits.length + 1];

        for (int i = bits.length - 1, j = 0; i > -1; i--, j++) {
            reverseBits[j] = bits[i];
        }
        reverseBits[reverseBits.length - 1] = DEFAULT_URL_MATCH;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("After concatenation & reversing");
            for (int i = 0; i < reverseBits.length; i++) {
                LOGGER.debug(" -> '" + reverseBits[i] + "'");
            }
        }

        return reverseBits;
    }

    private String getPath() {
        String base = getBasePath();
        String url = Web.getWebContext().getRequestURL().getPathInfo();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Base is " + base + " url is " + url);
        }

        Assert.isTrue(url.startsWith(base), "URL " + url + " starts with "
                                            + base);

        return url.substring(base.length() - 1);
    }

    /**
     * Provides the base URL of the application in the current Web request (i.e.
     * application's PrimaryURL). If no application can be found or no
     * PrimaryURL can be determined ROOT ("/") is returned.      *
     * XXX fix me, why can't we get this from Web.getWebContext().getRequestURL
     *
     * @return primary url of an application or ROOT
     */
    private String getBasePath() {

        // retrieve the application of the request
        CcmApplication app = Web.getWebContext().getApplication();
        if (app == null) {
            return "/";
        } else {
            return app.getPrimaryUrl().toString();
        }

    }

}
