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
package com.arsdigita.runtime;

import com.arsdigita.util.Classes;
import com.arsdigita.util.JavaPropertyReader;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.ParameterContext;
import com.arsdigita.util.parameter.ParameterReader;
import com.arsdigita.xml.XML;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.log4j.Logger;

/**
 * The ConfigRegistry class maps between config classes (subclasses of
 * {@link com.arsdigita.runtime.AbstractConfig}), and a location used
 * for persisting the values in a config class. 
 * 
 * The ConfigRegistry also stores the set of configured packages for a
 * particular CCMResourceManager instance.
 * Additionally it stores a list of URLs for parent configurations that are
 * used for defaulting values not present in the local configuration.
 * This mapping is maintained and extended by CCMResourceManager developers through
 * the use of an XML configuration file placed in the src tree for a
 * particular package. If a particular package is configured, the
 * ConfigRegistry class will look in the classpath for a registry
 * configuration file named <i>package-key</i>.config, and parse the
 * file according to the following specification:
 *
 * <blockquite><pre>
 * &lt;?xml version="1.0" encoding="utf-8"?&gt;
 * &lt;registry&gt;
 *   ...
 *   &lt;config class="CLASSNAME"
 *           storage="FILENAME"/&gt;
 *   ...
 * &lt;/registry&gt;
 * </pre></blockquite>
 *
 * The mappings stored by this ConfigRegistry will then be extended to include 
 * the classes and storage locations specified in the configuration file. These 
 * mappings are then used by the ConfigRegistry instance to load config objects.
 * 
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision$ $Date$
 * @version $Id$
 **/
public class ConfigRegistry {

    private static final Logger s_log = Logger.getLogger(ConfigRegistry.class);
    /**
     * Base url for registry location(s).
     * (i.e. $CATALINA_HOME/webapps/$context/WEB-INF/conf/registry in a
     * standard installation)
     */
    private URL m_url;
    private ClassLoader m_loader;
    private List m_packages = new ArrayList();
    private List m_contexts = new ArrayList();
    private Map m_storage = new HashMap();
    private List m_loaders = new ArrayList();

    /**
     * Constructs a new config registry that will resolve all
     * locations relative to <code>url</code>, and use
     * <code>loader</code> when searching the classpath for registry
     * configuration files.
     *
     * @param url The base url for registry locations.
     * @param loader The ClassLoader to use for retrieving registry
     *               configuration files.
     **/
    public ConfigRegistry(URL url, ClassLoader loader) {
        m_url = url;
        m_loader = loader;
        addContext(RegistryConfig.class, "registry.properties");
        initialize(m_url, new ErrorList());
    }

    /**
     * Convenience class which invokes {@link #ConfigRegistry(URL, ClassLoader)}
     *  defaulting the loader to the current context class loader.
     *
     * @see Thread#getContextClassLoader()
     *
     * @param url The base url for registry locations.
     */
    public ConfigRegistry(URL url) {
        this(url, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Convenience class which invokes {@link #ConfigRegistry(URL, ClassLoader)}
     * defaulting the URL to 
     * <code>new File(System.getProperty("ccm.conf")).toURL()</code>. The value
     * of the ccm.conf system property may or may not include a trailing slash.
     *
     * @param loader The ClassLoader to use when searching for
     * registry configuration files.
     */
    public ConfigRegistry(ClassLoader loader) {
        this(CCMResourceManager.getConfigURL(), loader);
    }

    /**
     * Invokes {@link #ConfigRegistry(URL)} defaulting the URL to <code>new
     * File(System.getProperty("ccm.conf")).toURL()</code>. The value of the
     * ccm.conf system property may or may not include a trailing slash.
     */
    public ConfigRegistry() {
        this(CCMResourceManager.getConfigURL());
    }

    /**
     *
     * @param url  Base url for registry location(s).
     * @param errs Errorlist
     */
    private void initialize(URL url, ErrorList errs) {

        ClassLoader ldr = new URLClassLoader(new URL[]{url}, null);

        RegistryConfig rc = new RegistryConfig();
        load(rc, errs, ldr);

        String[] packages = rc.getPackages();
        URL[] parents = rc.getParents();

        for (int i = 0; i < packages.length; i++) {
            if (!m_packages.contains(packages[i])) {
                initialize(packages[i]);
            }
        }

        for (int i = parents.length - 1; i >= 0; i--) {
            initialize(parents[i], errs);
        }

        m_loaders.add(ldr);
    }

    /**
     * This method is <strong>not</strong> supported API.
     */
    public final void initialize(String key) {
        s_log.debug(String.format("Initalizing for key '%s'", key));
        if (m_packages.contains(key)) {
            throw new IllegalArgumentException("already loaded: " + key);
        }

        InputStream is = m_loader.getResourceAsStream(key + ".config");
        if (is != null) {
            try {
                XML.parse(is, new ConfigRegistryParser());
                m_packages.add(key);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new UncheckedWrapperException(e);
                }
            }
        }
    }

    /**
     * Returns the list of configured packages for this ConfigRegistry.
     *
     * @return A list of package keys represented as Strings.
     **/
    public List getPackages() {
        return m_packages;
    }

    /**
     * Returns a list of config classes for this ConfigRegistry.
     *
     * @return A list of Class objects.
     **/
    public List getContexts() {
        return m_contexts;
    }

    /**
     * Returns the relative location used to store values for the
     * given config class.
     *
     * @param context a subclass of {@link
     * com.arsdigita.runtime.AbstractConfig}
     *
     * @return the relative storage location for <code>context</code>
     *
     * @throws IllegalArgumentException if this ConfigRegistry does
     * not contain a mapping for <code>context</code>
     **/
    public String getStorage(Class context) {
        if (!m_contexts.contains(context)) {
            throw new IllegalArgumentException("no such context: " + context
                                               + "; available contexts="
                                               + m_contexts
                                               + "; context->storage map: "
                                               + m_storage);
        }
        return (String) m_storage.get(context);
    }

    private void addContext(Class context, String storage) {
        s_log.debug(String.format("Adding context '%s', storage '%s'...",
                                  context.getName(), storage));
        m_contexts.add(context);
        m_storage.put(context, storage);
    }

    /**
     * Returns true if this ConfigRegistry contains a mapping for
     * <code>context</code>
     *
     * @param context a subclass of {@link
     * com.arsdigita.runtime.AbstractConfig}
     *
     * @return true if this ConfigRegistry contains a mapping for
     * <code>context</code>
     **/
    public boolean isConfigured(Class context) {
        return m_contexts.contains(context);
    }

    /**
     * Loads the given config object from the correct location based
     * on its class. Defaults all values based on the value of the
     * <code>waf.config.parents</code> parameter. Any errors
     * encountered during loading are reported in the given ErrorList.
     *
     * @param ctx the config object to load
     * @param errs used to accumulate errors during loading
     *
     * @throws IllegalArgumentException if this ConfigRegistry does
     * not contain a mapping for <code>ctx.getClass()</code>
     **/
    public void load(ParameterContext ctx, ErrorList errs) {
        for (Iterator it = m_loaders.iterator(); it.hasNext();) {
            ClassLoader ldr = (ClassLoader) it.next();
            load(ctx, errs, ldr);
        }
    }

    /**
     * Searches through this ConfigRegistry and its parents for the
     * given resource. If it is not found it is also searched for in
     * the classpath specified by the loader passed to this
     * ConfigRegistry on construction. This may be used to load
     * configuration information that is not stored in a config
     * object.
     *
     * @param resource the path to the resource
     *
     * @return an input stream containing the contents of the resource
     * or null if the resource is not found
     */
    public InputStream load(String resource) {
        for (int i = m_loaders.size() - 1; i >= 0; i--) {
            ClassLoader ldr = (ClassLoader) m_loaders.get(i);
            InputStream is = ldr.getResourceAsStream(resource);
            if (is != null) {
                return is;
            }
        }

        return m_loader.getResourceAsStream(resource);
    }

    private void load(ParameterContext ctx, ErrorList errs, ClassLoader ldr) {
        Properties props = getProperties(ldr, getStorage(ctx.getClass()));
        ParameterReader reader = new JavaPropertyReader(props);
        ctx.load(reader, errs);
    }

    private Properties getProperties(ClassLoader ldr, String resource) {
        Properties props = new Properties();        
        InputStream is = ldr.getResourceAsStream(resource);
        if (is != null) {            
            try {
                props.load(is);
            } catch (IOException e) {
                throw new UncheckedWrapperException(e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new UncheckedWrapperException(e);
                }
            }
        } 
        return props;
    }

    private class ConfigRegistryParser extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qn,
                                 Attributes attrs) {
            if (localName.equals("config")) {
                String klass = attrs.getValue(uri, "class");
                String storage = attrs.getValue(uri, "storage");
                // XXX: Is there a better way to handle errors that
                // includes line number information?
                if ((klass == null) || (storage == null)) {
                    throw new IllegalArgumentException(
                            "class and storage attributes are required");
                }

                Class context = Classes.loadClass(klass);
                addContext(context, storage);
            }
        }
    }
}
