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

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.ServletContext;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
/**
 * A copy of the {@link TemplateLoader} used by Krazo.
 *
 * The {@code TemplateLoader} used by Krazo is defined as inner class. This
 * class provides the same behaviour as "real" class so that we can use it with
 * Freemarker {@link MultiTemplateLoader}.
 *
 * As extension this implementation of the {@code TemplateLoader} interface will
 * not process template paths which start with {@code @themes/} or
 * {@code /@themes/}. These path are processed by the templates loaders for the
 * theming system.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class KrazoTemplateLoader implements TemplateLoader {

    private final ServletContext servletContext;

    public KrazoTemplateLoader(final ServletContext servletContext) {
        super();
        this.servletContext = servletContext;
    }

    @Override
    public Object findTemplateSource(final String name) throws IOException {
        if (name.startsWith("@themes") || name.startsWith("/@themes")) {
            return null;
        } else {
            // Freemarker drops "/"
            return servletContext.getResourceAsStream(
                String.format("/%s", name)
            );
        }
    }

    @Override
    public long getLastModified(final Object templateSource) {
        return -1;
    }

    @Override
    public Reader getReader(
        final Object templateSource, final String encoding
    ) throws IOException {
        return new InputStreamReader((InputStream) templateSource, encoding);
    }

    @Override
    public void closeTemplateSource(
        final Object templateSource
    ) throws IOException {
        final InputStream inputStream = (InputStream) templateSource;
        inputStream.close();
    }

}
