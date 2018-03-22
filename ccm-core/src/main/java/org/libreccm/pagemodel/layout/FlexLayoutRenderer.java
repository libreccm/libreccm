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
package org.libreccm.pagemodel.layout;

import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ComponentRenderer;
import org.libreccm.pagemodel.ComponentRendererManager;
import org.libreccm.pagemodel.RendersComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@RendersComponent(componentModel = FlexLayout.class)
public class FlexLayoutRenderer implements ComponentRenderer<FlexLayout> {

    @Inject
    private ComponentRendererManager componentRendererManager;

    @Override
    public Map<String, Object> renderComponent(
        final FlexLayout componentModel,
        final Map<String, Object> parameters) {

        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(parameters);

        final Map<String, Object> result = new HashMap<>();
        result.put("type", FlexLayout.class.getName());
        result.put("direction", componentModel.getDirection().toString());

        result.put("boxes",
                   componentModel
                       .getBoxes()
                       .stream()
                       .map(this::renderBox)
                       .collect(Collectors.toList()));

        return result;
    }

    private Map<String, Object> renderBox(
        final FlexBox box,
        final Map<String, Object> parameters) {

        final Map<String, Object> result = new HashMap<>();

        result.put("order", box.getOrder());
        result.put("size", box.getSize());

        result.put("component",
                   renderComponent(box.getComponent(),
                                   box.getComponent().getClass(),
                                   parameters));

        return result;
    }

    private <M extends ComponentModel> Object renderComponent(
        final ComponentModel componentModel,
        final Class<M> componentModelClass,
        final Map<String, Object> parameters) {

        final Optional<ComponentRenderer<M>> renderer = componentRendererManager
            .findComponentRenderer(componentModelClass);

        if (renderer.isPresent()) {
            @SuppressWarnings("unchecked")
            final M model = (M) componentModel;
            return renderer.get().renderComponent(model, parameters);
        } else {
            return null;
        }
    }

}
