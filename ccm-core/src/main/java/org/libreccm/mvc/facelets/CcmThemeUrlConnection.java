/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.mvc.facelets;

import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.Themes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Objects;

/**
 * Special {@link URLConnection} for loading Facelets templates from a LibreCCM
 * theme.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CcmThemeUrlConnection extends URLConnection {

    /**
     * Themes instance used as interface to the theme system
     */
    private final Themes themes;

    /**
     * Path of the template to load.
     */
    private final String path;

    /**
     * Theme to use. Initialized by {@link #connect()}.
     */
    private ThemeInfo themeInfo;

    /**
     * Path of the file relative to the theme root. Initialized by
     * {@link #connect()}
     */
    private String filePath;

    /**
     * Constructor for initalizing the instance, providing the required
     * parameters.
     *
     * @param themes Themes instance to use.
     * @param url    URL of the template to load.
     */
    public CcmThemeUrlConnection(final Themes themes, final URL url) {
        super(url);
        this.themes = themes;

        final String urlStr = url.toString();

        if (urlStr.startsWith("/")) {
            path = urlStr.substring(1);
        } else {
            path = urlStr;
        }
    }

    /**
     * Called by Java to connect to the source of the URL. In this case we
     * retrieve the {@link ThemeInfo} for the theme to use, and initalize the
     * {@link #filePath} property.
     *
     * @throws IOException If the theme is not found or if the URL is malformed.
     */
    @Override
    public void connect() throws IOException {
        final String[] tokens = path.split("/");
        if (tokens.length >= 4) {
            final String themeName = tokens[1];
            final ThemeVersion version = ThemeVersion.valueOf(tokens[2]);
            filePath = String.join(
                "/",
                Arrays.copyOfRange(
                    tokens, 3, tokens.length, String[].class
                )
            );

            themeInfo = themes.getTheme(themeName, version)
                .orElseThrow(() -> new IOException(
                String.format(
                    "Theme %s is available as  %s version.",
                    themeName,
                    Objects.toString(version)
                )));
        } else {
            throw new IOException(
                "Malformed URL for loading a facelets template from a theme."
            );
        }

    }

    /**
     * Get an {@link InputStream} for the resource to which the URL points. In
     * this case we delagate the retrieval of the the template to
     * {@link Themes#getFileFromTheme(org.libreccm.theming.ThemeInfo, java.lang.String)}.
     *
     * @return An {@code InputStream} for the requested template.
     *
     * @throws IOException If the template was not found in the theme.
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return themes
            .getFileFromTheme(themeInfo, filePath)
            .orElseThrow(
                () -> new IOException(
                    String.format(
                        "Template %s not found in %s version of the theme %s.",
                        filePath,
                        themeInfo.getVersion(),
                        themeInfo.getName()
                    )
                )
            );

    }

}
