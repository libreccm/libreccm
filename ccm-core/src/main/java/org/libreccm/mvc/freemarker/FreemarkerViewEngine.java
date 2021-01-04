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
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.eclipse.krazo.engine.ViewEngineBase;
import org.eclipse.krazo.engine.ViewEngineConfig;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
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

    private class NamedBeanInstance {

        private final String name;

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

}
