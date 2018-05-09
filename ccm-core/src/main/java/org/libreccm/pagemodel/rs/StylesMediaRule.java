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
import org.libreccm.pagemodel.styles.MediaRule;
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
 * {@link MediaRule}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(StylesRs.MEDIA_RULE_PATH)
public class StylesMediaRule implements Serializable {

    private static final long serialVersionUID = 3257114872624583807L;

    protected final static String PROPERTY_ID = "propertyId";
    protected final static String RULE_ID = "ruleId";

    protected final static String RULES_PATH = "/rules";
    protected final static String RULE_PATH = RULES_PATH
                                                  + "/{"
                                                  + RULE_ID
                                                  + "}";
    protected final static String PROPERTIES_PATH = RULE_PATH + "/properties";
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
     * Retrieves all {@link Rule}s of a {@link MediaRule}.
     *
     * @param appPath          The path of the {@link CcmApplication} to which
     *                         the {@link PageModel} belongs.
     * @param pageModelName    The name of the {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param containerKey     The key of the {@link ContainerModel} to which
     *                         the {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule}.
     *
     * @return A JSON array with the JSON representations of all {@link Rule}s
     *         belonging the {@link MediaRule} identified by the provided path.
     */
    @GET
    @Path(RULES_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray getRules(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);
        return stylesJsonMapper.mapRulesToJson(mediaRule.getRules());
    }

    /**
     * Retrieves a specific {@link Rule} from a {@link MediaRule}.
     *
     * @param appPath          The path of the {@link CcmApplication} to which
     *                         the {@link PageModel} belongs.
     * @param pageModelName    The name of the {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param containerKey     The key of the {@link ContainerModel} to which
     *                         the {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule}.
     * @param ruleIdParam      The ID of the {@link Rule} to retrieve.
     *
     * @return The JSON representation of the {@link Rule} identified by the
     *         provided path.
     */
    @GET
    @Path(RULE_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject getRule(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam,
        @PathParam(RULE_ID) String ruleIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(ruleIdParam);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);

        return stylesJsonMapper.mapRuleToJson(findRule(mediaRule,
                                                       ruleIdParam));
    }

    /**
     * Creates a new {@link Rule} for a {@link MediaRule}.
     *
     * @param appPath          The path of the {@link CcmApplication} to which
     *                         the {@link PageModel} belongs.
     * @param pageModelName    The name of the {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param containerKey     The key of the {@link ContainerModel} to which
     *                         the {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule}.
     * @param ruleData         The data from which the new {@link Rule} is
     *                         created.
     *
     * @return The JSON representation of the new {@link Rule}.
     */
    @POST
    @Path(RULES_PATH)
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject createRule(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam,
        final JsonObject ruleData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(ruleData);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);

        final Rule rule = new Rule();
        rule.setSelector(ruleData.getString("selector"));
        stylesManager.addRuleToMediaRule(rule, mediaRule);

        return stylesJsonMapper.mapRuleToJson(rule);
    }

    /**
     * Updates an existing {@link Rule}
     *
     * @param appPath          The path of the {@link CcmApplication} to which
     *                         the {@link PageModel} belongs.
     * @param pageModelName    The name of the {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param containerKey     The key of the {@link ContainerModel} to which
     *                         the {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule}.
     * @param ruleIdParam      The ID of the {@link Rule} to update.
     * @param ruleData         The data from which used to update the
     *                         {@link Rule}.
     *
     * @return The JSON representation of the updated {@link Rule}.
     */
    @PUT
    @Path(RULE_PATH)
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject updateRule(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam,
        @PathParam(RULE_ID) String ruleIdParam,
        final JsonObject ruleData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(ruleData);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);

        final Rule rule = findRule(mediaRule, ruleIdParam);
        rule.setSelector(ruleData.getString("selector"));
        stylesManager.addRuleToMediaRule(rule, mediaRule);

        return stylesJsonMapper.mapRuleToJson(rule);
    }

    @DELETE
    @Path(RULE_PATH)
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteRule(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam,
        @PathParam(RULE_ID) String ruleIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(ruleIdParam);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);

        final Rule rule = findRule(mediaRule, ruleIdParam);

        stylesManager.removeRuleFromMediaRule(rule, mediaRule);
        stylesRepo.deleteRule(rule);
    }

    /**
     * Retrieves all {@link CssProperty} objects assigned to {@link Rule} which
     * is assigned to {@link MediaRule}.
     *
     * @param appPath          The path of the {@link CcmApplication} to which
     *                         the {@link PageModel} belongs.
     * @param pageModelName    The name of the {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param containerKey     The key of the {@link ContainerModel} to which
     *                         the {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule}.
     * @param ruleIdParam      The ID of the {@link Rule} from which the
     *                         {@link CssProperty} objects are retrieved.
     *
     * @return A JSON array with the JSON representations of the
     *         {@link CssProperty} objects.
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
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam,
        @PathParam(RULE_ID) String ruleIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(ruleIdParam);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);

        final Rule rule = findRule(mediaRule, ruleIdParam);

        return stylesJsonMapper.mapPropertiesToJson(rule.getProperties());
    }

    /**
     * Retrieve a {@link CssProperty} assigned to {@link Rule} which is assigned
     * to {@link MediaRule}.
     *
     * @param appPath          The path of the {@link CcmApplication} to which
     *                         the {@link PageModel} belongs.
     * @param pageModelName    The name of the {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param containerKey     The key of the {@link ContainerModel} to which
     *                         the {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule}.
     * @param ruleIdParam      The ID of the {@link Rule} from which the
     *                         {@link CssProperty} is retrieved.
     * @param propertyIdParam  The ID of the {@link CssProperty} to retrieve.
     *
     * @return The JSON representation of the {@link CssProperty} identified by
     *         the provided path.
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
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam,
        @PathParam(RULE_ID) final String ruleIdParam,
        @PathParam(PROPERTY_ID) final String propertyIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(propertyIdParam);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);

        final Rule rule = findRule(mediaRule, ruleIdParam);
        final CssProperty property = findProperty(rule, propertyIdParam);

        return stylesJsonMapper.mapCssPropertyToJson(property);
    }

    /**
     * Creates a new {@link CssProperty} for a {@link Rule} of a
     * {@link MediaRule}.
     *
     * @param appPath          The path of the {@link CcmApplication} to which
     *                         the {@link PageModel} belongs.
     * @param pageModelName    The name of the {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param containerKey     The key of the {@link ContainerModel} to which
     *                         the {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule}.
     * @param ruleIdParam      The ID of the {@link Rule} for which the new
     *                         {@link CssProperty} is created.
     * @param propertyData     The data from which the new {@link CssProperty}
     *                         is created.
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
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam,
        @PathParam(RULE_ID) String ruleIdParam,
        final JsonObject propertyData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(propertyData);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);

        final Rule rule = findRule(mediaRule, ruleIdParam);

        final CssProperty property = new CssProperty();
        setCssPropertyData(property, propertyData);
        stylesManager.addCssPropertyToRule(property, rule);

        return stylesJsonMapper.mapCssPropertyToJson(property);
    }

    /**
     * Updates an existing {@link CssProperty} of {@link Rule} of a
     * {@link MediaRule}.
     *
     * @param appPath          The path of the {@link CcmApplication} to which
     *                         the {@link PageModel} belongs.
     * @param pageModelName    The name of the {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param containerKey     The key of the {@link ContainerModel} to which
     *                         the {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule}.
     * @param ruleIdParam      The ID of the {@link Rule} to which the
     *                         {@link CssProperty} belongs.
     * @param propertyIdParam  The ID of the {@link CssProperty} to update.
     * @param propertyData     The data which is used to update the
     *                         {@link CssProperty}.
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
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam,
        @PathParam(RULE_ID) String ruleIdParam,
        @PathParam(PROPERTY_ID) final String propertyIdParam,
        final JsonObject propertyData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(propertyIdParam);
        Objects.requireNonNull(propertyData);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);

        final Rule rule = findRule(mediaRule, ruleIdParam);

        final CssProperty property = findProperty(rule, propertyIdParam);
        setCssPropertyData(property, propertyData);
        stylesRepo.saveCssProperty(property);

        return stylesJsonMapper.mapCssPropertyToJson(property);
    }

    /**
     * Deletes a {@link CssProperty} of a {@link Rule} assigned to a
     * {@link MediaRule}.
     *
     * @param appPath          The path of the {@link CcmApplication} to which
     *                         the {@link PageModel} belongs.
     * @param pageModelName    The name of the {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param containerKey     The key of the {@link ContainerModel} to which
     *                         the {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule}.
     * @param ruleIdParam      The ID of the {@link Rule} to which the
     *                         {@link CssProperty} belongs.
     * @param propertyIdParam  The ID of the {@link CssProperty} to delete.
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
        @PathParam(StylesRs.MEDIA_RULE_ID) final String mediaRuleIdParam,
        @PathParam(RULE_ID) String ruleIdParam,
        @PathParam(PROPERTY_ID) final String propertyIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(propertyIdParam);

        final MediaRule mediaRule = stylesRs.findMediaRule(appPath,
                                                           pageModelName,
                                                           containerKey,
                                                           mediaRuleIdParam);

        final Rule rule = findRule(mediaRule, ruleIdParam);

        final CssProperty property = findProperty(rule, propertyIdParam);
        stylesManager.removeCssPropertyFromRule(property, rule);
        stylesRepo.deleteCssProperty(property);
    }

    /**
     * Helper method for finding a {@link CssProperty}.
     *
     * @param rule            The {@link Rule} to which the {@link CssProperty}
     *                        belongs.
     * @param propertyIdParam The ID of the {@link CssProperty} to find.
     *
     * @return The {@link CssProperty}.
     */
    private CssProperty findProperty(final Rule rule,
                                     final String propertyIdParam) {

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
     * Helper method for finding a {@link Rule} assigned to {@link MediaRule}.
     *
     * @param mediaRule   The {@link MediaRule}.
     * @param ruleIdParam The ID of {@link Rule} to find.
     *
     * @return The {@link Rule}.
     */
    private Rule findRule(final MediaRule mediaRule,
                          final String ruleIdParam) {

        final long ruleId;
        try {
            ruleId = Long.parseLong(ruleIdParam);
        } catch (NumberFormatException ex) {
            throw new WebApplicationException(ex);
        }

        Objects.requireNonNull(mediaRule);

        return mediaRule
            .getRules()
            .stream()
            .filter(rule -> ruleId == rule.getRuleId())
            .findAny()
            .orElseThrow(() -> new NotFoundException());
    }

    /**
     * Helper method for updating the values of the properties of
     * {@link CssProperty} object from its JSON representation.
     *
     * @param property     The {@link CssProperty}.
     * @param propertyData The {@link JsonObject} containing the data.
     */
    private void setCssPropertyData(final CssProperty property,
                                    final JsonObject propertyData) {

        Objects.requireNonNull(property);
        Objects.requireNonNull(propertyData);

        property.setName(propertyData.getString("name"));
        property.setValue(propertyData.getString("value"));
    }

}
