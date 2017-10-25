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

import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 * Interface for theme processors. A theme processor is responsible for
 * converting the result of rendering a {@link PageModel} into HTML.
 *
 * An implementation must be a CDI bean (recommended scope:
 * {@link RequestScoped}) which also annotated with the {@link ProcessesThemes}
 * annotation.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface ThemeProcessor {

    /**
     * Process the provided {@link PageModel} {@code page} and convert into HTML
     * using the theme {@code theme} provided by the
     * {@link ThemeProvider} {@code themeProvider}.
     *
     * @param page The page to convert the HTML.
     * @param theme The theme to use.
     * @param themeProvider The {@link ThemeProvider} which provides the the theme.
     *
     * @return The HTML for the provided {@code page}.
     */
    String process(Map<String, Object> page,
                   ThemeInfo theme,
                   ThemeProvider themeProvider);

}
