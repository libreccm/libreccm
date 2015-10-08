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

import com.arsdigita.web.Web;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.libreccm.web.CcmApplication;

/**
 * Generates a set of patterns corresponding to the current web application
 * prefix.
 */
public class WebAppPatternGenerator implements PatternGenerator {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int hte runtime environment and
     * set com.arsdigita.templating.WebAppPatternGenerator=DEBUG by uncommenting
     * or adding the line.
     */
    private static final Logger s_log = Logger.getLogger(
        WebAppPatternGenerator.class);

    /**
     *
     * @param key placeholder from the pattern string, without surrounding
     *            colons, constantly "webapp" here.
     * @param req current HttpServletRequest
     *
     * @return List of webapps contextPath names in an Array of Strings.
     */
    @Override
    public String[] generateValues(String key,
                                   HttpServletRequest req) {

        CcmApplication app = Web.getWebContext().getApplication();
        String ctx = (app == null) ? null : "";

        if (app == null || ctx == null || "".equals(ctx)) {
            ctx = Web.getWebappContextPath();
        }

        // JavaEE requires a leading "/" for web context part, but the pattern
        // string already contains a "/", so we have to remove it here to
        // too avoid a "//"
        if (ctx.startsWith("/")) {
            ctx = ctx.substring(1);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating Values key: " + key + " ["
                        + "Web.getWebContext(): " + Web.getWebContext() + " ,"
                        + "Application: " + Web.getWebContext().getApplication()
                        + "," + "ContextPath: >" + ctx + "<]");
        }

        /* "Older version: prior 6.6. Some modules used to be installed into
         * its own web application context, but needed access to the main
         * applications package files (e.g. bebop). Therefore the webapp context
         * (ServletContext in API speech) of the main CCM application had to be
         * added (which was ROOT by default)
         * 
         * As of version 6.6 all packages are installed in one web application
         * context, therefore no additional entry is required.
         * This variation had first be introduced with the APLAWS integration
         * package, which used to register an additional WebAppPatternGenerator,
         * which simply cuts ","+ Web.ROOT_WEBAPP, under a different key 
         * "Webapp" (singular)                                                */
        // return new String[] { ctx + "," + Web.getRootWebappContextPath() };
        return new String[]{ctx};
    }

}
