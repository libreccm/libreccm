/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.librecms.util;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class LanguageUtil {

    private static final Logger LOGGER = LogManager.
            getLogger(LanguageUtil.class);

    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Mapping from the ISO639-1 2-letter codes to the ISO639-2 3-letter codes
     */
    private static final String ISO639_2LA_3LA = "com.arsdigita.cms.util.iso639rev";
    private static final ResourceBundle LANG_3LA = ResourceBundle.getBundle(
            ISO639_2LA_3LA);

    /**
     * Mapping from the ISO639-1 2-letter codes to the full descriptive name
     */
    private static final String ISO639_2LA_FULL = "com.arsdigita.cms.util.iso639full";
    private static final ResourceBundle LANG_FULL = ResourceBundle.getBundle(
            ISO639_2LA_FULL);

    @Inject
    private ConfigurationManager confManager;

    private List<Locale> supportedLanguages;

    @PostConstruct
    private void init() {
        final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
        supportedLanguages = kernelConfig.getSupportedLanguages()
                .stream()
                .map(language -> new Locale(language))
                .collect(Collectors.toList());
    }

    public GlobalizedMessage globalizedLanguageName(final String key) {
        return new GlobalizedMessage(key, ISO639_2LA_FULL);
    }

    public List<Locale> getSupportedLanguages() {
        return supportedLanguages;
    }

    /**
     * Returns a list of the 2 letter language codes of all supported languages.
     *
     * @return all supported languages
     */
    public List<String> getSupportedLanguages2LA() {
        return getSupportedLanguages().stream()
                .map(locale -> locale.getLanguage())
                .collect(Collectors.toList());
    }

    /**
     * Returns the collection of all supported languages. Each entry is a pair
     * of 2 letter code as key and three letter code as value.
     *
     * @return all supported languages
     */
    public List<Pair> getSupportedLanguages3LA() {
        return getSupportedLanguages().stream()
                .map(locale -> new Pair(locale.getLanguage(),
                                        locale.getISO3Language()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the collection of all supported languages. Each entry is a pair
     * of 2 letter code as key and full language name as a value.
     *
     * @return all supported languages
     */
    public List<Pair> getSupportedLanguagesFull() {
        return getSupportedLanguages().stream()
                .map(locale -> new Pair(locale.getLanguage(),
                                        getLangFull(locale)))
                .collect(Collectors.toList());
    }

    /**
     * Returns three letter acronym for language code mapped from two letter
     * code.
     *
     * @param lang
     * @return three letter code for the two letter code. If the resource is not
     * found then the key itself is returned.
     */
    public String getLang3LA(final String lang) {
        String threeLA;
        try {
            // Lookup 3-letter language code via java.util.Locale
            threeLA = (new Locale(lang)).getISO3Language();
        } catch (MissingResourceException ex) {
            LOGGER.warn("No 3 letter code for \"{}\" available via "
                                + "Locale#getISO3Language.",
                        lang);
            LOGGER.warn(ex);

            // If there is none
            try {
                // Lookup 3-letter code via ressource bundle
                threeLA = LANG_3LA.getString(lang);
            } catch (MissingResourceException mex) {
                // if there is still no match, log a warning and return the 2-letter code
                LOGGER.warn("No 3 letter language code for key \"{}\" found.",
                            lang);
                LOGGER.warn(ex);
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
     * @return full language name for the given two letter code If the resource
     * is not found then the key itself is returned.
     */
    public String getLangFull(final Locale lang) {
        // Lookup language name via java.util.Locale
        String fullName = (lang.getDisplayLanguage(
                           globalizationHelper.getNegotiatedLocale()));

        if (lang.getLanguage().equals(fullName)) {
            // If that fails
            try {
                // Lookup language name vie ressource bundle
                fullName = LANG_FULL.getString(lang.getLanguage());
            } catch (MissingResourceException ex) {
                // If there is still nomatch, log a warning and return 2-letter code
                LOGGER.warn("Full language name for key \"{}\" not found.",
                            lang);
                LOGGER.warn(ex);
                fullName = lang.getLanguage();
            }
        }

        return fullName;
    }

    public String getLangFull(final String lang) {
        return getLangFull(new Locale(lang));
    }

    /**
     * Takes in a list of 2 letter codes and converts into 3 letter codes. Each
     * entry is pair of 2 letter code as key and 3 letter code as value.
     *
     * @param list
     * @return
     */
    public List<Pair> convertTo3LA(final List<String> list) {
        return list.stream()
                .map(lang2Code -> new Pair(lang2Code, getLang3LA(lang2Code)))
                .collect(Collectors.toList());
    }

    public List<Pair> convertToFull(final List<String> list) {
        return list.stream()
                .map(lang2Code -> new Pair(lang2Code, getLangFull(lang2Code)))
                .collect(Collectors.toList());
    }

    public List<Pair> convertToG11N(final List<String> list) {
        return list.stream()
                .map(lang2Code -> new Pair(lang2Code,
                                           globalizedLanguageName(lang2Code)))
                .collect(Collectors.toList());
    }

}
