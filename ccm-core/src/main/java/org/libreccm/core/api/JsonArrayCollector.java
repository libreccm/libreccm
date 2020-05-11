/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.core.api;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class JsonArrayCollector implements Collector<JsonValue, JsonArrayBuilder, JsonArray>{

    @Override
    public Supplier<JsonArrayBuilder> supplier() {
        return Json::createArrayBuilder;
    }

    @Override
    public BiConsumer<JsonArrayBuilder, JsonValue> accumulator() {
        return JsonArrayBuilder::add;
    }

    @Override
    public BinaryOperator<JsonArrayBuilder> combiner() {
        return (left, right) -> left.add(right);
    }

    @Override
    public Function<JsonArrayBuilder, JsonArray> finisher() {
        return JsonArrayBuilder::build;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
    
}
