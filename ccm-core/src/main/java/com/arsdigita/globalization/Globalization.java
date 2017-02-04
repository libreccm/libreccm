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
package com.arsdigita.globalization;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
//import com.arsdigita.kernel.Kernel;
//import com.arsdigita.persistence.DataCollection;
//import com.arsdigita.persistence.Session;
//import com.arsdigita.persistence.SessionManager;
//import com.arsdigita.persistence.TransactionContext;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * <p>
 * Utilities for the globalization process. The methods in this class make use
 * of the assumption that the ACS handles all locale and resource negotiation so
 * that the application developer doesn't have to worry about it.
 * </p>
 *
 * @version $Id$
 */
public class Globalization {

    private static final Logger LOGGER = LogManager.getLogger(
        Globalization.class);

    public static final String ENCODING_PARAM_NAME = "g11n.enc";

    /**
     * The default encoding for parameterts, as specified by the servlet spec
     */
    public static final String DEFAULT_PARAM_ENCODING = "ISO-8859-1";

    /**
     * The default encoding for request/response body data, as specified by the
     * servlet spec
     */
    public static final String DEFAULT_ENCODING = "ISO-8859-1";

//    private static Map s_localeToCharsetMap;
    private static String s_defaultCharset = DEFAULT_ENCODING;

    private static boolean initialized = false;

    static void init() {
        if (initialized) {
            return;
        }
//        loadLocaleToCharsetMap();
        initialized = true;
    }

//    // Load the Locale to Charset Map from persistent storage.
//    public static void loadLocaleToCharsetMap() {
//        // retrieve all Locale objects that have a defaultCharset associated
//        // with them.
//        Session s = SessionManager.getSession();
//
//        final TransactionContext tcontext = s.getTransactionContext();
//        boolean startedTransaction = false;
//
//        try {
//            if (!tcontext.inTxn()) {
//                tcontext.beginTxn();
//                startedTransaction = true;
//            }
//
//            DataCollection locales = s.retrieve(Locale.BASE_DATA_OBJECT_TYPE);
//            locales.addNotEqualsFilter("defaultCharset.id", null);
//
//            Map map = new HashMap();
//
//            while (locales.next()) {
//                Locale localeObject = new Locale(locales.getDataObject());
//                java.util.Locale locale = localeObject.toJavaLocale();
//                Charset defaultCharset = localeObject.getDefaultCharset();
//                Assert.exists(defaultCharset,
//                                     "DefaultCharset for locale \""
//                                     + locale + "\" (" + localeObject + ")");
//                String charset = defaultCharset.getCharset();
//
//                if (s_log.isInfoEnabled()) {
//                    s_log.info("Mapping locale " + locale.toString() +
//                               " to charset " + charset);
//                }
//
//                // insert locale and charset into map
//                map.put(locale.toString(), charset);
//            }
//
//            s_localeToCharsetMap = map;
//        } finally {
//            if (startedTransaction && tcontext.inTxn()) {
//                tcontext.commitTxn();
//            }
//        }
//    }
    static void setDefaultCharset(String encoding) {
        s_defaultCharset = encoding;
    }

    /**
     * Get the default character set for encoding data
     *
     * @return String the character set
     */
    public static String getDefaultCharset() {
        return s_defaultCharset;
    }

    /**
     * <p>
     * Get the default character set for a given locale.
     * </p>
     *
     * @param locale
     *
     * @return String the character set
     *
     * @see java.util.Locale
     */
    public static String getDefaultCharset(java.util.Locale locale) {
        init();
        return "UTF-8";
//        String charset;
//
//        if (locale == null || locale.toString().length() == 0) {
//            throw new IllegalArgumentException("locale cannot be empty.");
//        }
//        
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("Looking for charset for locale " + locale.toString());
//        }
//        // Try a full name match (may include country)
//        charset = "UTF-8";
//
//        if (charset != null) {
//            // Found a match
//            return charset;
//        }
//
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("Looking for charset for language " + locale.getLanguage());
//        }
//        // If we didn't find a full name match, try just the language
//        charset = "UTF-8";
//
//        if ( charset != null ) {
//            return charset;
//        }
//        
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("Falling back on default encoding " + getDefaultCharset());
//        }
//        return getDefaultCharset();
    }

    /**
     * Get the default character set for the request. First tries the
     * getCharacterENcoding() method, then falls back on the
     * DEFAULT_PARAM_ENCODING
     *
     * @return String the character set
     */
    public static String getDefaultCharset(HttpServletRequest req) {
        String charset = req.getCharacterEncoding();
        if (charset == null) {
            charset = DEFAULT_PARAM_ENCODING;
        }
        return charset;
    }

    /**
     * Get the best locale for this request.
     */
    private static java.util.Locale getLocale(HttpServletRequest req) {
        java.util.Locale l = DispatcherHelper.getNegotiatedLocale();
        if (l == null) {
            l = req.getLocale();
        }
        if (l == null) {
            l = java.util.Locale.getDefault();
        }
        return l;
    }

    /**
     * <p>
     * Decode the value of an HttpServletRequest parameter. The value is decoded
     * appropriately (lets hope so anyway).
     * </p>
     *
     * @param r    The HttpServletRequest for which to get the value.
     * @param name The name of the parameter to retrieve.
     *
     * @return String The decoded value of the parameter.
     */
    public static final String decodeParameter(
        HttpServletRequest r, String name
    ) {
        String re = r.getParameter(Globalization.ENCODING_PARAM_NAME);
        String original = r.getParameter(name);
        String real = null;

        if (re == null || re.length() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(ENCODING_PARAM_NAME + " is not set, using locale "
                             + "default encoding for parameter " + name);
            }
            re = getDefaultCharset(getLocale(r));
        }

        if (original == null || original.length() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parameter " + name + " has no value");
            }
            real = original;
        } else if (getDefaultCharset(r).equals(re)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parameter " + name
                             + " is already in correct encoding");
            }
            real = original;
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parameter " + name + " is being converted from "
                             + getDefaultCharset(r) + " into " + re);
            }
            try {
                real = new String(original.getBytes(getDefaultCharset(r)),
                                  re);
            } catch (UnsupportedEncodingException uee) {
                LOGGER.warn("encoding " + re
                            + " is not supported, falling back on system default");
                real = original;
            }
        }

        return real;
    }

    /**
     * <p>
     * Decode all of the values of an HttpServletRequest array parameter.
     * </p>
     *
     * @param r The HttpServletRequest for which to decode the parameters.
     *
     * @return String[] The decoded parameters.
     */
    public static final String[] decodeParameters(HttpServletRequest r,
                                                  String name) {
        String re = r.getParameter(Globalization.ENCODING_PARAM_NAME);
        String[] originals = r.getParameterValues(name);
        String[] real = null;

        if (re == null || re.length() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(ENCODING_PARAM_NAME + " is not set, using locale "
                             + "default encoding for parameter " + name);
            }
            re = getDefaultCharset(getLocale(r));
        }

        if (originals == null || originals.length == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parameter " + name + " has no value");
            }
            real = originals;
        } else if (getDefaultCharset(r).equals(re)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parameter " + name
                             + " is already in correct encoding");
            }
            real = originals;
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parameter " + name + " is being converted from "
                             + getDefaultCharset(r) + " into " + re);
            }
            try {
                real = new String[originals.length];
                for (int i = 0; i < originals.length; i++) {
                    real[i] = new String(originals[i].getBytes(
                        getDefaultCharset(r)),
                                         re);
                }
            } catch (UnsupportedEncodingException uee) {
                LOGGER.warn("encoding " + re
                            + " is not supported, falling back on system default");
                real = originals;
            }
        }

        return real;
    }

    /**
     * <p>
     * Get the appropriate ResourceBundle based ont he request and locale.
     * </p>
     *
     * @return ResourceBundle
     *
     * @see java.util.ResourceBundle
     */
    public static ResourceBundle getResourceBundle() {
        return getResourceBundle(DispatcherHelper.getRequest());
    }

    /**
     * <p>
     * Get the appropriate ResourceBundle based on the request and Locale
     * </p>
     *
     * @param r The current HttpServletRequest
     *
     * @return ResourceBundle
     *
     * @see java.util.ResourceBundle
     */
    public static ResourceBundle getResourceBundle(HttpServletRequest r) {
        RequestContext rc = DispatcherHelper.getRequestContext(r);
        ResourceBundle rb = null;

        rb = rc.getResourceBundle();

        if (rb != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(rb.getClass().getName()
                            + " is the chosen ResourceBundle.");
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No matching ResourceBundle found");
            }
        }

        return rb;
    }

    /**
     * <p>
     * Get an Object from the appropriate ResourceBundle based on the
     * appropriate Locale and key.
     * </p>
     *
     * @param r   The current HttpServletRequest.
     * @param key The key used to select the appropriate Object
     *
     * @return The localized Object
     *
     * @see java.util.ResourceBundle
     */
    public static Object getLocalizedObject(HttpServletRequest r,
                                            String key) {
        ResourceBundle rb = null;
        Object l7dObject = key;

        // If the key does not contain a '#' character, then use the
        // HttpServletRequest alone to determine the appropriate
        // ResourceBundle.
        int separator = key.indexOf('#');
        if (separator < 0) {
            rb = getResourceBundle(r);
        } else {
            java.util.Locale locale = DispatcherHelper.getNegotiatedLocale();
            String targetBundle = key.substring(0, separator);

            try {
                if (locale != null) {
                    rb = ResourceBundle.getBundle(targetBundle, locale);
                } else {
                    rb = ResourceBundle.getBundle(targetBundle);
                }
            } catch (MissingResourceException mre) {
                return key;
            }

            key = key.substring(separator + 1);
        }

        try {
            if (rb != null) {
                l7dObject = rb.getObject(key);
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No ResourceBundle available");
                }
            }
        } catch (MissingResourceException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Key " + key + " was not found in the "
                             + "ResourceBundle");
            }
        }

        return l7dObject;
    }

    /**
     * <p>
     * Get a String from the appropriate ResourceBundle based on the appropriate
     * Locale and key.
     * </p>
     *
     * @param r   The current HttpServletRequest.
     * @param key The key used to select the appropriate String
     *
     * @return The localized String
     *
     * @see java.util.ResourceBundle
     */
    public static String getLocalizedString(HttpServletRequest r,
                                            String key) {
        return (String) getLocalizedObject(r, key);
    }

    /**
     * <p>
     * Get a parameterized String (for doing MessageFormatting) from the
     * appropraite ResourceBundle based on the appropriate Locale and key. Then
     * interpolate the values for the other keys passed.
     * </p>
     *
     * @param r         The current HttpServletRequest.
     * @param key       The key used to select the appropriate String
     * @param arguments A Object[] containing the other keys to localize and
     *                  interpolate into the parameterized string. It may also
     *                  contain other Objects beside Strings, such as Date
     *                  objects and Integers, etc.
     *
     * @return The localized and interpolated String
     *
     * @see java.text.MessageFormat
     * @see java.util.ResourceBundle
     */
    public static String getLocalizedString(HttpServletRequest r,
                                            String key,
                                            Object[] arguments) {
        String l7dString = getLocalizedString(r, key);

        for (int i = 0; i < arguments.length; i++) {
            // if we encounter a String object then treat it as a key and try
            // to look it up in the appropriate ResourceBundle.
            if (arguments[i] instanceof String) {
                arguments[i] = getLocalizedString(r, (String) arguments[i]);
            }
        }

        // interpolate the values into the final string.
        l7dString = MessageFormat.format(l7dString, arguments);

        return l7dString;
    }

    /**
     * <p>
     * Find the ResourceBundle for this language without falling back to a
     * default ResourceBundle in another language
     * </p>
     *
     * @param targetBundle  The ResourceBundle we are looking for.
     * @param locale        The Locale object representing the language we want.
     * @param defaultLocale The Locale object representing the default language.
     */
    public static ResourceBundle getBundleNoFallback(String targetBundle,
                                                     java.util.Locale locale,
                                                     java.util.Locale defaultLocale) {
        ResourceBundle bundle = null;

        if (locale == null) {
            locale = (defaultLocale != null) ? defaultLocale : java.util.Locale
                .getDefault();
        }

        try {
            bundle = ResourceBundle.getBundle(targetBundle, locale);
        } catch (MissingResourceException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Didn't find ResourceBundle for " + targetBundle);
            }
        }

        String targetLanguage = locale.getLanguage();

        // Make sure that if we found a ResourceBundle it is either in the
        // language we were looking for or, by coincidence, the target
        // language happens to match the default language for the system.
        if (bundle != null) {
            if (targetLanguage.equals(bundle.getLocale().getLanguage())
                || targetLanguage.equals(defaultLocale.getLanguage())) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Found matching ResourceBundle for "
                                + targetBundle);
                }
            } else {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Found non-matching ResourceBundle for "
                                + targetBundle);
                }
                bundle = null;
            }
        }

        return bundle;
    }

}
