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

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * A converter for converting properties of the type {@link MimeType} to
 * {@code String}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Converter(autoApply = true)
public class MimeTypeConverter implements AttributeConverter<MimeType, String> {

    @Override
    public String convertToDatabaseColumn(final MimeType attribute) {
        if (attribute == null) {
            return null;
        } else {
            return attribute.toString();
        }
    }

    @Override
    public MimeType convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return new MimeType(dbData);
        } catch (MimeTypeParseException ex) {
            throw new IllegalArgumentException("Not a valid mime type", ex);
        }
    }

}
