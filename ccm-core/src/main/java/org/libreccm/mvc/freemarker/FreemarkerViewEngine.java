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

import freemarker.template.Configuration;
import freemarker.template.SimpleNumber;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import org.eclipse.krazo.engine.ViewEngineBase;
import org.eclipse.krazo.engine.ViewEngineConfig;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeProvider;
import org.libreccm.theming.freemarker.FreemarkerThemeProcessor;
import org.libreccm.theming.utils.L10NUtils;
import org.libreccm.theming.utils.SettingsUtils;
import org.libreccm.theming.utils.TextUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.mvc.MvcContext;
import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.servlet.http.HttpServletRequest;

/**
 * Customized version of the Freemarker View Engine. This class is based of the
 * View Engine from the Krazo project, but has been extended:
 * <ul>
 * <li>Named Beans are supported</li>
 * <li>Freemarker template have access to the MvcContext under the name
 * {@code mvc}, as in Facelet-based templates</li>
 * <li>The current {@link HttpServletRequest} is made avaiable in Freemarker
 * templates as {@link request}.</li>
 * <li>The following utility functions are made available:
 * <ul>
 * <li>{@code getSetting}: retreives the value of a setting from the theme</li>
 * <li>{@code localize}: retreives a localized value from the theme</li>
 * <li>{@code truncateText}: Truncates text to a specific length.</li>
 * </ul>
 * </li>
 * </ul>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
@Priority(ViewEngine.PRIORITY_APPLICATION)
public class FreemarkerViewEngine extends ViewEngineBase {

    @Inject
    private BeanManager beanManager;

    @Inject
    @ViewEngineConfig
    private Configuration configuration;

    @Inject
    private MvcContext mvc;

    @Inject
    private L10NUtils l10nUtils;

    @Inject
    private SettingsUtils settingsUtils;

    @Inject
    private TextUtils textUtils;

    @Inject
    private ThemeTemplateUtil themeTemplateUtil;

    @Override
    public boolean supports(String view) {
        return view.endsWith(".ftl");
    }

    @Override
    public void processView(final ViewEngineContext context)
        throws ViewEngineException {

        final Charset charset = resolveCharsetAndSetContentType(context);

        try (final Writer writer = new OutputStreamWriter(
            context.getOutputStream(), charset
        )) {
            final Template template = configuration.getTemplate(
                resolveView(context)
            );

            final Map<String, Object> model = new HashMap<>();
            model.put("mvc", mvc);
            model.put("request", context.getRequest(HttpServletRequest.class));

            final Optional<TemplateInfo> templateInfo = themeTemplateUtil
                .getTemplateInfo(context.getView());
            final ThemeProvider themeProvider = themeTemplateUtil
                .findThemeProvider(templateInfo.get().getThemeInfo());
            if (templateInfo.isPresent()) {
                final ThemeInfo themeInfo = templateInfo.get().getThemeInfo();
                model.put("getSetting",
                          new GetSettingMethod(themeInfo, themeProvider)
                );
                model.put("localize",
                          new LocalizeMethod(themeInfo, themeProvider)
                );
            }
            model.put("truncateText", new TruncateTextMethod());

            final Map<String, Object> namedBeans = beanManager
                .getBeans(Object.class)
                .stream()
                .filter(bean -> bean.getName() != null)
                .map(this::findBeanInstance)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(
                    Collectors.toMap(
                        NamedBeanInstance::getName,
                        NamedBeanInstance::getBeanInstance
                    )
                );

            model.putAll(namedBeans);
            model.putAll(context.getModels().asMap());

            template.process(model, writer);
        } catch (TemplateException | IOException e) {
            throw new ViewEngineException(e);
        }
    }

    /**
     * Helper method for retrieving a an instance of a named bean using CDI.
     *
     * @param bean The bean to retrieve.
     *
     * @return An instance of the bean.
     */
    @SuppressWarnings("rawtypes")
    private Optional<NamedBeanInstance> findBeanInstance(final Bean<?> bean) {
        final Context context = beanManager.getContext(bean.getScope());
        final CreationalContext creationalContext = beanManager
            .createCreationalContext(bean);
        @SuppressWarnings("unchecked")
        final Object instance = context.get(bean, creationalContext);

        if (instance == null) {
            return Optional.empty();
        } else {
            return Optional.of(
                new NamedBeanInstance(bean.getName(), instance)
            );
        }
    }

    /**
     * Helper class encapsulating the information about a named bean.
     */
    private class NamedBeanInstance {

        /**
         * The name of the bean.
         */
        private final String name;

        /**
         * The bean instance.
         */
        private final Object beanInstance;

        public NamedBeanInstance(String name, Object beanInstance) {
            this.name = name;
            this.beanInstance = beanInstance;
        }

        public String getName() {
            return name;
        }

        public Object getBeanInstance() {
            return beanInstance;
        }

    }

    /**
     * Retrieves a setting from the theme using the {@link SettingsUtils}.
     */
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

    /**
     * Retrieves a localized value from the theme using the {@link L10NUtils}.
     */
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

    /**
     * Truncates text to a specific length.
     */
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
