/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.dispatcher.ChainedDispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;

import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

/**
 * Dispatches to a file stored under the CMS package root
 * (<code>/packages/cms/www</code>). This includes both unmanaged files copied
 * or created directly in the file system, as well as pages and assets published
 * to the file system from CMS.
 *
 * @author Karl Goldstein (karlg@arsdigita.com)
 * @version $Revision$ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id$
 *
 */
public class FileDispatcher implements ChainedDispatcher {

    private static final Logger LOGGER = LogManager.getLogger(
        ChainedDispatcher.class);

    @Override
    public int chainedDispatch(HttpServletRequest request,
                               HttpServletResponse response,
                               RequestContext context)
        throws IOException, ServletException {

        File jspFile = getPackageFile(context);

        if (jspFile.exists() && !jspFile.isDirectory()) {
            String packageURL = context.getPageBase() + context
                .getRemainingURLPart();
            LOGGER.debug("DISPATCHING to " + packageURL);

            // don't match folders, since they don't actually match a file
            if (!packageURL.endsWith("/")) {
                LOGGER.debug("DISPATCHING to " + packageURL);
                // Don't set caching headers - let JSP file do it if required
                //DispatcherHelper.maybeCacheDisable(response);
                DispatcherHelper.setRequestContext(request, context);
                DispatcherHelper.forwardRequestByPath(packageURL, request,
                                                      response);
                return ChainedDispatcher.DISPATCH_BREAK;
            }
        }

        return ChainedDispatcher.DISPATCH_CONTINUE;
    }

    /**
     * Matches the request URL to a file in the package www directory.
     *
     */
    private File getPackageFile(RequestContext appContext) {

        ServletContext servletContext = appContext.getServletContext();

        String filePath = appContext.getRemainingURLPart();

        String packageDocRoot = servletContext.getRealPath(appContext
            .getPageBase());

        File jspFile = new File(packageDocRoot, filePath);

        return jspFile;
    }

}
