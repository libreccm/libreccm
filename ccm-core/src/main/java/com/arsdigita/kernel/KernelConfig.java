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
package com.arsdigita.kernel;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.EnumerationParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * @author Justin Ross
 * @see com.arsdigita.kernel.Kernel
 * @version $Id$
 */
public final class KernelConfig extends AbstractConfig {

    /** A logger instance.  */
    private static final Logger s_log = Logger.getLogger(KernelConfig.class);

    /** Singelton config object.  */
    private static KernelConfig s_conf;

    /**
     * Gain a KernelConfig object.
     *
     * Singelton pattern, don't instantiate a KernelConfig object using the
     * constructor directly!
     * @return
     */
    public static synchronized KernelConfig getConfig() {
        if (s_conf == null) {
            s_conf = new KernelConfig();
            s_conf.load();
        }

        return s_conf;
    }

    /**  TODO: should be renamed waf.kernel.debug"                           */
    private static Parameter m_debug = new BooleanParameter
            ("waf.debug", Parameter.REQUIRED, Boolean.FALSE);
    /** Whether WEB development support should be activated (true) or not. */
    // Handled in OLD initializer c.ad.webdevsupport.LegacyInitializer
    private static Parameter m_webdevSupport = new BooleanParameter
            ("waf.webdev_support", Parameter.REQUIRED, Boolean.FALSE);
    private final Parameter m_permissions = new BooleanParameter
            ("waf.kernel.data_permission_check_enabled", Parameter.REQUIRED,
             Boolean.TRUE);
    /** User Login by screen name or email address                           */
    private final EnumerationParameter m_identifier = new EnumerationParameter
            ("waf.kernel.primary_user_identifier", Parameter.REQUIRED,
             "email");
    /** 
     *                                                                        */
    private final Parameter m_SSO = new BooleanParameter
            ("waf.kernel.sso_login", Parameter.REQUIRED, Boolean.FALSE);

    /** 
     *                                                                        */
    private final Parameter m_remember = new BooleanParameter
            ("waf.kernel.remember_login", Parameter.REQUIRED, Boolean.TRUE);

    /** 
     *                                                                        */
    private final Parameter m_secureLogin = new BooleanParameter
        	("waf.kernel.secure_login", Parameter.REQUIRED, Boolean.FALSE);

    /** String containing the supported languages.
        The first one is considered default.                                 */
    private final Parameter m_supportedLanguages = new StringParameter
            ("waf.kernel.supported_languages", Parameter.REQUIRED,
             "en,de,fr,nl,it,pt,es");
    private final Parameter m_languageIndependentItems = new BooleanParameter
            ("waf.kernel.language_independent_items", Parameter.REQUIRED, Boolean.FALSE);
    private final Parameter m_languageIndependentCode = new StringParameter
            ("waf.kernel.language_independent_code", Parameter.OPTIONAL,
             "--");

    /**
     * Constructor
     */
    public KernelConfig() {

        // Add recognised Login user identification to enumeration parameter
        m_identifier.put("email", "email");
        m_identifier.put("screen_name", "screenName");
  

        register(m_debug);
        register(m_webdevSupport);
        register(m_permissions);
        register(m_identifier);
        register(m_SSO);
        register(m_remember);
        register(m_secureLogin);
        register(m_supportedLanguages);
        register(m_languageIndependentItems);
        register(m_languageIndependentCode);

        loadInfo();
    }

    
    public final boolean isDebugEnabled() {
        return ((Boolean) get(m_debug)).booleanValue();
    }

    /**
     * Return true, if WEB developer support should be activated.
     */
    public final boolean isWebdevSupportActive() {
        return ((Boolean) get(m_webdevSupport)).booleanValue();
    }

    public final boolean isDataPermissionCheckEnabled() {
        return ((Boolean) get(m_permissions)).booleanValue();
    }

    public final String getPrimaryUserIdentifier() {
        return (String) get(m_identifier);
    }
    
    public final boolean emailIsPrimaryIdentifier() {
        return "email".equals(get(m_identifier));
    }
    
    public final boolean screenNameIsPrimaryIdentifier() {
        return !emailIsPrimaryIdentifier();
    }
    
    public final boolean isSSOenabled() {
        return ((Boolean) get(m_SSO)).booleanValue();
    }

    // XXX Move this to WebConfig.
    public final boolean isLoginRemembered() {
        return ((Boolean) get(m_remember)).booleanValue();
    }
    
    public final boolean isSecureLoginRequired() {
        return ((Boolean) get(m_secureLogin)).booleanValue();
    }

    /**
     * Returns the defaultLanguage flag.
     */
    public final String getDefaultLanguage() {
        return ((String) get(m_supportedLanguages)).trim().substring(0, 2);
    }

    /**
     * Returns the supportedLanguages as String.
     */
    public final String getSupportedLanguages() {
        return (String) get(m_supportedLanguages);
    }

    /**
     * Returns the supportedLanguages as StringTokenizer.
     */
    public final StringTokenizer getSupportedLanguagesTokenizer() {
        return new StringTokenizer(this.getSupportedLanguages(), ",", false);
    }

    /**
     * Returns the languagesIndependentCode as String.
     */
    public final String getLanguagesIndependentCode() {
        return (String) get(m_languageIndependentCode);
    }

    /**
     * Return true, if language lang is part of supported langs
     */
    public final boolean hasLanguage(String lang) {
        return ((String) get(m_supportedLanguages)).contains(lang);
    }

    public final boolean languageIndependentItems() {
        return ((Boolean) get(m_languageIndependentItems)).booleanValue();
    }

}
