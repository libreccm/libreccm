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
package org.libreccm.pagemodel.rs;

import org.libreccm.pagemodel.PageModel;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS application for managing {@link PageModel}s.
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationPath("/page-models")
public class PageModelsApp extends Application {

    protected static final String APP_NAME = "appName";
    protected static final String PAGE_MODEL_NAME = "pageModelName";
    protected static final String CONTAINER_KEY = "containerKey";
    protected static final String COMPONENT_KEY = "componentKey";

    protected static final String PAGE_MODELS_PATH = "/{" + APP_NAME + "}";
    protected static final String PAGE_MODEL_PATH = PAGE_MODELS_PATH
                                                        + "/{"
                                                        + PAGE_MODEL_NAME
                                                        + "}";
    protected static final String CONTAINERS_PATH = PAGE_MODEL_PATH
                                                        + "/containers";
    protected static final String CONTAINER_PATH = CONTAINERS_PATH
                                                       + "/{" 
                                                   + CONTAINER_KEY
                                                       + "}";
    protected static final String COMPONENTS_PATH = CONTAINER_PATH
                                                        + "/components";
    protected static final String COMPONENT_PATH = COMPONENTS_PATH
                                                       + "/{"  
                                                   + COMPONENT_KEY 
                                                   + "}";

    @Override
    public Set<Class<?>> getClasses() {

        final Set<Class<?>> classes = new HashSet<>();
        classes.add(PageModels.class);
        classes.add(Containers.class);
        classes.add(Components.class);

        return classes;
    }

}
