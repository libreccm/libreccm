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
package com.arsdigita.ui.admin.pagemodels;

import com.arsdigita.bebop.Form;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ComponentModelRepository;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class PageModelsController implements Serializable {

    private static final long serialVersionUID = -5105462163244688201L;

    @Inject
    private ApplicationRepository applicationRepo;

    @Inject
    private ComponentModelRepository componentModelRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PageModelManager pageModelManager;

    @Inject
    private PageModelRepository pageModelRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<PageModelsTableRow> findPageModels() {

        return pageModelRepo
            .findAll()
            .stream()
            .map(this::buildRow)
            .sorted()
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected boolean isUnique(final long applicationId,
                               final String name) {

        final CcmApplication application = applicationRepo
            .findById(applicationId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No CcmApplication with ID %d in the database.",
                    applicationId)));

        return !pageModelRepo
            .findByApplicationAndName(application, name)
            .isPresent();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void deletePageModel(final long pageModelId) {

        final PageModel model = pageModelRepo
            .findById(pageModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %d in the database.",
                    pageModelId)));

        pageModelRepo.delete(model);
    }

    private PageModelsTableRow buildRow(final PageModel model) {

        final PageModelsTableRow row = new PageModelsTableRow();

        row.setModelId(model.getPageModelId());
        row.setName(model.getName());
        row.setTitle(globalizationHelper
            .getValueFromLocalizedString(model.getTitle()));
        row.setDescription(globalizationHelper
            .getValueFromLocalizedString(model.getDescription()));
        row.setApplicationName(model.getApplication().getPrimaryUrl());
        row.setLive(pageModelManager.isLive(model));

        return row;
    }

    protected Class<? extends Form> getComponentModelForm(
        final long componentModelId) {

        final ComponentModel componentModel = componentModelRepo
            .findById(componentModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ComponentModel with ID %d in the database.",
                    componentModelId)));

        final Class<? extends ComponentModel> clazz = componentModel
            .getClass();

        return getComponentModelForm(clazz);
    }

    protected Class<? extends Form> getComponentModelForm(
        final Class<? extends ComponentModel> clazz) {

        if (clazz.isAnnotationPresent(PageModelComponentModel.class)) {

            final PageModelComponentModel annotation = clazz
                .getAnnotation(PageModelComponentModel.class);

            return annotation.editor();
        } else {
            return null;
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<ComponentModel> retrieveComponents(final long pageModelId) {

        final PageModel model = pageModelRepo
            .findById(pageModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %d in the database.",
                    pageModelId)));

        return model.getComponents();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void removeComponentModel(final long pageModelId,
                                        final long componentModelId) {

        final PageModel model = pageModelRepo
            .findById(pageModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %d in the database.",
                    pageModelId)));

        final ComponentModel componentModel = componentModelRepo
            .findById(componentModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ComponentModel with ID %d in the database.",
                    componentModelId)));

        pageModelManager.removeComponentModel(model, componentModel);
    }

}
