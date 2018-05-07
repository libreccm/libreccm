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

import org.libreccm.core.CoreConstants;
import org.libreccm.pagemodel.ContainerModel;
import org.libreccm.pagemodel.ContainerModelRepository;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

import java.util.Optional;

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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
public class Containers {

    @Inject
    private PageModelsController controller;

    @Inject
    private ContainerModelRepository containerModelRepo;

    @Inject
    private PageModelManager pageModelManager;

    @Inject
    private PageModelRepository pageModelRepo;

    @GET
    @Path(PageModelsApp.CONTAINERS_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray getContainers(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName) {

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        pageModel
            .getContainers()
            .stream()
            .map(containerModel -> mapContainerModelToJson(containerModel))
            .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    @GET
    @Path(PageModelsApp.CONTAINER_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject getContainer(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey) {

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);

        return mapContainerModelToJson(container);
    }

    @PUT
    @Path(PageModelsApp.CONTAINER_PATH)
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject putContainer(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        final JsonObject containerModelJson) {

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final Optional<ContainerModel> result = pageModel
            .getContainers()
            .stream()
            .filter(model -> model.getKey().equals(containerKey))
            .findAny();

        final ContainerModel containerModel;
        if (result.isPresent()) {

            containerModel = result.get();

            result.get().setKey(containerKey);
            containerModelRepo.save(result.get());
        } else {

            containerModel = new ContainerModel();
            containerModel.setKey(containerKey);

            pageModelManager.addContainerModel(pageModel, containerModel);
        }

        return mapContainerModelToJson(containerModel);
    }

    @DELETE
    @Path(PageModelsApp.CONTAINER_PATH)
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteContainer(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey) {

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);

        pageModelManager.removeContainerModel(pageModel, container);
    }

    private JsonObject mapContainerModelToJson(
        final ContainerModel containerModel) {

        return Json
            .createObjectBuilder()
            .add("containerId", Long.toString(containerModel.getContainerId()))
            .add("uuid", containerModel.getUuid())
            .add("containerUuid", containerModel.getContainerUuid())
            .add("key", containerModel.getKey())
            .build();
    }

}
