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
import org.libreccm.pagemodel.styles.CssProperty;
import org.libreccm.pagemodel.styles.Rule;
import org.libreccm.pagemodel.styles.StylesManager;
import org.libreccm.pagemodel.styles.StylesRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

/**
 * Provides RESTful endpoints for retrieving, creating, updating and deleting
 * {@link Rule} objects.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(StylesRs.RULE_PATH)
public class StylesRule implements Serializable {

    private static final long serialVersionUID = -8447970787677773230L;

    protected final static String PROPERTY_ID = "propertyId";

    protected final static String PROPERTIES_PATH = "/properties";
    protected final static String PROPERTY_PATH = PROPERTIES_PATH
                                                      + "/{"
                                                      + PROPERTY_ID
                                                      + "}";

    @Inject
    private StylesJsonMapper stylesJsonMapper;

    @Inject
    private StylesManager stylesManager;

    @Inject
    private StylesRepository stylesRepo;

    @Inject
    private StylesRs stylesRs;

    /**
     * Retrieves all {@link CssProperty} objects of a {@link Rule} assigned to
     * the {@link Styles} entity of a {@link ContainerModel}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication} to
     *                      which the {@link PageModel} belongs.
     * @param pageModelName The name of the {@link PageModel} to which the
     *                      {@link ContainerModel} belongs.
     * @param containerKey  The key of the {@link ContainerModel} to which the
     *                      {@link Rule} belongs.
     * @param ruleIdParam   The ID of the {@link Rule} from which the
     *                      {@link CssProperty} objects are retrieved.
     *
     * @return A JSON array with the JSON representation of the
     *         {@link CssProperty} objects of the {@link Rule} identified by the
     *         provided path.
     */
    @GET
    @Path(PROPERTIES_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray getProperties(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.RULE_ID) final String ruleIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(ruleIdParam);

        final Rule rule = stylesRs.findRule(appPath,
                                            pageModelName,
                                            containerKey,
                                            ruleIdParam);
        return stylesJsonMapper.mapPropertiesToJson(rule.getProperties());
    }

    /**
     * Retrieves a specific {@link CssProperty} from a {@link Rule} assigned to
     * the {@link Styles} entity of a {@link ContainerModel}.
     *
     * @param appPath         The primary URL of the {@link CcmApplication} to
     *                        which the {@link PageModel} belongs.
     * @param pageModelName   The name of the {@link PageModel} to which the
     *                        {@link ContainerModel} belongs.
     * @param containerKey    The key of the {@link ContainerModel} to which the
     *                        {@link Rule} belongs.
     * @param ruleIdParam     The ID of the {@link Rule} to which the
     *                        {@link CssProperty} is assigned.
     * @param propertyIdParam The ID of the {@link CssProperty} to retrieve.
     *
     * @return The JSON representation of the {@link CssProperty}.
     */
    @GET
    @Path(PROPERTY_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject getProperty(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.RULE_ID) final String ruleIdParam,
        @PathParam(PROPERTY_ID) final String propertyIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(propertyIdParam);

        final Rule rule = stylesRs.findRule(appPath,
                                            pageModelName,
                                            containerKey,
                                            ruleIdParam);

        return stylesJsonMapper
            .mapCssPropertyToJson(findProperty(rule,
                                               propertyIdParam));
    }

    /**
     * Creates a new {@link CssProperty} for a {@link Rule} assigned to the
     * {@link Styles} entity of a {@link ContainerModel}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication} to
     *                      which the {@link PageModel} belongs.
     * @param pageModelName The name of the {@link PageModel} to which the
     *                      {@link ContainerModel} belongs.
     * @param containerKey  The key of the {@link ContainerModel} to which the
     *                      {@link Rule} belongs.
     * @param ruleIdParam   The ID of the {@link Rule} to which the
     *                      {@link CssProperty} is assigned.
     * @param propertyData  The data used to create the new {@link CssProperty}.
     *
     * @return The JSON representation of the new {@link CssProperty}.
     */
    @POST
    @Path(PROPERTIES_PATH)
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject createProperty(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.RULE_ID) final String ruleIdParam,
        final JsonObject propertyData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(propertyData);

        final Rule rule = stylesRs.findRule(appPath,
                                            pageModelName,
                                            containerKey,
                                            ruleIdParam);

        final CssProperty property = new CssProperty();
        setCssPropertyData(property, propertyData);
        stylesManager.addCssPropertyToRule(property, rule);

        return stylesJsonMapper.mapCssPropertyToJson(property);
    }

    /**
     * Updates an existing {@link CssProperty} for a {@link Rule} assigned to
     * the {@link Styles} entity of a {@link ContainerModel}.
     *
     * @param appPath         The primary URL of the {@link CcmApplication} to
     *                        which the {@link PageModel} belongs.
     * @param pageModelName   The name of the {@link PageModel} to which the
     *                        {@link ContainerModel} belongs.
     * @param containerKey    The key of the {@link ContainerModel} to which the
     *                        {@link Rule} belongs.
     * @param ruleIdParam     The ID of the {@link Rule} to which the
     *                        {@link CssProperty} is assigned.
     * @param propertyIdParam The ID of the {@link CssProperty} to update.
     * @param propertyData    The data used to update the {@link CssProperty}.
     *
     * @return The JSON representation of the updated {@link CssProperty}.
     */
    @PUT
    @Path(PROPERTY_PATH)
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject updateProperty(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.RULE_ID) final String ruleIdParam,
        @PathParam(PROPERTY_ID) final String propertyIdParam,
        final JsonObject propertyData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(propertyIdParam);
        Objects.requireNonNull(propertyData);

        final Rule rule = stylesRs.findRule(appPath,
                                            pageModelName,
                                            containerKey,
                                            ruleIdParam);

        final CssProperty property = findProperty(rule, propertyIdParam);
        setCssPropertyData(property, propertyData);
        stylesRepo.saveCssProperty(property);

        return stylesJsonMapper.mapCssPropertyToJson(property);
    }

    /**
     * Deletes{@link CssProperty} for a {@link Rule} assigned to the
     * {@link Styles} entity of a {@link ContainerModel}.
     *
     * @param appPath         The primary URL of the {@link CcmApplication} to
     *                        which the {@link PageModel} belongs.
     * @param pageModelName   The name of the {@link PageModel} to which the
     *                        {@link ContainerModel} belongs.
     * @param containerKey    The key of the {@link ContainerModel} to which the
     *                        {@link Rule} belongs.
     * @param ruleIdParam     The ID of the {@link Rule} to which the
     *                        {@link CssProperty} is assigned.
     * @param propertyIdParam The ID of the {@link CssProperty} to delete.
     */
    @DELETE
    @Path(PROPERTY_PATH)
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteProperty(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.RULE_ID) final String ruleIdParam,
        @PathParam(PROPERTY_ID) final String propertyIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(propertyIdParam);

        final Rule rule = stylesRs.findRule(appPath,
                                            pageModelName,
                                            containerKey,
                                            ruleIdParam);

        final CssProperty property = findProperty(rule, propertyIdParam);
        stylesManager.removeCssPropertyFromRule(property, rule);
        stylesRepo.deleteCssProperty(property);
    }

    /**
     * Helper method for finding a {@link CssProperty} assigned to {@link Rule}.
     *
     * @param rule            The {@link Rule}.
     * @param propertyIdParam The ID of the {@link CssProperty} to find.
     *
     * @return The {@link CssProperty} identified by {@code propertyIdParam}.
     */
    private CssProperty findProperty(final Rule rule,
                                     final String propertyIdParam) {

        Objects.requireNonNull(rule);
        Objects.requireNonNull(propertyIdParam);

        final long propertyId;
        try {
            propertyId = Long.parseLong(propertyIdParam);
        } catch (NumberFormatException ex) {
            throw new WebApplicationException(ex);
        }

        return rule
            .getProperties()
            .stream()
            .filter(property -> propertyId == property.getPropertyId())
            .findAny()
            .orElseThrow(() -> new NotFoundException());
    }

    /**
     * Helper method for updating a {@link CssProperty} object with data from
     * its JSON representation.
     *
     * @param property     The {@link CssProperty}.
     * @param propertyData The data.
     */
    private void setCssPropertyData(final CssProperty property,
                                    final JsonObject propertyData) {

        Objects.requireNonNull(property);
        Objects.requireNonNull(propertyData);

        property.setName(propertyData.getString("name"));
        property.setValue(propertyData.getString("value"));
    }

}
