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
package org.libreccm.theming.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.Themes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
class FreemarkerConfigurationProvider {

    @Inject
    private ServletContext servletContext;
    
    @Inject
    private Themes themes;

    private final Map<ThemeInfo, Configuration> configurations = new HashMap<>();

    protected Configuration getConfiguration(final ThemeInfo forTheme) {

        if (configurations.containsKey(forTheme)) {

            return configurations.get(forTheme);
        } else {

            final Configuration configuration = new Configuration(
                Configuration.VERSION_2_3_27);
            configuration.setDefaultEncoding("UTF-8");
            configuration
                .setTemplateExceptionHandler(
                    TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setLogTemplateExceptions(false);
            configuration.setWrapUncheckedExceptions(false);
            configuration.setLocalizedLookup(false);
            
            configuration.setTemplateLoader(
                new MultiTemplateLoader(new TemplateLoader[]{
                    // For for files from themes
                    new CcmTemplateLoader(forTheme), 
                    // Loader for MacroLibs provided by CCM modules
                    new WebappTemplateLoader(
                        servletContext, "/themes/freemarker" 
                    ),
                    new ClassTemplateLoader(getClass(), "/themes/freemarker")
                })
            );

            configurations.put(forTheme, configuration);

            return configuration;
        }
    }

    private class CcmTemplateLoader implements TemplateLoader {

        private final ThemeInfo fromTheme;

        public CcmTemplateLoader(final ThemeInfo fromTheme) {
            this.fromTheme = fromTheme;
        }

        @Override
        public Object findTemplateSource(final String name) throws IOException {

            final Optional<InputStream> source = themes.getFileFromTheme(fromTheme, name);
            if (source.isPresent()) {
                return source.get();
            } else {
                return null;
            }
        }

        @Override
        public long getLastModified(final Object templateSource) {

            return -1;
        }

        @Override
        public Reader getReader(final Object templateSource,
                                final String encoding) throws IOException {

            final InputStream inputStream = (InputStream) templateSource;
            return new InputStreamReader(inputStream, encoding);
        }

        @Override
        public void closeTemplateSource(final Object templateSource)
            throws IOException {

            final InputStream inputStream = (InputStream) templateSource;
            inputStream.close();
        }

    }

}
