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

import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ComponentModelRepository;
import org.libreccm.pagemodel.ContainerModel;
import org.libreccm.pagemodel.ContainerModelRepository;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

/**
 * A helper class providing some functionality used by the JAX-RS classes in
 * this package.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class PageModelsController {

    @Inject
    private ApplicationRepository appRepo;

    @Inject
    private ComponentModelRepository componentModelRepo;

    @Inject
    private ContainerModelRepository containerRepo;

    @Inject
    private PageModelRepository pageModelRepo;

    /**
     * Finds a {@link CcmApplication} using its {@code primaryUrl}.
     *
     * @param appPath The primary URL of the {@link CcmApplication}.
     *
     * @return The {@link CcmApplication} with the provided primary URL.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected CcmApplication findCcmApplication(final String appPath) {

        return appRepo
            .retrieveApplicationForPath(Objects.requireNonNull(appPath))
            .orElseThrow(() -> new NotFoundException(String
            .format("No application with path \"%s\" found.",
                    appPath)));
    }

    /**
     * Finds a {@link ComponentModel} using its {@code componentKey}.
     *
     * @param app            The {@link CcmApplication}.
     * @param pageModel      The {@link PageModel}.
     * @param containerModel The {@link ContainerModel}.
     * @param componentKey   The key the {@link ComponentModel}.
     *
     * @return The {@link ComponentModel} identified by the provided
     *         {@code componentKey}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected ComponentModel findComponentModel(
        final CcmApplication app,
        final PageModel pageModel,
        final ContainerModel containerModel,
        final String componentKey) {

        return componentModelRepo
            .findComponentByContainerAndKey(containerModel, componentKey)
            .orElseThrow(() -> new NotFoundException(String
            .format(
                "The Container \"%s\" of the PageModel \"%s\" of application"
                    + "\"%s\" does not contain a component with the key \"%s\".",
                containerModel.getKey(),
                pageModel.getName(),
                app.getPrimaryUrl(),
                componentKey)));
    }

    /**
     * Finds a {@link ContainerModel} using its {@code containerKey}.
     *
     * @param app          The {@link CcmApplication}.
     * @param pageModel    The {@link PageModel}.
     * @param containerKey The key of the {@link ContainerModel}.
     *
     * @return The {@link ContainerModel} identified by the provided
     *         {@code containerKey}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected ContainerModel findContainer(final CcmApplication app,
                                           final PageModel pageModel,
                                           final String containerKey) {

        return containerRepo
            .findContainerByKeyAndPageModel(
                Objects.requireNonNull(containerKey),
                Objects.requireNonNull(pageModel))
            .orElseThrow(() -> new NotFoundException(String
            .format("The PageModel \"%s\" of application \"%s\" does not have "
                        + "a container identified by the key \"%s\".",
                    pageModel.getName(),
                    app.getPrimaryUrl(),
                    containerKey)));
    }

    /**
     * Determines if a {@link PageModel} with the provided name exists for the
     * provided {@link CcmApplication}.
     *
     * @param app           The {@link CcmApplication} to which the
     *                      {@link PageModel} belongs.
     * @param pageModelName The name of the {@link PageModel}.
     *
     * @return {@code true} if a {@link PageModel} with the name provided by the
     *         parameter {@code pageModelName} exists for the provided
     *         {@link CcmApplication} {@code app}, {@code false} otherwise.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected boolean existsPageModel(final CcmApplication app,
                                      final String pageModelName) {
        return pageModelRepo
            .findDraftByApplicationAndName(app, pageModelName)
            .isPresent();
    }

    /**
     * Finds a {@link PageModel} using its name.
     *
     * @param app           The {@link CcmApplication} to which the
     *                      {@link PageModel} belongs.
     * @param pageModelName The name of the {@link PageModel} to retrieve.
     *
     * @return The {@link PageModel} identified by the name
     *         {@code pageModelName} for the {@link CcmApplication} {@code app}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected PageModel findPageModel(final CcmApplication app,
                                      final String pageModelName) {

        return pageModelRepo
            .findDraftByApplicationAndName(
                Objects.requireNonNull(app),
                Objects.requireNonNull(pageModelName))
            .orElseThrow(() -> new NotFoundException(String.format(
            "No PageModel with name \"%s\" for application \"%s\".",
            pageModelName, app.getPrimaryUrl())));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void savePageModel(final PageModel pageModel) {
        pageModelRepo.save(pageModel);
    }

}
