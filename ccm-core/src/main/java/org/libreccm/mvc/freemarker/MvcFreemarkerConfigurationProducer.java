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

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.eclipse.krazo.engine.ViewEngineConfig;
import org.eclipse.krazo.ext.freemarker.DefaultConfigurationProducer;
import org.libreccm.theming.Themes;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.mvc.Models;
import javax.servlet.ServletContext;

/**
 * Extends the default configuration for Freemarker of Eclipse Krazo to support
 * Freemarker templates in CCM themes.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class MvcFreemarkerConfigurationProducer
    extends DefaultConfigurationProducer {

    @Inject
    private Models models;

    @Inject
    private ServletContext servletContext;

    @Inject
    private Themes themes;
    
    @Inject
    private ThemeTemplateUtil themeTemplateUtil;

    @Produces
    @ViewEngineConfig
    @Specializes
    @Override
    public Configuration getConfiguration() {
        final Configuration configuration = new Configuration(
            Configuration.VERSION_2_3_30
        );

        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(
            TemplateExceptionHandler.RETHROW_HANDLER
        );
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(false);
        configuration.setLocalizedLookup(false);
        configuration.setTemplateLoader(
            new MultiTemplateLoader(
                new TemplateLoader[]{
                    new KrazoTemplateLoader(servletContext),
                    new ThemesTemplateLoader(themes, themeTemplateUtil),
                    // For loading Freemarker macro libraries from WEB-INF 
                    // resources
                    new WebappTemplateLoader(
                        servletContext, "/themes/freemarker"
                    ),
                    // For loading Freemarker macro libraries from classpath 
                    // resources
                    new ClassTemplateLoader(getClass(), "/themes/freemarker")
                }
            )
        );

        return configuration;
    }

}
