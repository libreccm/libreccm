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
package org.libreccm.theming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.pagemodel.PageModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Central interface for using themes. In most cases users of the theming system
 * will use this class instead of directly working with {@link ThemeProvider}s
 * and {@link ThemeProcessor}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class Themes implements Serializable {

    private static final long serialVersionUID = 6861457919635241221L;

    private static final Logger LOGGER = LogManager.getLogger(Themes.class);

    @Inject
    private Instance<ThemeProvider> providers;
//
//    @Inject
//    private Instance<ThemeProcessor> processors;

    @Inject
    private ThemeProcessors themeProcessors;

    /**
     * Retrieve all available themes.
     *
     * @return A list with information about all available themes (draft
     *         versions).
     */
    public List<ThemeInfo> getAvailableThemes() {

        final List<ThemeInfo> themes = new ArrayList<>();
        for (final ThemeProvider provider : providers) {
            themes.addAll(provider.getThemes());
        }

        return themes;
    }

    /**
     * Retrieve all available live themes.
     *
     * @return A list with informations about all live themes.
     */
    public List<ThemeInfo> getLiveThemes() {

        final List<ThemeInfo> themes = new ArrayList<>();
        for (final ThemeProvider provider : providers) {
            themes.addAll(provider.getLiveThemes());
        }

        return themes;
    }

    /**
     * Get information about a specific theme.
     *
     * @param name    Then name of the theme.
     * @param version The version of the theme.
     *
     * @return An {@link Optional} with informations about theme {@code theme}
     *         or an empty optional if there is no such theme.
     */
    public Optional<ThemeInfo> getTheme(final String name,
                                        final ThemeVersion version) {

        for (final ThemeProvider provider : providers) {
            if (provider.providesTheme(name, version)) {
                return provider.getThemeInfo(name, version);
            }
        }

        return Optional.empty();
    }

    /**
     * Creates HTML from the result of rendering a {@link PageModel}.
     *
     * @param page  The page to convert to HTML.
     * @param theme The theme to use.
     *
     * @return The HTML representation of the page.
     */
    public String process(final Map<String, Object> page,
                          final ThemeInfo theme) {

        final Instance<? extends ThemeProvider> forTheme = providers.select(
            theme.getProvider());

        if (forTheme.isUnsatisfied()) {
            LOGGER.error("ThemeProvider \"{}\" not found.",
                         theme.getProvider().getName());
            throw new UnexpectedErrorException(String.format(
                "ThemeProvider \"%s\" not found.",
                theme.getProvider().getName()));
        }

        final ThemeProcessor processor = themeProcessors
            .findThemeProcessorForType(theme.getType())
            .orElseThrow(() -> new UnexpectedErrorException(String
            .format("No ThemeProcessor implementation for type \"%s\" of "
                        + "theme \"%s\".",
                    theme.getType(),
                    theme.getName())));
        final ThemeProvider provider = forTheme.get();

        return processor.process(page, theme, provider);
    }

}
