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
package com.arsdigita.dispatcher;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import org.apache.log4j.Logger;

/**
 * @author Randy Graebner
 * @version $Id: DispatcherConfig.java 1169 2006-06-14 13:08:25Z fabrice $
 */
public final class DispatcherConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(DispatcherConfig.class);

    private final Parameter m_cachingActive;
    private final Parameter m_defaultExpiry;
    private final Parameter m_staticURLPrefix;

    /** Default top-level container for all Bebop components and containersPage
     *  to use for dispatching Bebop pages. A custom installation may provide 
     *  it's own implementation. Use with care because all pages inherit from
     *  this class! 
     *  Default is {@see com.arsdigita.bebop.Page}                            */
    private final Parameter m_defaultPageClass= new 
            StringParameter("waf.dispatcher.default_page_class",
                            Parameter.OPTIONAL,
                            "com.arsdigita.bebop.Page");

    public DispatcherConfig() {
        m_cachingActive = new BooleanParameter
            ("waf.dispatcher.is_caching_active", 
             Parameter.REQUIRED, Boolean.TRUE);

        // defaults to three days 
        m_defaultExpiry = new IntegerParameter
            ("waf.dispatcher.default_expiry", Parameter.REQUIRED,
             new Integer(259200));

        m_staticURLPrefix = new StringParameter
            ("waf.dispatcher.static_url_prefix", Parameter.REQUIRED,
             "/STATICII/");

        register(m_staticURLPrefix);
        register(m_cachingActive);
        register(m_defaultExpiry);
        register(m_defaultPageClass);

        loadInfo();
    }

    /**
     *  Get the URL for static items
     */
    public String getStaticURLPrefix() {
        return (String)get(m_staticURLPrefix);
    }

    /**
     *  This returns Boolean.TRUE if the caching is active
     */
    public Boolean getCachingActive() {
        return (Boolean)get(m_cachingActive);
    }

    public boolean isCachingActive() {
        return Boolean.TRUE.equals(getCachingActive());
    }

    /**
     *  This returns the number of seconds something is cached for
     */
    public Integer getDefaultExpiryTime() {
        return (Integer)get(m_defaultExpiry);
    }
    
    /**
     * Retrieve the top-level container for all Bebop components and 
     * containersPage to use by dispatcher.
     * Most installation should use the provided default implementation in
     * {@see com.arsdigita.bebop.Page}
     */ 
    public String getDefaultPageClass() {
    	return (String)get(m_defaultPageClass);
    }
}
