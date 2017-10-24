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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class Themes {

    private static final Logger LOGGER = LogManager.getLogger(Themes.class);

    @Inject
    private Instance<ThemeProvider> providers;

    @Inject
    private Instance<ThemeProcessor> processors;

    public List<ThemeInfo> getAvailableThemes() {

        final List<ThemeInfo> themes = new ArrayList<>();
        for (final ThemeProvider provider : providers) {
            themes.addAll(provider.getThemes());
        }

        return themes;
    }

    public List<ThemeInfo> getLiveThemes() {

        final List<ThemeInfo> themes = new ArrayList<>();
        for (final ThemeProvider provider : providers) {
            themes.addAll(provider.getLiveThemes());
        }

        return themes;
    }
    
    public Optional<ThemeInfo> getTheme(final String name, 
                                        final ThemeVersion version) {
        
        for(final ThemeProvider provider : providers) {
            if (provider.providesTheme(name, version)) {
                return provider.getThemeInfo(name, version);
            }
        }
        
        return Optional.empty();
    }

    public String process(Map<String, Object> page, ThemeInfo theme) {

        final ThemeTypeLiteral themeType = new ThemeTypeLiteral(theme.getType());

        final Instance<ThemeProcessor> forType = processors.select(themeType);
        if (forType.isUnsatisfied()) {
            LOGGER.error("No ThemeProcessor implementation for type \"{}\" of "
                             + "theme \"{}\".",
                         theme.getType(),
                         theme.getName());
            throw new UnexpectedErrorException(String
                .format("No ThemeProcessor implementation for type \"%s\" of "
                            + "theme \"%s\".",
                        theme.getType(),
                        theme.getName()));
        }

        if (forType.isAmbiguous()) {
            LOGGER.error(
                "Mutiple ThemeProcessor implementations for type \"{}\" of "
                    + "theme \"{}\".",
                theme.getType(),
                theme.getName());
            throw new UnexpectedErrorException(String
                .format(
                    "Mutiple ThemeProcessor implementations for type \"%s\" of "
                        + "theme \"%s\".",
                    theme.getType(),
                    theme.getName()));
        }

        final Instance<ThemeProvider> forTheme = providers.select(theme
            .getProvider());

        if (forTheme.isUnsatisfied()) {
            LOGGER.error("ThemeProvider \"{}\" not found.",
                         theme.getProvider().getName());
            throw new UnexpectedErrorException(String.format(
                "ThemeProvider \"%s\" not found.",
                theme.getProvider().getName()));
        }

        final ThemeProcessor processor = forType.get();
        final ThemeProvider provider = forTheme.get();

        return processor.process(page, theme, provider);
    }

    private static class ThemeTypeLiteral extends AnnotationLiteral<ThemeType>
        implements ThemeType {

        private static final long serialVersionUID = 3377237291286175824L;

        private final String value;

        public ThemeTypeLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

    }

}
