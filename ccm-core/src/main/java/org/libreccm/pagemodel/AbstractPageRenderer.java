/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.pagemodel;

import java.util.Map;

import javax.inject.Inject;

import java.util.Optional;

/**
 * An abstract base class for implementations of the {@link PageRenderer}
 * interface providing some functionality needed by all implementations of the
 * {@link PageRenderer} interface.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public abstract class AbstractPageRenderer implements PageRenderer {

    @Inject
    private ComponentRendererManager componentRendererManager;

    /**
     * Renders a {@code Page} based on a {@link PageModel}. This implementation
     * first calls {@link #renderPage()} to create the page object. After that
     * all {@link ComponentModel}s of the {@link PageModel} are processed and
     * the component objects created by the {@link ComponentRenderer}s are added
     * to the page.
     *
     * @param pageModel  The {@link PageModel} to render.
     * @param parameters Parameters provided by application which wants to
     *                   render a {@link PageModel}. The parameters are passed
     *                   the {@link ComponentRenderer}s.
     *
     * @return A map containing the results from rendering the components of the
     *         page model.
     */
    @Override
    public Map<String, Object> renderPage(final PageModel pageModel,
                                          final Map<String, Object> parameters) {

        final Map<String, Object> page = renderPage(parameters);

        for (final ComponentModel componentModel : pageModel.getComponents()) {
            final Optional<Object> component = renderComponent(
                componentModel, componentModel.getClass(),
                parameters);
            if (component.isPresent()) {
                page.put(componentModel.getIdAttribute(),
                         component);
            }
        }

        return page;
    }

    /**
     * Helper method for rendering the components.
     *
     * @param <M>                 Generics variable for the type of rendered
     *                            component
     * @param componentModel      The {@link ComponentModel} to process.
     * @param componentModelClass The class of the {@link ComponentModel}.
     * @param parameters          Parameters provided by application which wants
     *                            to render a {@link PageModel}. The parameters
     *                            are passed the {@link ComponentRenderer}s.
     *
     * @return A map containing the results from rendering the components of the
     *         page model.
     */
    protected <M extends ComponentModel> Optional<Object> renderComponent(
        final ComponentModel componentModel,
        final Class<M> componentModelClass,
        final Map<String, Object> parameters) {

        componentRendererManager
            .findComponentRenderer(componentModel.getClass());

        final Optional<ComponentRenderer<M>> renderer = componentRendererManager
            .findComponentRenderer(componentModelClass);

        if (renderer.isPresent()) {
            @SuppressWarnings("unchecked")
            final M model = (M) componentModel;
            return Optional
                .of(renderer.get().renderComponent(model, parameters));
        } else {
            return Optional.empty();
        }
    }

}
