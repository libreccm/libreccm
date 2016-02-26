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

import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.Logger;

/**
 * An implementation of the URIResolver interface that keeps track of all the
 * URLs that have been loaded. 
 * If you set this as the URI resolver for a Transformer then this will track 
 * all the <code>xsl:import</code> and <code>xsl:include</code> statements.
 *
 * @version $Id$
 */
final class SimpleURIResolver implements URIResolver {

    private static final Logger s_log = LogManager.getLogger
                                               (SimpleURIResolver.class);

    private final Set m_uniqueStylesheetURIs;
    private final List m_stylesheetURIs;

    /**
     * Constructor, just initializes internal properties.
     */
    public SimpleURIResolver() {
        m_uniqueStylesheetURIs = new HashSet();
        m_stylesheetURIs = new ArrayList();
    }

    /**
     * Returns all the stylesheet URIs encountered so far.
     *
     * @return a Set whose elements are isntances of java.net.URL
     */
    public List getStylesheetURIs() {
        return m_stylesheetURIs;
    }

    /**
     * Resolves a URL and returns a stream source.
     *
     * @param href the url to resolve
     * @param base the base url to resolve relative to
     */
    @Override
    public Source resolve(final String href, final String base)
                  throws TransformerException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Resolve " + href + " (found in " + base + ")");
        }

        URL baseURL = null;

        if (base != null) {
            try {
                baseURL = new URL(base);
            } catch (MalformedURLException ex) {
                throw new TransformerException("cannot parse href " + base, ex);
            }
        }

        URL thisURL = null;

        try {
            if (baseURL == null) {
                thisURL = new URL(href);
            } else {
                thisURL = new URL(baseURL, href);
            }

            if (!m_uniqueStylesheetURIs.contains(thisURL)) {
                m_uniqueStylesheetURIs.add(thisURL);
                m_stylesheetURIs.add(thisURL);
            }
        } catch (MalformedURLException ex) {
            throw new TransformerException("cannot parse href " + href, ex);
        }

        try {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Got url " + thisURL);
            }

            // Optimize calls to resource servlet into file:///
            // where possible
            URL xfrmedURL = Templating.transformURL(thisURL);

            if ( xfrmedURL == null ) {
                throw new TransformerException
                    ("URL does not exist: " + thisURL);
            }

            if (s_log.isInfoEnabled()) {
                s_log.info("Loading URL " + xfrmedURL);
            }

            InputStream is = xfrmedURL.openStream();

            // NB, don't pass through 'xfrmedURL' since imports
            // are relative to 'thisURL'
            return new StreamSource(is, thisURL.toString());
        } catch (IOException ex) {
            throw new TransformerException(
                                 String.format("cannot read stream for %s", 
                                               thisURL.toString()), 
                                 ex);
        }
    }
}
