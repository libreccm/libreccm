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
package com.arsdigita.bebop.page;

import com.arsdigita.bebop.BebopConfig;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.templating.XSLParameterGenerator;
import com.arsdigita.templating.XSLTemplate;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.CCMDispatcherServlet;
import com.arsdigita.web.Debugger;
import com.arsdigita.web.TransformationDebugger;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;

/**
 * A class for managing and obtaining a Stylesheet based on the current
 * request's location in the site map. First, we try to find a stylesheet
 * specific to this site node. If we can't find one, then we walk up the site
 * map until we find a parent of this site node that has a stylesheet associated
 * with it.
 *
 * If we haven't found one by the time we reach the root, then we'll do the same
 * tree walk except we'll look for the stylesheet associated with the
 * <em>package</em> mounted on each site node.
 *
 * @author Bill Schneider
 * @version $Id: PageTransformer.java 2071 2010-01-28 18:24:06Z pboy $
 */
public class PageTransformer implements PresentationManager {

    private static final Logger s_log = Logger.getLogger(PageTransformer.class);
    // this keeps track of all of the XSLParameters that can be added to
    // stylesheets
    private static final HashMap s_XSLParameters = new HashMap();

    // load the default xsl parameter generators
    static {
        s_log.debug("Static initalizer starting...");

        registerXSLParameterGenerator("contextPath",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return request
                                              .getContextPath();
                                      }

                                  });

        registerXSLParameterGenerator("root-context-prefix",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return CCMDispatcherServlet
                                              .getContextPath();
                                      }

                                  });

        registerXSLParameterGenerator("context-prefix",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return Web.getWebContext()
                                              .getRequestURL()
                                              .getContextPath();
                                      }

                                  });

        registerXSLParameterGenerator("internal-theme",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return Web.getWebContext()
                                              .getRequestURL()
                                              .getContextPath()
                                                     + com.arsdigita.web.URL.INTERNAL_THEME_DIR;
                                      }

                                  });

        registerXSLParameterGenerator("dispatcher-prefix",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return com.arsdigita.web.URL
                                              .getDispatcherPath();
                                      }

                                  });

        registerXSLParameterGenerator("dcp-on-buttons",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          if (BebopConfig.getConfig()
                                              .getDcpOnButtons()) {
                                              return "true";
                                          } else {
                                              return null;
                                          }

                                      }

                                  });

        registerXSLParameterGenerator("dcp-on-links",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          if (BebopConfig.getConfig()
                                              .getDcpOnLinks()) {
                                              return "true";
                                          } else {
                                              return null;
                                          }

                                      }

                                  });

        registerXSLParameterGenerator("user-agent",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return request.getHeader(
                                              "User-Agent");
                                      }

                                  });

        registerXSLParameterGenerator("negotiated-language",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return GlobalizationHelper
                                              .getNegotiatedLocale()
                                              .getLanguage();
                                      }

                                  });

        registerXSLParameterGenerator("selected-language",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          Locale selectedLocale
                                                     = com.arsdigita.globalization.GlobalizationHelper
                                              .getSelectedLocale(request);
                                          return (selectedLocale != null)
                                                     ? selectedLocale
                                              .toString() : "";
                                      }

                                  });

        registerXSLParameterGenerator("request-scheme",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return request.getScheme();
                                      }

                                  });

        registerXSLParameterGenerator("server-name",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return request.getServerName();
                                      }

                                  });

        registerXSLParameterGenerator("server-port",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          return Integer.toString(
                                              request.getServerPort());
                                      }

                                  });

        registerXSLParameterGenerator("host",
                                      new XSLParameterGenerator() {

                                      @Override
                                      public String generateValue(
                                          HttpServletRequest request) {
                                          if (request.getServerPort()
                                                  == 80) {
                                              return String.format(
                                                  "%s://%s", request
                                                  .getScheme(), request
                                                  .getServerName());
                                          } else {
                                              return String.format(
                                                  "%s://%s:%d",
                                                  request.getScheme(),
                                                  request
                                                  .getServerName(),
                                                  request
                                                  .getServerPort());
                                          }
                                      }

                                  });

        s_log.debug("Static initalizer finished.");
    }
    // XXX These need to move somewhere else.

    /**
     * This is used to indicate that all xsl templates used should be pulled
     * from the disk and not from the cache. If this is in the request with a
     * value of Boolean.TRUE then all XSL Stylesheets are pulled from the disk,
     * not the cache
     */
    public static final String CACHE_XSL_NONE = "cacheXSLNone";
    /**
     * This is used to indicate that the "fancy errors" should be used and that
     * the errors should be placed in the request for later use. To use this,
     * have the code place an attribute with this name in the request with the
     * value of Boolean.TRUE
     */
    public static final String FANCY_ERRORS = "fancyErrors";
    /**
     * State flag for preventing caching in every case.
     */
    public static final String CACHE_NONE = "none";
    /**
     * State flag for per-user caching.
     */
    public static final String CACHE_USER = "user";
    /**
     * State flag for enabling caching in every case.
     */
    public static final String CACHE_WORLD = "world";
    /**
     * State flag for disabling HTTP header caching.
     */
    public static final String CACHE_DISABLE = "disable";
    private static String s_defaultCachePolicy = CACHE_DISABLE;

    /**
     * Sets the default cache behavior for the site.
     *
     * @param policy a <code>String</code> policy, one of {@link
     * #CACHE_NONE}, {@link #CACHE_DISABLE}, {@link #CACHE_USER},
     * {@link #CACHE_WORLD}
     */
    public static void setDefaultCachePolicy(final String policy) {
        s_defaultCachePolicy = policy;
    }

    /**
     * Sets the content type of the response and then gets the PrintWriter
     */
    private PrintWriter getWriter(final HttpServletResponse resp,
                                  final String contentType,
                                  final String charset) {
        Assert.exists(contentType);
        Assert.exists(charset);

        resp.setContentType(contentType + "; " + "charset=" + charset);

        try {
            return resp.getWriter();
        } catch (IllegalStateException e) {
            s_log.warn("Using getOutputStream instead of getWriter");

            try {
                return new PrintWriter(new OutputStreamWriter(resp.
                    getOutputStream(),
                                                              charset));
            } catch (IOException ex) {
                throw new UncheckedWrapperException(ex);
            }
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    /**
     * Uses {@link #servePage(Document, HttpServletRequest,
     * HttpServletResponse, Map)} to implement the
     * <code>PresentationManager</code> interface.
     */
    @Override
    public void servePage(final Document doc,
                          final HttpServletRequest req,
                          final HttpServletResponse resp) {
        servePage(doc, req, resp, null);
    }

    /**
     * Serves an XML Document, getting and applying the appropriate XSLT. Also
     * allows for parameters to be set for the transformer. These will become
     * top-level xsl:params in the stylesheet. The "contextPath" parameter will
     * always be passed to XSLT, which is the value of
     * <code>req.getWebContextPath()</code>.
     *
     * @param doc    the Bebop page to serve
     * @param req    the servlet request
     * @param resp   the servlet response
     * @param params a set of name-value pairs to pass as parameters to the
     *               Transformer
     */
    public void servePage(final Document doc,
                          final HttpServletRequest req,
                          final HttpServletResponse resp,
                          final Map params) {
        if (resp.isCommitted()) {
            return;
        }
        if (Assert.isEnabled()) {
            Assert.exists(doc, Document.class);
            Assert.exists(req, HttpServletRequest.class);
            Assert.exists(resp, HttpServletResponse.class);
        }

        try {
            final String charset = Globalization
                .getDefaultCharset(DispatcherHelper.getNegotiatedLocale());

            final String output = req.getParameter("output");
            s_log.info("output=" + output);

            if (output == null) {

                boolean fancyErrors
                            = BebopConfig.getConfig().getFancyErrors()
                                  || Boolean.TRUE.equals(req.getAttribute(
                        FANCY_ERRORS));

                // Get the stylesheet transformer object corresponding to the
                // current request.
                final XSLTemplate template = Templating.getTemplate(
                    req,
                    fancyErrors,
                    !Boolean.TRUE.equals(req.getAttribute(CACHE_XSL_NONE)));

                final PrintWriter writer = getWriter(resp, "text/html", charset);

                final Transformer xf = template.newTransformer();
                endTransaction(req);

                // Transformers are not thread-safe, so we assume we have
                // exclusive use of xf here. But we could recycle it.
                xf.clearParameters();

                if (params != null) {
                    final Iterator entries = params.entrySet().iterator();

                    while (entries.hasNext()) {
                        final Map.Entry entry = (Map.Entry) entries.next();

                        xf.setParameter((String) entry.getKey(),
                                        entry.getValue());
                    }
                }

                addXSLParameters(xf, req);

                // This has no effect on the resulting encoding of the
                // output generated by the XSLT transformer. Why?
                // Because we pass the transformer an instance of the
                // Writer class. The Writer class provides no methods
                // for changing the encoding. So, the only thing this
                // does is, it causes the transformer to include a
                // line like <meta http-equiv="Content-Type"
                // content="text/html; charset=foo"> in the output
                // document.
                xf.setOutputProperty("encoding", charset);

                try {
                    xf.transform(new DOMSource(doc.getInternalDocument()),
                                 new StreamResult(writer));
                } catch (TransformerException ex) {
                    throw new UncheckedWrapperException(
                        "cannot transform document", ex);
                }

                // copy and paste from BasePresentationManager
                if (KernelConfig.getConfig().isDebugEnabled()) {
                    Document origDoc = (Document) req.getAttribute(
                        "com.arsdigita.xml.Document");
                    Debugger.addDebugger(new TransformationDebugger(template.
                        getSource(), template.getDependents()));
                    writer.print(Debugger.getDebugging(req));
                }

            } else if (output.equals("xml")) {
                endTransaction(req);

                final PrintWriter writer = getWriter(resp, "text/xml", charset);

                DispatcherHelper.forceCacheDisable(resp);

                writer.println(doc.toString(true));
            } else if (output.equals("xsl")) {
                XSLTemplate template = null;
                try {
                    // Get the stylesheet transformer object corresponding to
                    // the
                    // current request.
                    template = Templating.getTemplate(req,
                                                      Boolean.TRUE.equals(req
                                                          .getAttribute(
                                                              PageTransformer.FANCY_ERRORS)),
                                                      !Boolean.TRUE.equals(req
                                                          .getAttribute(
                                                              PageTransformer.CACHE_XSL_NONE)));
                    endTransaction(req);
                } finally {
                }

                try {
                    Date now = new Date();
                    SimpleDateFormat fmt = new SimpleDateFormat(
                        "yyyy-MM-dd-HH-mm");
                    String prefix = "waf-xsl-" + fmt.format(now);

                    final OutputStream os = resp.getOutputStream();
                    resp.reset();
                    resp.setContentType("application/zip");
                    resp.setHeader("Content-Disposition",
                                   "attachment; filename=\"" + prefix + ".zip\"");
                    DispatcherHelper.forceCacheDisable(resp);

                    template.toZIP(os, prefix);

                    resp.flushBuffer();
                } catch (IOException ex) {
                    throw new UncheckedWrapperException(ex);
                } finally {
                }
            } else {
                throw new IllegalStateException(output
                                                    + " is an unknown output");
            }
        } finally {
        }
    }

    /**
     * Ends the current transaction. Is a performance optimization to end ASAP
     * before serving the page.
     *
     * @param req HTTP request.
     */
    private void endTransaction(final HttpServletRequest req) {
        // There is no longer any need for a database handle.
        if (req.getAttribute(PageContext.EXCEPTION) == null) {

        } else {

        }
    }

    /**
     * This adds a generator to the list of parameters available to
     * XSLStylesheets. If this is called a second time with the same parameter
     * name then all previous calls are overwritten and only the last registered
     * generator is used.
     *
     * @param parameterName
     * @param parameterGenerator
     */
    public static void registerXSLParameterGenerator(String parameterName,
                                                     XSLParameterGenerator parameterGenerator) {
        s_XSLParameters.put(parameterName, parameterGenerator);
    }

    /**
     * This removes the parameter from the list of parameters that will be added
     * to stylesheets
     *
     * @param parameterName
     */
    public static void removeXSLParameterGenerator(String parameterName) {
        s_XSLParameters.remove(parameterName);
    }

    /**
     * This is a Collection of all names of XSL Parameters that have been
     * registered
     *
     * @return
     */
    public static Collection getXSLParameterNames() {
        return s_XSLParameters.keySet();
    }

    /**
     * This takes a name and request and returns the value that should be used
     * in the XSL for the given name
     *
     * @param name
     * @param request
     *
     * @return
     */
    public static String getXSLParameterValue(String name,
                                              HttpServletRequest request) {
        XSLParameterGenerator generator
                                  = (XSLParameterGenerator) s_XSLParameters
            .get(name);
        if (generator != null) {
            return generator.generateValue(request);
        } else {
            return null;
        }
    }

    /**
     * This takes in a transformer and adds all of the registered xsl
     * paraemters.
     *
     * @param transformer
     * @param request
     */
    public static void addXSLParameters(Transformer transformer,
                                        HttpServletRequest request) {
        final Iterator entries = s_XSLParameters.entrySet().iterator();

        while (entries.hasNext()) {
            final Map.Entry entry = (Map.Entry) entries.next();

            String value = ((XSLParameterGenerator) entry.getValue()).
                generateValue(request);
            if (value == null) {
                // XSL does not like nulls
                value = "";
            }
            transformer.setParameter((String) entry.getKey(), value);
        }
    }

}
