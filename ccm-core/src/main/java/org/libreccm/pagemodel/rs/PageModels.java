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
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ComponentModelRepository;
import org.libreccm.pagemodel.ContainerModel;
import org.libreccm.pagemodel.ContainerModelRepository;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/{appPath}")
public class PageModels {

    @Inject
    private ApplicationRepository appRepo;

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
//    public List<Map<String, String>> findAllPageModels(
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
    @Path("/")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray findAllPageModels(
        @PathParam("appPath") String appPath) {

        final CcmApplication app = findCcmApplication(
            String.format("/%s/", appPath));
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        pageModelRepo
            .findDraftByApplication(app)
            .stream()
            .map(this::mapPageModelToJson)
            .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    @GET
//    @Path("/{appPath}/{pageModelName}")
    @Path("/{pageModelName}")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject findPageModel(
        @PathParam("appPath") final String appPath,
        @PathParam("pageModelName") final String pageModelName) {

        final CcmApplication app = findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = pageModelRepo
            .findDraftByApplicationAndName(app, pageModelName)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No PageModel with name \"%s\" for application \"%s\".",
            pageModelName, appPath)));
        return mapPageModelToJson(pageModel);
    }

    @GET
//    @Path("/{appPath}/{pageModelName}/containers")
    @Path("/{pageModelName}/containers")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray getContainers(
        @PathParam("appPath") final String appPath,
        @PathParam("pageModelName") final String pageModelName) {

        final CcmApplication app = findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = pageModelRepo
            .findDraftByApplicationAndName(app, pageModelName)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No PageModel with name \"%s\" for application \"%s\".",
            pageModelName, appPath)));

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        pageModel
            .getContainers()
            .stream()
            .map(containerModel -> mapContainerModelToJson(containerModel))
            .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    @GET
    @Path("/{pageModelName}/containers/{key}")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject getContainer(
        @PathParam("appPath") final String appPath,
        @PathParam("pageModelName") final String pageModelName,
        @PathParam("key") final String key) {

        final CcmApplication app = findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = pageModelRepo
            .findDraftByApplicationAndName(app, pageModelName)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No PageModel with name \"%s\" for application \"%s\".",
            pageModelName, appPath)));

        final ContainerModel container = containerRepo
            .findContainerByKeyAndPageModel(key, pageModel)
            .orElseThrow(() -> new NotFoundException(String
            .format("The PageModel \"%s\" of application \"%s\" does not have "
                        + "a container identified by the key \"%s\".",
                    pageModelName,
                    appPath,
                    key)));

        return mapContainerModelToJson(container);
    }

    @GET
    @Path("/{pageModelName}/containers/{key}/components")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray getComponents(
        @PathParam("appPath") final String appPath,
        @PathParam("pageModelName") final String pageModelName,
        @PathParam("key") final String key) {

        final CcmApplication app = findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = pageModelRepo
            .findDraftByApplicationAndName(app, pageModelName)
            .orElseThrow(() -> new NotFoundException(String.format(
            "No PageModel with name \"%s\" for application \"%s\".",
            pageModelName, appPath)));

        final ContainerModel container = containerRepo
            .findContainerByKeyAndPageModel(key, pageModel)
            .orElseThrow(() -> new NotFoundException(String
            .format("The PageModel \"%s\" of application \"%s\" does not have "
                        + "a container identified by the key \"%s\".",
                    pageModelName,
                    appPath,
                    key)));

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        container
            .getComponents()
            .stream()
            .map(component -> mapComponentModelToJson(component))
            .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    private CcmApplication findCcmApplication(final String appPath) {

        return appRepo
            .retrieveApplicationForPath(appPath)
            .orElseThrow(() -> new NotFoundException(String
            .format("No application with path \"%s\" found.",
                    appPath)));
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

    private Map<String, String> mapPageModelToDataMap(final PageModel pageModel) {

        final Map<String, String> dataMap = new HashMap<>();

        dataMap.put("description",
                    globalizationHelper
                        .getValueFromLocalizedString(pageModel.getDescription()));
        dataMap.put("modelUuid", pageModel.getModelUuid());
        dataMap.put("name", pageModel.getName());
        dataMap.put("pageModelId", Long.toString(pageModel.getPageModelId()));
        dataMap.put("title",
                    globalizationHelper
                        .getValueFromLocalizedString(pageModel.getTitle()));
        dataMap.put("type", pageModel.getType());
        dataMap.put("uuid", pageModel.getUuid());
        dataMap.put("version", pageModel.getVersion().toString());

        return dataMap;
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

    private JsonObject mapComponentModelToJson(
        final ComponentModel componentModel) {

        final JsonObjectBuilder objectBuilder = Json
            .createObjectBuilder()
            .add("componentModelId",
                 Long.toString(componentModel.getComponentModelId()))
            .add("uuid", componentModel.getUuid())
            .add("modelUuid", componentModel.getModelUuid())
            .add("key", componentModel.getKey())
            .add("type", componentModel.getClass().getName());

        if (componentModel.getIdAttribute() != null) {
            objectBuilder.add("idAttribute", componentModel.getIdAttribute());
        }

        if (componentModel.getClassAttribute() != null) {
            objectBuilder.add("classAttribute",
                              componentModel.getClassAttribute());
        }

        if (componentModel.getStyleAttribute() != null) {
            objectBuilder.add("styleAttribute",
                              componentModel.getStyleAttribute());
        }

        return objectBuilder.build();
    }

}
