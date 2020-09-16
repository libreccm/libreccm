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

import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.theming.Themes;

import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.ViewResource;

/**
 * A {@link ViewResource} implementation for templates from LibreCCM themes.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CcmThemeViewResource extends ViewResource {

    private final String path;

    private final Themes themes;

    /**
     * Constructor for the {@code ViewResource} instance.
     * 
     * @param themes Interface to the theme system
     * @param path The path of the template.
     */
    public CcmThemeViewResource(final Themes themes, final String path) {
        this.themes = themes;
        this.path = path;
    }

    /**
     * Gets the URL of the URL of the template as {@code ViewResource}.
     * 
     * @return The URL of the {@code ViewResource}.
     */
    @Override
    public URL getURL() {
        // The URL build here is a "pseudo" URL. Most of the parts of the URL
        // are no relevant. An special URLStreamHandler implementation is
        // also passed to the URL. This URLStreamHandler implementation takes
        // care of retrieving the template from the theme.
        try {
            return new URL(
                "libreccm",
                null,
                0,
                path,
                new CcmThemeUrlStreamHandler(themes)
            );
        } catch (MalformedURLException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

}
