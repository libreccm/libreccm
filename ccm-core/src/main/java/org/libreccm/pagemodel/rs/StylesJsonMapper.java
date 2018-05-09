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

import org.libreccm.pagemodel.styles.CssProperty;
import org.libreccm.pagemodel.styles.Dimension;
import org.libreccm.pagemodel.styles.MediaQuery;
import org.libreccm.pagemodel.styles.MediaRule;
import org.libreccm.pagemodel.styles.Rule;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 * Utility class for mapping the entities from the
 * {@link org.libreccm.pagemodel.styles} package to JSON.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class StylesJsonMapper {

    /**
     * Map a {@link Dimension} object to JSON.
     *
     * @param dimension The {@link Dimension} object to map.
     *
     * @return A JSON object representing the provided {@link Dimension} object.
     */
    protected JsonObject mapDimensionToJson(final Dimension dimension) {

        Objects.requireNonNull(dimension);

        return Json
            .createObjectBuilder()
            .add("value", dimension.getValue())
            .add("unit", dimension.getUnit().toString())
            .build();
    }

    /**
     * Maps a List of {@link MediaRule} objects to JSON.
     *
     * @param mediaRules The {@link MediaRule}s to map.
     *
     * @return An JSON array with the data from the {@link MediaRule} objects in
     *         the list.
     */
    protected JsonArray mapMediaRulesToJson(final List<MediaRule> mediaRules) {

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        Objects
            .requireNonNull(mediaRules)
            .stream()
            .map(this::mapMediaRuleToJson)
            .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    /**
     * Maps a {@link MediaRule} object to JSON.
     *
     * @param mediaRule The {@link MediaRule} object to map.
     *
     * @return The JSON representation of the provided {@link MediaRule} object.
     */
    protected JsonObject mapMediaRuleToJson(final MediaRule mediaRule) {

        Objects.requireNonNull(mediaRule);

        return Json
            .createObjectBuilder()
            .add("mediaRuleId", mediaRule.getMediaRuleId())
            .add("mediaQuery", mapMediaQueryToJson(mediaRule.getMediaQuery()))
            .add("rules", mapRulesToJson(mediaRule.getRules()))
            .build();
    }

    /**
     * Maps a {@link MediaQuery} object to JSON.
     *
     * @param mediaQuery The {@link MediaQuery} object to map.
     *
     * @return The JSON representation of the provided {@link MediaQuery}
     *         object.
     */
    protected JsonObject mapMediaQueryToJson(final MediaQuery mediaQuery) {

        Objects.requireNonNull(mediaQuery);

        return Json
            .createObjectBuilder()
            .add("mediaQueryId", mediaQuery.getMediaQueryId())
            .add("mediaType", mediaQuery.getMediaType().toString())
            .add("minWidth", mapDimensionToJson(mediaQuery.getMinWidth()))
            .add("maxWidth", mapDimensionToJson(mediaQuery.getMaxWidth()))
            .build();
    }

    /**
     * Maps a list of {@link Rule} objects to JSON.
     *
     * @param rules The list of {@link Rule} objects to map.
     *
     * @return A JSON array with the JSON representations of the {@link Rule}
     *         objects in the list.
     */
    protected JsonArray mapRulesToJson(final List<Rule> rules) {

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        Objects
            .requireNonNull(rules)
            .stream()
            .map(this::mapRuleToJson)
            .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    /**
     * Maps a {@link Rule} object to JSON.
     *
     * @param rule The {@link Rule} object to map.
     *
     * @return The JSON representation of the provided {@link RuleObject}.
     */
    protected JsonObject mapRuleToJson(final Rule rule) {

        Objects.requireNonNull(rule);

        return Json
            .createObjectBuilder()
            .add("ruleId", rule.getRuleId())
            .add("selector", rule.getSelector())
            .add("properties", mapPropertiesToJson(rule.getProperties()))
            .build();
    }

    /**
     * Maps a list of {@link CssProperty} objects to JSON.
     *
     * @param properties The list of {@link CssProperty} objects to map.
     *
     * @return A JSON array containing the JSON representations of the
     *         {@link CssProperty} objects in the list.
     */
    protected JsonArray mapPropertiesToJson(final List<CssProperty> properties) {

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        Objects
            .requireNonNull(properties)
            .stream()
            .map(this::mapCssPropertyToJson)
            .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    /**
     * Maps a {@link CssProperty} object to JSON.
     * 
     * @param property The {@link CssProperty} to map.
     * @return The JSON representation of the provided {@link CssProperty}.
     */
    protected JsonObject mapCssPropertyToJson(final CssProperty property) {

        Objects.requireNonNull(property);

        return Json
            .createObjectBuilder()
            .add("propertyId", property.getPropertyId())
            .add("name", property.getName())
            .add("value", property.getValue())
            .build();
    }

}
