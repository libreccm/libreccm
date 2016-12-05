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

/**
 *
 * @param <P> Page class
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public abstract class AbstractPageBuilder<P> implements PageBuilder<P> {

    @Inject
    private ComponentBuilderManager componentBuilderManager;

    @Override
    public P buildPage(final PageModel pageModel) {
        final P page = buildPage();

        for (final ComponentModel componentModel : pageModel.getComponents()) {
            final Object component = buildComponent(
                    componentModel, componentModel.getClass());
            addComponent(page, component);
        }

        return page;
    }

    protected <M extends ComponentModel> Object buildComponent(
            final ComponentModel componentModel, 
            final Class<M> componentModelClass) {

        componentBuilderManager.findComponentBuilder(componentModel.getClass(),
                                                     getType());
        
        final ComponentBuilder<M, ?> builder = componentBuilderManager
                .findComponentBuilder(componentModelClass, getType());
        
        return builder.buildComponent((M) componentModel);
    }

    protected abstract String getType();

    protected abstract void addComponent(P page, Object component);
}
