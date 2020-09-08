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

import static org.libreccm.theming.ThemeConstants.*;

import freemarker.template.SimpleNumber;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.theming.ProcessesThemes;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeProcessor;
import org.libreccm.theming.ThemeProvider;
import org.libreccm.theming.manifest.ThemeTemplate;
import org.libreccm.theming.utils.L10NUtils;
import org.libreccm.theming.utils.SettingsUtils;
import org.libreccm.theming.utils.SystemInfoUtils;
import org.libreccm.theming.utils.TextUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * A {@link ThemeProcessor} implementation for Freemarker based themes.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ProcessesThemes("freemarker")
@RequestScoped
public class FreemarkerThemeProcessor implements ThemeProcessor {

    private static final long serialVersionUID = -5631706431004020559L;

    @Inject
    private FreemarkerConfigurationProvider configurationProvider;

    @Inject
    private L10NUtils l10nUtils;

    @Inject
    private SystemInfoUtils systemInfoUtils;

    @Inject
    private SettingsUtils settingsUtils;

    @Inject
    private TextUtils textUtils;

    @Override
    public String process(final Map<String, Object> page,
                          final ThemeInfo theme,
                          final ThemeProvider themeProvider) {

        final String pathToTemplate;
        if (page.containsKey(PAGE_PARAMETER_TEMPLATE)) {

            final String templateName = (String) page
                .get(PAGE_PARAMETER_TEMPLATE);

            final Optional<ThemeTemplate> template = theme
                .getManifest()
                .getTemplates()
                .stream()
                .filter(current -> current.getName().equals(templateName))
                .findAny();

            if (template.isPresent()) {
                pathToTemplate = template.get().getPath();
            } else {
                throw new UnexpectedErrorException(String
                    .format("Theme \"%s\" does provide template \"%s\".",
                            theme.getName(),
                            templateName));
            }
        } else {
            pathToTemplate = theme.getManifest().getDefaultTemplate();
        }

        page.put("getContextPath", new GetContextPathMethod());
        page.put("getSetting", new GetSettingMethod(theme, themeProvider));
        page.put("localize", new LocalizeMethod(theme, themeProvider));
        page.put("truncateText", new TruncateTextMethod());

        final Template template;
        try {
            template = configurationProvider
                .getConfiguration(theme)
                .getTemplate(pathToTemplate);
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final StringWriter writer = new StringWriter();
        try {
            template.process(page, writer);
        } catch (TemplateException | IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        return writer.toString();
    }

    private class GetContextPathMethod implements TemplateMethodModelEx {

        @Override
        public Object exec(final List arguments) throws TemplateModelException {

            return systemInfoUtils.getContextPath();
        }

    }

    private class GetSettingMethod implements TemplateMethodModelEx {

        private final ThemeInfo fromTheme;
        private final ThemeProvider themeProvider;

        public GetSettingMethod(final ThemeInfo fromTheme,
                                final ThemeProvider themeProvider) {
            this.fromTheme = fromTheme;
            this.themeProvider = themeProvider;
        }

        @Override
        public Object exec(final List arguments) throws TemplateModelException {

            switch (arguments.size()) {
                case 2: {
                    final String filePath = ((TemplateScalarModel) arguments
                                             .get(0))
                        .getAsString();
                    final String settingName = ((TemplateScalarModel) arguments
                                                .get(0))
                        .getAsString();

                    return settingsUtils.getSetting(fromTheme,
                                                    themeProvider,
                                                    filePath,
                                                    settingName);
                }
                case 3: {
                    final String filePath
                                     = ((TemplateScalarModel) arguments.get(0))
                            .getAsString();
                    final String settingName
                                     = ((TemplateScalarModel) arguments.get(1))
                            .getAsString();
                    final String defaultValue
                                     = ((TemplateScalarModel) arguments.get(2))
                            .getAsString();

                    return settingsUtils.getSetting(fromTheme,
                                                    themeProvider,
                                                    filePath,
                                                    settingName,
                                                    defaultValue);
                }
                default:
                    throw new TemplateModelException(
                        "Illegal number of arguments.");
            }
        }

    }

    private class LocalizeMethod implements TemplateMethodModelEx {

        private final ThemeInfo fromTheme;
        private final ThemeProvider themeProvider;

        public LocalizeMethod(final ThemeInfo fromTheme,
                              final ThemeProvider themeProvider) {
            this.fromTheme = fromTheme;
            this.themeProvider = themeProvider;
        }

        @Override
        public Object exec(final List arguments) throws TemplateModelException {

            if (arguments.isEmpty()) {
                throw new TemplateModelException("No string to localize.");
            }

            final String bundle;
            if (arguments.size() > 1) {
                bundle = ((TemplateScalarModel) arguments.get(1)).getAsString();
            } else {
                bundle = "theme-bundle";
            }

            final String key = ((TemplateScalarModel) arguments.get(0))
                .getAsString();

            return l10nUtils.getText(fromTheme, themeProvider, bundle, key);
        }

    }

    private class TruncateTextMethod implements TemplateMethodModelEx {

        @Override
        public Object exec(final List arguments) throws TemplateModelException {

            if (arguments.size() == 2) {
                final String text = ((TemplateScalarModel) arguments.get(0))
                    .getAsString();
                final int length = ((SimpleNumber) arguments.get(1))
                    .getAsNumber()
                    .intValue();

                return textUtils.truncateText(text, length);
            } else {
                throw new TemplateModelException("Illegal number of arguments.");
            }
        }

    }

}
