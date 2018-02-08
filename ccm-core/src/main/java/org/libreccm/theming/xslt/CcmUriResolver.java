/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.theming.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.theming.ThemeProvider;
import org.libreccm.theming.ThemeVersion;

import java.io.InputStream;
import java.util.Objects;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CcmUriResolver implements URIResolver {

    private static final Logger LOGGER = LogManager
        .getLogger(CcmUriResolver.class);
    
    private final String theme;
    private final ThemeVersion version;
    private final ThemeProvider themeProvider;
    
    protected CcmUriResolver(final String theme,
                             final ThemeVersion version,
                             final ThemeProvider themeProvider) {
        
        Objects.requireNonNull(theme);
        Objects.requireNonNull(version);
        Objects.requireNonNull(themeProvider);
        
        LOGGER.debug("Creating new instance of {} with these parameters:",
                     getClass().getName());
        LOGGER.debug("\ttheme = {}", theme);
        LOGGER.debug("\tversion = {}", Objects.toString(version));
        LOGGER.debug("\tthemeProvider = {}", 
                     themeProvider.getClass().getName());
        
        this.theme = theme;
        this.version = version;
        this.themeProvider = themeProvider;
    }
    
    @Override
    public Source resolve(final String href,
                          final String base) throws TransformerException {
        
        LOGGER.debug("Resolving href = \"{}\" using base = \"{}\"...",
                     href,
                     base);
        
        final String path;
        if (base == null) {
            path = href;
        } else {
            path = String.join("/", base, href);
        }
        LOGGER.debug("Using path \"{}\"...", path);
        
        final InputStream inputStream = themeProvider
            .getThemeFileAsStream(theme, version, path)
        .orElseThrow(() -> new TransformerException(String
            .format("Failed to resolve URI with href = \"%s\" and base = \"%s\" "
                + "for theme \"%s\" (version = \"%s\" using "
                + "ThemeProvider \"%s\".",
                    href,
                    base,
                    theme,
                    version,
                    themeProvider.getClass().getName())));
        
        LOGGER.debug("Resolved href = \"{}\" with base \"{}\" successfully.",
                     href,
                     path);
        return new StreamSource(inputStream);
    }

}

