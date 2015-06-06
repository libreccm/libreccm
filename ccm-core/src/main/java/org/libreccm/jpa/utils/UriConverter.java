/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.jpa.utils;

import java.net.URI;
import java.net.URISyntaxException;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * A converter for converting URI properties to String. JPA does not support
 * URI as type and will store them as LOBs without this converter. The converter
 * is automatically applied to all URI properties.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Converter(autoApply = true)
public class UriConverter implements AttributeConverter<URI, String> {

    /**
     * {@inheritDoc}
     * 
     * @param attribute {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String convertToDatabaseColumn(final URI attribute) {
        return attribute.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @param dbData {@inheritDoc}
     * @return  {@inheritDoc}
     */
    @Override
    public URI convertToEntityAttribute(final String dbData) {
        try {
            return new URI(dbData);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(
                    String.format("Failed to convert String value '%s' from "
                                          + "database to an URI.",
                                  dbData),
                    ex);
        }
    }

}
