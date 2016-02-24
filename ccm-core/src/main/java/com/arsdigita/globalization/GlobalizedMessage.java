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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;

/**
 * <p>
 * Represents a key into a ResourceBundle, a target ResourceBundle, and possibly an array of
 * arguments to interpolate into the retrieved message using the MessageFormat class.
 * </p>
 * <p>
 * This class should be used in any situation where the application needs to output localizeable
 * objects.
 * </p>
 *
 * @see java.text.MessageFormat
 * @see java.util.Locale
 * @see java.util.ResourceBundle
 *
 * @version $Id$
 */
public class GlobalizedMessage {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by editing
     * /WEB-INF/conf/log4j.properties int hte runtime environment and set
     * com.arsdigita.globalization.GlobalizedMessage=DEBUG by uncommenting or adding the line.
     */
    private static final Logger LOGGER = Logger.getLogger(GlobalizedMessage.class.getName());
    private String m_key = "";
    private String m_bundleName = "";
    /**
     * {@link ResourceBundle.Control} used by this {@code GlobalizedMessage} for looking up
     * the ResourceBundle. Defaults to {@link ResourceBundle.Control#getNoFallbackControl(java.util.List)}
     * to avoid that the locale of the server is taken into account.
     */
    private ResourceBundle.Control rbControl = ResourceBundle.Control.getNoFallbackControl(
            ResourceBundle.Control.FORMAT_DEFAULT);
    private Object[] m_args = null;

    /**
     * <p>
     * Constructor. Takes in a key to be used to look up a message in the ResourceBundle for the
     * current running application. The base name of the ResourceBundle to do the lookup in is
     * retrieved from the ApplicationContext.
     * </p>
     *
     * @param key The key to use to look up a message in the ResourceBundle.
     */
    public GlobalizedMessage(final String key) {
        setKey(key);
        setBundleName();
    }

    /**
     * <p>
     * Constructor. Takes in a key to be used to look up a message in the ResourceBundle specified.
     * </p>
     *
     * @param key The key to use to look up a message in the ResourceBundle.
     * @param bundleName The base name of the target ResourceBundle.
     */
    public GlobalizedMessage(final String key, final String bundleName) {
        setKey(key);
        setBundleName(bundleName);
    }

    /**
     * <p>
     * Constructor. Takes in a key to be used to look up a message in the ResourceBundle for the
     * current running application. The base name of the ResourceBundle to do the lookup in is
     * retrieved from the ApplicationContext. Also takes in an Object[] of arguments to interpolate
     * into the retrieved message using the MessageFormat class.
     * </p>
     *
     * @param key The key to use to look up a message in the ResourceBundle.
     * @param args An Object[] of arguments to interpolate into the retrieved message.
     */
    public GlobalizedMessage(final String key, final Object[] args) {
        this(key);
        setArgs(args);
    }

    /**
     * <p>
     * Constructor. Takes in a key to be used to look up a message in the ResourceBundle specified.
     * Also takes in an Object[] of arguments to interpolate into the retrieved message using the
     * MessageFormat class.
     * </p>
     *
     * @param key The key to use to look up a message in the ResourceBundle.
     * @param bundleName The base name of the target ResourceBundle.
     * @param args An Object[] of arguments to interpolate into the retrieved message.
     */
    public GlobalizedMessage(final String key,
                             final String bundleName,
                             final Object[] args) {
        this(key, bundleName);
        setArgs(args);
    }
    
    public GlobalizedMessage(final String key,
                             final ResourceBundle.Control rbControl) {
        this(key);
        this.rbControl = rbControl;
    }
    
    public GlobalizedMessage(final String key,
                             final Object[] args, 
                             final ResourceBundle.Control rbControl) {
        this(key, args);
        this.rbControl = rbControl;
    }
    
    public GlobalizedMessage(final String key,
                             final String bundleName,
                             final ResourceBundle.Control rbControl) {
        this(key, bundleName);
        this.rbControl = rbControl;
    }
    
    public GlobalizedMessage(final String key,
                             final String bundleName,
                             final Object[] args,
                             final ResourceBundle.Control rbControl) {
        this(key, bundleName, args);
        this.rbControl = rbControl;
    }

    /**
     * <p>
     * Get the key for this GlobalizedMessage.
     * </p>
     *
     * @return String The key for this GlobalizedMessage.
     */
    public final String getKey() {
        return m_key;
    }

    /**
     *
     * @param key
     */
    private void setKey(final String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("key cannot be empty.");
        }

        m_key = key;
    }

    private String getBundleName() {
        return m_bundleName;
    }

    private void setBundleName() {
        // setBundleName(ApplicationContext.get().getTargetBundle());
        setBundleName("com.arsdigita.dummy.DummyResources");
    }

    private void setBundleName(final String bundleName) {
        if (bundleName == null || bundleName.length() == 0) {
            throw new IllegalArgumentException("bundleName cannot be empty.");
        }

        m_bundleName = bundleName;
    }

    private void setArgs(final Object[] args) {
        m_args = args;
    }

    /**
     * <p>
     * Localize this message. If no message is found the key is returned as the message. This is
     * done so that developers or translators can see the messages that still need localization.
     * </p>
     * <p>
     * Any arguments this message has are interpolated into it using the java.text.MessageFormat
     * class.
     * </p>
     *
     * @return Object Represents the localized version of this message. The reason this method
     * returns an Object and not a String is because we might want to localize resources other than
     * strings, such as icons or sound bites. Maybe this class should have been called
     * GlobalizedObject?
     */
    public Object localize() {
        return localize(CdiUtil.createCdiUtil().findBean(
                    GlobalizationHelper.class).getNegotiatedLocale());
    }

    /**
     * <p>
     * Localize this message according the specified request. If no message is found the key is
     * returned as the message. This is done so that developers or translators can see the messages
     * that still need localization.
     * </p>
     * <p>
     * Any arguments this message has are interpolated into it using the java.text.MessageFormat
     * class.
     * </p>
     *
     * @param request The current running request.
     *
     * @return Object Represents the localized version of this message. The reason this method
     * returns an Object and not a String is because we might want to localize resources other than
     * strings, such as icons or sound bites. Maybe this class should have been called
     * GlobalizedObject?
     */
    public Object localize(final HttpServletRequest request) {
        return localize(CdiUtil.createCdiUtil().findBean(
                    GlobalizationHelper.class).getNegotiatedLocale());
    }

    /**
     * <p>
     * Localize this message with the provided locale. If no message is found the key is returned as
     * the message. This is done so that developers or translators can see the messages that still
     * need localization.
     * </p>
     * <p>
     * Any arguments this message has are interpolated into it using the java.text.MessageFormat
     * class.
     * </p>
     *
     * @param locale The locale to try to use to localize this message.
     *
     * @return Object Represents the localized version of this message. The reason this method
     * returns an Object and not a String is because we might want to localize resources other than
     * strings, such as icons or sound bites. Maybe this class should have been called
     * GlobalizedObject?
     */
    public Object localize(final Locale locale) {
        Object message = getKey();
        ResourceBundle resourceBundle = null;

        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null.");
        }

        try {
            // jensp 2013-03-16: 
            // Previously, ResourceBundle#getBundle(String, Locale) was called here. That was causing problems under 
            // specific circumstances:
            // - The browser of the user is set the english (britain), languge code en_GB
            // - The system language of the server running CCM is set to german (de_DE).
            // In this case, the ResourceBundle.getBundle method first tries to find a ResourceBundle for en_GB, than
            // for en. Usally, both attempts will fail because the english labels are in the default bundle 
            // (no language code). The standard search algorithm of ResourceBundle#getBundle than falls back to the 
            // system language (the language of the SERVER), which is German is this case. Therefore the content center
            // was shown with german texts...
            // Luckily, there is a simple solution: The search algorithm is implemented in the inner class 
            // ResourceBundle.Control. There are also variants of the getBundle method which allow it to pass an 
            // custom implementation of ResouceBundle.Control. Also ResourceBundle.Control has a factory method which
            // offers an implementation of ResourceBundle.Control which does not use the system language. 
            // Therefore, all what was to do was to change the call of getBundle here from 
            // ResourceBundle#getBundle(String, Locale) to ResourceBundle#getBundle(String, Locale, ResourceControl)
            // with ResourceBundle.Control.getNoFallbackControl(List<String>).
            // jensp 2014-07-10: 
            // It is now possible to pass the custom implementation of ResourceBundle.Control to
            // a GlobalizedMessage
            resourceBundle = ResourceBundle.getBundle(
                    getBundleName(),
                    locale,
                    rbControl);
        } catch (MissingResourceException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "ResourceBundle " + getBundleName() + " was not found.");
            }
        }

        try {
            if (resourceBundle == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No ResourceBundle available");
                }
            } else {
                message = resourceBundle.getObject(getKey());
            }
        } catch (MissingResourceException e2) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(getKey() + " was not found in the ResourceBundle.");
            }
        }

        if (m_args != null && m_args.length > 0 && message instanceof String) {
            Object[] args = new Object[m_args.length];
            System.arraycopy(m_args, 0, args, 0, m_args.length);

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof GlobalizedMessage) {
                    args[i] = ((GlobalizedMessage) args[i]).localize(locale);
                }
            }

            message = MessageFormat.format((String) message, args);
        }

        return message;
    }

    /**
     * For debugging, not for localizing.
     *
     * If you need a String, use an additional localize() to get an object and cast it to String.
     * e.g. String label = (String) GlobalizedMessage(key,bundleName).localize();
     *
     * @return The contents in String form for debugging.
     */
    @Override
    public String toString() {
        return getBundleName() + "#" + getKey();
    }

}
