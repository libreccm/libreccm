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

import org.libreccm.core.UnexpectedErrorException;

import java.io.Serializable;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 * Provides access to the available implementations of {@link ThemeProcessor}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ThemeProcessors implements Serializable {

    private static final long serialVersionUID = -2019759931022734946L;

    /**
     * All available implementations of {@link ThemeProcessor}.
     */
    @Inject
    @Any
    private Instance<ThemeProcessor> processors;

    /**
     * Find the implementation of {@link ThemeProcessor} for {@code type}.
     *
     * @param type The type of the theme to process.
     *
     * @return The implementation {@link ThemeProcessor} which can process
     *         themes of the provided {@code type} or an empty {@code Optional}
     *         if there is no suitable implementation of {@link ThemeProcessor}
     *         is available.
     */
    public Optional<ThemeProcessor> findThemeProcessorForType(final String type) {

        final ProcessesThemeLiteral literal = new ProcessesThemeLiteral(type);

        final Instance<ThemeProcessor> instance = processors
            .select(literal);

        if (instance.isUnsatisfied()) {
            return Optional.empty();
        } else if (instance.isAmbiguous()) {
            throw new UnexpectedErrorException(String
                .format("Multiple implementations of ThemeProcessor found for"
                            + " type \"%s\".", type));
        } else {
            return Optional.of(instance.get());
        }
    }

    private static class ProcessesThemeLiteral
        extends AnnotationLiteral<ProcessesThemes>
        implements ProcessesThemes {

        private static final long serialVersionUID = -7367770572916053117L;

        private final String value;

        public ProcessesThemeLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

    }

}
