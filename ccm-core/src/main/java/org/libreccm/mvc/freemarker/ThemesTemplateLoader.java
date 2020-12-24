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
package org.libreccm.mvc.freemarker;

import freemarker.cache.TemplateLoader;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.Themes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Optional;

/**
 * Loads Freemarker templates from a theme.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ThemesTemplateLoader implements TemplateLoader {

    private final Themes themes;

    public ThemesTemplateLoader(final Themes themes) {
        this.themes = themes;
    }

    /**
     * Loads the template from a theme. The path of the theme file must follow
     * the following format:
     *
     * {@code @themes/$themeName/$version/$path/$to/$file}
     *
     * The {@code @themes} prefix is mandantory. {@code $themeName} is the name
     * of the theme from which the template is loaded. {@code $version} is the
     * version of the theme to use. This token is converted to
     * {@link ThemeVersion}. Valid values are therefore {@code DRAFT} and
     * {@code LIVE}. The remainder of the path is the path to the file inside the
     * theme.
     *
     * @param path The path of the file. The path must include the theme and its
     *             version.
     *
     * @return An {@link InputStream} for the template if the template was found
     * in the theme. Otherwise {@code null} is returned.
     *
     * @throws IOException
     */
    @Override
    public Object findTemplateSource(final String path) throws IOException {
        if (path.startsWith("@themes") 
            || path.startsWith("/@themes") 
            || path.startsWith("WEB-INF/views/@themes")) {
            final String[] tokens;
            if (path.startsWith("/")) {
                tokens = path.substring(1).split("/");
            } else {
                tokens = path.split("/");
            }
            return findTemplateSource(tokens);
        } else {
            return null;
        }
    }

    private InputStream findTemplateSource(final String[] tokens) {
        if (tokens.length >= 4) {
            final String themeName = tokens[1];
            final ThemeVersion themeVersion = ThemeVersion
                .valueOf(tokens[2]);
            final String filePath = String.join(
                "/",
                Arrays.copyOfRange(
                    tokens, 3, tokens.length, String[].class
                )
            );

            return findTemplateSource(themeName, themeVersion, filePath);
        } else {
            return null;
        }
    }

    private InputStream findTemplateSource(
        final String themeName,
        final ThemeVersion themeVersion,
        final String filePath
    ) {
        final Optional<ThemeInfo> themeInfo = themes.getTheme(
            themeName, themeVersion
        );
        if (themeInfo.isPresent()) {
            return findTemplateSource(themeInfo.get(), filePath);
        } else {
            return null;
        }
    }

    private InputStream findTemplateSource(
        final ThemeInfo themeInfo, final String filePath
    ) {
        final Optional<InputStream> source = themes.getFileFromTheme(
            themeInfo, filePath
        );
        if (source.isPresent()) {
            return source.get();
        } else {
            return null;
        }
    }

    @Override
    public long getLastModified(Object templateSource) {
        return -1;
    }

    @Override
    public Reader getReader(
        final Object templateSource, final String encoding
    ) throws IOException {
        final InputStream inputStream = (InputStream) templateSource;
        return new InputStreamReader(inputStream, encoding);
    }

    @Override
    public void closeTemplateSource(
        final Object templateSource
    ) throws IOException {
        final InputStream inputStream = (InputStream) templateSource;
        inputStream.close();
    }

}
