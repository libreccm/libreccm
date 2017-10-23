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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Optional;

/**
 * Provides access to all available implementations of the
 * {@link ComponentRenderer} interface.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ComponentRendererManager {

    private static final Logger LOGGER = LogManager.getLogger(
        ComponentRendererManager.class);

    @Inject
    private Instance<ComponentRenderer<?>> componentRenderers;

    /**
     * Find an implementation of the {@link ComponentRenderer} interface for a
     * specific {@link ComponentModel}.
     *
     * @param <M>                 Generic variable for the subtype of
     *                            {@link ComponentModel} which is produced by
     *                            the {@link ComponentRenderer} implementation.
     * @param componentModelClass The sub class of the {@link ComponentModel}
     *                            for which is processed by the
     *                            {@link ComponentRenderer}.
     *
     * @return An {@link Optional} containing the implementation of the
     *         {@link ComponentRenderer} interface for the specified parameters.
     *         If there is no implementation for the specified parameters an
     *         empty {@link Optional} is returned.
     */
    @SuppressWarnings("unchecked")
    public <M extends ComponentModel> Optional<ComponentRenderer<M>> findComponentRenderer(
        final Class<M> componentModelClass) {

        LOGGER.debug("Trying to find ComponentRenderer for ComponentModel\"{}\""
                         + "and type \"{}\"...",
                     componentModelClass.getName());

        final ComponentModelTypeLiteral literal = new ComponentModelTypeLiteral(
            componentModelClass);

        final Instance<ComponentRenderer<?>> instance = componentRenderers
            .select(literal);
        if (instance.isUnsatisfied()) {
            LOGGER.warn("No ComponentRenderer for component model \"%s\" "
                            + "and type \"%s\". Ignoring component model.");
            return Optional.empty();
        } else if (instance.isAmbiguous()) {
            throw new IllegalStateException(String.format(
                "Multiple ComponentRenderers for component model \"%s\"available. "
                + "Something is wrong",
                componentModelClass.getName()));
        } else {
            final Iterator<ComponentRenderer<?>> iterator = instance.
                iterator();
            final ComponentRenderer<?> componentRenderer = iterator.next();

            return Optional.of((ComponentRenderer<M>) componentRenderer);
        }

    }

    /**
     * Annotation literal for the {@link ComponentModelType} annotation.
     */
    private class ComponentModelTypeLiteral
        extends AnnotationLiteral<ComponentModelType>
        implements ComponentModelType {

        private static final long serialVersionUID = -2601632434295178600L;

        private final Class<? extends ComponentModel> componentModel;

        public ComponentModelTypeLiteral(
            final Class<? extends ComponentModel> componentModel) {
            this.componentModel = componentModel;
        }

        @Override
        public Class<? extends ComponentModel> componentModel() {
            return componentModel;
        }

    }

}
