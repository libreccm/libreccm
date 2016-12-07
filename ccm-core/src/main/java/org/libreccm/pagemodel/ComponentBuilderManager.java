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

import java.util.Iterator;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides access to all available implementations of the
 * {@link ComponentBuilder} interface.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ComponentBuilderManager {

    private static final Logger LOGGER = LogManager.getLogger(
        ComponentBuilderManager.class);

    @Inject
    private Instance<ComponentBuilder<?, ?>> componentBuilders;

    /**
     * Find an implementation of the {@link ComponentBuilder} interface for a
     * specific {@link ComponentModel} and type.
     *
     * @param <M>                 Generic variable for the subtype of
     *                            {@link ComponentModel} which is produced by
     *                            the {@link ComponentBuilder} implementation.
     * @param componentModelClass The sub class of the {@link ComponentModel}
     *                            for which is processed by the
     *                            {@link ComponentBuilder}.
     * @param type                The type for which the
     *                            {@link ComponentBuilder} produces the
     *                            component(s).
     *
     * @return An {@link Optional} containing the implementation of the
     *         {@link ComponentBuilder} interface for the specified parameters.
     *         If there is no implementation of the specified parameters an
     *         empty {@link Optional} is returned.
     */
    @SuppressWarnings("unchecked")
    public <M extends ComponentModel> Optional<ComponentBuilder<M, ?>> findComponentBuilder(
        final Class<M> componentModelClass,
        final String type) {

        LOGGER.debug("Trying to find ComponentBuilder for ComponentModel\"{}\""
                         + "and type \"{}\"...",
                     componentModelClass.getName(),
                     type);

        final ComponentModelTypeLiteral literal = new ComponentModelTypeLiteral(
            componentModelClass, type);

        final Instance<ComponentBuilder<?, ?>> instance = componentBuilders
            .select(literal);
        if (instance.isUnsatisfied()) {
            LOGGER.warn("No ComponentBuilder for component model \"%s\" "
                            + "and type \"%s\". Ignoring component model.");
            return Optional.empty();
        } else if (instance.isAmbiguous()) {
            throw new IllegalStateException(String.format(
                "Multiple ComponentBuilders for component model \"%s\" and "
                    + "type \"%s\" available. Something is wrong",
                componentModelClass.getName(),
                type));
        } else {
            final Iterator<ComponentBuilder<?, ?>> iterator = instance.
                iterator();
            final ComponentBuilder<?, ?> componentBuilder = iterator.next();

            return Optional.of((ComponentBuilder<M, ?>) componentBuilder);
        }

    }

    private class ComponentModelTypeLiteral
        extends AnnotationLiteral<ComponentModelType>
        implements ComponentModelType {

        private static final long serialVersionUID = -2601632434295178600L;

        private final Class<? extends ComponentModel> componentModel;
        private final String type;

        public ComponentModelTypeLiteral(
            final Class<? extends ComponentModel> componentModel,
            final String type) {
            this.componentModel = componentModel;
            this.type = type;
        }

        @Override
        public Class<? extends ComponentModel> componentModel() {
            return componentModel;
        }

        @Override
        public String type() {
            return type;
        }

    }

}
