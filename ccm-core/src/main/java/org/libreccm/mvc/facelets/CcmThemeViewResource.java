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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CcmThemeViewResource extends ViewResource {

    private final String path;

    private final Themes themes;

    public CcmThemeViewResource(final Themes themes, final String path) {
        this.themes = themes;
        this.path = path;
    }

    @Override
    public URL getURL() {
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
