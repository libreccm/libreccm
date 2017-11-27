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

import java.util.Collection;
import java.util.Map;

/**
 * A {@code ComponentRenderer} transforms a {@link ComponentModel} into a
 * component.
 *
 * An implementation must be annotation with the {@link ComponentModelType}
 * qualifier annotation.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <M> Type of the model the component renderer processes.
 */
public interface ComponentRenderer<M extends ComponentModel> {

    /**
     * Renders a {@link ComponentModel}.
     *
     * The result of the rendering process is a {@link Map} which uses strings
     * as key. The values are either Java primitive types or Collections. More
     * exactly the values are objects of one the following types:
     *
     * <ul>
     * <li>{@link Double}</li>
     * <li>{@link Float}</li>
     * <li>{@link Integer}</li>
     * <li>{@link Long}</li>
     * <li>{@link Short}</li>
     * <li>{@link String}</li>
     * <li>{@link List}</li>
     * <li>{@link Map}</li>
     * </ul>
     *
     * Other subtypes {@link Collection} are might be supported but there is no
     * guarantee for that. The values in a collection must be one of the types
     * in the list above. Collections might contain multiple types from the list
     * above. The keys for a map should always be strings.
     *
     * @param componentModel The component model to render.
     * @param parameters     Parameters provided by the calling
     *                       {@link PageRenderer}.
     *
     * @return A map representing the rendered component.
     */
    Map<String, Object> renderComponent(M componentModel,
                                        Map<String, Object> parameters);

}
