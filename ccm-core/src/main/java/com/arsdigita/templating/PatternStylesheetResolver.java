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

import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.LineNumberReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;



/**
 * <p>
 * This stylesheet resolver is used by the <code>*PresentationManager</code>
 * class to work out which XSLT stylesheet to apply to the current Bebop XML
 * output.
 * </p>
 *
 * <p>
 * This particular stylesheet resolver uses a flat file containing a list of
 * stylesheet patterns, one per line. The file is called
 * <code>WEB-INF/resources/stylesheet-paths.txt</code>. Such a file could look
 * like this:
 * </p>
 *
 * <pre>
 * # Comments and empty lines are ignored.
 *
 * /packages/aplaws/xsl/::vhost::/cms_::locale::.xsl
 * /packages/aplaws/xsl/::vhost::/cms.xsl
 * /packages/aplaws/xsl/default/cms_::locale::.xsl
 * /packages/aplaws/xsl/default/cms.xsl
 * /packages/content-section/xsl/cms_::locale::.xsl
 * /packages/content-section/xsl/cms.xsl
 * </pre>
 *
 * <p>
 * You may use the <code>com.arsdigita.templating.stylesheet_paths</code> system
 * property to change the file from which the stylesheet patterns are drawn.
 * </p>
 *
 * <p>
 * The patterns, such as <code>::vhost::</code>, are substituted for string
 * values:
 * </p>
 *
 * <table border="1">
 * <tr> <th> Pattern </th> <th> Meaning </th> <th> Examples </th> </tr>
 * <tr>
 * <td> <code>::host::</code> </td>
 * <td> Host name </td>
 * <td> <code>www.aplaws.org</code> </td>
 * </tr>
 * <tr>
 * <td> <code>::vhost::</code> </td>
 * <td> Virtual hostname. </td>
 * <td> <code>business.camden.gov.uk</code> </td>
 * </tr>
 * <tr>
 * <td> <code>::webapp::</code> </td>
 * <td> Current web application name (ie. context or document root) </td>
 * <td> <code>ccm</code> </td>
 * </tr>
 * <tr>
 * <td> <code>::application::</code> </td>
 * <td> Current CCM Application name </td>
 * <td> <code>navigation</code> </td>
 * </tr>
 * <tr>
 * <td> <code>::url::</code> </td>
 * <td> URL stub of the current applications name tree</td>
 * <td> <code>admin</code> </td>
 * </tr>
 * <tr>
 * <td> <code>::prefix::</code> </td>
 * <td> ??</td>
 * <td> <code>??</code> </td>
 * </tr>
 * <tr>
 * <td> <code>::outputtype::</code> </td>
 * <td> Output format. </td>
 * <td> <code>text_html</code> </td>
 * </tr>
 * <tr>
 * <td> <code>::locale::</code> </td>
 * <td> Current locale </td>
 * <td> <code>fr_FR</code> </td>
 * </tr>
 * <tr>clear
 *
 * <td> <code>::outputtype::</code> </td>
 * <td> Output format. </td>
 * <td> <code>text_html</code> </td>
 * </tr>
 * </table>
 *
 * <p>
 * Each substituted string is cleaned up using the following rules:
 * </p>
 *
 * <ul>
 * <li> Whitespace is trimmed.
 * <li> Converted to lowercase
 * <li> If the string is null, it is converted to "default".
 * <li> Any "/" characters are converted to "_" (underscore).
 * </ul>
 *
 * <p>
 * The resolver looks at each stylesheet in turn, and the first one which
 * actually exists on disk is returned.
 * </p>
 *
 * Developer may customize the process by writing a custom pattern generator and
 * add it in a custom integration package Initializer (e.g. ccm-ldn-aplaws) by
 * following code: // Register additional PatternStyleSheetResolver for Web app.
 * PatternStylesheetResolver.registerPatternGenerator( "[myKey]", new
 * [My]PatternGenerator() );
 *
 *
 * @author Richard W.M. Jones
 */
public class PatternStylesheetResolver implements StylesheetResolver {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int the runtime environment and
     * set com.arsdigita.templating.PatternStylesheetResolver=DEBUG by
     * uncommenting or adding the line.
     */
    private static final Logger s_log = LogManager.getLogger(
        PatternStylesheetResolver.class);

    /**
     * List of registered pattern generators which are queried in turn.
     */
    private static final HashMap s_generators = new HashMap();

    /**
     * Registers a new pattern generator for the given key.
     *
     * @param key the key as it appears in the pattern string
     * @param gen a pattern generator for producing values to be substituted for
     *            <code>key</code>
     */
    public static void registerPatternGenerator(String key,
                                                PatternGenerator gen) {
        s_generators.put(key, gen);
    }

    /* Statiic initializer block to initialize the standard pattern generators
     * at load time.                                                          */
    static {
        s_log.debug("Static initalizer starting...");
        registerPatternGenerator("locale", new LocalePatternGenerator());
        registerPatternGenerator("url", new URLPatternGenerator());
        registerPatternGenerator("application",
                                 new ApplicationPatternGenerator());
        registerPatternGenerator("outputtype", new OutputTypePatternGenerator());
        registerPatternGenerator("prefix", new PrefixPatternGenerator());
        registerPatternGenerator("webapp", new WebAppPatternGenerator());
        registerPatternGenerator("host", new HostPatternGenerator());
        s_log.debug("Static initalizer finished.");
    }

    /**
     * Complete path to the file specifying stylesheet patterns. Configurable by
     * configuration option in TemplatingConfig
     */
    private String m_path = null;
    /**
     * A List of Lists each of its lists containing one pattern to resolve a
     * probably appropriate stylesheet to apply. (i.e. one row of the file
     * m_path above)
     */
    private List m_paths = null;

    /**
     *
     * @param request
     *
     * @return
     */
    @Override
    public URL resolve(HttpServletRequest request) {
        synchronized (this) {
            if (m_paths == null) {
                loadPaths(Templating.getConfig().getStylesheetPaths());
            }
        }
        s_log.debug("m_paths is " + m_paths);

        HashMap values = new HashMap();
        ArrayList paths = new ArrayList();
        Iterator it = m_paths.iterator();
        while (it.hasNext()) {
            List pathList = (List) it.next();
            String[] bits = (String[]) pathList.toArray(
                new String[pathList.size()]
            );
            expandPlaceholders(bits, paths, values, request);
        }

        Iterator files = paths.iterator();
        while (files.hasNext()) {
            String[] bits = (String[]) files.next();

            String resource = StringUtils.join(bits, "");
            // UGLY HACK
            // If a placeholder returns an empty string (as in the example of
            // the root webapp) the provided string contains a "//" as there is
            // a slash before as well as after the placeholder in the pattern
            // string. It's ugly so we'll replace it.
            resource = resource.replace("//", "/");
            // The hack destroys the http protocol as well so we need another hack
            resource = resource.replace("http:/", "http://");

            if (s_log.isInfoEnabled()) {
                s_log.info("Looking to see if resource " + resource + " exists");
            }

            URL origURL = null;
            try {
                origURL = new URL(resource);
            } catch (MalformedURLException ex) {
                throw new UncheckedWrapperException(
                    "malformed URL " + resource, ex);
            }

            if (s_log.isInfoEnabled()) {
                s_log.info("origURL is " + origURL);
            }

            final URL xfrmedURL = (origURL == null) ? null : Templating
                .transformURL(origURL);

            if (s_log.isInfoEnabled()) {
                s_log.info("Transformed resource is " + xfrmedURL);
            }

            try {
                InputStream is = null;
                if (xfrmedURL != null) {
                    is = xfrmedURL.openStream();
                }
                if (is != null) {
                    is.close();
                    // xfrmedURL may test for existence either as http request
                    // or as a file lookup. Anyway we return the original URL
                    // which used to be a http request.
                    // Note: we are returning the URL, not the resource!
                    return origURL;
                }
            } catch (FileNotFoundException ex) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("File not found " + resource, ex);
                }
                // fall through & try next pattern
            } catch (IOException ex) {
                throw new UncheckedWrapperException("cannot open stream "
                                                        + resource, ex);
            }
        }

        throw new RuntimeException("no path to XSL stylesheet found; "
                                       + "try modifying " + m_path);
    }

    /**
     *
     * @param inBits
     * @param paths
     * @param values
     * @param request
     */
    private void expandPlaceholders(String[] inBits,
                                    ArrayList paths,
                                    HashMap values,
                                    HttpServletRequest request) {
        LinkedList queue = new LinkedList();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Queue initial entry " + StringUtils.join(inBits, ""));
        }
        queue.add(inBits);
        while (!queue.isEmpty()) {
            String[] bits = (String[]) queue.removeFirst();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Process queue entry " + StringUtils.join(bits, ""));
            }
            boolean clean = true;
            for (int i = 0; i < bits.length && clean; i++) {
                if (bits[i].startsWith("::") && bits[i].endsWith("::")) {
                    clean = false;
                    String[] vals = getValues(bits[i]
                        .substring(2, bits[i].length() - 2),
                                              values,
                                              request);
                    if (vals != null) {
                        for (int k = 0; k < vals.length; k++) {
                            String[] newBits = new String[bits.length];
                            // In case the pattern for an element is an empty
                            // string (e.g. for the ROOT webapp) the slash before
                            // as well as after the placeholder are added 
                            // resulting in a "//" which does no harm but is
                            // ugly.
                            for (int j = 0; j < bits.length; j++) {
                                if (j == i) {
                                    newBits[j] = vals[k];
                                } else {
                                    newBits[j] = bits[j];
                                }
                            }
                            if (s_log.isDebugEnabled()) {
                                s_log.debug("Requeue " + StringUtils.join(
                                    newBits, ""));
                            }
                            queue.add(newBits);
                        }
                    }
                }
            }

            if (clean) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Finished expanding placeholders in "
                                    + StringUtils.join(bits, ""));
                }
                paths.add(bits);
            }
        }
    }

    /**
     *
     * @param key
     * @param values
     * @param request
     *
     * @return
     */
    private String[] getValues(String key,
                               HashMap values,
                               HttpServletRequest request) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Lookup placeholder keys for " + key);
        }
        String[] vals = (String[]) values.get(key);
        if (vals == null) {
            PatternGenerator gen = (PatternGenerator) s_generators.get(key);
            if (gen == null) {
                return new String[]{};
            }
            vals = gen.generateValues(key, request);
            values.put(key, vals);
        }
        return vals;
    }

    /**
     *
     * @param path
     */
    private void loadPaths(String path) {
        if (s_log.isInfoEnabled()) {
            s_log.info("Loading paths from " + path);
        }

        m_path = path;
        try {
            // Read the source file.
            ClassLoader cload = Thread.currentThread().getContextClassLoader();
            InputStream stream = cload.getResourceAsStream(path.substring(0));
            s_log.debug("got stream using path " + path.substring(0));
            s_log.debug("stream.available is " + stream.available());
            m_paths = new ArrayList();

            LineNumberReader file = new LineNumberReader(new InputStreamReader(
                stream));
            String line;
            int lineNum;
            while ((line = file.readLine()) != null) {
                lineNum = file.getLineNumber();
                // Ignore blank lines and comments.
                line = line.trim();
                s_log.debug("line is " + line);
                if ("".equals(line)
                        || line.startsWith("#")
                        || line.startsWith("!")
                        || line.startsWith("//")) {
                    continue;
                }

                // Split up the line.
                List list = StringUtils.splitUp(line, "::\\w+::");
                // Save the split line.
                m_paths.add(list);
            }
        } catch (IOException ex) {
            throw new UncheckedWrapperException(
                "cannot read XSLT paths from " + path, ex);

        } catch (Exception e) {
            s_log.debug("loadPaths threw exception " + e);
        }
    }

}
