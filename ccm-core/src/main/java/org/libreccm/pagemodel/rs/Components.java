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
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.ComponentModelRepository;
import org.libreccm.pagemodel.ContainerModel;
import org.libreccm.pagemodel.ContainerModelManager;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path("/")
public class Components {

    @Inject
    private ComponentModelRepository componentRepo;

    @Inject
    private ContainerModelManager containerManager;

    @Inject
    private PageModelsController controller;

    @GET
    @Path(PageModelsApp.COMPONENTS_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray getComponents(
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

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        container
            .getComponents()
            .stream()
            .map(component -> mapComponentModelToJson(component))
            .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    @GET
    @Path(PageModelsApp.COMPONENT_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject getComponent(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(PageModelsApp.COMPONENT_KEY) final String componentKey) {

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));
        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);
        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);
        final ComponentModel component = controller
            .findComponentModel(app, pageModel, container, componentKey);

        return mapComponentModelToJson(component);
    }

    @PUT
    @Path(PageModelsApp.COMPONENT_PATH)
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject putComponent(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(PageModelsApp.COMPONENT_KEY) final String componentKey,
        final JsonObject componentModelJson) {

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));
        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);
        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);

        final Optional<ComponentModel> result = container
            .getComponents()
            .stream()
            .filter(c -> c.getKey().equals(componentKey))
            .findAny();

        final ComponentModel componentModel;
        if (result.isPresent()) {

            componentModel = result.get();

        } else {

            componentModel = createComponentModel(componentModelJson);
            componentModel.setKey(componentKey);
            containerManager.addComponentModel(container, componentModel);
        }

        setComponentPropertiesFromJson(componentModelJson, componentModel);

        componentRepo.save(componentModel);

        return mapComponentModelToJson(componentModel);
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

    private ComponentModel createComponentModel(final JsonObject data) {

        if (!data.containsKey("type")) {
            throw new BadRequestException("The JSON data for creating the "
                                              + "component has no value for the type of the component to "
                                          + "create.");
        }

        final String type = data.getString("type");
        final Class<? extends ComponentModel> clazz = findComponentModelClass(
            type);

        final ComponentModel componentModel;
        try {
            componentModel = clazz.getConstructor().newInstance();
        } catch (IllegalAccessException
                 | InstantiationException
                 | InvocationTargetException
                 | NoSuchMethodException ex) {
            throw new WebApplicationException(ex);
        }

        return componentModel;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends ComponentModel> findComponentModelClass(
        final String type) {
        try {
            final Class<?> clazz = Class.forName(type);

            if (clazz.isAssignableFrom(ComponentModel.class)) {
                return (Class<? extends ComponentModel>) clazz;
            } else {
                throw new BadRequestException(String.format(
                    "The type \"%s\" is not a subclass of \"%s\".",
                    type,
                    ComponentModel.class.getName()));
            }
        } catch (ClassNotFoundException ex) {
            throw new BadRequestException(String.format(
                "The component model type \"%s\" "
                    + "does not exist.",
                type));
        }
    }

    private void setComponentPropertiesFromJson(
        final JsonObject data,
        final ComponentModel componentModel) {

        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(componentModel.getClass());
        } catch (IntrospectionException ex) {
            throw new WebApplicationException(ex);
        }

        Arrays
            .stream(beanInfo.getPropertyDescriptors())
            .forEach(
                propertyDesc -> setComponentPropertyFromJson(componentModel,
                                                             propertyDesc,
                                                             data));
    }

    private void setComponentPropertyFromJson(
        final ComponentModel componentModel,
        final PropertyDescriptor propertyDesc,
        final JsonObject data) {

        // Ignore key and type (handled by other methods).
        if ("key".equals(propertyDesc.getName())
                || "type".equals(propertyDesc.getName())) {

            return;
        }

        if (data.containsKey(propertyDesc.getName())) {

            final Method writeMethod = propertyDesc.getWriteMethod();

            if (writeMethod != null) {
                try {
                    writeMethod.invoke(componentModel,
                                       data.getString(propertyDesc.getName()));
                } catch (IllegalAccessException
                         | InvocationTargetException ex) {
                    throw new WebApplicationException(ex);
                }
            }
        }
    }

}
