/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.l10n.jaxb;

import org.libreccm.l10n.LocalizedString;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB adapter for {@link LocalizedString#values} to produce a more compact XML
 * for the values map.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LocalizedStringValuesAdapter
    extends XmlAdapter<LocalizedStringValues, Map<Locale, String>> {

    @Override
    public Map<Locale, String> unmarshal(final LocalizedStringValues values)
        throws Exception {

        return values
            .getValues()
            .stream()
            .collect(Collectors.toMap(value -> new Locale(value.getLocale()),
                                      value -> value.getValue()));

    }

    @Override
    public LocalizedStringValues marshal(final Map<Locale, String> values)
        throws Exception {

        final List<LocalizedStringValue> list = values
            .entrySet()
            .stream()
            .map(this::generateValue)
            .collect(Collectors.toList());

        final LocalizedStringValues result = new LocalizedStringValues();
        result.setValues(list);

        return result;
    }

    private LocalizedStringValue generateValue(
        final Map.Entry<Locale, String> entry) {

        final LocalizedStringValue value = new LocalizedStringValue();
        value.setLocale(entry.getKey().toString());
        value.setValue(entry.getValue());

        return value;
    }

}
