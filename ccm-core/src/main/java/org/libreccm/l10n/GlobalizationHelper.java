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
package org.libreccm.l10n;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;

import java.util.Enumeration;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Provides the locale which has been selected based on the available languages
 * configured in the {@link KernelConfig}, the preferred languages of the client
 * provided an the request, the (optional) {@code lang} attribute in the current
 * session and the optional {@code lang} parameter in the current request.
 *
 * The algorithm used in the class is as follows:
 *
 * <ol>
 * <li>
 * If there is an attribute {@code lang} in the current session use that
 * language.
 * </li>
 * <li>
 * If there is a parameter {@code lang} for the current request, use that
 * language <em>and</em> store the selected language in the session.
 * </li>
 * <li>
 * Get the languages preferred by the client as transmitted in the request and
 * use the first match between the languages preferred by the client and
 * available languages (from the {@link KernelConfig}).
 * </li>
 * </ol>
 *
 * A historic note: This CDI bean replaces the old {@code GlobalizationHelper}
 * class which used static methods and relied on the old
 * {@code DispatcherHelper} for getting the current request. In a CDI
 * environment we can simply inject the current request and don't need to bother
 * with static methods etc.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class GlobalizationHelper {

    private static final String LANG_PARAM = "lang";

    @Inject
    private HttpServletRequest request;

    @Inject
    private ConfigurationManager confManager;

    /**
     * An utility method for getting an value from a {@link LocalizedString}.
     *
     * First tries to get a value for the negotiated locale. If the
     * {@code localizedString} does not have a value for the negotiated locale
     * the default locale set in {@link KernelConfig} is used. If that also
     * values the first value available locale (ordered alphabetically) is used.
     *
     * @param localizedString
     * @return
     */
    public String getValueFromLocalizedString(
        final LocalizedString localizedString) {

        if (localizedString.hasValue(getNegotiatedLocale())) {
            return localizedString.getValue(getNegotiatedLocale());
        }

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        if (localizedString.hasValue(kernelConfig.getDefaultLocale())) {
            return localizedString.getValue(kernelConfig.getDefaultLocale());
        }

        if (localizedString.getAvailableLocales().isEmpty()) {
            return "";
        }

        return localizedString
            .getValues()
            .entrySet()
            .stream()
            .sorted((entry1, entry2) -> {
                return entry1
                    .getKey()
                    .toString()
                    .compareTo(entry2.getKey().toString());
            })
            .findFirst()
            .get()
            .getValue();
    }

    public Locale getNegotiatedLocale() {
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);

        Locale preferred = kernelConfig.getDefaultLocale();

        final Locale selectedLocale = getSelectedLocale();
        if (selectedLocale == null || !kernelConfig.hasLanguage(selectedLocale
            .getLanguage())) {
            final Enumeration<Locale> acceptedLocales = request.getLocales();

            while (acceptedLocales.hasMoreElements()) {
                final Locale current = acceptedLocales.nextElement();
                if (kernelConfig.hasLanguage(current.getLanguage())) {
                    preferred = new Locale(current.getLanguage());
                    break;
                }
            }
        } else {
            preferred = selectedLocale;
        }

        return preferred;
    }

    public Locale getSelectedLocale() {
        // Get the current session, create one if there is none
        final HttpSession session = request.getSession(true);

        // Retrieve previously selected language from session. Might be null if
        // no language has been selected in a previous request.
        final String langSession = (String) session.getAttribute(LANG_PARAM);

        // Get value of lang parameter from request URL. Will be null
        // if the parameter is not set for the current request
        final String langRequest = request.getParameter(LANG_PARAM);

        Locale selected = null;

        if (langRequest == null) {
            if (langSession != null) {
                selected = scanLocale(langSession);
            }
        } else {
            final Locale localeRequest = scanLocale(langRequest);
            if (localeRequest != null) {
                session.setAttribute(LANG_PARAM, langRequest);
                selected = localeRequest;
            }
        }

        return selected;
    }

    private Locale scanLocale(final String language) {
        if (language == null || language.isEmpty()) {
            return null;
        } else {
            final String[] tokens = language.split("_");
            if (tokens.length == 1) {
                return new Locale(tokens[0]);
            } else if (tokens.length == 2) {
                return new Locale(tokens[0], tokens[1]);
            } else if (tokens.length >= 3) {
                return new Locale(tokens[0], tokens[1], tokens[2]);
            } else {
                return null;
            }
        }
    }

}
