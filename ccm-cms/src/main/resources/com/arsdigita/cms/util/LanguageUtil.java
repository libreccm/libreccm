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
package com.arsdigita.cms.util;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Pair;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Utility methods for dealing with the multilingual items.
 *
 * @author Shashin Shinde (sshinde@redhat.com)
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class LanguageUtil {

    private static org.apache.log4j.Logger s_log = org.apache.log4j.Logger.getLogger(
        LanguageUtil.class);
    private static String s_languages = null;
    private static String[] s_languagesArray = null;
    /**
     * Mapping from the ISO639-1 2-letter codes to the ISO639-2 3-letter codes
     */
    private static final String ISO639_2LA_3LA = "com.arsdigita.cms.util.iso639rev";
    private static ResourceBundle s_lang3LA = ResourceBundle.getBundle(ISO639_2LA_3LA);
    /**
     * Mapping from the ISO639-1 2-letter codes to the full descriptive name
     */
    private static final String ISO639_2LA_FULL = "com.arsdigita.cms.util.iso639full";
    private static ResourceBundle s_langFull = ResourceBundle.getBundle(ISO639_2LA_FULL);

    public static GlobalizedMessage globalize(String key) {
        return new LanguageGlobalizedMessage(key);
    }

    /**
     * Sets the supported languages, eliminates all spaces and trims
     *
     * @param comma separated list of langages initialized from initializer at the server startup
     */
    public static void setSupportedLanguages(String languages) {
        s_languages = languages.replace(" ", "").trim();
    }

    /**
     * Get the comma separated list of all supported languages
     */
    public static String getSupportedLanguages() {
        Assert.exists(s_languages, "supported languages not set");
        return s_languages;
    }

    /**
     * Returns the collection of all supported languages.
     *
     * @return all supported languages
     */
    public static Collection getSupportedLanguages2LA() {
        String allLanguages = getSupportedLanguages();
        StringTokenizer tokenizer = new StringTokenizer(allLanguages, ",");
        Collection langList = new LinkedList();
        while (tokenizer.hasMoreElements()) {
            String language = tokenizer.nextToken();
            langList.add(language);
        }
        return langList;
    }

    /**
     * Returns the collection of all supported languages. Each entry is a pair of 2 letter code as
     * key and three letter code as value.
     *
     * @return all supported languages
     */
    public static Collection getSupportedLanguages3LA() {
        String allLanguages = getSupportedLanguages();
        StringTokenizer tokenizer = new StringTokenizer(allLanguages, ",");
        Collection langList = new LinkedList();
        while (tokenizer.hasMoreElements()) {
            String language = tokenizer.nextToken();
            langList.add(new Pair(language, getLang3LA(language)));
        }
        return langList;
    }

    /**
     * Returns the collection of all supported languages. Each entry is a pair of 2 letter code as
     * key and full language name as a value.
     *
     * @return all supported languages
     */
    public static Collection getSupportedLanguagesFull() {
        String allLanguages = getSupportedLanguages();
        StringTokenizer tokenizer = new StringTokenizer(allLanguages, ",");
        Collection langList = new LinkedList();
        while (tokenizer.hasMoreElements()) {
            String language = tokenizer.nextToken();
            langList.add(new Pair(language, getLangFull(language)));
        }
        return langList;
    }

    /**
     * Get the List of languages in which this item can be created. Usefull on UI where we need to
     * display the list of languages in which this Item can be created.
     */
    public static Collection getCreatableLanguages(ContentPage item) {
        ContentBundle bundle = item.getContentBundle();
        Collection allList = getSupportedLanguages2LA();
        allList.removeAll(bundle.getLanguages());
        return allList;
    }

    /**
     * Returns three letter acronym for language code mapped from two letter code.
     *
     * @return three letter code for the two letter code. If the resource is not found then the key
     *         itself is returned.
     */
    public static String getLang3LA(String lang) {
        String threeLA;
        try {
            // Lookup 3-letter language code via java.util.Locale
            threeLA = (new Locale(lang)).getISO3Language();
        } catch (MissingResourceException mre) {
            // If there is none
            try {
                // Lookup 3-letter code via ressource bundle
                threeLA = s_lang3LA.getString(lang);
            } catch (MissingResourceException mexc) {
                // if there is still no match, log a warning and return the 2-letter code
                s_log.warn("Three letter language code for key '" + lang + "' not found: " + mexc);
                threeLA = lang;
            }
        }
        return threeLA;
    }

    /**
     * Returns the full language name mapped from the two letter acronym.
     *
     * @param lang 2 letter language code
     *
     * @return full language name for the given two letter code If the resource is not found then
     *         the key itself is returned.
     */
    public static String getLangFull(String lang) {
        // Lookup language name via java.util.Locale
        String fullName = (new Locale(lang)).getDisplayLanguage(GlobalizationHelper.
            getNegotiatedLocale());

        if (lang.equals(fullName)) {
            // If that fails
            try {
                // Lookup language name vie ressource bundle
                fullName = s_langFull.getString(lang);
            } catch (MissingResourceException mexc) {
                // If there is still nomatch, log a warning and return 2-letter code
                s_log.warn("Full  language name for key '" + lang + "' not found " + mexc);
                fullName = lang;
            }
        }
        return fullName;
    }

    /**
     * Takes in a list of 2 letter codes and converts into 3 letter codes. Each entry is pair of 2
     * letter code as key and 3 letter code as value.
     */
    public static Collection convertTo3LA(Collection list) {
        Collection conList = new LinkedList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String lang2Code = (String) iter.next();
            conList.add(new Pair(lang2Code, getLang3LA(lang2Code)));
        }
        return conList;
    }

    public static Collection convertToFull(Collection list) {
        Collection conList = new LinkedList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String lang2Code = (String) iter.next();
            conList.add(new Pair(lang2Code, getLangFull(lang2Code)));
        }
        return conList;
    }

    public static Collection convertToG11N(Collection list) {
        Collection conList = new LinkedList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String lang2Code = (String) iter.next();
            conList.add(new Pair(lang2Code, globalize(lang2Code)));
        }
        return conList;
    }

    // Special GlobalizedMessage for use with the LanguageUtil#globalize method
    private static class LanguageGlobalizedMessage extends GlobalizedMessage {

        public LanguageGlobalizedMessage(String key) {
            super(key);
        }

        @Override
        public Object localize(Locale locale) {
            return LanguageUtil.getLangFull(this.getKey());
        }

    }
}
