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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

/**
 * Provides RESTful endpoints for retrieving, creating, updating and deleting
 * {@link ComponentModel}s of a {@link ContainerModel}.
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

    /**
     * Retrieve all {@link ComponentModel} of a {@link ContainerModel}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     *
     * @return A JSON array containing the data of all {@link ComponentModel} of
     *         the {@link ContainerModel}.
     */
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

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);

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

    /**
     * Retrieves a specific {@link ComponentModel}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     * @param componentKey  The key of the {@link ComponentModel}.
     *
     * @return A JSON object containing the data of the {@link ComponentModel}.
     */
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

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(componentKey);

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

    /**
     * Creates or updates a {@link ComponentModel}.
     *
     * If a {@link ComponentModel} with provided {@code componentKey} already
     * exists in the container identified by {@code appPath},
     * {@code pageModelName} and {@code containerKey} the {@link ComponentModel}
     * is updated with the data from {@code componentModelData}.
     *
     * Otherwise a new {@link ComponentModel} is created using the data from
     * {@code componentModelData}.
     *
     * @param appPath            The primary URL of the {@link CcmApplication}.
     * @param pageModelName      The name of the {@link PageModel}.
     * @param containerKey       The key of the {@link ContainerModel}.
     * @param componentKey       The key of the {@link ComponentModel} to create
     *                           or update.
     * @param componentModelData The data for creating or updating the
     *                           {@link ComponentModel}.
     *
     * @return The new or updated {@link ComponentModel}.
     */
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
        final JsonObject componentModelData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(componentKey);
        Objects.requireNonNull(componentModelData);

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

            componentModel = createComponentModel(componentModelData);
            componentModel.setKey(componentKey);
            containerManager.addComponentModel(container, componentModel);
        }

        setComponentPropertiesFromJson(componentModelData, componentModel);

        componentRepo.save(componentModel);

        return mapComponentModelToJson(componentModel);
    }

    /**
     * Deletes a {@link ComponentModel}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     * @param componentKey  The key of the {@link ComponentModel} to delete.
     *
     */
    @DELETE
    @Path(PageModelsApp.COMPONENT_PATH)
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteComponent(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(PageModelsApp.COMPONENT_KEY) final String componentKey) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(componentKey);

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));
        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);
        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);
        final ComponentModel component = controller
            .findComponentModel(app, pageModel, container, componentKey);

        containerManager.removeComponentModel(container, component);
    }

    /**
     * Helper method for mapping a {@link ComponentModel} to JSON.
     *
     * @param componentModel The {@link ComponentModel} to map.
     *
     * @return The JSON representation of the
     *         {@link ComponentModel} {@code componentModel}.
     */
    private JsonObject mapComponentModelToJson(
        final ComponentModel componentModel) {

        Objects.requireNonNull(componentModel);

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

        final Class<? extends ComponentModel> clazz = componentModel.getClass();
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException ex) {
            throw new WebApplicationException(ex);
        }

        for (final PropertyDescriptor propertyDescriptor
                 : beanInfo.getPropertyDescriptors()) {

            final Method readMethod = propertyDescriptor.getReadMethod();
            final Object value;
            try {
                value = readMethod.invoke(componentModel);
            } catch (IllegalAccessException
                     | InvocationTargetException ex) {
                throw new WebApplicationException(ex);
            }
            
            final String valueStr;
            if (value == null) {
                valueStr = "";
            } else {
                valueStr = value.toString();
            }

            objectBuilder.add(propertyDescriptor.getName(), valueStr);

        }

        return objectBuilder.build();
    }

    /**
     * Creates a new {@link ComponentModel} instance.
     *
     * Uses reflection and the value of {@code type} property from the JSON
     * {@code data} to determine the correct class.
     *
     * @param data The data from which the new {@link ComponentModel} is
     *             created.
     *
     * @return The new {@link ComponentModel}.
     */
    private ComponentModel createComponentModel(final JsonObject data) {

        Objects.requireNonNull(data);

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

    /**
     * Helper method for finding the correct subclass of {@link ComponentModel}
     * using the fully qualified name the class.
     *
     * @param type The fully qualified name of the subclass of
     *             {@link ComponentModel}.
     *
     * @return The subclass of {@link ComponentModel}.
     *
     * @throws BadRequestException If there is no subclass of
     *                             {@link ComponentModel} with the fully
     *                             qualified name provided by the {@code type}
     *                             parameter.
     */
    @SuppressWarnings("unchecked")
    private Class<? extends ComponentModel> findComponentModelClass(
        final String type) {

        Objects.requireNonNull(type);;

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

    /**
     * Helper method for setting the properties of a {@link ComponentModel} from
     * the JSON data.
     *
     * @param data           The JSON data.
     * @param componentModel The {@link ComponentModel}.
     */
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

    /**
     * Helper emthod for setting a property of a {@link ComponentModel} using a
     * value from JSON data.
     *
     * @param componentModel The {@link ComponentModel}
     * @param propertyDesc   The {@link PropertyDescriptor} for the property to
     *                       set.
     * @param data           The JSON data containing the new value of the
     *                       property.
     */
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
