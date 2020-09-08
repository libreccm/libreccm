/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.utils;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeProvider;
import org.libreccm.theming.xslt.XsltThemeProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Utility for getting localized texts for a theme.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class L10NUtils implements Serializable {

    private static final long serialVersionUID = 7077097386650257429L;

    @Inject
    private GlobalizationHelper globalizationHelper;

    public ResourceBundle getBundle(final ThemeInfo fromTheme,
                                    final ThemeProvider themeProvider,
                                    final String bundleName) {

        return ResourceBundle
            .getBundle(
                bundleName,
                globalizationHelper.getNegotiatedLocale(),
                new LocalizedResourceBundleControl(fromTheme,
                                                   themeProvider));
    }

    public String getText(final ThemeInfo fromTheme,
                          final ThemeProvider themeProvider,
                          final String bundleName,
                          final String key) {
        
        final ResourceBundle bundle = getBundle(fromTheme, 
                                                themeProvider,
                                                bundleName);
        
        return bundle.getString(key);
    }
    
    public String getText(final ThemeInfo fromTheme,
                          final ThemeProvider themeProvider,
                          final String bundleName,
                          final String key,
                          final String arguments) {
        
        final ResourceBundle bundle = getBundle(fromTheme, 
                                                themeProvider,
                                                bundleName);
        
        return MessageFormat.format(bundle.getString(key), arguments);
    }

    private class LocalizedResourceBundleControl
        extends ResourceBundle.Control {

        private final ThemeInfo theme;
        private final ThemeProvider themeProvider;

        public LocalizedResourceBundleControl(
            final ThemeInfo theme,
            final ThemeProvider themeProvider) {

            this.theme = theme;
            this.themeProvider = themeProvider;
        }

        @Override
        public List<String> getFormats(final String baseName) {
            Objects.requireNonNull(baseName);

            return Arrays.asList("java.properties");
        }

        @Override
        public ResourceBundle newBundle(final String baseName,
                                        final Locale locale,
                                        final String format,
                                        final ClassLoader classLoader,
                                        final boolean reload)
            throws IllegalAccessException,
                   InstantiationException,
                   IOException {

            if ("java.properties".equals(format)) {

                final String bundleName = toBundleName(baseName, locale);

                final Optional<InputStream> inputStream = themeProvider
                    .getThemeFileAsStream(theme.getName(),
                                          theme.getVersion(),
                                          String.format("%s.properties",
                                                        bundleName));
                if (inputStream.isPresent()) {
                    return new PropertyResourceBundle(inputStream.get());
                } else {
                    return super.newBundle(baseName,
                                           locale,
                                           format,
                                           classLoader,
                                           reload);
                }

            } else {
                return super.newBundle(baseName,
                                       locale,
                                       format,
                                       classLoader,
                                       reload);
            }
        }
    }
}
