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
import org.libreccm.pagemodel.ComponentModels;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * CDI bean encapsulating some actions for the components of the
 * {@link PageModelTab}.
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
    private ComponentModels componentModels;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PageModelManager pageModelManager;

    @Inject
    private PageModelRepository pageModelRepo;

    /**
     * Loads the data for rows of the table of page models. Takes care of
     * loading all required lazily fetched properties.
     *
     * @return
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected List<PageModelsTableRow> findPageModels() {

        return pageModelRepo
            .findAll()
            .stream()
            .map(this::buildRow)
            .sorted()
            .collect(Collectors.toList());
    }

    /**
     * Checks if the name of a {@link PageModel} is unique within the page
     * models for an application.
     *
     * @param applicationId The ID of the application.
     * @param name          The name to check.
     *
     * @return {@code true} if the name is unique, {@code false} otherwise.
     */
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

    /**
     * Deletes a {@link PageModel}.
     *
     * @param pageModelId The ID of the {@link PageModel} to delete.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected void deletePageModel(final long pageModelId) {

        final PageModel model = pageModelRepo
            .findById(pageModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %d in the database.",
                    pageModelId)));

        pageModelRepo.delete(model);
    }

    /**
     * Helper method for building the data object containing all data required
     * for one row the tables of {@link PageModel}s.
     *
     * @param model The {@link PageModel} which is represented by the row.
     *
     * @return The {@link PageModelsTableRow} containing all data about the
     *         provided {@link PageModel} required to create the row about the
     *         {@link PageModel} in the table of {@link PageModel}s.
     */
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

    /**
     * Retrieves the localised title of the {@link ComponentModel}.
     *
     * @param clazz The class of the {@link ComponentModel}.
     *
     * @return The localised title of the {@link ComponentModel}.
     */
    protected String getComponentModelTitle(
        final Class<? extends ComponentModel> clazz) {

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

    /**
     * Retrieves the form for editing a {@link ComponentModel}.
     *
     * @param componentModelId The ID of the {@link ComponentModel} instance.
     *
     * @return The form for editing the properties of the {@link ComponentModel}
     *         instance.
     */
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

    /**
     * Retrieves the form for creating/editing an instance of
     * {@link ComponentModel}.
     *
     * @param clazz The class of the {@link ComponentModel}.
     *
     * @return The form for the {@link ComponentModel}.
     */
    protected Class<? extends Form> getComponentModelForm(
        final Class<? extends ComponentModel> clazz) {

        Objects.requireNonNull(clazz);

        final Optional<PageModelComponentModel> info = componentModels
            .getComponentModelInfo(clazz);

        return info
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No data about ComponentModel class \"%s\" available.",
                    clazz.getName())))
            .editor();
    }

    /**
     * Retrieves a list of all {@link ComponentModel} instances assigned to a
     * {@link PageModel}.
     *
     * @param pageModelId The ID of the {@link PageModel}.
     *
     * @return A list of all {@link ComponentModel}s assigned to the
     *         {@link PageModel}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected List<ComponentModel> retrieveComponents(final long pageModelId) {

        final PageModel model = pageModelRepo
            .findById(pageModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %d in the database.",
                    pageModelId)));

        final List<ComponentModel> components = new ArrayList<>();
        for (final ComponentModel component : model.getComponents()) {
            components.add(component);
        }
        return components;
    }

    /**
     * Creates an instance of a {@link ComponentModel} and adds the instance to
     * a {@link PageModel}.
     *
     * @param pageModelId    The ID of the {@link PageModel} to which the new
     *                       {@link ComponentModel} is assigned.
     * @param componentModel The new {@link ComponentModel}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected void addComponentModel(final long pageModelId,
                                     final ComponentModel componentModel) {

        final PageModel model = pageModelRepo
            .findById(pageModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %d in the database.",
                    pageModelId)));

        pageModelManager.addComponentModel(model, componentModel);

    }

    /**
     * Removes a {@link ComponentModel} instance from a {@link PageModel}. This
     * deletes the component model.
     *
     * @param pageModelId      The ID of the {@link PageModel} from which the
     *                         {@link ComponentModel} is removed.
     * @param componentModelId The ID of the {@link ComponentModel} to remove.
     */
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
