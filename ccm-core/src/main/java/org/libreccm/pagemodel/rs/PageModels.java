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

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

import java.util.Locale;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Provides RESTful endpoints for retrieving, creating, updating and deleting
 * {@link PageModels}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
public class PageModels {

    @Inject
    private PageModelsController controller;

    @Inject
    private PageModelRepository pageModelRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ConfigurationManager confManager;

    /**
     * Retrieves all {@link PageModel}s available for an {@link CcmApplication}.
     *
     * @param appPath The path of the {@code app}.
     *
     * @return A JSON array with the data of all {@link PageModel}s of the
     *         {@link CcmApplication} {@code app}.
     *
     * @throws NotFoundException If there is no {@link CcmApplication} with the
     *                           primary URL {@code appPath} an
     *                           {@link NotFoundException} thrown resulting in
     *                           404 response.
     */
    @GET
    @Path(PageModelsApp.PAGE_MODELS_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray getAllPageModels(
        @PathParam(PageModelsApp.APP_NAME) String appPath) {

        Objects.requireNonNull(appPath);

        final CcmApplication app = controller
            .findCcmApplication(String.format("/%s/", appPath));
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        pageModelRepo
            .findDraftByApplication(app)
            .stream()
            .map(this::mapPageModelToJson)
            .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    /**
     * Retrieves a specific {@link PageModel}.
     *
     * @param appPath       The path ({@link CcmApplication#primaryUrl} of the
     *                      {@link CcmApplication} to which the
     *                      {@link PageModel} belongs (see
     *                      {@link PageModel#application}).
     * @param pageModelName The name of the {@link PageModel} to retrieve (see
     *                      {@link PageModel#name}).
     *
     * @return A JSON object containing the data of the {@link PageModel}.
     *
     * @throws NotFoundException If there is not {@link CcmApplication} with the
     *                           primary URL {@code appPath} a
     *                           {@link NotFoundException} is thrown resulting
     *                           in a 404 response. A {@link NotFoundException}
     *                           is also thrown if there no {@link PageModel}
     *                           identified by {@code pageModelName} for the
     *                           {@link CcmApplication} with the primary URL
     *                           {@code appPath}.
     */
    @GET
    @Path(PageModelsApp.PAGE_MODEL_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject getPageModel(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);

        final CcmApplication app = controller
            .findCcmApplication(String.format("/%s/", appPath));
        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);
        return mapPageModelToJson(pageModel);
    }

    /**
     * Creates or updates a {@link PageModel}.
     *
     * If a {@link PageModel} with the name {@code pageModelName} already exists
     * for the {@link CcmApplication} with the primary URL {@code appPath} the
     * {@link PageModel} is updated. If there is no such {@link PageModel} a new
     * {@link PageModel} is created and associated with the
     * {@link CcmApplication} identified by the primary URL {@code appPath}.
     *
     *
     * @param appPath       The primary URL of the {@link CcmApplication} to
     *                      which the {@link PageModel} belongs.
     * @param pageModelName The name of the {@link PageModel}.
     * @param pageModelData The data for creating or updating the
     *                      {@link PageModel}.
     *
     * @return The new or updated {@link PageModel}.
     */
    @PUT
    @Path(PageModelsApp.PAGE_MODEL_PATH)
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject putPageModel(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        final JsonObject pageModelData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);

        final CcmApplication app = controller
            .findCcmApplication(String.format("/%s/", appPath));

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);

        final PageModel pageModel;
        if (controller.existsPageModel(app, pageModelName)) {
            pageModel = controller.findPageModel(app, pageModelName);
        } else {
            pageModel = new PageModel();
            pageModel.setApplication(app);
        }
        pageModel.setName(pageModelName);
        if (pageModelData.containsKey("title")) {
            pageModel.getTitle().addValue(kernelConfig.getDefaultLocale(),
                                          pageModelData.getString("title"));
        }
        if (pageModelData.containsKey("description")) {
            pageModel
                .getDescription()
                .addValue(kernelConfig.getDefaultLocale(),
                          pageModelData.getString("description"));
        }

        controller.savePageModel(pageModel);

        return mapPageModelToJson(controller.findPageModel(app, pageModelName));
    }

    /**
     * Deletes a {@link PageModel}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication} to
     *                      which the {@link PageModel} belongs.
     * @param pageModelName The name of the {@link PageModel} to delete.
     */
    @DELETE
    @Path(PageModelsApp.PAGE_MODEL_PATH)
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deletePageModel(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);

        final CcmApplication app = controller
            .findCcmApplication(String.format("/%s/", appPath));
        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);
        pageModelRepo.delete(pageModel);
    }

    /**
     * Helper method for mapping a {@link PageModel} object to JSON:
     *
     * @param pageModel The {@link PageModel} to map.
     *
     * @return A {@link JSON} object with the data of the provided
     *         {@link PageModel}.
     */
    private JsonObject mapPageModelToJson(final PageModel pageModel) {

        Objects.requireNonNull(pageModel);

        return Json
            .createObjectBuilder()
            .add("description",
                 globalizationHelper
                     .getValueFromLocalizedString(pageModel.getDescription()))
            .add("modelUuid", pageModel.getModelUuid())
            .add("name", pageModel.getName())
            .add("pageModelId", Long.toString(pageModel.getPageModelId()))
            .add("title",
                 globalizationHelper
                     .getValueFromLocalizedString(pageModel.getTitle()))
            .add("type", pageModel.getType())
            .add("uuid", pageModel.getUuid())
            .add("version", pageModel.getVersion().toString())
            .build();
    }

}
