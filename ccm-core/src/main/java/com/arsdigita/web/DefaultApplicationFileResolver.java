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
package com.arsdigita.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

import org.apache.log4j.Logger;
import org.libreccm.web.Application;


/**
 * The default implementation deals with templates files belonging to a specific 
 * application, e.g. cms. Because of the modular structure of CCM all file 
 * resources of an application are stored below that application's module 
 * directory. The directory structure itself is application specific.
 */
public class DefaultApplicationFileResolver implements ApplicationFileResolver {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.web.DefaultApplicationFileResolver=DEBUG by 
     *  uncommenting or adding the line.                                      */
    private static final Logger s_log = Logger.getLogger
                                        (DefaultApplicationFileResolver.class);

    /** List of alternative greeting files. Typical vales are index.jsp and
     *  index.html                                                            */
    private static final String[] WELCOME_FILES = new String[] {
        "index.jsp", "index.html"
    };

    /**
     * Determines from the passsed in request URL a suitable template file in
     * the templates subdirectory. It returns an identified template wrapped
     * in a RequestDispatcher enabling it to be executed (forwarded). The 
     * request will typically something like 
     * <pre>/[appCtx]/[webappInstance]/[webappInstInternalDir]/[template.jsp]</pre>
     * For the content section "info" administration page installed in the
     * ROOT context (i.e. [appCtx] is empty) in would be
     * <pre>/info/admin/index.jsp</pre>
     * The actual template is actual stored in the file system at
     * <pre>/templates/ccm-cms/content-section/admin/index.jsp</pre> and the 
     * content-section to be administrated has to be passed in as parameter.
     * 
     * @param templatePath
     * @param sreq
     * @param sresp
     * @param app
     * @return
     */
    @Override
    public RequestDispatcher resolve(String templatePath,
                                     HttpServletRequest sreq,
                                     HttpServletResponse sresp,
                                     Application app) {

        String pathInfo = sreq.getPathInfo();  // effectively provides an url
        if (s_log.isDebugEnabled()) {          // with application part stripped
            s_log.debug("Resolving resource for " + pathInfo);
        }

        // determine the URL the application INSTANCE is really installed at
        // will replace the application part stripped above
        String node = app.getPrimaryUrl().toString(); 

        do {
            
            // First check the complete path for the instance. Parameter 
            // templatePath denotes the template directory for the application
            // TYPE.
            String path = templatePath + node + pathInfo;

            // Just in case of a directory the list of welcome files have to be
            // probed.
            if (path.endsWith("/")) {
                for (String welcomeFile : WELCOME_FILES) { //1.5 enhanced for-loop
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Trying welcome resource " + 
                                path + welcomeFile);
                    }
                    RequestDispatcher rd = Web.findResourceDispatcher(
                                           "" + path + welcomeFile);
                    if (rd != null) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Got dispatcher " + rd);
                        }
                        return rd;
                    }
                }
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Trying resource " + path);
                }
                
                RequestDispatcher rd = Web.findResourceDispatcher(
                                               "" + path);
                if (rd != null) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Got dispatcher " + rd);
                    }
                    return rd;
                }
            }
            
            // If nothing has been found at the complete path, probe variations
            // of the node part by clipping element-wise 
            if ("".equals(node)) {
                // if node is already empty we can't clip anything - fallthrough
                node = null;
            } else {
                // clipp the last part of node retaining the first / in case
                // of multiple parts or clip at all (in case of a single part)
                int index = node.lastIndexOf("/", node.length() - 2);
                node = node.substring(0, index);
            }
        } while (node != null);

        if (s_log.isDebugEnabled()) {
            s_log.debug("No dispatcher found");
        }
        // fallthrough, no success - returning null
        return null;
    }
    
}
