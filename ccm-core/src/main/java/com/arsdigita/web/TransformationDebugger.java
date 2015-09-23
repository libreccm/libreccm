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

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 *
 * A debugger that displays the original XML source of a document prior to
 * transformation (only applies if using Bebop JSP), the generated XML document
 * before transformation, and the XSL stylesheet files used for transformation.
 *
 * To view a page using this debugger, pass "debug=transform" in as a query
 * variable.
 *
 * @see com.arsdigita.bebop.jsp.ShowPage
 *
 * @author Justin Ross
 * &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: TransformationDebugger.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class TransformationDebugger extends Debugger {

    private static final Logger s_log = Logger.getLogger(
        TransformationDebugger.class);

    // private Document m_original;
    // private Document m_source;
    private URL m_sheet;
    private List m_dependents;

    /**
     * The value passed in to the "debug" query string that activates this
     * particular debugger.
     */
    public static final String TRANSFORM_DEBUG_VALUE = "transform";

    // Debuggers are per-request objects.
    /**
     * @pre sheet != null
     * @pre dependents != null
     *
     */
    public TransformationDebugger(Document original,
                                  Document source,
                                  URL sheet,
                                  List dependents) {
        Assert.exists(sheet, URL.class);
        Assert.exists(sheet, List.class);
        // m_original = original;
        // m_source = source;
        m_sheet = sheet;
        m_dependents = dependents;
    }

    /**
     * @see #TransformationDebugger(Document, Document, URL, List)
     *
     */
    public TransformationDebugger(URL sheet, List dependents) {
        this(null, null, sheet, dependents);
    }

    public boolean isRequested(HttpServletRequest sreq) {
        String value = sreq.getParameter(DEBUG_PARAMETER);

        return value != null && value.indexOf(TRANSFORM_DEBUG_VALUE) != -1;
    }

    public String debug() {
        StringBuffer buffer = new StringBuffer(1024);

        buffer.append("<h2>The Stylesheet files</h2>");
        buffer.append("<ul>");

        try {
            Iterator sources = m_dependents.iterator();

            File root = new File(DispatcherHelper.getRequestContext()
                .getServletContext().getRealPath("/"));
            String base = root.toURL().toExternalForm();

            while (sources.hasNext()) {
                String path = sources.next().toString();

                if (path.startsWith(base)) {
                    path = path.substring(base.length());
                }

                buffer.append("<li><code><a href=\"" + path + "\">" + path
                              + "</a></code></li>");
            }
        } catch (IOException ioe) {
            throw new Error(ioe);
        }

        buffer.append("</ul>");
        return buffer.toString();
    }

    protected String getStylesheetContents() {
        try {
            URLConnection con = m_sheet.openConnection();

            StringBuffer buffer = new StringBuffer();

            String contentType = con.getContentType();

            String encoding = "ISO-8859-1";
            int offset = (contentType == null ? -1 : contentType.indexOf(
                          "charset="));
            if (offset != -1) {
                encoding = contentType.substring(offset + 8).trim();
            }
            if (s_log.isDebugEnabled()) {
                s_log.debug("Received content type " + contentType);
            }
            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, encoding);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Process with character encoding " + isr
                    .getEncoding());
            }
            BufferedReader input = new BufferedReader(isr);

            String line;
            while ((line = input.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            input.close();
            return buffer.toString();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return "Stylesheet contents unavailable: " + ex.getMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "Stylesheet contents unavailable: " + ex.getMessage();
        }
    }

}
