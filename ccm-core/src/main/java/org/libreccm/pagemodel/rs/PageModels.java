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
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.pagemodel.ComponentModelRepository;
import org.libreccm.pagemodel.ContainerModelRepository;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
public class PageModels {

    @Inject
    private PageModelsController controller;

    @Inject
    private ComponentModelRepository componentModelRepo;

    @Inject
    private ContainerModelRepository containerRepo;

    @Inject
    private PageModelRepository pageModelRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

//    @GET
//    @Path("/{appPath}")
//    @Produces("application/json; charset=utf-8")
//    @Transactional(Transactional.TxType.REQUIRED)
//    @AuthorizationRequired
//    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
//    public List<Map<String, String>> getAllPageModels(
//        @PathParam("appPath") String appPath) {
//
//        final CcmApplication app = findCcmApplication(
//            String.format("/%s/", appPath));
//        return pageModelRepo
//            .findDraftByApplication(app)
//            .stream()
//            .map(this::mapPageModelToDataMap)
//            .collect(Collectors.toList());
//    }
    @GET
//    @Path("/{appPath}")
    @Path(PageModelsApp.PAGE_MODELS_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray getAllPageModels(
        @PathParam(PageModelsApp.APP_NAME) String appPath) {

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

    @GET
    @Path(PageModelsApp.PAGE_MODEL_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject getPageModel(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName) {

        final CcmApplication app = controller
            .findCcmApplication(String.format("/%s/", appPath));
        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);
        return mapPageModelToJson(pageModel);
    }

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
        final JsonObject pageModelJson) {

        final CcmApplication app = controller
            .findCcmApplication(String.format("/%s/", appPath));

        final PageModel pageModel;
        if (controller.existsPageModel(app, pageModelName)) {
            pageModel = controller.findPageModel(app, pageModelName);
        } else {
            pageModel = new PageModel();
            pageModel.setApplication(app);
        }
        pageModel.setName(pageModelName);

        controller.savePageModel(pageModel);

        return mapPageModelToJson(controller.findPageModel(app, pageModelName));
    }

    @DELETE
    @Path(PageModelsApp.PAGE_MODEL_PATH)
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deletePageModel(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName) {

        final CcmApplication app = controller
        .findCcmApplication(String.format("/%s/", appPath));
        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);
        pageModelRepo.delete(pageModel);
    }

    private JsonObject mapPageModelToJson(final PageModel pageModel) {

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
