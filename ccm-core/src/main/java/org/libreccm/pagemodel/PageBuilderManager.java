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
import org.libreccm.web.CcmApplication;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Optional;

/**
 * Provides access to all available {@link PageBuilder} implementations.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageBuilderManager {

    private static final Logger LOGGER = LogManager.getLogger(
        PageBuilderManager.class);

    @Inject
    private Instance<PageBuilder<?>> pageBuilders;

    /**
     * Find a {@link PageBuilder} for a specific type and application type.
     *
     * @param type            The type of the {@link PageBuilder}.
     * @param applicationType The application type for which the
     *                        {@link PageBuilder} builds pages.
     *
     * @return An {@link Optional} containing the {@link PageBuilder}
     *         implementation for the specified {@code type} and
     *         {@code applicationType}. If there is no {@code PageBuilder} for
     *         the specified parameters an empty {@link Optional} is returned.
     */
    public Optional<PageBuilder<?>> findPageBuilder(
        final String type,
        final Class<? extends CcmApplication> applicationType) {

        LOGGER.debug("Trying to find PageBuilder for type \"{}\" and "
                         + "application type \"{}\"...",
                     type,
                     applicationType);

        final PageModelTypeLiteral literal = new PageModelTypeLiteral(
            type, applicationType);

        final Instance<PageBuilder<?>> instance = pageBuilders.select(literal);
        if (instance.isUnsatisfied()) {
            LOGGER.warn("No PageBuilder for type \"{}\" and application type "
                            + "\"{}\"  available.",
                        type,
                        applicationType);
            return Optional.empty();
        } else if (instance.isAmbiguous()) {
            throw new IllegalArgumentException(String.format(
                "Multiple PageBuilders for type \"%s\" and "
                    + "application type \"%s\" avilable. Something is wrong.",
                type,
                applicationType));
        } else {
            LOGGER.debug("Found PageBuilder for type \"{}\" and application "
                             + "type \"{}\"...",
                         type,
                         applicationType);
            final Iterator<PageBuilder<?>> iterator = instance.iterator();
            final PageBuilder<?> pageBuilder = iterator.next();

            return Optional.of(pageBuilder);
        }
    }

    private class PageModelTypeLiteral
        extends AnnotationLiteral<PageModelType>
        implements PageModelType {

        private static final long serialVersionUID = 5919950993273871601L;

        private final String type;
        private final Class<? extends CcmApplication> applicationType;

        public PageModelTypeLiteral(
            final String type,
            final Class<? extends CcmApplication> applicationType) {

            this.type = type;
            this.applicationType = applicationType;
        }

        @Override
        public String type() {
            return type;
        }

        @Override
        public Class<? extends CcmApplication> applicationType() {
            return applicationType;
        }

    }

}
