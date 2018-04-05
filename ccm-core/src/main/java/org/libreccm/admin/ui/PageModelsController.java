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
package org.libreccm.admin.ui;

import com.vaadin.cdi.ViewScoped;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ComponentModelRepository;
import org.libreccm.pagemodel.ComponentModels;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.pagemodel.PageModelRepository;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class PageModelsController implements Serializable {

    private static final long serialVersionUID = 6204724295214879943L;

    @Inject
    private ComponentModelRepository componentModelRepo;

    @Inject
    private ComponentModels componentModels;

    @Inject
    private PageModelComponentEditorController componentEditorController;
    
    @Inject
    private PageModelManager pageModelManager;

    @Inject
    private PageModelRepository pageModelRepo;

    @Inject
    private PageModelsTableDataProvider pageModelsTableDataProvider;

    @Inject
    private PageModelComponentModelsTableDataProvider componentModelsTableDataProvider;

    @Inject
    private PageModelComponentModelTypesDataProvider componentModelTypesDataProvider;

    protected ComponentModels getComponentModels() {
        return componentModels;
    }
    
    protected PageModelComponentEditorController getComponentEditorController() {
        return componentEditorController;
    }
    
    protected PageModelManager getPageModelManager() {
        return pageModelManager;
    }

    protected PageModelRepository getPageModelRepo() {
        return pageModelRepo;
    }

    protected PageModelsTableDataProvider getPageModelsTableDataProvider() {
        return pageModelsTableDataProvider;
    }

    protected PageModelComponentModelsTableDataProvider getComponentModelsTableDataProvider() {
        return componentModelsTableDataProvider;
    }

    protected PageModelComponentModelTypesDataProvider getComponentModelTypesDataProvider() {
        return componentModelTypesDataProvider;
    }

    /**
     * Retrieves the localised title of the {@link ComponentModel}.
     *
     * @param clazz The class of the {@link ComponentModel}.
     *
     * @return The localised title of the {@link ComponentModel}.
     */
    protected String getComponentModelTitle(
        final Class<? extends ComponentModel> clazz) {

        Objects.requireNonNull(clazz);

        final Optional<PageModelComponentModel> info = componentModels
            .getComponentModelInfo(clazz);

        if (info.isPresent()) {
            final ResourceBundle bundle = ResourceBundle
                .getBundle(info.get().descBundle());

            return bundle.getString(info.get().titleKey());
        } else {
            return clazz.getName();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void deletePageModel(final long pageModelId) {

        final PageModel pageModel = pageModelRepo
            .findById(pageModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %d in the database.",
                    pageModelId)));

        pageModelRepo.delete(pageModel);
        pageModelsTableDataProvider.refreshAll();
    }

// ToDo
//    @Transactional(Transactional.TxType.REQUIRED)
//    protected void addComponentModel(final PageModel pageModel,
//    final ComponentModel componentModel) {
//        
//        Objects.requireNonNull(pageModel);
//        Objects.requireNonNull(componentModel);
//
//        final PageModel toPageModel = pageModelRepo
//            .findById(pageModel.getPageModelId())
//            .orElseThrow(() -> new IllegalArgumentException(String
//            .format("No PageModel with ID %d in the database.",
//                    pageModel.getPageModelId())));
//        
//        pageModelManager.addComponentModel(toPageModel, componentModel);
//    }
    
// ToDo   
//    @Transactional(Transactional.TxType.REQUIRED)
//    protected void removeComponentModel(final PageModel pageModel,
//                                        final ComponentModel componentModel) {
//
//        Objects.requireNonNull(pageModel);
//        Objects.requireNonNull(componentModel);
//
//        final PageModel fromPageModel = pageModelRepo
//            .findById(pageModel.getPageModelId())
//            .orElseThrow(() -> new IllegalArgumentException(String
//            .format("No PageModel with ID %d in the database.",
//                    pageModel.getPageModelId())));
//
//        final ComponentModel theComponentModel = componentModelRepo
//            .findById(componentModel.getComponentModelId())
//            .orElseThrow(() -> new IllegalArgumentException(String
//            .format("No ComponentModel with ID %d in the database.",
//                    componentModel.getComponentModelId())));
//        
//        pageModelManager.removeComponentModel(fromPageModel, theComponentModel);
//    }

}
