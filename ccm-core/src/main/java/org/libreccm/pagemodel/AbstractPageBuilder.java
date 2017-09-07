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

import javax.inject.Inject;
import java.util.Optional;

/**
 * An abstract base class for implementations of the {@link PageBuilder}
 * interface providing some functionality needed by all implementations of the
 * {@link PageBuilder} interface.
 *
 * @param <P> Generics variable for the class which represents the page created
 *            by the {@link PageBuilder}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public abstract class AbstractPageBuilder<P> implements PageBuilder<P> {

    @Inject
    private ComponentBuilderManager componentBuilderManager;

    /**
     * Build a {@code Page} based on a {@link PageModel}. This implementation
     * first calls {@link #buildPage()} to create the page object. After that
     * all {@link ComponentModel}s of the {@link PageModel} are processed and
     * the component objects created by the {@link ComponentBuilder}s are added
     * to the page.
     *
     * @param pageModel The {@link PageModel\ to process.
     *
     * @return A page containing all components from the {@link PageModel}.
     */
    @Override
    public P buildPage(final PageModel pageModel) {
        final P page = buildPage();

        for (final ComponentModel componentModel : pageModel.getComponents()) {
            final Optional<Object> component = buildComponent(
                componentModel, componentModel.getClass());
            if (component.isPresent()) {
                addComponent(page, component);
            }
        }

        return page;
    }

    /**
     * Helper method for building the components.
     *
     * @param <M>                 Generics variable for the type the component
     *                            created.
     * @param componentModel      The {@link ComponentModel} to process.
     * @param componentModelClass The class of the {@link ComponentModel}.
     *
     * @return The components described by the {@code componentModel}.
     */
    protected <M extends ComponentModel> Optional<Object> buildComponent(
        final ComponentModel componentModel,
        final Class<M> componentModelClass) {

        componentBuilderManager.findComponentBuilder(componentModel.getClass(),
                                                     getType());

        final Optional<ComponentBuilder<M, ?>> builder = componentBuilderManager
            .findComponentBuilder(componentModelClass, getType());

        if (builder.isPresent()) {
            return Optional.of(builder.get().buildComponent((M) componentModel));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Abstract method returning the type (view technology) for which the
     * {@link PageBuilder} processes {@link PageModel}s.
     *
     * @return
     */
    protected abstract String getType();

    /**
     * A helper method for adding components to the page. How this is done
     * depends on the view technology, therefore this method must be implemented
     * by the implementations of this abstract class.
     *
     * @param page The page to which the component is added.
     * @param component The component to add to the page.
     */
    protected abstract void addComponent(P page, Object component);

}
