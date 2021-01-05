/*
 * Copyright (C) 2021 LibreCCM Foundation.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeProvider;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.Themes;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ThemeTemplateUtil {

    private static final Logger LOGGER = LogManager.getLogger(ThemeTemplateUtil.class);

    @Inject
    private Instance<ThemeProvider> themeProviders;

    @Inject
    private Themes themes;

    public boolean isValidTemplatePath(final String templatePath) {
        return templatePath.startsWith("@themes")
                   || templatePath.startsWith("/@themes");
    }

    public Optional<TemplateInfo> getTemplateInfo(final String templatePath) {
        if (!isValidTemplatePath(templatePath)) {
            throw new IllegalArgumentException(
                String.format(
                    "Provided template \"%s\" path does not start with "
                        + "\"@theme\" or \"/@theme\".",
                    templatePath
                )
            );
        }

        final String[] tokens;
        if (templatePath.startsWith("/")) {
            tokens = templatePath.substring(1).split("/");
        } else {
            tokens = templatePath.split("/");
        }

        return getTemplateInfo(tokens);
    }

    public ThemeProvider findThemeProvider(final ThemeInfo forTheme) {
        final Instance<? extends ThemeProvider> provider = themeProviders
            .select(forTheme.getProvider());

        if (provider.isUnsatisfied()) {
            LOGGER.error("ThemeProvider \"{}\" not found.",
                         forTheme.getProvider().getName());
            throw new UnexpectedErrorException(
                String.format(
                    "ThemeProvider \"%s\" not found.",
                    forTheme.getProvider().getName()
                )
            );
        }

        return provider.get();
    }

    private Optional<TemplateInfo> getTemplateInfo(final String[] tokens) {
        if (tokens.length >= 4) {
            final String themeName = tokens[1];
            final ThemeVersion themeVersion = ThemeVersion.valueOf(
                tokens[2]
            );
            final String filePath = String.join(
                "/",
                Arrays.copyOfRange(
                    tokens, 3, tokens.length, String[].class
                )
            );

            final Optional<ThemeInfo> themeInfo = themes.getTheme(
                themeName, themeVersion
            );
            
            if (themeInfo.isPresent()) {
                return Optional.of(new TemplateInfo(themeInfo.get(), filePath));
            } else {
                return Optional.empty();
            }
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "Template path has wrong format. Expected at least "
                        + "four tokens separated by slashes, but found only %d",
                    tokens.length
                )
            );
        }
    }

}
