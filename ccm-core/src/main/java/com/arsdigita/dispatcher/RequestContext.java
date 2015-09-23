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
package com.arsdigita.dispatcher;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;

/**
 * Interface used when dispatchers are
 * chained or piped together. Part of the requested URL will be used
 * at each stage of the dispatch, and this interface is used by the
 * dispatcher to tell what part of the URL has already been used to
 * dispatch the request so far. The remainder is what the current
 * dispatcher must work with.  Because form/URL variables are not
 * order-dependent, We only keep track of the path portion
 * of the URL.
 *
 * @author Bill Schneider 
 * @version $Id$
 * @since 4.5 */

public interface RequestContext {


    /**
     * Gets the portion of the URL that has not been used by
     * previous dispatchers in the chain.
     * @return the portion of the URL that must be used by
     * the current dispatcher.
     */
    public String getRemainingURLPart();

    /**
     * Gets the portion of the URL that has already been used by
     * previous dispatchers in the chain.
     * @return the portion of the URL that has already been used.
     */
    public String getProcessedURLPart();

    /**
     * Gets the original URL requested by the end user's browser.
     * This URL does <em>not</em> change when a request is forwarded
     * by the application; "/foo/bar" is still the original request
     * URI in the browser even if we've dispatched the request to
     * "/packages/foo/www/bar.jsp".
     *
     * @return the original URL requested by the end user's browser.
     * All generated HREF, IMG SRC, and FORM ACTION attributes, and
     * any redirects, will be relative to this URL.
     */
    public String getOriginalURL();

    /**
     * Gets the current servlet context.
     * @return the current servlet context, which must be set by implementation.
     */
    public ServletContext getServletContext();

    /**
     * more methods will be implemented as needed, for locale,
     * form variables, etc.
     */

    /**
     * Gets the locale for the current request context.
     * @return the locale for the current request context.
     */
    public Locale getLocale();

    /**
     * Returns a <code>java.util.ResourceBundle</code> for the
     * current request, based on the requested application and the
     * user's locale preference.
     *
     * @return the current <code>java.util.ResourceBundle</code> to use
     * in this request.
     */
    public ResourceBundle getResourceBundle();

    /**
     * Gets the requested output type.
     * @return the requested output type (normally "text/html" by default
     * for a web browser request).
     */
    public String getOutputType();

    /**
     * Gets the debugging flag.
     * @return the debugging flag.
     * Currently, debugging applies to XSL transformation.
     */
    public boolean getDebugging();

    /**
     * Gets the show-XML-only flag.
     * @return if true, indicates that the active
     * <code>PresentationManager</code> should output raw, untransformed
     * XML instead of processing it with XSLT.
     */
    public boolean getDebuggingXML();

    /**
     * Gets the show-XSL-only flag.
     * @return if true, indicates that the active
     * <code>PresentationManager</code> should output the XSLT stylesheet
     * in effect for this request.
     */
    public boolean getDebuggingXSL();


    /**
     * Gets the base path, relative to the webapp root, where JSP-based
     * resources (and static pages) will be found.
     * @return the base path, relative to the webapp root, where
     * JSP-based resources will be found.
     * Returns with a trailing slash (for example,
     * /packages/package-key/www/).
     */
    public String getPageBase();
}
