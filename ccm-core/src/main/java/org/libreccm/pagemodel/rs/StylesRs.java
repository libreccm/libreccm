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
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.styles.Dimension;
import org.libreccm.pagemodel.styles.MediaRule;
import org.libreccm.pagemodel.styles.MediaType;
import org.libreccm.pagemodel.styles.Rule;
import org.libreccm.pagemodel.styles.Styles;
import org.libreccm.pagemodel.styles.StylesManager;
import org.libreccm.pagemodel.styles.StylesRepository;
import org.libreccm.pagemodel.styles.Unit;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

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
 * Provides RESTful endpoints for managing the (CSS) styles of a
 * {@link ContainerModel}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Path(PageModelsApp.STYLES_PATH)
public class StylesRs {

    protected static final String MEDIA_RULE_ID = "mediaRuleId";
    protected static final String RULE_ID = "ruleId";

    protected static final String MEDIA_RULES_PATH = "/media-rules";
    protected static final String MEDIA_RULE_PATH = MEDIA_RULES_PATH
                                                        + "/{"
                                                        + MEDIA_RULE_ID
                                                        + "}";
    protected static final String RULES_PATH = "/rules";
    protected static final String RULE_PATH = RULES_PATH
                                                  + "/{"
                                                  + RULE_ID
                                                  + "}";

    @Inject
    private PageModelsController controller;

    @Inject
    private StylesJsonMapper stylesJsonMapper;

    @Inject
    private StylesManager stylesManager;

    @Inject
    private StylesRepository stylesRepo;

    /**
     * Retrieves all {@link MediaRule}s from the {@link Styles} entity of a
     * {@link ContainerModel}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     *
     * @return The JSON Array with the JSON representations of all
     *         {@link MediaRule}s of the {@link ContainerModel} identified by
     *         the provided path.
     */
    @GET
    @Path(MEDIA_RULES_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonArray getMediaRules(
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

        final Styles styles = container.getStyles();

        return stylesJsonMapper.mapMediaRulesToJson(styles.getMediaRules());

    }

    /**
     * Retrieves a specific {@link MediaRule} from the {@link Styles} entity of
     * a {@link ContainerModel}.
     *
     * @param appPath          The primary URL of the {@link CcmApplication}.
     * @param pageModelName    The name of the {@link PageModel}.
     * @param containerKey     The key of the {@link ContainerModel}.
     * @param mediaRuleIdParam The ID of the {@link MediaRule} to retrieve.
     *
     * @return The JSON representation of the {@link MediaRule}.
     */
    @GET
    @Path(MEDIA_RULE_PATH)
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject getMediaRule(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(MEDIA_RULE_ID) final String mediaRuleIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);

        return stylesJsonMapper
            .mapMediaRuleToJson(findMediaRule(appPath,
                                              pageModelName,
                                              containerKey,
                                              mediaRuleIdParam));
    }

    /**
     * Creates a new {@link MediaRule}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     * @param mediaRuleData The data for the new {@link MediaRule}.
     *
     * @return The JSON representation of the new {@link MediaRule}.
     */
    @POST
    @Path(MEDIA_RULES_PATH)
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject createMediaRule(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        final JsonObject mediaRuleData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleData);

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);
        final Styles styles = container.getStyles();

        final MediaRule mediaRule = new MediaRule();
        setMediaRuleProperties(mediaRuleData, mediaRule);
        stylesManager.addMediaRuleToStyles(mediaRule, styles);

        return stylesJsonMapper.mapMediaRuleToJson(mediaRule);
    }

    /**
     * Update a {@link MediaRule}.
     *
     * @param appPath          The primary URL of the {@link CcmApplication}.
     * @param pageModelName    The name of the {@link PageModel}.
     * @param containerKey     The key of the {@link ContainerModel}.
     * @param mediaRuleIdParam The ID of the {@link MediaRule} to update.
     * @param mediaRuleData    The data for updating the {@link MediaRule}.
     *
     * @return The JSON representation of the updated {@link MediaRule}.
     */
    @PUT
    @Path(MEDIA_RULE_PATH)
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public JsonObject updateMediaRule(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(MEDIA_RULE_ID) final String mediaRuleIdParam,
        final JsonObject mediaRuleData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);
        Objects.requireNonNull(mediaRuleData);

        final MediaRule mediaRule = findMediaRule(appPath,
                                                  pageModelName,
                                                  containerKey,
                                                  mediaRuleIdParam);
        setMediaRuleProperties(mediaRuleData, mediaRule);
        stylesRepo.saveMediaRule(mediaRule);

        return stylesJsonMapper.mapMediaRuleToJson(mediaRule);
    }

    /**
     * Deletes a {@link MediaRule}.
     *
     * @param appPath          The primary URL of the {@link CcmApplication}.
     * @param pageModelName    The name of the {@link PageModel}.
     * @param containerKey     The key of the {@link ContainerModel}.
     * @param mediaRuleIdParam The ID of the {@link MediaRule} to delete.
     */
    @DELETE
    @Path(MEDIA_RULE_PATH)
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteMediaRule(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(MEDIA_RULE_ID) final String mediaRuleIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(mediaRuleIdParam);

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);

        final Styles styles = container.getStyles();

        final MediaRule mediaRule = findMediaRule(pageModel,
                                                  container,
                                                  mediaRuleIdParam);
        stylesManager.removeMediaRuleFromStyles(mediaRule, styles);
    }

    /**
     * Retrieves all {@link Rule}s from the {@link Styles} entity of a
     * {@link ContainerModel}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     *
     * @return A JSON array with the JSON representation of all {@link Rule}s of
     *         the {@link ContainerModel}.
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

        final Styles styles = container.getStyles();

        return stylesJsonMapper.mapRulesToJson(styles.getRules());
    }

    /**
     * Retrieves a specific {@link Rule} from the {@link Styles} entity of a
     * {@link ContainerModel}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     * @param ruleIdParam   The ID of the {@link Rule} to retrieve.
     *
     * @return The JSON representation of the {@link Rule}.
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
        @PathParam(RULE_ID) final String ruleIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(ruleIdParam);

        return stylesJsonMapper
            .mapRuleToJson(findRule(appPath,
                                    pageModelName,
                                    containerKey,
                                    ruleIdParam));
    }

    /**
     * Creates a new {@link Rule}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     * @param ruleData      The data for the new {@link Rule}.
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
        final JsonObject ruleData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(ruleData);

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);
        final Styles styles = container.getStyles();

        final Rule rule = new Rule();
        rule.setSelector(ruleData.getString("selector"));
        stylesManager.addRuleToStyles(rule, styles);

        return stylesJsonMapper.mapRuleToJson(rule);
    }

    /**
     * Updates an existing {@link Rule}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     * @param ruleIdParam   The ID of the {@link Rule} to update.
     * @param ruleData      The data for updating the {@link Rule}.
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
        @PathParam(RULE_ID) final String ruleIdParam,
        final JsonObject ruleData) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(ruleIdParam);
        Objects.requireNonNull(ruleData);

        final Rule rule = findRule(appPath,
                                   pageModelName,
                                   containerKey,
                                   ruleIdParam);
        rule.setSelector(ruleData.getString("selector"));
        stylesRepo.saveRule(rule);
        return stylesJsonMapper.mapRuleToJson(rule);
    }

    /**
     * Deletes a {@link Rule}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     * @param ruleIdParam   The ID of the {@link Rule} to delete.
     */
    @DELETE
    @Path(RULE_PATH)
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public void deleteRule(
        @PathParam(PageModelsApp.APP_NAME) final String appPath,
        @PathParam(PageModelsApp.PAGE_MODEL_NAME) final String pageModelName,
        @PathParam(PageModelsApp.CONTAINER_KEY) final String containerKey,
        @PathParam(RULE_ID) final String ruleIdParam) {

        Objects.requireNonNull(appPath);
        Objects.requireNonNull(pageModelName);
        Objects.requireNonNull(containerKey);
        Objects.requireNonNull(ruleIdParam);

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);

        final Styles styles = container.getStyles();

        final Rule rule = findRule(pageModel, container, ruleIdParam);
        stylesManager.removeRuleFromStyles(rule, styles);
    }

    /**
     * An utility method for finding a {@link MediaRule}.
     *
     * @param appPath          The primary URL of the {@link CcmApplication}.
     * @param pageModelName    The name of the {@link PageModel}.
     * @param containerKey     The key of the {@link ContainerModel}.
     * @param mediaRuleIdParam The ID of the {@link MediaRule} to find.
     *
     * @return The {@link MediaRule} with the provided {@code mediaRuleId}.
     */
    protected MediaRule findMediaRule(final String appPath,
                                      final String pageModelName,
                                      final String containerKey,
                                      final String mediaRuleIdParam) {

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);

        return findMediaRule(pageModel, container, mediaRuleIdParam);
    }

    /**
     * An utility method for finding a {@link MediaRule}.
     *
     * @param pageModel        The {@link PageModel} to which the
     *                         {@link ContainerModel} belongs.
     * @param container        The {@link ContainerModel} to which the
     *                         {@link MediaRule} belongs.
     * @param mediaRuleIdParam The ID of the {@link MediaRule} to find.
     *
     * @return The {@link MediaRule} with the ID {@code mediaRuleIdParam}.
     */
    private MediaRule findMediaRule(final PageModel pageModel,
                                    final ContainerModel container,
                                    final String mediaRuleIdParam) {

        final Styles styles = container.getStyles();

        final long mediaRuleId;
        try {
            mediaRuleId = Long.parseLong(mediaRuleIdParam);
        } catch (NumberFormatException ex) {
            throw new WebApplicationException(String.format(
                "The provided mediaRuleId \"%s\" numeric.", mediaRuleIdParam));
        }

        return styles
            .getMediaRules()
            .stream()
            .filter(mediaRule -> mediaRuleId == mediaRule.getMediaRuleId())
            .findAny()
            .orElseThrow(() -> new NotFoundException(String.format(
            "No MediaRule with ID %d available in the Styles for "
                + "Container \"%s\" of PageModel \"%s\".",
            mediaRuleId,
            container.getKey(),
            pageModel.getName())));
    }

    /**
     * Utility method for finding a {@link Rule}.
     *
     * @param appPath       The primary URL of the {@link CcmApplication}.
     * @param pageModelName The name of the {@link PageModel}.
     * @param containerKey  The key of the {@link ContainerModel}.
     * @param ruleIdParam   The ID of the {@link Rule} to find.
     *
     * @return The {@link Rule} identified by {@code ruleIdParam}.
     */
    protected Rule findRule(final String appPath,
                            final String pageModelName,
                            final String containerKey,
                            final String ruleIdParam) {

        final CcmApplication app = controller.findCcmApplication(
            String.format("/%s/", appPath));

        final PageModel pageModel = controller.findPageModel(app,
                                                             pageModelName);

        final ContainerModel container = controller.findContainer(app,
                                                                  pageModel,
                                                                  containerKey);

        return findRule(pageModel, container, ruleIdParam);
    }

    /**
     * An utility method for finding a {@link Rule}.
     *
     * @param pageModel   The {@link PageModel} to which the
     *                    {@link ContainerModel} belongs.
     * @param container   The {@link ContainerModel} to which the {@link Rule}
     *                    belongs.
     * @param ruleIdParam The ID of the {@link Rule} to find.
     *
     * @return The {@link Rule} with the ID {@code ruleIdParam}.
     */
    private Rule findRule(final PageModel pageModel,
                          final ContainerModel container,
                          final String ruleIdParam) {

        final Styles styles = container.getStyles();

        final long ruleId;
        try {
            ruleId = Long.parseLong(ruleIdParam);
        } catch (NumberFormatException ex) {
            throw new WebApplicationException(String.format(
                "The provided mediaRuleId \"%s\" numeric.", ruleIdParam));
        }

        return styles
            .getRules()
            .stream()
            .filter(rule -> ruleId == rule.getRuleId())
            .findAny()
            .orElseThrow(() -> new NotFoundException(String.format(
            "No Rule with ID %d available in the Styles for "
                + "Container \"%s\" of PageModel \"%s\".",
            ruleId,
            container.getKey(),
            pageModel.getName())));
    }

    /**
     * Helper method for setting the values of the properties of a
     * {@link MediaRule} using the data from a JSON object.
     *
     * @param mediaRuleData The JSON object providing the data.
     * @param mediaRule The {@link MediaRule}.
     */
    private void setMediaRuleProperties(final JsonObject mediaRuleData,
                                        final MediaRule mediaRule) {

        Objects.requireNonNull(mediaRuleData);
        Objects.requireNonNull(mediaRule);

        final JsonObject mediaQueryData = mediaRuleData
            .getJsonObject("mediaQuery");
        final JsonObject maxWidthData = mediaQueryData
            .getJsonObject("maxWidth");
        final JsonObject minWidthData = mediaQueryData
            .getJsonObject("minWidth");

        final Dimension maxWidth = new Dimension();
        maxWidth.setUnit(Unit.valueOf(maxWidthData.getString("unit")));
        maxWidth.setValue(maxWidthData.getJsonNumber("value").doubleValue());
        final MediaType mediaType = MediaType.valueOf(mediaQueryData
            .getString("mediaType"));

        final Dimension minWidth = new Dimension();
        minWidth.setUnit(Unit.valueOf(minWidthData.getString("unit")));
        minWidth.setValue(minWidthData.getJsonNumber("minWidth").doubleValue());

        mediaRule.getMediaQuery().setMaxWidth(maxWidth);
        mediaRule.getMediaQuery().setMediaType(mediaType);
        mediaRule.getMediaQuery().setMinWidth(minWidth);
    }

}
