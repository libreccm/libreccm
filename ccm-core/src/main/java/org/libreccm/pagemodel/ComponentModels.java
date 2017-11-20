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
package org.libreccm.pagemodel;

import org.libreccm.modules.CcmModule;
import org.libreccm.modules.Module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class ComponentModels {

    private final List<PageModelComponentModel> availableComponentModels = new ArrayList<>();
    
    @PostConstruct
    private void init() {

        final ServiceLoader<CcmModule> modules = ServiceLoader
            .load(CcmModule.class);

        for (final CcmModule module : modules) {

            final Module moduleData = module
                .getClass()
                .getAnnotation(Module.class);
            
            final PageModelComponentModel[] componentModels = moduleData
            .pageModelComponentModels();
            
            for(final PageModelComponentModel componentModel : componentModels) {
                availableComponentModels.add(componentModel);
            }
        }
    }

    public List<PageModelComponentModel> findAvailableComponentModels() {
        return Collections.unmodifiableList(availableComponentModels);
    }

}
