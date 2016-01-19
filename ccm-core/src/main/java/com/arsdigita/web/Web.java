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
 */
package com.arsdigita.web;

import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * An entry point for functions of the web package.
 *
 * @author Rafael Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class Web {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int the runtime environment and
     * set com.arsdigita.web.Web=DEBUG by uncommenting or adding the line.
     */
    private static final Logger s_log = Logger.getLogger(Web.class);

    private static final LegacyWebConfig s_config = LegacyWebConfig.getInstanceOf();

    private static final ThreadLocal s_request = new InternalRequestLocal();
    private static final ThreadLocal s_servletContext
                                     = new InternalRequestLocal();
    private static final ThreadLocal s_userContext = new InternalRequestLocal();
    private static ThreadLocal s_context;

    static final WebContext s_initialContext = new WebContext();

    /**
     * Internal service property to temporarly save the ServletContext as
     * determined by findResource(resource) method to make it available to those
     * methods of this class which use findResource to lookup a resource as a
     * base for determining additional information, e.g. provide a dispatcher
     * (findResourceDispatcher)
     */
    static private ServletContext s_urlContext;

    /**
     * String containing the webapp context path portion of the WEB application
     * where this CCM instance is executed. (I.e. where the WEB-INF directory is
     * located in the servlet container webapps directory).
     */
    private static String s_contextPath;

    /**
     * Static Initializer block.
     */
    static void init(final HttpServletRequest sreq,
                     final ServletContext sc) {

        Assert.exists(sreq, HttpServletRequest.class);
        Assert.exists(sc, ServletContext.class);

        s_request.set(sreq);
        s_servletContext.set(sc);
        s_contextPath = CCMDispatcherServlet.getContextPath();
    }

    /**
     * Provide the configuration record for code in the web package.
     *
     * @return A <code>LegacyWebConfig</code> configuration record; it cannot be null
     */
    public static LegacyWebConfig getConfig() {
        return s_config;
    }

    /**
     * Gets the web context object from the current thread.
     *
     * @return A <code>WebContext</code> object; it cannot be null Note: Rename
     *         from getContext()
     */
    public static WebContext getWebContext() {
        if (s_context == null) {
            s_context = new WebContextLocal();
        }
        return (WebContext) s_context.get();
    }

    /**
     * Gets the servlet request object of the current thread.
     *
     * @return The current <code>HttpServletRequest</code>; it can be null
     */
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) s_request.get();
    }

    /**
     * Gets the servlet context of the current thread.
     *
     * @return The current <code>ServletContext</code>; it can be null
     */
    public static ServletContext getServletContext() {
        return (ServletContext) s_servletContext.get();
    }

        /**
     * Gets the webapp context path portion of the WEB application where this 
     * CCM instance is executed. (I.e. where the WEB-INF directory is located 
     * in the servlet container webapps directory, known as ServletContext in
     * the Servlet API)
     *
     * @return web context path portion as a String, may be used to construct
     *         a URL (NOT the RealPath!). The ROOT context returns an empty
     *         String("").
     */
    public static String getWebappContextPath() {
            return (String) s_contextPath;
    }

    /**
     * Sets the webapp context path portion of the WEB application where this 
     * CCM instance is executed. (I.e. where the WEB-INF directory is located 
     * in the servlet container webapps directory, known as ServletContext in
     * the Servlet API)
     * Meant to be executed by CCMDispatcherServlet only.
     *
     * @param contextPath
     */
    protected static void setWebappContextPath(String contextPath) {
        s_contextPath = contextPath;
    }

    
    /**
     * Processes an URL String trying to identify a corresponding recource which
     * is mapped to the given path String. The method ensures that the resource
     * definitely exists (using the URL returned) or definitely not (returning
     * null).
     *
     * The resourcePath may be stored at various sources (file system, jar file,
     * database, etc) depending on the implementation of the URL handlers and
     * URLConnection objects.
     *
     *
     * @param resourcePath Path to the resource as String. It may include the
     *                     web context in its first part or may be relative to
     *                     the current webapp document root (i.e. its context).
     *                     Additionally, the web application component (if any)
     *                     may be a comma separate list of webapps to search for
     *                     the rest of the path String. So, if the
     *                     'resourcePath' is:      <pre>
     *                 /myproj,ccm-cms/themes/heirloom/admin/index.xsl
     *                     </pre> then this method will look for resourcePaths at
     * <pre>
     *                 /myproj/themes/heirloom/admin/index.xsl
     *                 /ccm-cms/themes/heirloom/admin/index.xsl
     * </pre>
     *
     * @return the URL for the resourcePath, or null if no resource is mapped to
     *         the resourcePath String
     */
    public static URL findResource(String resourcePath) {

        if (resourcePath == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Parameter resource is null. Giving up.");
            }
            return null;
        }
        // ensure a leading "/"
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }
        if (resourcePath.length() < 2) {
            if (s_log.isDebugEnabled()) {
                s_log
                    .debug("Resource spec is too short: >" + resourcePath + "<");
            }
            return null;
        }

        // determine my own webapp context
        ServletContext myctx = getServletContext();

        // Check for old style resourcePath format including a comma seoarated list
        // of webapps
        if (resourcePath.indexOf(",") <= 0) {
            // no comma separated list found, process as normal

            // just try to find the resourcePath in my own context
            try {
                URL url = myctx.getResource(resourcePath);
                if (url != null) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Got URL " + url + " for " + resourcePath);
                    }
                    return url;   // Return adjusted resourcePath url
                }
            } catch (MalformedURLException ex) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Cannot get resource for " + resourcePath);
                }
                // Try the first part of resourcePath as a webapp context path and
                // check far a resourcePath there
                int offset = resourcePath.indexOf("/", 1); // search for second "/"
                String testPath = resourcePath.substring(1, offset);
                String path = resourcePath.substring(offset);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Try to find a context at " + testPath);
                }
                // Try to achieve a context
                ServletContext ctx = myctx.getContext(testPath);
                if (s_log.isDebugEnabled()) {
                    s_log
                        .debug("Servlet context for " + testPath + " is " + ctx);
                }
                if (ctx != null) {
                    // successs, try to finf a resourcePath for the remaining
                    // string as path
                    try {
                        URL url = ctx.getResource(path);
                        if (url != null) {
                            if (s_log.isDebugEnabled()) {
                                s_log.debug("Got URL " + url + " for " + path);
                            }
                            return url;   // Return adjusted resourcePath url
                        } else {
                            if (s_log.isDebugEnabled()) {
                                s_log.debug("No URL present for " + path);
                            }
                        }
                    } catch (MalformedURLException exc) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("cannot get resource for " + path);
                        }
                    }
                }
            }

            return null;  // fall through

        } else {
            // comma separated list found
            // processing old style, comma separated webapp list
            int offset = resourcePath.indexOf("/", 1); // search for second "/"
            String webappList = resourcePath.substring(1, offset);
            String path = resourcePath.substring(offset);

            String[] webapps = StringUtils.split(webappList, ',');
            if (s_log.isDebugEnabled()) {
                s_log.debug("Web app list " + webappList + " path " + path);
            }

            for (int i = (webapps.length - 1); i >= 0; i--) {

                String ctxPath = webapps[i];
                if (!ctxPath.startsWith("/")) {
                    ctxPath = "/" + ctxPath;
                }
                    // No trailing slash allowed by servlet API!
                // if (!ctxPath.endsWith("/")) {
                //     ctxPath = ctxPath + "/";
                // }

                ServletContext ctx = myctx.getContext(ctxPath);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Servlet context for " + ctxPath + " is " + ctx);
                }
                if (ctx != null) {
                    try {
                        URL url = ctx.getResource(path);
                        if (url != null) {
                            if (s_log.isDebugEnabled()) {
                                s_log.debug("Got URL " + url + " for " + path);
                            }
                            return url;   // Return adjusted resourcePath url
                        } else {
                            if (s_log.isDebugEnabled()) {
                                s_log.debug("No URL present for " + path);
                            }
                        }
                    } catch (MalformedURLException ex) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("cannot get resource for " + path);
                        }
                    }
                }

            }

            return null;  // fall through when nothing found 
        }  // end processing old style comma separated list
    }

    /**
     * Follows the same rules as findResource(String[], String), but instead
     * returns an input stream for reading the resource
     *
     * @param resource Path to the resource as String. It may include the web
     *                 context in its first part or may be relative to the
     *                 current webapp document root (i.e. its context).
     *                 Additionally, it the web application component (if any)
     *                 may be a comma separate list of webapps to search for the
     *                 rest of the path String. So, if the 'resource' is:      <pre>
     *                 /myproj,ccm-cms/themes/heirloom/admin/index.xsl
     *                 </pre> then this method will look for resources at
     * <pre>
     *                 /myproj/themes/heirloom/admin/index.xsl
     *                 /ccm-cms/themes/heirloom/admin/index.xsl
     * </pre>
     *
     * @return the input stream for the resource, or null
     *
     * @throws java.io.IOException
     */
    public static InputStream findResourceAsStream(String resource)
        throws IOException {

        URL url = findResource(resource);
        return url == null ? null : url.openStream();
    }

    /**
     * Follows the same rules as findResource(String), but instead returns a
     * request dispatcher for serving the resource. It is mainly used to find an
     * application's jsp template(s) stored in the file system (or war file in
     * case of unexploded distribution) and provide a handle to execute it.
     * These jsp templates used to be stored a directory named "templates" and
     * there within a directory carrying the modules name. As example:
     * "/templates/ccm-navigation/index.jsp". Inside the modules subdirectory
     * there might by a module specific subdirectory structure. It's up to the
     * module.
     *
     * @param resourcePath Path to the resource as String. It may include the
     *                     web context in its first part or may be relative to
     *                     the current webapp document root (i.e. its context).
     *                     LEGACY FORMAT: Additionally, the web application
     *                     component (if any) may be a comma separate list of
     *                     webapps to search for the rest of the path String.
     *                     So, if the 'resource' is:      <pre>
     *                 /myproj,ccm-cms/themes/heirloom/admin/index.xsl
     *                     </pre> then this method will look for resources at
     * <pre>
     *                 /myproj/themes/heirloom/admin/index.xsl
     *                 /ccm-cms/themes/heirloom/admin/index.xsl
     * </pre> LEGACY FORMAT SUPPORT NOT IMPLEMENTED YET! LEGACY FORMAT MAY BE
     * COMPLETELY REMOVED IN FUTURE RELEASE
     *
     * @return the request dispatcher for the resource, or null
     */
    public static RequestDispatcher findResourceDispatcher(String resourcePath) {

        if (resourcePath == null) {
            return null;
        }
        ServletContext ctx = getServletContext();
        URL url = null;

        // Check for old style resource format including a comma seoarated list
        // of webapps
        if (resourcePath.indexOf(",") <= 0) {
            // no comma separated list found, process as normal

            try {
                url = ctx.getResource(resourcePath);
            } catch (MalformedURLException ex) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Resource for " + resourcePath + " not found.");
                }
                // throw new UncheckedWrapperException(
                //           "No resource at " + resourcePath, ex);
                return null;
            }
            if (url == null) {
                return null;
            } else {
                RequestDispatcher rd = (ctx == null) ? null : ctx
                    .getRequestDispatcher(resourcePath);
                return rd;
            }

        } else {

            // old style format not implemented yet here
            return null;

        }

    }

    /**
     *
     */
    private static class WebContextLocal extends InternalRequestLocal {

        @Override
        protected Object initialValue() {
            return Web.s_initialContext.copy();
        }

        @Override
        protected void clearValue() {
            ((WebContext) get()).clear();
        }

    }

    //  ///////////////////////////////////////////////////////////////////////
    //
    //  DEPRECATED METHODS
    //  ==================
    //  This method assume the main ccm application installed in the hosts root
    //  context and somme other ccm applications installed in its own context.
    //  It assumes futher that each ccm applications registers itself in a
    //  home made application directory and it is viable to query this
    //  directory to find the context for a given ccm application. 
    //
    //  ///////////////////////////////////////////////////////////////////////
    /**
     * Constant to denote the context of the ROOT application (main CCM app).
     * Used by some classes to determine the application context (in terms of
     * servlet specification, i.e. document root of the web application where
     * all the code, specifically WEB-INF, is copied into when unpacking the WAR
     * file.
     *
     * This results in a fixed location (context) for CCM which is no longer
     * valid. Replace by invoking method getWebappContextPath
     *
     * @deprecated without direct replacement. See above
     */
//  private static final String ROOT_WEBAPP = "ROOT";
    /**
     * Map containing a list of registered ccm webapps and corresponding webapp
     * context (ServletContext in JavaEE terms).
     *
     * @deprecated without direct replacement, see above.
     */
//  private static final Map s_contexts = new HashMap();
    /**
     * @deprecated renamed to getWebContext
     */
//  getContext()
    /**
     * Gets the servlet context matching the provided URI. The URI is relative
     * to the root of the server and must start and end with a '/'. It is
     * provided by ContextRegistrationServlet as manually configured in web.xml
     *
     * This should be used in preference to ServletContext#getWebContext(String)
     * since on all versions of Tomcat, this fails if the path of the context
     * requested is below the current context.
     *
     * @param uri the context URI
     *
     * @return the servlet context matching uri, or null
     *
     * @deprecated currently without direct replacement The hash map s_contexts
     * contains a kind of repository where (i.e. in which web application
     * context) a resource may be found. Part of the code access the file system
     * directly which is normally forbidden and violates the principle of
     * isolation Previously it has been used to allow the installation of some
     * modules in its own web application context (e.g. Themedirector) where
     * each module used to register here via ContextRegistrationServlet. This
     * mechanism has to be replaced by a inter-web-application communication, if
     * modules should be enabled to execute in it's web application context.
     *
     */
//  public static ServletContext getServletContext(String uri) {
//      Assert.isTrue(uri.startsWith("/"), "uri must start with /");
//      Assert.isTrue(uri.endsWith("/"), "uri must end with /");
//     return (ServletContext)s_contexts.get(uri);
//  }
    /**
     * Registers a servlet context against a URI. Only intended to be used by
     * ContextRegistrationServlet
     *
     * @deprecated without direct replacement. See getServletContext
     */
//  static final void registerServletContext(String uri,
//                                           ServletContext ctx) {
//      s_log.debug("Mapping " + ctx + " to " + uri);
//      Assert.isTrue(s_contexts.get(uri) == null,
//                   "a context mapping exists at " + uri);
//      // Save the web context as manually configured in web.xml
//      // along with the context as provided by ServletContext.
//      s_contexts.put(uri, ctx);
//  }
    /**
     * Unregisters the servlet context against a URI. Only intended to be used
     * by ContextRegistrationServlet
     *
     * @deprecated without direct replacement. See getServletContext
     */
//  static final void unregisterServletContext(String uri) {
//      s_log.debug("Unmapping " + uri);
//      s_contexts.remove(uri);
//  }
    /**
     * Finds a concrete URL corresponding to an abstract webapp resource. The
     * first argument is a list of webapp paths to search through for the path.
     * So if the webapps param is { 'myproj', 'ccm-cms', 'ROOT' } and the path
     * parma is '/themes/heirloom/apps/content-section/index.xsl' then the paths
     * that are searched are:
     * <pre>
     *  /myproj/themes/heirloom/apps/content-section/index.sl
     *  /ccm-cms/themes/heirloom/apps/content-section/index.sl
     *  /ROOT/themes/heirloom/apps/content-section/index.sl
     * </pre>
     *
     * @param webapps the list of webapps
     * @param path    the resource path
     *
     * @return the URL for the resource, or null
     *
     * @deprecated without direct replacement at the moment.
     */
//  public static URL findResource(String[] webapps,
//                                 String path) {
//        
//      ServletContext ctx = findResourceContext(webapps,
//                                               path);
//        
//      URL url = null;
//      try {
//          url = (ctx == null ? null :
//                 ctx.getResource(path));
//      } catch (IOException ex) {
//          throw new UncheckedWrapperException("cannot get URL for " + path, ex);
//      }
//      if (s_log.isDebugEnabled()) {
//          s_log.debug("URL for " + path + " is " + url);
//      }
//      return url;
//  }
    /**
     * Follows the same rules as findResource(String), but instead returns an
     * input stream for reading the resource
     *
     * @param resource the resource name
     *
     * @return the input stream for the resource, or null
     *
     * @deprecated without direct replacement at the moment.
     */
//  public static InputStream findResourceAsStream(String resource) 
//      throws IOException {
//      ResourceSpec spec = parseResource(resource);
//
//      return findResourceAsStream(spec.getWebapps(),
//                                  spec.getPath());
//  }
    /**
     * Follows the same rules as findResource(String[], String), but instead
     * returns an input stream for reading the resource
     *
     * @param webapps the list of webapps
     * @param path    the resource path
     *
     * @return the input stream for the resource, or null
     *
     * @deprecated without direct replacement at the moment.
     */
//  public static InputStream findResourceAsStream(String[] webapps,
//                                                 String path)
//      throws IOException {
//
//      URL url = findResource(webapps, path);
//        
//      return url == null ? null :
//          url.openStream();
//  }
    /**
     * Follows the same rules as findResource(String), but instead returns a
     * request dispatcher for serving the resource
     *
     * @param resource the resource name
     *
     * @return the request dispatcher for the resource, or null
     *
     * @deprecated without direct replacement at the moment.
     */
//  public static RequestDispatcher findResourceDispatcher(String resource) {
//      ResourceSpec spec = parseResource(resource);
//        
//      return findResourceDispatcher(spec.getWebapps(),
//                                    spec.getPath());
//  }
//  /**
//   * Follows the same rules as findResource(String[], String), but
//   * instead returns a request dispatcher for serving
//   * the resource
//   *
//   * @param webapps the list of webapps
//   * @param path the resource path
//   * @return the request dispatcher for the resource, or null
//   * @deprecated without direct replacement at the moment. 
//   */
//  public static RequestDispatcher findResourceDispatcher(String[] webapps,
//                                                         String path) {
//      ServletContext ctx = findResourceContext(webapps,
//                                               path);
//        
//      return ctx == null ? null : ctx.getRequestDispatcher(path);
//  }
    /**
     *
     * @param webapps
     * @param path    path to the resource, starting with "/" and relative to
     *                the current context root, or relative to the
     *                /META-INF/resources directory of a JAR file inside the web
     *                application's /WEB-INF/lib directory
     *
     * @return
     *
     * @deprecated without direct replacement at the moment.
     */
//  private static ServletContext findResourceContext(String[] webapps,
//                                                    String path) {
//      for (int i = (webapps.length - 1) ; i >= 0 ; i--) {
//           // trash here, depends of a kind of "home made" list of 
//          // webapps/webcontexts (or ServletContexts) which are part of CCM
//          // but installed in its own context (it is the structure of APLAWS
//          // until 1.0.4. 
//          String ctxPath = ROOT_WEBAPP.equals(webapps[i]) ? 
//              "" : webapps[i];
//
//          if (!ctxPath.startsWith("/")) {
//              ctxPath = "/" + ctxPath;
//          }
//          if (!ctxPath.endsWith("/")) {
//              ctxPath = ctxPath + "/";
//          }
//
//          ServletContext ctx = getServletContext(ctxPath);
//          if (s_log.isDebugEnabled()) {
//              s_log.debug("Servlet context for " + ctxPath + " is " + ctx);
//          }
//
//          if (ctx != null) {
//              try {
//                  URL url = ctx.getResource(path);
//                  if (url != null) {
//                      if (s_log.isDebugEnabled()) {
//                          s_log.debug("Got URL " + url + " for " + path);
//                      }
//                      return ctx;
//                  } else {
//                      if (s_log.isDebugEnabled()) {
//                          s_log.debug("No URL present for " + path);
//                      }
//                  }
//              } catch (IOException ex) {
//                  throw new UncheckedWrapperException(
//                      "cannot get resource " + path, ex);
//              }
//          }
//      }
//      return null;
//  }
    // /////////////////////////////////////////////////////////////////////////
    // Private classes and methods
    // /////////////////////////////////////////////////////////////////////////
    /**
     * Splits the resource string into a StringArray of webapps (ServletContexts
     * in terms of JavaEE) and a path to a resource inside a the that webapp.
     * The part between the first and the second slash is always treated as
     * webapp part! This part may consist of a comma separated list in which
     * case the result is an array of webapps > 1.
     *
     * As of version 6.6x CCM is installed into one webapp context by default
     * and the assumption of the first part being a web app is nolonger
     * reloiable. Therefore this routine provides invalid results in some
     * circumstances! In best cases it provides redundancy specifying just the
     * local webapp.
     *
     * Code may be refactored to ensure the first part is really a webapp by
     * inquiring the servlet container using javax.management
     *
     * @param resource
     *
     * @return
     *
     * @deprecated without direct replacement.
     */
//  private static ResourceSpec parseResource(String resource) {
//       if (resource == null || resource.length() < 2) {
//          throw new IllegalArgumentException(
//              "Resource spec is too short: " + resource);
//      }
//
//      int offset = resource.indexOf("/", 1);
//      if (offset == -1) {
//          throw new IllegalArgumentException(
//              "Cannot find second '/' in resource spec : " + resource);
//      }
//        
//      String webappList = resource.substring(1, offset);
//      String path = resource.substring(offset);
//        
//      String[] webapps = StringUtils.split(webappList, ',');
//        
//      if (s_log.isInfoEnabled()) {
//          s_log.info("Web app list " + webappList + " path " + path);
//      }
//        
//      return new ResourceSpec(webapps, path);
//  }
//
//
//  /**
//   * Container to hold a pointer to a resource. The pointer specifically
//   * consists of an array of webapps probably containing the requested
//   * resource and a path to that resource that has to be equal for each 
//   * webapp.
//   * @deprecated without direct replacement at the moment. 
//   */
//  private static class ResourceSpec {
//      private final String[] m_webapps;
//      private final String m_path;
//            
//      /**
//       * Constructor. 
//       * @param webapps
//       * @param path 
//       */
//      public ResourceSpec(String[] webapps,
//                          String path) {
//          m_webapps = webapps;
//          m_path = path;
//      }
//        
//      public String[] getWebapps() {
//          return m_webapps;
//      }
//
//      public String getPath() {
//          return m_path;
//      }
//  }
}
